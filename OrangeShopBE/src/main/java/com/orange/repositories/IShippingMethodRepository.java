package com.orange.repositories;

import com.orange.domain.model.ShippingMethod;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IShippingMethodRepository extends JpaRepository<ShippingMethod, Long> {
    List<ShippingMethod> findAllByStatusTrue();
}