package com.orange.repositories;

import com.orange.domain.model.GroupCustomer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IGroupCustomerRepository extends JpaRepository<GroupCustomer, Long> {
}