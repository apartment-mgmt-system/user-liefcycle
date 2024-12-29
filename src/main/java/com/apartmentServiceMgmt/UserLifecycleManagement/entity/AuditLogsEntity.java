package com.apartmentServiceMgmt.UserLifecycleManagement.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity(name="AUDIT_LOGS")
public class AuditLogsEntity {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long auditId;
	private String actionType;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
	private UserEntity userId;
	private String details;
	
    @CreatedDate
    private LocalDateTime createdDate;
}
