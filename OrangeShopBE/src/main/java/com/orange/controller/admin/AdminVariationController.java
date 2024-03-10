package com.orange.controller.admin;

import com.orange.common.payload.Result;
import com.orange.services.IVariationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/admin/variation")
@RequiredArgsConstructor
public class AdminVariationController {
    private final IVariationService variationService;

    @GetMapping("/by-category")
    public Result<?> getByCategory(@RequestParam(defaultValue = "0") Long categoryId){
        return Result.result(HttpStatus.OK.value(), "Lấy dữ liệu thuộc tính thành công!", variationService.getAllByCategory(categoryId));
    }
}
