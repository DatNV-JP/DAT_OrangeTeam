package com.orange.services.impl;

import com.orange.utils.AccountUtils;
import com.orange.utils.vnpay.VNPAYUtils;
import com.orange.domain.dto.OrderDTO;
import com.orange.domain.mapper.IOrderMapper;
import com.orange.domain.model.EOrderStatus;
import com.orange.domain.model.OrderStatus;
import com.orange.domain.model.Transaction;
import com.orange.domain.model.vnpay.VNPAY;
import com.orange.exception.AmountDifference;
import com.orange.exception.TransactionExistedOrProcessed;
import com.orange.exception.TransactionFailedException;
import com.orange.exception.UnsupportedPaymentTypeException;
import com.orange.payload.request.UpdateOrderStatus;
import com.orange.repositories.ITransactionRepository;
import com.orange.services.IOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final IOrderService orderService;
    private final ITransactionRepository transactionRepository;
    private final IOrderMapper orderMapper;
    private final AccountUtils accountUtils;

    public String createUrlPayment(OrderDTO orderDTO, HttpServletRequest request) {
        String paymentType = orderDTO.getPaymentType().getValue();
        switch (paymentType) {
            case "VNPAY":
                return createVNPAYUrlPayment(orderDTO, request);
            default:
                throw new UnsupportedPaymentTypeException("Hiện chưa hỗ chợ thay toán qua " + paymentType);
        }
    }

    public String createVNPAYUrlPayment(OrderDTO orderDTO, HttpServletRequest request) {
        try {
            OrderDTO payOrder = createWaitForPayOrder(orderDTO);
            String description = "Thanh Toan don hang cho thue bao " +
                    payOrder.getConsigneePhone() +
                    ". So tien " +
                    payOrder.getOrderTotal() + " VND";

            // create payment VNPAY
            Integer amount = (int) (payOrder.getOrderTotal() * 100);
            String vnp_TxnRef = String.valueOf(payOrder.getId()) + "sanvadio";
            Map<String, String> vnp_Params = new HashMap();
            vnp_Params.put(VNPAY.VNPAY_VERSION.getKey(), VNPAY.VNPAY_VERSION.getValue());
            vnp_Params.put(VNPAY.VNPAY_COMMAND.getKey(), VNPAY.VNPAY_COMMAND.getValue());
            vnp_Params.put(VNPAY.VNPAY_TMN_CODE.getKey(), VNPAY.VNPAY_TMN_CODE.getValue());
            vnp_Params.put("vnp_Amount", String.valueOf(amount));
            vnp_Params.put("vnp_CurrCode", "VND");
//        vnp_Params.put("vnp_BankCode", null);
        /*String bank_code = req.getParameter("bankcode");
        if (bank_code != null && !bank_code.isEmpty()) {
            vnp_Params.put("vnp_BankCode", bank_code);
        }*/
            vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
            vnp_Params.put("vnp_OrderInfo", description);
            vnp_Params.put(VNPAY.VNPAY_ORDER_TYPE.getKey(), VNPAY.VNPAY_ORDER_TYPE.getValue());
            vnp_Params.put(VNPAY.VNPAY_LOCALE_DEFAULT.getKey(), VNPAY.VNPAY_LOCALE_DEFAULT.getValue());
            vnp_Params.put(VNPAY.VNPAY_RETURN_URL.getKey(), VNPAY.VNPAY_RETURN_URL.getValue());
            vnp_Params.put("vnp_IpAddr", VNPAYUtils.getIpAddress(request));
            Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));

            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            String vnp_CreateDate = formatter.format(cld.getTime());

            vnp_Params.put("vnp_CreateDate", vnp_CreateDate);
            cld.add(Calendar.MINUTE, 15);
//            String vnp_ExpireDate = formatter.format(cld.getTime());
            //Add Params of 2.1.0 Version
//            vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);
            //Build data to hash and querystring
            List fieldNames = new ArrayList(vnp_Params.keySet());
            Collections.sort(fieldNames);
            StringBuilder hashData = new StringBuilder();
            StringBuilder query = new StringBuilder();
            Iterator itr = fieldNames.iterator();
            while (itr.hasNext()) {
                String fieldName = (String) itr.next();
                String fieldValue = (String) vnp_Params.get(fieldName);
                if ((fieldValue != null) && (fieldValue.length() > 0)) {
                    //Build hash data
                    hashData.append(fieldName);
                    hashData.append('=');
                    hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                    //Build query
                    query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                    query.append('=');
                    query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                    if (itr.hasNext()) {
                        query.append('&');
                        hashData.append('&');
                    }
                }
            }
            String queryUrl = query.toString();
            String vnp_SecureHash = VNPAYUtils.hmacSHA512(VNPAY.VNPAY_HASH_SECRET.getValue(), hashData.toString());
            queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
            String paymentUrl = VNPAY.VNPAY_URL.getValue() + "?" + queryUrl;
            return paymentUrl;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new RuntimeException("Có lỗi xảy ra trong quá trình thanh toán");
        }
    }

    public OrderDTO savePaymentInfo(HttpServletRequest request)
            throws UnsupportedEncodingException, ParseException {
        try {

        /*  IPN URL: Record payment results from VNPAY
        Implementation steps:
        Check checksum
        Find transactions (vnp_TxnRef) in the database (checkOrderId)
        Check the payment status of transactions before updating (checkOrderStatus)
        Check the amount (vnp_Amount) of transactions before updating (checkAmount)
        Update results to Database
        Return recorded results to VNPAY
        */

            // ex:  	    PaymnentStatus = 0; pending
            //              PaymnentStatus = 1; success
            //              PaymnentStatus = 2; Faile

            //Begin process return from VNPAY
            Map fields = new HashMap();

            for (Enumeration params = request.getParameterNames(); params.hasMoreElements(); ) {
                String fieldName = URLEncoder.encode((String) params.nextElement(), StandardCharsets.US_ASCII.toString());
                String fieldValue = URLEncoder.encode(request.getParameter(fieldName), StandardCharsets.US_ASCII.toString());
                if ((fieldValue != null) && (fieldValue.length() > 0)) {
                    fields.put(fieldName, fieldValue);
                }
            }

            String vnp_SecureHash = request.getParameter("vnp_SecureHash");
            if (fields.containsKey("vnp_SecureHashType")) {
                fields.remove("vnp_SecureHashType");
            }
            if (fields.containsKey("vnp_SecureHash")) {
                fields.remove("vnp_SecureHash");
            }

            // Check checksum
            String signValue = VNPAYUtils.hashAllFields(fields);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");

            if (signValue.equals(vnp_SecureHash)) {
                String txnRef = String.valueOf(fields.get("vnp_TxnRef")).replaceAll("[^\\d]", "");
                int addTime = request.getLocale().getCountry().equals("VN") ? 0 : 7;
                OrderDTO orderDTO = this.orderService.findById(Long.parseLong(txnRef));
                int amount = (int) (orderDTO.getOrderTotal() * 100);
                boolean checkAmount = amount == Integer.parseInt(fields.get("vnp_Amount").toString()); // vnp_Amount is valid (Check vnp_Amount VNPAY returns compared to the amount of the code (vnp_TxnRef) in the Your database).

                boolean checkOrderStatus = EOrderStatus.WAIT_FOR_PAY.getId().equals(orderDTO.getOrderStatus().getId()); // PaymnentStatus = 0 (pending)


                if (checkAmount) {
                    if (checkOrderStatus) {
                        Transaction transaction = Transaction.builder()
                                .transationResCode(request.getParameter("vnp_ResponseCode"))
                                .bankCode(request.getParameter("vnp_BankCode"))
                                .bankTranNo(request.getParameter("vnp_BankTranNo"))
                                .transactionNo(Long.valueOf(request.getParameter("vnp_TransactionNo")))
                                .order(orderMapper.toEntity(orderDTO))
                                .amount(orderDTO.getOrderTotal())
                                .transactionRef(String.valueOf(fields.get("vnp_TxnRef")))
                                .currencyCode("VND")
                                .build();
                        if ("00".equals(request.getParameter("vnp_ResponseCode"))
                                && request.getParameter("vnp_TransactionStatus").equals("00")
                                && transaction != null) {
                            //Here Code update PaymnentStatus = 1 into your Database
                            UpdateOrderStatus orderStatus = UpdateOrderStatus.builder()
                                    .orderStatusId(EOrderStatus.WAIT_FOR_CONFIRMATION.getId())
                                    .orderId(orderDTO.getId())
                                    .build();
                            OrderDTO updatedOrder = this.orderService.updateOrderStatus(orderStatus);

//                            transaction.setTransactionStatus(1);
//                            transaction.setPayDate(new Timestamp(formatter.parse(request.getParameter("vnp_PayDate")).getTime() + addTime * 60 * 60 * 1000L));
//                            transactionRepository.save(transaction);
                            return updatedOrder;
                        } else {
                            // Here Code update PaymnentStatus = 2 into your Database
                            transaction.setTransactionStatus(2);
                            transaction.setPayDate(new Timestamp(formatter.parse(request.getParameter("vnp_PayDate")).getTime() + addTime * 60 * 60 * 1000L));
                            transactionRepository.save(transaction);
                            throw new TransactionFailedException("Giao dịch Không thành công");
                        }
                    } else {
                        //out.print("{\"RspCode\":\"02\",\"Message\":\"Order already confirmed\"}");
                        throw new TransactionExistedOrProcessed("Giao dịch đã tồn tại hoặc đã được xử lý");
                    }
                } else {
                    //out.print("{\"RspCode\":\"04\",\"Message\":\"Invalid Amount\"}");
                    throw new AmountDifference("Số dư sai do chênh lệch");
                }

            } else {
                //out.print("{\"RspCode\":\"97\",\"Message\":\"Invalid Checksum\"}");
                throw new RuntimeException("Sai chữ ký");
            }
        } catch (Exception e) {
            throw e;
        }
    }


    private OrderDTO createWaitForPayOrder(OrderDTO orderDTO) {
        orderDTO.setWaitForPayTime(new Date());
        orderDTO.setOrderStatus(new OrderStatus(EOrderStatus.WAIT_FOR_PAY.getId()));
        Long userId = accountUtils.getUserOrAnonymousUser() == null ? null: accountUtils.getUserId();
        return orderService.create(orderDTO, userId);
    }

    public Object createUrlRepayment(OrderDTO orderDTO, HttpServletRequest request) {
        return null;
    }
}
