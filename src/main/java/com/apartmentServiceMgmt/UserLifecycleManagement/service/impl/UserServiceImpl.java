package com.apartmentServiceMgmt.UserLifecycleManagement.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.apartmentServiceMgmt.UserLifecycleManagement.controllerAdvice.UnauthenticatedAccessException;
import com.apartmentServiceMgmt.UserLifecycleManagement.entity.RoleEntity;
import com.apartmentServiceMgmt.UserLifecycleManagement.entity.UserEntity;
import com.apartmentServiceMgmt.UserLifecycleManagement.repository.RoleRepository;
import com.apartmentServiceMgmt.UserLifecycleManagement.repository.UserRepository;
import com.apartmentServiceMgmt.UserLifecycleManagement.security.JWTUtil;
import com.apartmentServiceMgmt.UserLifecycleManagement.service.MonitorPerformance;
import com.apartmentServiceMgmt.UserLifecycleManagement.service.UserService;
import com.apartmentServiceMgmt.model.SucessfulLoginResponse;
import com.apartmentServiceMgmt.model.UserDto;
import com.apartmentServiceMgmt.model.UserLoginRequest;
import com.apartmentServiceMgmt.model.UserResponse;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	private JWTUtil jwtUtil;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Override
	@MonitorPerformance
	public UserResponse createUser(UserDto userDto) {
		log.info("Creating User");
		if (userRepository.findByUserName(userDto.getUserName()).isPresent()) {
			throw new IllegalArgumentException(String.format("User %s already exist.", userDto.getUserName()));
		}
		List<RoleEntity> roles = userDto.getRoles().stream()
				.map(roleCode -> roleRepository.findByRoleCode(roleCode)
						.orElseThrow(() -> new IllegalArgumentException(String.format("Role %s not found.", roleCode))))
				.collect(Collectors.toList());

		UserEntity userEntity = UserEntity.builder().name(userDto.getName()).email(userDto.getEmail())
				.userName(userDto.getUserName()).encodedPwd(bCryptPasswordEncoder.encode(userDto.getUserName()))
				.apartmentDetails(userDto.getApartmentDetails()).roles(roles).build();

		UserEntity savedUser = userRepository.save(userEntity);
		return mapToUserResponse(savedUser);
	}

	@Override
	public UserResponse getUserByUserName(String userName) {
		log.info("Getting user");
		UserEntity userEntity = userRepository.findByUserName(userName)
				.orElseThrow(() -> new IllegalArgumentException(String.format("User %s not found.", userName)));
		return mapToUserResponse(userEntity);
	}

	@Override
	public List<UserResponse> getAllUsers() {
		return userRepository.findAll().stream().map(this::mapToUserResponse).collect(Collectors.toList());
	}

	@Override
	public UserResponse updateUser(String userName, UserDto userDto) {
		UserEntity userEntity = userRepository.findByUserName(userName)
				.orElseThrow(() -> new IllegalArgumentException(String.format("User %s not found.", userName)));

		List<RoleEntity> roles = userDto.getRoles().stream()
				.map(roleCode -> roleRepository.findByRoleCode(roleCode)
						.orElseThrow(() -> new IllegalArgumentException(String.format("Role %s Not Found", roleCode))))
				.collect(Collectors.toList());

		userEntity.setName(userDto.getName());
		userEntity.setEmail(userDto.getEmail());
		userEntity.setUserName(userDto.getUserName());
		userEntity.setApartmentDetails(userDto.getApartmentDetails());
		userEntity.setRoles(roles);

		UserEntity updatedUser = userRepository.save(userEntity);
		return mapToUserResponse(updatedUser);
	}

	@Override
	public String deleteUser(String userName) {
		UserEntity userEntity = userRepository.findByUserName(userName)
				.orElseThrow(() -> new IllegalArgumentException(String.format("User %s not found.", userName)));
		userEntity.setDeletedDate(LocalDateTime.now());
		userEntity.setDeletedBy("System");
		userEntity.getRoles().forEach(role -> {
			role.setDeletedDate(LocalDateTime.now());
			role.setDeletedBy("System");
			});
		return userRepository.save(userEntity).getModifiedBy();
	}

	private UserResponse mapToUserResponse(UserEntity userEntity) {
		UserResponse userResponse = new UserResponse();
		userResponse.setUserId(userEntity.getUserId().intValue());
		userResponse.setUserName(userEntity.getUserName());
		userResponse.setName(userEntity.getName());
		userResponse.setEmail(userEntity.getEmail());
		userResponse.setApartmentDetails(userEntity.getApartmentDetails());
		userResponse.setRoles(userEntity.getRoles().stream().map(RoleEntity::getRoleCode).collect(Collectors.toList()));
		return userResponse;
	}

	@Override
	public SucessfulLoginResponse authenticateUser(UserLoginRequest userLoginRequest) {
		log.info("Authenticating user:"+ userLoginRequest);
		SucessfulLoginResponse loginResponse = new SucessfulLoginResponse();
		UserEntity userEntity = userRepository.findByUserName(userLoginRequest.getUsername())
				.orElseThrow(() -> new UnauthenticatedAccessException(String.format("User %s do not exist.", userLoginRequest.getUsername())));
		boolean validatePwd = bCryptPasswordEncoder.matches(userLoginRequest.getPassword(), userEntity.getEncodedPwd());
		if(validatePwd) {
			String token = jwtUtil.generateToken(userEntity);
			loginResponse.setUserName(userLoginRequest.getUsername());
			loginResponse.setToken(token);
			loginResponse.setRoles(userEntity.getRoles().stream().map(RoleEntity::getRoleCode).collect(Collectors.toList()));
		}
		else {
			throw new UnauthenticatedAccessException(String.format("User's password is invalid", userLoginRequest.getUsername()));
		}
		return loginResponse;
	}
}
