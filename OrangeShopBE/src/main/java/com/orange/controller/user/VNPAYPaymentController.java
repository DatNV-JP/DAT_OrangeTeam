package com.orange.controller.user;

import com.orange.utils.AccountUtils;
import com.orange.utils.vnpay.VNPAYUtils;
import com.orange.common.payload.Result;
import com.orange.domain.dto.OrderDTO;
import com.orange.domain.mapper.IOrderMapper;
import com.orange.domain.model.EOrderStatus;
import com.orange.domain.model.OrderStatus;
import com.orange.domain.model.Transaction;
import com.orange.domain.model.vnpay.VNPAY;
import com.orange.exception.*;
import com.orange.payload.request.UpdateOrderStatus;
import com.orange.repositories.ITransactionRepository;
import com.orange.services.IOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@CrossOrigin
@RequestMapping("/payment")
@RequiredArgsConstructor
public class VNPAYPaymentController {

    private final HttpServletRequest request;

    private final RestTemplate restTemplate;
    private final IOrderService orderService;
    private final ITransactionRepository transactionRepository;
    private final IOrderMapper orderMapper;
    private final AccountUtils accountUtils;

    @PostMapping("/create-url")
    public Result<?> createUrlPayment(@RequestBody Optional<OrderDTO> optional) {
        if (optional.isPresent()) {
            // Get user
            Long userId = accountUtils.getUserOrAnonymousUser() == null ? null: accountUtils.getUserId();
            // create order
            OrderDTO orderDTO = optional.get();
            orderDTO.setOrderStatus(new OrderStatus(EOrderStatus.WAIT_FOR_PAY.getId()));
            orderDTO.setWaitForPayTime(new Date());
            orderDTO = orderService.create(orderDTO, userId);
            String paymentUrl = "";
            if (orderDTO.getPaymentType().getValue().equalsIgnoreCase("VNPAY")) {
                paymentUrl = getVNPAYPaymentUrl(orderDTO);
            }
            return Result.result(HttpStatus.OK.value(), "success", paymentUrl);
        } else {
            throw GlobalException.throwException("error.missing.parameter");
        }
    }

    private String getVNPAYPaymentUrl(OrderDTO orderDTO) {
        String description = "Thanh Toan don hang cho thue bao " +
                orderDTO.getConsigneePhone() +
                ". So tien " +
                orderDTO.getOrderTotal() + " VND";

        // create payment VNPAY
        Integer amount = (int) (orderDTO.getOrderTotal() * 100);
        String vnp_TxnRef = String.valueOf(orderDTO.getId()) + "sanvadio";
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
        String vnp_ExpireDate = formatter.format(cld.getTime());
        //Add Params of 2.1.0 Version
//        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);
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
                try {
                    hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                    query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                    query.append('=');
                    query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                } catch (UnsupportedEncodingException e) {
                    throw GlobalException.throwException("error.global");
                }
                //Build query
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
    }

    @GetMapping("/payment-info")
    public Result<?> transactionHandle() {
        String txnRef = String.valueOf(request.getParameter("vnp_TxnRef")).replaceAll("[^\\d]", "");
        int addTime = request.getLocale().getCountry().equals("VN")?0:7;
        OrderDTO orderDTO = this.orderService.findById(Long.parseLong(txnRef));
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
                                && transaction !=null) {
                            //Here Code update PaymnentStatus = 1 into your Database
                            UpdateOrderStatus orderStatus = UpdateOrderStatus.builder()
                                    .orderStatusId(EOrderStatus.WAIT_FOR_CONFIRMATION.getId())
                                    .orderId(orderDTO.getId())
                                    .build();
                            orderDTO = this.orderService.updateOrderStatus(orderStatus);

                            transaction.setTransactionStatus(1);
                            transaction.setPayDate(new Timestamp(formatter.parse(request.getParameter("vnp_PayDate")).getTime()+addTime*60*60*1000L));
                            transactionRepository.save(transaction);
                            return Result.result(HttpStatus.OK.value(), "Thanh toán thành công, đơn hàng đang được xử lý", orderDTO);
                        } else {
                            // Here Code update PaymnentStatus = 2 into your Database
                            assert transaction != null;
                            transaction.setTransactionStatus(2);
                            transaction.setPayDate(new Timestamp(formatter.parse(request.getParameter("vnp_PayDate")).getTime()+addTime*60*60*1000L));
                            transactionRepository.save(transaction);
                            this.orderService.trackingPageNotificationToAdmin(orderMapper.toEntity(orderDTO), "Đơn hàng: " + orderDTO.getId() + " đã thanh toán thất bại", "[CẢNH BÁO] giao dịch Không thành công");
                            return Result.result(HttpStatus.PAYMENT_REQUIRED.value(), "Giao dịch Không thành công", orderDTO);
                        }
                    } else {
                        this.orderService.trackingPageNotificationToAdmin(orderMapper.toEntity(orderDTO),
                                "Đơn hàng: " + orderDTO.getId() + " đã thanh toán thất bại do giao dịch đã tồn tại hoặc đã được xử lý",
                                "[CẢNH BÁO] giao dịch Không thành công");
                        //out.print("{\"RspCode\":\"02\",\"Message\":\"Order already confirmed\"}");
                        return Result.result(HttpStatus.FORBIDDEN.value(), "Giao dịch đã tồn tại hoặc đã được xử lý", orderDTO);
                    }
                } else {
                    this.orderService.trackingPageNotificationToAdmin(orderMapper.toEntity(orderDTO),
                            "Đơn hàng: " + orderDTO.getId() + " đã thanh toán thất bại vì số dư sai do chênh lệch",
                            "[CẢNH BÁO] giao dịch Không thành công");
                    //out.print("{\"RspCode\":\"04\",\"Message\":\"Invalid Amount\"}");
                    return Result.result(HttpStatus.FORBIDDEN.value(), "Số dư sai do chênh lệch", orderDTO);
                }

            } else {
                this.orderService.trackingPageNotificationToAdmin(orderMapper.toEntity(orderDTO),
                        "Đơn hàng: " + orderDTO.getId() + " đã thanh toán thất bại do sai chữ ký",
                        "[CẢNH BÁO] giao dịch Không thành công");
                //out.print("{\"RspCode\":\"97\",\"Message\":\"Invalid Checksum\"}");
                return Result.result(HttpStatus.BAD_REQUEST.value(), "Sai chữ ký", orderDTO);
            }
        } catch (Exception e) {
            e.printStackTrace();
            this.orderService.trackingPageNotificationToAdmin(orderMapper.toEntity(orderDTO),
                    "Đơn hàng: " + orderDTO.getId() + " đã thanh toán thất bại. Không rõ nguyên nhân",
                    "[CẢNH BÁO] giao dịch Không thành công");
            return Result.result(HttpStatus.BAD_REQUEST.value(), "Giao dịch không thành công", orderDTO);
        }
    }

    @GetMapping("/refund")
    public Result<?> doRefund( HttpServletRequest request)
    {
        try{
            System.out.println(request.getHeaderNames());
//            StringBuilder query = new StringBuilder(VNPAY.VNPAY_TRANSACTION_URL.getValue());
//            query.append("?");
            StringBuilder query = new StringBuilder();
            String vnp_RequestId = VNPAYUtils.getRandomNumber(15);
            String vnp_Version ="2.1.0";
            String vnp_Command ="refund";
            String vnp_TmnCode = VNPAY.VNPAY_TMN_CODE.getValue();
            String vnp_TransactionType = "02";
//            Optional<Transaction> transactionOp = transactionRepository.getByTransactionCode(refundRequestDTO.getTransactionCode());
//            if(!transactionOp.isPresent() || transactionOp.get().getTransactionStatus() !=1 || transactionOp.get().getOrder().getOrderStatus()!=1)
//                throw new RuntimeException("Transaction not found or not successfull transaction");
            String vnp_TxnRef = "527sanvadio";
//            if(refundRequestDTO.getAmount() > transactionOp.get().getAmount())
//                throw new RuntimeException("Requested Refund Amount exceeded the transaction's amount");
            Long vnp_Amount = Long.valueOf(55500*100);
            String vnp_OrderInfo ="Hoan tien cho don hang 527sanvadio";
            Long vnp_TransactionNo =13974472L; // can empty

            Calendar calendar =Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            String vnp_TransactionDate = formatter.format(calendar.getTime());
            String vnp_CreateBy ="Sanvadio";
            String vnp_createDate = formatter.format(calendar.getTime());
            String vnp_IpAddr = VNPAYUtils.getIpAddress(request);
            System.out.println(vnp_IpAddr);
            if(vnp_IpAddr ==null) throw GlobalException.throwException("error.global");

            // put to a map
            Map vnp_Params = new LinkedHashMap();
            vnp_Params.put("vnp_RequestId",vnp_RequestId);
            vnp_Params.put("vnp_Version",vnp_Version);
            vnp_Params.put("vnp_Command",vnp_Command);
            vnp_Params.put("vnp_TmnCode",vnp_TmnCode);
            vnp_Params.put("vnp_TransactionType",vnp_TransactionType);
            vnp_Params.put("vnp_TxnRef",vnp_TxnRef);
            vnp_Params.put("vnp_Amount",vnp_Amount);
            vnp_Params.put("vnp_TransactionNo",vnp_TransactionNo);
            vnp_Params.put("vnp_TransactionDate",vnp_TransactionDate);
            vnp_Params.put("vnp_CreateBy",vnp_CreateBy);
            vnp_Params.put("vnp_CreateDate",vnp_createDate);
            vnp_Params.put("vnp_IpAddr",vnp_IpAddr);
            vnp_Params.put("vnp_OrderInfo",vnp_OrderInfo);

            List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
            StringBuilder hashData = new StringBuilder();
            Iterator itr = fieldNames.iterator();
            while (itr.hasNext()){
                String fieldKey = (String) itr.next();
                String fieldVal = String.valueOf(vnp_Params.get(fieldKey));
                if(fieldKey!=null && StringUtils.hasText(fieldVal))
                {
                    query.append(fieldKey).append("=").append(URLEncoder.encode(fieldVal,StandardCharsets.US_ASCII.toString()));
                    hashData.append(fieldKey).append("=").append(URLEncoder.encode(fieldVal,StandardCharsets.US_ASCII.toString()));
                }
                if(itr.hasNext()){
                    query.append("&");
                    hashData.append("|");
                }
            }
            String vnp_SecureHash = VNPAYUtils.hmacSHA512(VNPAY.VNPAY_HASH_SECRET.getValue(), hashData.toString());
            query.append("&").append("vnp_SecureHash").append("=").append(vnp_SecureHash);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> requestEntity = new HttpEntity<>(query.toString(), headers);
            try {
                String result = restTemplate.exchange
                                (
                                        "https://sandbox.vnpayment.vn/merchant_webapi/api/transaction",
                                        HttpMethod.POST,
                                        requestEntity,
                                        String.class
                                )
                        .getBody();
                return Result.result(HttpStatus.OK.value(), "oke", result);
            } catch (RestClientException e) {
                e.printStackTrace();
                throw GlobalException.throwException("address.user.notfound");
            } catch (Exception e){
                e.printStackTrace();
                throw GlobalException.throwException("error.global");
            }

        } catch (Exception ex)
        {
            ex.printStackTrace();
            return Result.error(HttpStatus.BAD_REQUEST.value(), "fail");
        }
    }
}
