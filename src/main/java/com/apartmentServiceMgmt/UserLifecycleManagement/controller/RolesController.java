package com.apartmentServiceMgmt.UserLifecycleManagement.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RestController;

import com.apartmentServiceMgmt.UserLifecycleManagement.service.RoleService;
import com.apartmentServiceMgmt.api.RolesApi;
import com.apartmentServiceMgmt.model.RoleDto;
import com.apartmentServiceMgmt.model.RoleResponse;

import jakarta.validation.Valid;

@RestController
@Secured("ROLE_ADMIN")
public class RolesController implements RolesApi {

	@Autowired
	private RoleService roleService;

	@Override
	public ResponseEntity<RoleResponse> createRole(@Valid RoleDto roleDto) {
		RoleResponse roleResponse = roleService.createRole(roleDto);
		return ResponseEntity.status(201).body(roleResponse);
	}

	@Override
	public ResponseEntity<RoleResponse> updateRole(String code, RoleDto roleDto) {
		RoleResponse roleResponse = roleService.updateRole(code, roleDto);
		return ResponseEntity.ok(roleResponse);
	}

	@Override
	public ResponseEntity<List<RoleResponse>> getAllRoles() {
		List<RoleResponse> roles = roleService.getAllRoles();
		return ResponseEntity.ok(roles);
	}

	@Override
	public ResponseEntity<RoleResponse> getRoleByCode(String code) {
		RoleResponse roleResponse = roleService.getRoleByCode(code);
		return ResponseEntity.ok(roleResponse);
	}
}
