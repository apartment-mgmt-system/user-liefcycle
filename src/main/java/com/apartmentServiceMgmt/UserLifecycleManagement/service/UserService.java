package com.apartmentServiceMgmt.UserLifecycleManagement.service;

import com.apartmentServiceMgmt.model.SucessfulLoginResponse;
import com.apartmentServiceMgmt.model.UserDto;
import com.apartmentServiceMgmt.model.UserLoginRequest;
import com.apartmentServiceMgmt.model.UserResponse;

import java.util.List;

public interface UserService {
	UserResponse createUser(UserDto userDto);

	UserResponse getUserByUserName(String userName);

	List<UserResponse> getAllUsers();

	UserResponse updateUser(String userName, UserDto userDto);

	String deleteUser(String userName);

	SucessfulLoginResponse authenticateUser(UserLoginRequest userLoginRequest);
}
