package com.orange.controller.user;

import com.orange.utils.FilterUtils;
import com.orange.utils.SortUtils;
import com.orange.common.payload.Page;
import com.orange.common.payload.Result;
import com.orange.domain.dto.*;
import com.orange.exception.EntityIsEmptyException;
import com.orange.exception.EntityType;
import com.orange.exception.ExceptionType;
import com.orange.exception.GlobalException;
import com.orange.services.IProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.JpaSort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping("/product")
public class ProductController {
    private final IProductService productService;

    @GetMapping("")
    private Result<?> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<?> pages = (Page<?>) this.productService.fillAll(PageRequest.of(page, size));
        return Result.result(HttpStatus.OK.value(), "Lấy dữ liệu thành công!", pages);
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
        Page<?> pages = this.productService.search(categoryId, keyWord, FilterUtils.getFilterPrice(filterPrice), PageRequest.of(page, size, SortUtils.getSort(sort)));
        return Result.result(HttpStatus.OK.value(), "Lấy dữ liệu thành công!", pages);
    }

    @GetMapping("by-category")
    private Result<?> getProductsByCategory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam Optional<Long> cid) {
        if (cid.isPresent()) {
            Page<?> pages = this.productService.findProductsByCategory(PageRequest.of(page, size), cid.get());
            return Result.result(HttpStatus.OK.value(), "Lấy dữ liệu thành công!", pages);
        } else {
            throw GlobalException.throwException(EntityType.product, ExceptionType.ENTITY_NOT_FOUND, "category.error.notfound");
        }

    }
    @GetMapping("by-category-id")
    private Result<?> getProductsByCategoryId(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam Optional<Long> cid) {
        if (cid.isPresent()) {
            Page<?> pages = this.productService.getAllProductsByCategoryId(PageRequest.of(page, size), cid.get());
            return Result.result(HttpStatus.OK.value(), "Lấy dữ liệu thành công!", pages);
        } else {
            throw GlobalException.throwException(EntityType.product, ExceptionType.ENTITY_NOT_FOUND, "category.error.notfound");
        }

    }

    @GetMapping("/product-detail")
    public Result<?> getProductById(@RequestParam(value = "id", defaultValue = "0") Optional<Long> id) {
        if (!id.isPresent()) {
            throw GlobalException.throwException("address.user.id.notfound");
        }
        ProductDTO productDTO = this.productService.findById(id.get());
        return Result.result(HttpStatus.OK.value(), "Lấy chi tiết sản phẩm thành công!", productDTO);
    }

    @GetMapping("get-product-by-filter")
    public Result<?> getProductByFilter(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size,
            @RequestParam(required = false) Long idCategory,
            @RequestParam(required = false) Double fromPrice,
            @RequestParam(required = false) Double toPrice,
            @RequestParam(required = false) String namePr,
            @RequestParam(defaultValue = "true") Boolean isAsc,
            @RequestParam(required = false, defaultValue = "name") String sortBy
    ){
        if(idCategory == 0) idCategory = null;
        if (sortBy.equals("price")){
            String filterPrice = "productDetails.priceSale,"+fromPrice+":"+toPrice;
            String sortByFilter[] = {"productDetails."+sortBy+"Sale",(isAsc==true) ? "desc":"asc"};
            Page<?>  result = productService.search(idCategory,namePr,FilterUtils.getFilterPrice(filterPrice),PageRequest.of(page, size, SortUtils.getSort(sortByFilter)));
            return Result.result(HttpStatus.OK.value(), "Bộ Lọc Product Thành Công!",result);
        }
        String nameSort = sortBy.equals("date") ? "modifiedDate":"name";
        Sort sort = null;
        if (isAsc){ sort = JpaSort.unsafe(Sort.Direction.ASC, nameSort);}
        if (!isAsc){ sort = JpaSort.unsafe(Sort.Direction.DESC, nameSort);}
        if(namePr.isEmpty()) namePr = null;
        Page<?> result = productService.getProductByFillter(idCategory,fromPrice,toPrice,namePr,PageRequest.of(page,size,sort));
        return Result.result(HttpStatus.OK.value(), "Bộ Lọc Product Thành Công!",result);
    }

    @GetMapping("/reindex")
    public Result<?> reindexProduct() throws InterruptedException {
        productService.indexData();
        return Result.success();
    }
}
