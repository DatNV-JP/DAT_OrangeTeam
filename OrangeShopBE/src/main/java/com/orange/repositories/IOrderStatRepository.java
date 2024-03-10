package com.orange.repositories;

import com.orange.domain.model.OrderStat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;

public interface IOrderStatRepository extends JpaRepository<OrderStat, Long> {
    Optional<OrderStat> findByUser_Id(Long userId);

    @Query("SELECT os FROM OrderStat os INNER JOIN os.user u WHERE os.totalAmount >= :totalAmount AND u.activate = true")
    Page<OrderStat> findAllByTotalAmountLessThanEqual(@Param("totalAmount") BigDecimal totalAmount, Pageable pageable);

    @Query("SELECT os FROM OrderStat os INNER JOIN os.user u WHERE os.totalOrders = 0 AND u.activate = true")
    Page<OrderStat> findUserNotOrderYet(Pageable pageable);
}