package com.freewayemi.merchant.commons.type;

public enum PaymentTypeEnum {
	
	DOWN_PAYMENT("downPayment");
	
	private String displayName; 
	
	PaymentTypeEnum(String displayName) {
		this.displayName = displayName;
	}

	public String getDisplayName() {
		return displayName;
	}

}
