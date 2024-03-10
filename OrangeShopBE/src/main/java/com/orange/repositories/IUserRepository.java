package com.orange.repositories;

import com.orange.common.payload.Page;
import com.orange.domain.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IUserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    @Query("FROM User u where u.activate = true " +
            "and (u.username like CONCAT('%', :keyWord, '%') " +
            "or u.firstName like CONCAT('%', :keyWord, '%')" +
            "or u.lastName like CONCAT('%', :keyWord, '%'))")
    List<User> findAllByUsernamOrName(@Param("keyWord") String keyWord);
    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);
    @Query("select count(o.id) > 0 from User o where o.email = :email and o.username <> :username")
    Boolean existsByEmailNotUseUsernameLogin(String email, String username);

    @Query(value = "select o from User o where o.activate = :status",nativeQuery = false)
    org.springframework.data.domain.Page<User> findAllByUserActive(Boolean status, Pageable pageable);
}
