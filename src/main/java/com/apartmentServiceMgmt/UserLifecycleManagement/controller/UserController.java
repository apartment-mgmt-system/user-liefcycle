package com.apartmentServiceMgmt.UserLifecycleManagement.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.apartmentServiceMgmt.UserLifecycleManagement.service.UserService;
import com.apartmentServiceMgmt.api.UsersApi;
import com.apartmentServiceMgmt.model.UserDto;
import com.apartmentServiceMgmt.model.UserResponse;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;

@RestController
@Secured("ROLE_ADMIN")
public class UserController implements UsersApi{

	@Autowired
	private UserService userService;

	@Override
	public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserDto userDto) {
		UserResponse userResponse = userService.createUser(userDto);
		return ResponseEntity.status(201).body(userResponse);
	}

	@Override
	@Secured("ROLE_ADMIN")
	public ResponseEntity<List<UserResponse>> getAllUsers() {
		List<UserResponse> users = userService.getAllUsers();
		return ResponseEntity.ok(users);
	}
	
	@Override
	public ResponseEntity<UserResponse> getUserByuserName(@Size(min = 1, max = 50) String userName) {
		UserResponse userResponse = userService.getUserByUserName(userName);
		return ResponseEntity.ok(userResponse);
	}

	@Override
	public ResponseEntity<UserResponse> updateUser(@PathVariable String userName, @Valid @RequestBody UserDto userDto) {
		UserResponse userResponse = userService.updateUser(userName, userDto);
		return ResponseEntity.ok(userResponse);
	}

	@Override
	public ResponseEntity<String> deleteUser(@PathVariable String userName) {
		String deletedBy = userService.deleteUser(userName);
		return ResponseEntity.ok(String.format("Successfully Deleted By: %s", deletedBy));
	}
}
