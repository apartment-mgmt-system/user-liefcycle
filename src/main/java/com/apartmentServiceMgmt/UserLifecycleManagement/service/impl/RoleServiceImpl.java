package com.apartmentServiceMgmt.UserLifecycleManagement.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.apartmentServiceMgmt.UserLifecycleManagement.controllerAdvice.ResourceNotFoundException;
import com.apartmentServiceMgmt.UserLifecycleManagement.entity.RoleEntity;
import com.apartmentServiceMgmt.UserLifecycleManagement.repository.RoleRepository;
import com.apartmentServiceMgmt.UserLifecycleManagement.service.MonitorPerformance;
import com.apartmentServiceMgmt.UserLifecycleManagement.service.RoleService;
import com.apartmentServiceMgmt.model.RoleDto;
import com.apartmentServiceMgmt.model.RoleResponse;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    @MonitorPerformance
    public RoleResponse createRole(RoleDto roleDto) {
        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setRoleCode(roleDto.getRoleCode());
        roleEntity.setRoleName(roleDto.getRoleName());
        RoleEntity savedEntity = roleRepository.save(roleEntity);
        
        return mapToRoleResponse(savedEntity);
    }

    @Override
    public RoleResponse updateRole(String code, RoleDto roleDto) {
        RoleEntity roleEntity = roleRepository.findByRoleCode(code)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Role %s Not Found",code)));
        roleEntity.setRoleCode(roleDto.getRoleCode());
        roleEntity.setRoleName(roleDto.getRoleName());
        RoleEntity updatedEntity = roleRepository.save(roleEntity);
        
        return mapToRoleResponse(updatedEntity);
    }

    @Override
    public RoleResponse getRoleByCode(String code) {
        RoleEntity roleEntity = roleRepository.findByRoleCode(code)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Role %s Not Found",code)));
        return mapToRoleResponse(roleEntity);
    }

    @Override
    public List<RoleResponse> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(this::mapToRoleResponse)
                .collect(Collectors.toList());
    }
    private RoleResponse mapToRoleResponse(RoleEntity roleEntity) {
        RoleResponse roleResponse = new RoleResponse();
        roleResponse.setRoleId(String.valueOf(roleEntity.getRoleId()));
        roleResponse.setRoleCode(roleEntity.getRoleCode());
        roleResponse.setRoleName(roleEntity.getRoleName());
        return roleResponse;
    }
}
