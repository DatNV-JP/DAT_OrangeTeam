package com.orange.repositories;

import com.orange.domain.model.Voucher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface IVoucherRepository extends JpaRepository<Voucher, Long> {

    @Query("SELECT v FROM Voucher v " +
            "LEFT JOIN v.users u ON u.id=:uId " +
            "WHERE (v.isGlobal=true OR u.id IS NOT NULL)" +
            "and (v.usageLimit > v.used)" +
            "and v.status=1 " +
            "and :currentTime " +
            "between v.startDate and v.endDate")
    Page<Voucher> findVoucherForUser(@Param("uId") Long uId, @Param("currentTime") Instant currentTime, Pageable pageable);

    @Query("SELECT v FROM Voucher v " +
            "LEFT JOIN v.users u ON u.id=:uId " +
            "WHERE (v.isGlobal=true OR u.id IS NOT NULL)" +
            "and (v.usageLimit > v.used)" +
            "and v.status=1 " +
            "and :currentTime " +
            "between v.startDate and v.endDate")
    List<Voucher> findVoucherForUser(@Param("uId") Long uId, @Param("currentTime") Instant currentTime);

    @Query("SELECT v FROM Voucher v " +
            "WHERE v.isGlobal=true " +
            "and v.status=1" +
            "and (v.usageLimit > v.used)" +
            "and :currentTime " +
            "between v.startDate and v.endDate")
    Page<Voucher> findOngoingGlobalVoucher(@Param("currentTime") Instant currentTime, Pageable pageable);

    @Query("SELECT v FROM Voucher v " +
            "WHERE v.isGlobal=true " +
            "and v.status=1" +
            "and (v.usageLimit > v.used)" +
            "and :currentTime " +
            "between v.startDate and v.endDate")
    List<Voucher> findOngoingGlobalVoucher(@Param("currentTime") Instant currentTime);

    @Query("FROM Voucher v where v.status=1")
    Page<Voucher> findVoucherForAdmin(Pageable pageable);
    @Query("SELECT v FROM Voucher v " +
            "WHERE v.status=1" +
            "and v.startDate > :currentTime")
    Page<Voucher> findUpcomingVoucherForAdmin(@Param("currentTime") Instant currentTime, Pageable pageable);

    @Query("SELECT v FROM Voucher v " +
            "WHERE v.status=1" +
            "and :currentTime " +
            "between v.startDate and v.endDate")
    Page<Voucher> findOngoingVoucherForAdmin(@Param("currentTime") Instant currentTime, Pageable pageable);

    @Query("SELECT v FROM Voucher v " +
            "WHERE v.status=1" +
            "and v.endDate < :currentTime")
    Page<Voucher> findExpiredVoucherForAdmin(@Param("currentTime") Instant currentTime, Pageable pageable);
    Optional<Voucher> findByCode(String code);
}