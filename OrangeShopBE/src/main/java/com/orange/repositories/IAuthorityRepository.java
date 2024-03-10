package com.orange.repositories;

import com.orange.domain.model.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IAuthorityRepository extends JpaRepository<Authority, Long> {

}