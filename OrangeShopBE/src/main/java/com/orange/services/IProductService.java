package com.orange.services;

import com.orange.utils.FillterPrice;
import com.orange.domain.dto.ProductDTO;
import com.orange.domain.model.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

public interface IProductService extends BaseService<ProductDTO, Long> {
    @Transactional
    void delete(Long[] ids);

    com.orange.common.payload.Page<?> getAllProductsByCategoryId(Pageable pageable, Long cid);

    com.orange.common.payload.Page<?> findProductsByCategory(Pageable pageable, Long cid);

    Product getOneProductByCategoryID(Long idCategory, Boolean status);

    com.orange.common.payload.Page<?> getProductByFillter(Long idCategory, Double fromPrice, Double toPrice, String namePr, Pageable pageable);

    com.orange.common.payload.Page<?> search(Long categoryId, String keyWord, FillterPrice filterPrice, Pageable pageable);

    void indexData() throws InterruptedException;
}
