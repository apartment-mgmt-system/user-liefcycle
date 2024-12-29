package com.apartmentServiceMgmt.UserLifecycleManagement.service.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.AuditorAware;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.apartmentServiceMgmt.UserLifecycleManagement.controllerAdvice.ResourceNotFoundException;
import com.apartmentServiceMgmt.UserLifecycleManagement.entity.UserEntity;
import com.apartmentServiceMgmt.UserLifecycleManagement.entity.VisitorRequestEntity;
import com.apartmentServiceMgmt.UserLifecycleManagement.enums.Status;
import com.apartmentServiceMgmt.UserLifecycleManagement.repository.UserRepository;
import com.apartmentServiceMgmt.UserLifecycleManagement.repository.VisitorRequestRepository;
import com.apartmentServiceMgmt.UserLifecycleManagement.service.VisitorService;
import com.apartmentServiceMgmt.model.VisitorRequestDto;

@Service
public class VisitorServiceImpl implements VisitorService {
	@Autowired
	private VisitorRequestRepository visitorRequestRepository;
	@Autowired
	private KafkaTemplate<String, Map<String, Object>> kafkaTemplate;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private AuditorAware<String> auditor;
	@Value("${application.base.url}")
	private String baseUrl;

	@Override
	public int requestVisitorAccess(VisitorRequestDto visitorRequestDto) {
		VisitorRequestEntity visitorRequestEntity = VisitorRequestEntity.builder()
				.visitorName(visitorRequestDto.getVisitorName()).requestedBy(visitorRequestDto.getRequestedBy())
				.tenantName(visitorRequestDto.getTenantName()).status(Status.INPROGRESS)
				.visitorEmail(visitorRequestDto.getEmail()).build();
		Map<String, Object> map = new HashMap<String, Object>();
		VisitorRequestEntity savedVisitor = visitorRequestRepository.save(visitorRequestEntity);
		UserEntity tenant = userRepository.findByUserName(visitorRequestDto.getTenantName())
				.orElseThrow(() -> new ResourceNotFoundException("Tenant Not available"));
		String approvalLink = baseUrl + "/admin/visitors/request/" + savedVisitor.getRequestId() + "/"
				+ Status.ACCEPTED.toString();
		String rejectionLink = baseUrl + "/admin/visitors/request/" + savedVisitor.getRequestId() + "/"
				+ Status.REJECTED.toString();

		StringBuilder emailBody = new StringBuilder();
		emailBody.append("Hi ").append(tenant.getName()).append(", ")
				.append("Please take action on the visitor access request for ")
				.append(visitorRequestEntity.getVisitorName()).append(" by accepting -> Link:").append(approvalLink)
				.append(" or rejecting -> Link:").append(rejectionLink).append(".");
		map.put("To", tenant.getEmail());
		map.put("Subject", "Asset Depreciation Alert");
		map.put("Body", emailBody.toString());
		kafkaTemplate.send("visitor-topic", map);
		return savedVisitor.getRequestId().intValue();
	}

	@Override
	public void approveRejectVisitorAccess(Long requestId, String status) {
		Optional<VisitorRequestEntity> visitorRequestOpt = visitorRequestRepository.findById(requestId);

		if (!visitorRequestOpt.isPresent()) {
			throw new ResourceNotFoundException("Visitor request not found");
		}

		VisitorRequestEntity visitorRequest = visitorRequestOpt.get();

		try {
			Status newStatus = Status.valueOf(status.toUpperCase());
			visitorRequest.setStatus(newStatus);
			visitorRequest.setApprovedBy(auditor.getCurrentAuditor().get());
			visitorRequestRepository.save(visitorRequest);
			
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("To", visitorRequest.getVisitorEmail());
			map.put("Subject", "Visitor Request Approved");
			map.put("Body", "Welcome!! Your request to access society has been approved.");
			kafkaTemplate.send("visitor-topic", map);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException("Invalid status provided");
		}
	}

}
