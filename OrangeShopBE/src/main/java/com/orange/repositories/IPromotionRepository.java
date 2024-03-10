package com.orange.repositories;

import com.orange.domain.model.Promotion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Set;

public interface IPromotionRepository extends JpaRepository<Promotion, Long> {
    Page<Promotion> findAllByStatusIsTrue(Pageable pageable);

    /**
     * Khởi chạy chương trình khuyến mại
     * @param date
     * @return
     */
    @Query(value = "SELECT * FROM promotion WHERE status = true and is_date = false AND end_date >= ?1 and start_date <= ?1 Order by start_date asc", nativeQuery = true)
    List<Promotion> findAllByHoursAsc(Date date);

    @Query(value = "SELECT * FROM promotion\n" +
            "where status = true and is_date = true and date(end_date) >= date(?1) and date(start_date) <= date(?1) \n" +
            "Order by start_date asc;", nativeQuery = true)
    List<Promotion> findAllByDate(Date endDate);
    @Transactional
    @Modifying
    @Query(value = "DELETE FROM product_promotion where product_id=?1 and promotion_id=?2", nativeQuery = true)
    void deleteProductPromotionByProductId(Long productId, Long promotionId);

    @Query(value = "SELECT COUNT(*) FROM product_detail_promotion pdp INNER JOIN datn_spring2023.promotion p ON pdp.promotion_id = p.id \n" +
            "WHERE p.status = true \n" +
            "AND p.is_date = ?3 \n" +
            "AND pdp.product_detail_id = ?1 \n" +
            "AND pdp.promotion_id != ?2 \n" +
            "AND ((?4 >= p.start_date AND ?4 < p.end_date) \n" +
            "OR (?5 > p.start_date AND ?5 <= p.end_date) \n" +
            "OR (?4 <= p.start_date AND ?5 >= p.end_date));", nativeQuery = true)
    int getCountPromotionByPromotionIdAndProductDetailId(Long productId, Long promotionId, Boolean isDate, Date startDate, Date endDate);

    /**
     * Kiểm tra tổng số khuyến mại vào thời gian đã cho
     * @param productDetailId
     * @param startDate
     * @param endDate
     * @param isDate
     * @return
     */

    @Query(value = "SELECT COUNT(*) FROM product_detail_promotion pdp INNER JOIN promotion p ON pdp.promotion_id = p.id \n" +
            "WHERE p.status = true AND p.is_date= ?2 \n" +
            "AND pdp.product_detail_id = ?1 \n" +
            "AND ((?3 >= p.start_date AND ?3 < p.end_date) \n" +
            "OR (?4 > p.start_date AND ?4 <= p.end_date) \n" +
            "OR (?3 <= p.start_date AND ?4 >= p.end_date));", nativeQuery = true)
    int countPromotionProductDetail(Long productDetailId, Boolean isDate, Date startDate, Date endDate);

    /**
     * Kiểm tra trong thời gian start và end có khuyến mại nào cho cùng 1 sản phẩm chưa
     * @param productDetailId
     * @param startDate
     * @param endDate
     * @return
     */
    @Query(value = "SELECT * FROM promotion p \n" +
            "WHERE p.status = true AND p.is_date = ?2 \n" +
            "AND p.id IN (SELECT promotion_id FROM product_detail_promotion WHERE product_detail_id = ?1) \n" +
            "AND ((?3 BETWEEN p.start_date AND p.end_date) \n" +
            "OR (?4 BETWEEN p.start_date AND p.end_date)\n" +
            "OR (?3 < p.start_date AND ?4 > p.end_date));", nativeQuery = true)
    Set<Promotion> promotionAlreadyExistList(Long productDetailId, Boolean isDate, Date startDate, Date endDate);

    /**
     * Lấy product ID để delete khuyến mại -> tạo khuyến mại mới
     * @param productId
     * @param startDate
     * @param endDate
     * @param isDate
     * @return
     */
    @Query(value = "select pp.product_id FROM product_promotion pp inner join datn_spring2023.promotion p  \n" +
            "on pp.promotion_id = p.id where p.status = true  and p.is_date=?4 \n" +
            "and pp.product_id = ?1 \n" +
            "and ((?2 between p.start_date and p.end_date) \n" +
            "OR (?3 between p.start_date and p.end_date) \n" +
            "OR (?2 < p.start_date and ?3 < p.end_date))", nativeQuery = true)
    Set<Long> getProductIdPromotion(Long productId, Date startDate, Date endDate, Boolean isDate);

    /**
     * Tìm khuyến mại đã hết hạn theo giờ
     * status = true còn hoạt động
     * is_date = false : khuyến mại theo giờ
     * @param date
     * @return
     */
    @Query(value = "SELECT * FROM promotion where status = true and is_date = false and end_date < ?1 and start_date < ?1", nativeQuery = true)
    List<Promotion> findAllByStatusEqualsAndEndDateLessThan(Date date);

    /**
     * Tìm khuyến mại đã hết hạn theo ngày
     * status = true còn hoạt động
     * is_date = true : khuyến mại theo ngày
     * @param date
     * @return
     */
    @Query(value = "SELECT * FROM promotion WHERE status = true and is_date = true and date(end_date) < date(?1) and date(start_date) < date(?1)", nativeQuery = true)
    List<Promotion> findAllPromotionEndDateLessThan(Date date);

    /**
     * Update khuyến mại hết hạn
     * @param promotionId
     */
    @Modifying
    @Query("UPDATE Promotion SET status = 0 WHERE id = :promotionId")
    void updatePromotionExpired(@Param("promotionId") Long promotionId);

    /**
     * Get all khuyến mại status = ?
     * @return
     */
    @Query(value = "SELECT * FROM promotion where status = false and start_date < ?1 and end_date > ?1", nativeQuery = true)
    List<Promotion> findAllByInActice(Date startDate);

}
