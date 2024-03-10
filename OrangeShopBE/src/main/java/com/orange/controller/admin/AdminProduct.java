package com.orange.controller.admin;

import com.orange.common.payload.Result;
import com.orange.domain.dto.ProductDTO;
import com.orange.domain.dto.ProductDetailDTO;
import com.orange.services.IProductDetailService;
import com.orange.services.IProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("admin/product")
@RequiredArgsConstructor
public class AdminProduct {
    private final IProductService productService;
    private final IProductDetailService productDetailService;

    @PostMapping("/create-product")
    public Result<?> createProduct(@RequestBody ProductDTO dto) {
        ProductDTO result = productService.create(dto);
        return Result.result(HttpStatus.OK.value(), "Thêm sản phẩm mới thành công!", result);
    }

    @PutMapping("/update-product")
    public Result<?> updateProduct(
            @RequestBody ProductDTO productDTO) {
        ProductDTO result = this.productService.update(productDTO);
        return Result.result(HttpStatus.OK.value(), "Cập nhập sản phẩm thành công!", result);
    }

    @PutMapping("/delete-product")
    public Result<?> deleteProduct(
            @RequestBody Long[] ids) {
        this.productService.delete(ids);
        return Result.result(HttpStatus.OK.value(), "Xóa sản phẩm thành công!", null);
    }

    @PutMapping("/delete-product-detail")
    public Result <?> deleteProductDetail(@RequestBody ProductDetailDTO dto) {
        ProductDetailDTO result = productDetailService.delete(dto);
        return Result.result(HttpStatus.OK.value(), "Xóa chi tiết sản phẩm thành công!", result);
    }
}
