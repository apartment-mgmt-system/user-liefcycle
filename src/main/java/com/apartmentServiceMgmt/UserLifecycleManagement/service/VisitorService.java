package com.apartmentServiceMgmt.UserLifecycleManagement.service;

import com.apartmentServiceMgmt.model.VisitorRequestDto;

public interface VisitorService {
	int requestVisitorAccess(VisitorRequestDto visitorRequestDto);
	void approveRejectVisitorAccess(Long requestId,String status);
}
