package com.apartmentServiceMgmt.UserLifecycleManagement.enums;

import lombok.Getter;

@Getter
public enum Status {
	ACCEPTED("accepted"), REJECTED("rejected"), INPROGRESS("inprogress");

	private String status;

	Status(String status) {
		this.status = status;
	}
	
	@Override
	public String toString() {
		return status;
	}
}
