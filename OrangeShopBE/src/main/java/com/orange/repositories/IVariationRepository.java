package com.orange.repositories;

import com.orange.domain.model.Variation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IVariationRepository extends JpaRepository<Variation, Long> {

    List<Variation> findAllByCategory_IdAndStatusTrue(Long categoryId);
}