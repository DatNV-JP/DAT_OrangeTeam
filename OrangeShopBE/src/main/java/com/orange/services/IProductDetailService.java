package com.orange.services;

import com.orange.common.payload.Page;
import com.orange.domain.dto.ProductDetailDTO;
import com.orange.utils.FillterPrice;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface IProductDetailService extends BaseService<ProductDetailDTO,Long>{

    Page<?> findByPrice(Double fromPrice, Double toPrice, Pageable pageable);

    Page<?> findProductDetailsByProduct(Pageable pageable, Long cid);

    Page<?> getPageByFillter(Long idCategory, Double fromPrice, Double toPrice, String namePr, Pageable pageable);

    Page<?> findUnpopularProductDetails(LocalDateTime startDate, LocalDateTime endDate, Integer totalQuantity, Pageable pageable);

    Page<?> findForPromotion(Long idCategory, Double fromPrice, Double toPrice, String namePr, Pageable pageable);

    Page<?> search(Long categoryId, String keyWord, FillterPrice filterPrice, Pageable pageable);

    void indexData() throws InterruptedException;
}
