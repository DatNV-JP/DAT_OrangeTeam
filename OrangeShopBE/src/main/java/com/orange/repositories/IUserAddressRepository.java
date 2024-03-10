package com.orange.repositories;

import com.orange.domain.model.Address;
import com.orange.domain.model.User;
import com.orange.domain.model.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IUserAddressRepository extends JpaRepository<UserAddress, Long> {
    List<UserAddress> findByUser_Id(Long id);

    List<UserAddress> findAllByStatusIsTrueAndUser_Id(Long id);

    List<UserAddress> findUserAddressesByUser_Id(Long userId);

    @Query("update UserAddress ua set ua.isDefault = false where ua.user.id = :userId")
    @Modifying
    Integer updateAllDefaultAddressToFalse(Long userId);

    @Query(value = "UPDATE user_address SET is_default = 0 WHERE user_id = :userId and id <> :u_aId",nativeQuery = true)
    @Modifying
    Integer updateAllDefault(Long userId, Long u_aId);


    Optional<UserAddress> findFirstByUser_IdAndAddress_Id(@Param("uid") Long uid, @Param("aid") Long aid);
}