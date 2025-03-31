package com.infinity.commerce.auth_service.repository;

import com.infinity.commerce.auth_service.entity.UserRole;
import com.infinity.commerce.auth_service.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRoleRepository extends JpaRepository<UserRole,Long> {
    // select * from userrole where userid=id;
    List<UserRole> findByUserId(Long id);

}
