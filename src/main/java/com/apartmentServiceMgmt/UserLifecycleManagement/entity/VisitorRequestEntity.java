package com.apartmentServiceMgmt.UserLifecycleManagement.entity;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.apartmentServiceMgmt.UserLifecycleManagement.enums.Status;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name="VISITOR_REQUESTS")
@EntityListeners(value = { AuditingEntityListener.class })
public class VisitorRequestEntity extends AuditableEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long requestId;
	private String visitorName;
	private String requestedBy;
	private String tenantName;
	@Enumerated(EnumType.STRING)
	private Status status;
	private String approvedBy;
	private String visitorEmail;
}
