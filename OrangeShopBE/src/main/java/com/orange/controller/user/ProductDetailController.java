package com.orange.controller.user;

import com.orange.common.payload.Page;
import com.orange.common.payload.Result;
import com.orange.domain.dto.ProductDetailDTO;
import com.orange.domain.model.ProductDetail;
import com.orange.exception.EntityNotFoundException;
import com.orange.exception.EntityType;
import com.orange.exception.ExceptionType;
import com.orange.exception.GlobalException;
import com.orange.services.IProductDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("/test/product-detail")
@RequiredArgsConstructor
public class ProductDetailController {
    private final IProductDetailService productDetailService;



    @GetMapping("/find-by-id/{id}")
    private Result<?> findbyId(@PathVariable Long id){
        ProductDetailDTO productDetailDTO = productDetailService.findById(id);
        return Result.result(HttpStatus.OK.value(),"Lấy dữ liệu thành công",productDetailDTO);
    }

    @GetMapping("/find-by-price")
    private Result<?> findByPrice(
            @RequestParam(value = "fromPrice") Double fromPrice,
            @RequestParam(value = "toPrice") Double toPrice,
            @RequestParam(defaultValue = "5") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,asc") String[] sort
    ){
        Page<ProductDetailDTO> list = (Page<ProductDetailDTO>) productDetailService.findByPrice(fromPrice,toPrice,PageRequest.of(page, size, Sort.by(sort[0]).descending()));
        return Result.result(HttpStatus.OK.value(),"Lấy dữ liệu thành công",list);
    }
    @GetMapping("")
    private Result<?> getProductDetails(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Page<?> pages = productDetailService.fillAll(PageRequest.of(page, size));
        return Result.result(HttpStatus.OK.value(), "Lấy dữ liệu thành công!", pages);
    }

    @GetMapping("/by-product")
    private Result<?> getProductDetailsByProductIsTrue(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam Optional<Long> cid
    ) {
        if (cid.isPresent()) {
            Page<?> pages = productDetailService.findProductDetailsByProduct(PageRequest.of(page, size),cid.get());
            return Result.result(HttpStatus.OK.value(), "Lấy dữ liệu thành công!", pages);
        } else {
            throw GlobalException.throwException(EntityType.product, ExceptionType.ENTITY_NOT_FOUND, "product.error.notfound");
        }
    }

    @GetMapping("/product-detail")
    public Result<?> getProductDetailById(@RequestParam(value = "id", defaultValue = "0") Optional<Long> id) {
        if (!id.isPresent()) {
            throw GlobalException.throwException("address.user.id.notfound");
        }
        ProductDetailDTO productDetailDTO = productDetailService.findById(id.get());
        return Result.result(HttpStatus.OK.value(), "Lấy thông tin sản phẩm thành công!", productDetailDTO);
    }

    @PostMapping("/product-detail")
    public Result<?> create(@RequestBody ProductDetailDTO dto) {
        ProductDetailDTO productDetailDTO = productDetailService.create(dto);
        return Result.result(HttpStatus.OK.value(), "Thêm mới sản phẩm thành công!", productDetailDTO);
    }

    @GetMapping("get-product-detail-by-filter")
    public Result<?> getProductByFilter(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long idCategory,
            @RequestParam(required = false) Double fromPrice,
            @RequestParam(required = false) Double toPrice,
            @RequestParam(required = false) String namePr
    ){
        Sort sort = Sort.by(Sort.Direction.ASC, "priceSale");
        Page<?> result = productDetailService.getPageByFillter(idCategory,fromPrice,toPrice,namePr,PageRequest.of(page,size,sort));
        return Result.result(HttpStatus.OK.value(), "Bộ Lọc Thành Công!", result);
    }

}
