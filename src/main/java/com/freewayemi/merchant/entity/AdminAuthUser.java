package com.freewayemi.merchant.entity;

import com.freewayemi.merchant.commons.dto.Address;
import com.freewayemi.merchant.commons.dto.DocumentInfo;
import com.freewayemi.merchant.commons.entity.BaseEntity;
import com.freewayemi.merchant.dto.PasswordPolicy;
import com.freewayemi.merchant.dto.sales.Target;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Document(collection = "admin_auth_users")
@Data
@EqualsAndHashCode(callSuper = true)
public class AdminAuthUser extends BaseEntity {
    private String merchantId;
    private String login;
    private String role;
    private List<String> permissions;
    private String status;
    private String password;
    private String salt;
    private String motp;
    private Boolean mobileVerified;

    @Field("skip_otp_release")
    private Boolean skipOtpRelease;
    private String userType;

    @Field("exclude_permissions")
    private List<String> excludePermissions;
    private String mobile;
    private String otp;
    private String type;
    private List<String> merchantIds;
    private PasswordPolicy passwordPolicy;
    private String name;
    private String password1;
    private String salt1;
    private String reporter;
    private String source;
    private String deviceToken;
    private String designation;
    private String city;
    private String pincode;
    private String referralId;
    private String appType;
    private String appVersion;
    private Instant appInstalledDate;
    private AccountDetails accountDetails;
    private List<DocumentInfo> documents;
    private String DOB;
    private Boolean consent;
    private Instant passwordResetLinkGeneratedAt;
    private String partner;
    private String salesLead;
    private Target target;
    private String userProfileUpdate;
    private Map<String, String> metadata;
    private String createdBy;
    private Boolean useVpn;
    private Address address;
    private List<DesignationLog> designation_log;
    private List<LocationLog> location_log;
    private String location;
    private List<ReporterLog> reporter_log;
    private String brandId;
    private Instant dateOfJoin;
    private SiteLog site;
    private List<SiteLog> site_logs;

}
