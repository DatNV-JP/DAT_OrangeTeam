package com.orange.repositories;

import com.orange.domain.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IAddressRepository extends JpaRepository<Address, Long> {
    Optional<Address> findByAddressLine1AndAddressLine2AndVillageId(String addressLine1, String addressLine2, String villageId);
    @Query("select o from Address o where o.addressLine1 = :addressLine1 and o.village.id = :villageId")
    Optional<Address> find_by_address (String addressLine1, String villageId);

    @Query("FROM Address a JOIN a.userAddresses ua WHERE ua.user.id = :id and ua.status = true ")
    List<Address> findAddressByUser(@Param("id")Long id);
}