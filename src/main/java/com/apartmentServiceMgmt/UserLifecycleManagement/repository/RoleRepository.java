package com.apartmentServiceMgmt.UserLifecycleManagement.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.apartmentServiceMgmt.UserLifecycleManagement.entity.RoleEntity;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
	Optional<RoleEntity> findByRoleCode(String roleCode);
}
