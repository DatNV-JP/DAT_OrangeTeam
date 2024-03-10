package com.orange.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.orange.utils.vnpay.VNPAYUtils;
import com.orange.domain.model.vnpay.VNPAY;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class RefundService {

    private final HttpServletRequest request;

    public void doVNPAYRefund(Long id) throws UnsupportedEncodingException, JsonProcessingException {
        // Tạo đối tượng WebClient
        WebClient webClient = WebClient.create();

        // Tạo các thông tin cần thiết
        String orderNo = "572sanvadio"; // Mã đơn hàng cần hoàn tiền
        String txnRef = "572sanvadio"; // Mã giao dịch hoàn tiền
        Long amount = 6650000L; // Số tiền cần hoàn trả (đơn vị VNĐ)
        String refundDesc = "Hoàn tiền đơn hàng " + orderNo; // Mô tả hoàn tiền
        String vnp_RequestId = VNPAYUtils.getRandomNumber(15);

        // Tạo thời gian hiện tại
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

        // Tạo map chứa các tham số
        Map<String, String> params = new HashMap<>();
        params.put("vnp_Version", VNPAY.VNPAY_VERSION.getValue());
        params.put("vnp_Command", "refund");
        params.put("vnp_TmnCode", VNPAY.VNPAY_TMN_CODE.getValue());
        params.put("vnp_OrderInfo", refundDesc);
        params.put("vnp_TransactionNo", "2124565");
        params.put("vnp_CreateBy ", "Sanvadio");
        params.put("vnp_Amount", amount.toString());
        params.put("vnp_TransDate", now.format(formatter));
        params.put("vnp_CreateDate", now.format(formatter));
        params.put("vnp_IpAddr", "127.0.0.1");
        params.put("vnp_TxnRef", txnRef);
        params.put("vnp_RequestId ", vnp_RequestId);
//        params.put("vnp_SecureHashType", "MD5");
//        params.put("vnp_VersionCode", "01");
//        params.put("vnp_Locale", "vn");
//        params.put("vnp_BankCode", "");
//        params.put("vnp_Currency", "VND");
//        params.put("vnp_OrderType", "refund");
        params.put("vnp_TransactionType", "02");
//        params.put("vnp_ReturnUrl", "");

        List fieldNames = new ArrayList(params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) params.get(fieldName);
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

        params.put("vnp_SecureHash", vnp_SecureHash);
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        params.forEach((key, value) -> formData.add(key, value));
        // Tạo yêu cầu HTTP POST và gửi đi
        Mono<String> response = webClient.post()
                .uri(VNPAY.VNPAY_TRANSACTION_URL.getValue())
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .bodyToMono(String.class);
        System.out.println(response);
        // Xử lý kết quả trả về
        ObjectMapper mapper = new ObjectMapper();
        JsonNode responseNode = mapper.readTree(response.block());

        String code = responseNode.get("code").asText();
        String message = responseNode.get("message").asText();
        if ("00".equals(code)) {
            // Xử lý hoàn tiền thành công
            System.out.println("OKe");
        } else {
            // Xử lý hoàn tiền thất bại
            System.out.println("Fail");
        }
    }
}
