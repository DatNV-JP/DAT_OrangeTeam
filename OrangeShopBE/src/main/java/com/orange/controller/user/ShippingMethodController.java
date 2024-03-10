package com.orange.controller.user;

import com.orange.common.payload.Result;
import com.orange.services.IShippingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@RequestMapping("/shipping-method")
@RequiredArgsConstructor
public class ShippingMethodController {
    private final IShippingService shippingService;

    @GetMapping("")
    public Result<?> getAllShippingMethod() {
        return Result.result(HttpStatus.OK.value(), "Lấy dữ liệu thành công", shippingService.getAllShippingMethod());
    }
}
