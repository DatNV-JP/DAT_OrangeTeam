package com.orange.repositories;

import com.orange.domain.model.VariationOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IVariationOptionRepository extends JpaRepository<VariationOption, Long> {
    List<VariationOption> findAllByProductDetailsId(Long id);
}