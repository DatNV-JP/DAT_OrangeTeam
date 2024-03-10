package com.orange.repositories;

import com.orange.domain.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ICategoryRepository extends JpaRepository<Category, Long> {
    Page<Category> findAllByStatusIsTrue(Pageable pageable);
    List<Category> findAllByStatusIsTrueAndParentCategoryIsNull();
    @Query(value = "SELECT * FROM category o where o.id_parent_category = :id and o.status = true order by o.id desc limit 1",nativeQuery = true)
    Category findChildById(Long id);
    @Query(value = "SELECT * FROM datn_spring2023.category WHERE id_parent_category = :id UNION SELECT c1.* FROM datn_spring2023.category c1 JOIN datn_spring2023.category c2 ON c2.id = c1.id_parent_category WHERE c2.id_parent_category = :id",nativeQuery = true)
    List<Category> getAllRelationsById(Long id);

}