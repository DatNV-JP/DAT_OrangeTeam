package com.orange.controller.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.orange.common.payload.Result;
import com.orange.domain.dto.OrderDTO;
import com.orange.exception.EntityInvalid;
import com.orange.exception.GlobalException;
import com.orange.services.impl.PaymentService;
import com.orange.services.impl.RefundService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("/payment-v2")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final RefundService refundService;
    private final HttpServletRequest request;

    @PostMapping("/create-pay-url")
    public Result<?> createPaymentUrl(@RequestBody Optional<OrderDTO> optional) {
        if (optional.isPresent()) {
            return Result.result(HttpStatus.OK.value(), "Đã tạo url yêu cầu thanh toán thành công", paymentService.createUrlPayment(optional.get(), request));
        } else {
            throw GlobalException.throwException("error.notfound");
        }
    }

    @PostMapping("/repay-url")
    public Result<?> createRepaymentUrl(@RequestBody Optional<OrderDTO> optional) {
        if (optional.isPresent()) {
            return Result.result(HttpStatus.OK.value(), "Đã tạo url yêu cầu thanh toán thành công", paymentService.createUrlRepayment(optional.get(), request));
        } else {
            throw GlobalException.throwException("error.notfound");
        }
    }

    @GetMapping("/payment-info")
    public Result<?> paymentInfo() {
        try {
            return Result.result(HttpStatus.OK.value(), "Thanh toán thành công, đơn hàng đang được xử lý", paymentService.savePaymentInfo(request));
        } catch (UnsupportedEncodingException | ParseException ue) {
            throw GlobalException.throwException("error.global");
        }
    }
    @GetMapping("/payment-refund")
    public Result<?> paymentRefund() {
        try {
            refundService.doVNPAYRefund(506L);
            return Result.result(HttpStatus.OK.value(), "Thanh toán thành công, đơn hàng đang được xử lý", true);
        } catch (JsonProcessingException | UnsupportedEncodingException e) {
            throw GlobalException.throwException("error.global");
        }
    }

}
