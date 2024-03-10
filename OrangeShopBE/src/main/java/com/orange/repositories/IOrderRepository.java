package com.orange.repositories;

import com.orange.domain.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

public interface IOrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByShippingCodeLike(String shippingCode);
    Optional<Order> findByUser_IdAndId(Long id, Long id1);

    @Query("from Order o where o.user.id =:uId")
    Page<Order> findAllForUser(@Param("uId") Long uId, Pageable pageable);

    @Query("from Order o where o.orderStatus.id =:statusId")
    Page<Order> findByOrderStatus_Id(@Param("statusId")Long statusId, Pageable pageable);

    @Query("from Order o where o.orderStatus.id =:statusId and o.user.id =:userId")
    Page<Order> findByOrderStatus_IdAndUser_Id(@Param("statusId")Long statusId, @Param("userId")Long userId, Pageable pageable);

    @Query(value = "select o from Order o where (o.id =:id or :id is null) " +
            "and (o.orderStatus.id =:statusId or :statusId is null) " +
            "and (o.user.id =:uId or :uId is null) " +
            "and (DATE(o.createDate) = DATE(:createDate) or :createDate is null) " +
            "and (((:keyWord IS NULL OR :keyWord = '') OR LOWER(o.shippingCode) LIKE LOWER(CONCAT('%', :keyWord, '%')))" +
            "or ((:keyWord IS NULL OR :keyWord = '') OR LOWER(o.consigneeName) LIKE LOWER(CONCAT('%', :keyWord, '%'))))",nativeQuery = false)
    Page<Order> findfilter(@Param("uId")Long uId, @Param("id")Long id, @Param("keyWord")String keyWord, @Param("statusId")Long statusId, @Param("createDate") LocalDate createDate, Pageable pageable);
}