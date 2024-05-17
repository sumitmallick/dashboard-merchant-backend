package com.freewayemi.merchant.commons.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "auth_users")
@Data
@EqualsAndHashCode(callSuper = true)
public class AuthUser extends BaseEntity {
	private String userType;
	private String userId;
	private String otp;
	private String password;
	private long expiry;
}
