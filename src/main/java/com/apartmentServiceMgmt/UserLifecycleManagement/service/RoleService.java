package com.apartmentServiceMgmt.UserLifecycleManagement.service;

import java.util.List;

import com.apartmentServiceMgmt.model.RoleDto;
import com.apartmentServiceMgmt.model.RoleResponse;

public interface RoleService {
    RoleResponse createRole(RoleDto roleDto);
    RoleResponse updateRole(String code, RoleDto roleDto);
    RoleResponse getRoleByCode(String code);
    List<RoleResponse> getAllRoles();
}