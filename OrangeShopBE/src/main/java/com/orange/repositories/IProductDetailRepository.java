package com.orange.repositories;

import com.orange.domain.model.Product;
import com.orange.domain.model.Promotion;
import org.springframework.data.domain.Page;
import com.orange.domain.model.ProductDetail;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;


public interface IProductDetailRepository extends JpaRepository<ProductDetail, Long> {
    @Query(value = "select o from ProductDetail o where o.priceSale between :fromPrice and :toPrice",nativeQuery = false)
    Page<ProductDetail> findByPriceRange(Double fromPrice, Double toPrice, Pageable pageable);
    @Query("SELECT pd FROM ProductDetail  pd WHERE pd.product.status = true and pd.product.id=?1")
    Page<ProductDetail> findByProductIdAndStatusIsTrue(Long cid, Pageable pageable);

    /**
     * Hàm lấy danh sách id chi tiết sản phẩm theo danh mục được sale
     * @param categoryId
     * @return
     */
    @Query("SELECT pb.id FROM Product p JOIN ProductDetail pb ON p.id = pb.product.id JOIN Category c on p.category.id = c.id WHERE (p.category.id =: categoryId OR c.parentCategory.id = :categoryId) AND pb.priceSale IS NOT NULL ")
    List<Long> findAllProductSaleOfByCategory(@Param("categoryId") Long categoryId);

    /**
     * Update giá sản phẩm về gía mặc định khi hết khuyến mại
     * @param productDetailId
     */
//    @Query("SELECT pd.id FROM Product p JOIN ProductDetail pd ON p.id = pd.product.id WHERE p.id = :productId AND pd.priceSale > 0 ")
//    List<Long> findAllProductSaleOfByProduct(@Param("productId") Long productId);

    /**
     * Hàm lấy danh sách sản phẩm ít người mua(bán ế)
     * @param startDate
     * @param endDate
     * @param totalQuantity
     * @return
     */
    @Query(value = "SELECT * FROM datn_spring2023.product_detail pd \n" +
            "INNER JOIN product p on pd.product_id = p.id \n" +
            "INNER JOIN (\n" +
            "\tSELECT od.product_detail_id, od.order_id, SUM(od.quantity) AS total_quantity\n" +
            "    FROM datn_spring2023.order_detail od\n" +
            "    INNER JOIN datn_spring2023.order o ON od.order_id = o.id\n" +
            "    WHERE o.completed_time BETWEEN :startDate AND :endDate\n" +
            "    GROUP BY od.product_detail_id\n" +
            ") odc ON pd.id = odc.product_detail_id \n" +
            "WHERE odc.total_quantity < :totalQuantity", nativeQuery = true)
    Page<ProductDetail> findUnpopularProductDetails(@Param("startDate") LocalDateTime startDate,
                                                    @Param("endDate") LocalDateTime endDate,
                                                    @Param("totalQuantity") int totalQuantity,
                                                    Pageable pageable);
    @Modifying
    @Query("UPDATE ProductDetail SET priceSale= priceDefault WHERE id = :productDetailId")
    void stopSaleOffProduct(@Param("productDetailId") Long productDetailId);
    @Modifying
    @Query("UPDATE ProductDetail SET priceSale = :priceSale where id = :productDetailId")
    void startSaleOffProduct(@Param("priceSale") Double priceSale, @Param("productDetailId") Long id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE Product_Detail pd SET pd.price_sale= 0 WHERE pd.id in (SELECT pdp.product_detail_id FROM product_detail_promotion pdp where pdp.promotion_id =?1)", nativeQuery = true)
    void stopSaleOffByProduct(Long productDetailId);

    @Query(value = "select o from ProductDetail o where (o.product.category.id =:idCategory or :idCategory is null) and (:fromPrice IS NULL OR :toPrice IS NULL OR COALESCE(o.priceSale,o.priceDefault) BETWEEN :fromPrice AND :toPrice) And ((:namePr IS NULL OR :namePr = '') OR LOWER(o.product.name) LIKE LOWER(CONCAT('%', :namePr, '%')))",nativeQuery = false)
    Page<ProductDetail> fillterProduct(Long idCategory, Double fromPrice, Double toPrice,String namePr,Pageable pageable);
}