package com.orange.repositories;

import com.orange.domain.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface IProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByIdAndStatusTrueAndProductDetails_StatusTrue(Long id);
    Product findByIdAndStatusTrue(Long id);
    Page<Product> findByCategoryIdAndStatusIsTrue(Long cid, Pageable pageable);

    @Query(value = "SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.productDetails pd WHERE p.category.id = :categoryId OR p.category.id IN :categoryIds",
            countQuery = "SELECT COUNT(DISTINCT p) FROM Product p WHERE p.status = true and (p.category.id = :categoryId OR p.category.id IN :categoryIds)")
    Page<Product> findByCategoryIdOrCategoryIds(@Param("categoryId") Long categoryId, @Param("categoryIds") Collection<Long> categoryIds, Pageable pageable);

    Page<Product> findByStatusIsTrueAndCategory_IdOrCategory_IdIn(Long categoryId, Collection<Long> subCategoryIds, Pageable pageable);
    Page<Product> findAllByStatusIsTrueAndCategory_Id(Long cid, Pageable pageable);
    Page<Product> findAllByStatusIsTrue(Pageable pageable);
    List<Product> findByCategoryId(Long id);
    @Query(value = "SELECT * FROM product o where o.category_id = :idCategory and o.status = :status order by o.id limit 1",nativeQuery = true)
    Product find1ProductByCategory(Long idCategory, Boolean status);
    @Query(value = "select o from Product o join o.productDetails pd where (:idCategory is null or o.category.id =:idCategory) and (:fromPrice IS NULL OR :toPrice IS NULL OR pd.priceSale BETWEEN :fromPrice AND :toPrice) and ((:namePr IS NULL OR :namePr = '') OR LOWER(o.name) LIKE LOWER(CONCAT('%', :namePr, '%'))) and o.status = true group by o.id",nativeQuery = false )
    Page<Product> fillterProduct(Long idCategory, Double fromPrice, Double toPrice,String namePr, Pageable pageable);

    @Modifying
    @Query(value = "update Product set status = 0 where id = :id", nativeQuery = false)
    void deleteProduct(Long id);
}