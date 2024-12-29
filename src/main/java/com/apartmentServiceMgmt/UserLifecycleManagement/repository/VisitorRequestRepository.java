package com.apartmentServiceMgmt.UserLifecycleManagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.apartmentServiceMgmt.UserLifecycleManagement.entity.VisitorRequestEntity;

public interface VisitorRequestRepository extends JpaRepository<VisitorRequestEntity, Long> {
}