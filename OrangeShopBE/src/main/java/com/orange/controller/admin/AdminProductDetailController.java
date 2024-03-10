package com.orange.controller.admin;

import com.orange.utils.FillterPrice;
import com.orange.utils.FilterUtils;
import com.orange.utils.SortUtils;
import com.orange.common.payload.Page;
import com.orange.common.payload.Result;
import com.orange.services.IProductDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@CrossOrigin
@RequestMapping("admin/product-detail")
@RequiredArgsConstructor
public class AdminProductDetailController {

    private final IProductDetailService productDetailService;

    @GetMapping("")
    public Result<?> getAllProductDetailForPromotion(@RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "10") int size,
                                                     @RequestParam(required = false) Long idCategory,
                                                     @RequestParam(required = false) Double fromPrice,
                                                     @RequestParam(required = false) Double toPrice,
                                                     @RequestParam(required = false) String namePr,
                                                     @RequestParam(required = false) LocalDateTime startDate,
                                                     @RequestParam(required = false) LocalDateTime endDate,
                                                     @RequestParam(required = false) Integer totalQuantity,
                                                     @RequestParam(defaultValue = "1") Integer findBy
    ) {
        if (findBy == 1) {
            return Result.result(HttpStatus.OK.value(), "Lấy dữ liệu thành công!", productDetailService.findForPromotion(idCategory, fromPrice, toPrice, namePr, PageRequest.of(page, size)));
        }
        return Result.result(HttpStatus.OK.value(), "Lấy dữ liệu thành công!", productDetailService.findUnpopularProductDetails(startDate, endDate, totalQuantity, PageRequest.of(page, size)));
    }

    @GetMapping("/search")
    private Result<?> searchProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "") String keyWord,
            @RequestParam(defaultValue = "createDate,desc") String[] sort,
            @RequestParam(defaultValue = "priceSale,0:0") String filterPrice,
            @RequestParam(required = false) Long categoryId
    ) {
        Page<?> pages = this.productDetailService.search(categoryId, keyWord, FilterUtils.getFilterPrice(filterPrice), PageRequest.of(page, size, SortUtils.getSort(sort)));
        return Result.result(HttpStatus.OK.value(), "Lấy dữ liệu thành công!", pages);
    }

    @GetMapping("/reindex")
    public Result<?> reindexProduct() throws InterruptedException {
        productDetailService.indexData();
        return Result.success();
    }
}
