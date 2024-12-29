package com.apartmentServiceMgmt.UserLifecycleManagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.apartmentServiceMgmt.UserLifecycleManagement.service.VisitorService;
import com.apartmentServiceMgmt.api.VisitorApprovalApi;
import com.apartmentServiceMgmt.model.VisitorRequestDto;

import jakarta.validation.Valid;

@RestController
public class VisitorController implements VisitorApprovalApi {
	@Autowired
	VisitorService visitorRequestService;

	@Override
	public ResponseEntity<String> requestVisitorAccess(@Valid VisitorRequestDto visitorRequestDto) {

		int requestSubmitted = visitorRequestService.requestVisitorAccess(visitorRequestDto);
		return ResponseEntity.ok(String.format(
				"Visitor access request submitted successfully. Keep RequestId: %s for reference.", requestSubmitted));
	}

	@PatchMapping("/admin/visitors/request/{requestId}/{status}")
	public ResponseEntity<String> updateStatus(@PathVariable("requestId") Long requestId,
			@PathVariable("status") String status) {

		visitorRequestService.approveRejectVisitorAccess(requestId, status);
		return ResponseEntity.ok("Status updated successfully");
	}
}
