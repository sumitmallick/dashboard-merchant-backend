package com.freewayemi.merchant.bo;

import com.freewayemi.merchant.commons.bo.*;
import com.freewayemi.merchant.commons.dto.*;
import com.freewayemi.merchant.commons.entity.Params;
import com.freewayemi.merchant.commons.exception.FreewayException;
import com.freewayemi.merchant.commons.utils.DateUtil;
import com.freewayemi.merchant.commons.utils.paymentConstants;
import com.freewayemi.merchant.commons.utils.Util;
import com.freewayemi.merchant.controller.AdminAuthUserController;
import com.freewayemi.merchant.dto.BankAccount.BankAccountAuthReq;
import com.freewayemi.merchant.dto.BankAccount.BankAccountAuthResp;
import com.freewayemi.merchant.dto.LoginResponse;
import com.freewayemi.merchant.dto.Password;
import com.freewayemi.merchant.dto.PasswordPolicy;
import com.freewayemi.merchant.dto.request.*;
import com.freewayemi.merchant.dto.response.BasicResponse;
import com.freewayemi.merchant.dto.response.StoreUserResponse;
import com.freewayemi.merchant.entity.*;
import com.freewayemi.merchant.enums.Status;
import com.freewayemi.merchant.enums.StoreUserStatus;
import com.freewayemi.merchant.repository.AdminAuthUserRepository;
import com.freewayemi.merchant.service.DigitalIdentityService;
import com.freewayemi.merchant.type.MerchantConstants;
import com.freewayemi.merchant.type.Source;
import com.freewayemi.merchant.utils.OffsetBasedPageRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.freewayemi.merchant.commons.utils.paymentConstants.SALES;
import static com.freewayemi.merchant.commons.utils.paymentConstants.STORE_USER;
import static com.freewayemi.merchant.utils.Constants.*;

@Component
public class AdminAuthUserBO {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminAuthUserController.class);

    private final AdminAuthUserRepository adminAuthUserRepository;
    private final NotificationService notificationService;
    private final MerchantUserBO merchantUserBO;
    private final AuthUserBO authUserBO;
    private final PaymentServiceBO paymentServiceBO;
    private final S3UploadService s3UploadService;
    private final NtbCoreService ntbCoreService;
    private final MerchantSessionBO merchantSessionBO;
    private final JwtTokenBO jwtTokenBO;
    private final Boolean isProduction;
    private final Boolean concurrentLogin;
    private final WebEngageService webEngageService;
    private final DigitalIdentityService digitalIdentityService;

    @Autowired
    public AdminAuthUserBO(AdminAuthUserRepository adminAuthUserRepository, NotificationService notificationService,
                           MerchantUserBO merchantUserBO, AuthUserBO authUserBO, S3UploadService s3UploadService,
                           NtbCoreService ntbCoreService, PaymentServiceBO paymentServiceBO,
                           MerchantSessionBO merchantSessionBO, @Value("${payment.deployment.env}") String env,
                           JwtTokenBO jwtTokenBO, @Value("CONCURRENT_LOGIN_SESSIONS_ALLOWED") String concurrentLogin,
                           DigitalIdentityService digitalIdentityService, WebEngageService webEngageService) {
        this.adminAuthUserRepository = adminAuthUserRepository;
        this.notificationService = notificationService;
        this.merchantUserBO = merchantUserBO;
        this.authUserBO = authUserBO;
        this.paymentServiceBO = paymentServiceBO;
        this.s3UploadService = s3UploadService;
        this.ntbCoreService = ntbCoreService;
        this.merchantSessionBO = merchantSessionBO;
        this.isProduction = paymentConstants.PRODENV.equals(env);
        this.jwtTokenBO = jwtTokenBO;
        this.concurrentLogin = concurrentLogin.equals("true");
        this.webEngageService = webEngageService;
        this.digitalIdentityService = digitalIdentityService;
    }

    public AdminAuthUser findById(String id) {
        return adminAuthUserRepository.findById(id).orElse(null);
    }

    public void pushNotification(String type, Map<String, String> request, String merchantId) {
        try {
            if (StringUtils.hasText(merchantId)) {
                MerchantUser merchantUser = merchantUserBO.getUserById(merchantId);
                if (null != merchantUser && null != merchantUser.getAddress() &&
                        StringUtils.hasText(merchantUser.getAddress().getCity())) {
                    request.put("city", merchantUser.getAddress().getCity());
                }
            }
            if (null != request && request.containsKey("leadOwnerId")) {
                String leadOwnerId = request.get("leadOwnerId");
                AdminAuthUser adminAuthUser = this.findById(leadOwnerId);
                if (null != adminAuthUser && StringUtils.hasText(adminAuthUser.getDeviceToken())) {
                    request.put("device", adminAuthUser.getDeviceToken());
                    if ("transaction".equals(type)) {
                        notificationService.sendTransactionStatusToSales(request);
                        return;
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("Exception occurred while sending push notification to sales person for request {}", request);
        }
        LOGGER.info("not sending notification to with request {}", request);
    }

    public void validateOtp(String storeUserId, OtpRequest request) {
        authUserBO.validate(storeUserId, request.getOtp());
        AdminAuthUser auu = adminAuthUserRepository.findById(storeUserId).orElse(null);
        if (null != auu) {
            auu.setMobileVerified(true);
            auu.setStatus(StoreUserStatus.ACTIVE.toString().toLowerCase());
            adminAuthUserRepository.save(auu);
            webEngageService.sendWebEngageStoreUserProps(auu);
            notificationService.sendStoreUserActivatedNotification(auu.getMobile(), auu.getName());
        }
    }

    public void checkUserExists(CheckUserExist checkUserExist) {
        if (StringUtils.hasText(checkUserExist.getMobile())) {
            AdminAuthUser user = adminAuthUserRepository.findByMobile(checkUserExist.getMobile()).orElse(null);
            if (user != null) throw new FreewayException("User already Exists");
            MerchantUser mu = merchantUserBO.getUserByMobile(checkUserExist.getMobile());
            if (null != mu) throw new FreewayException("User already exists as merchant");
        }
        if (StringUtils.hasText(checkUserExist.getEmail())) {
            AdminAuthUser user = adminAuthUserRepository.findByLogin(checkUserExist.getEmail()).orElse(null);
            if (user != null) throw new FreewayException("User already Exists");
        }
    }

    public void resendOtp(String storeUserId) {
        AdminAuthUser auu = adminAuthUserRepository.findById(storeUserId)
                .orElseThrow(() -> new FreewayException("User not exists."));
        if (null != auu) {
            authUserBO.createAuthUser(auu.getMobile(), storeUserId, STORE_USER, false, false);
        }
    }

    public StoreUserResponse addStoreUser(String name, String mobile, String email, String userType,
                                          String partner, MerchantUser merchantUser)
            throws FreewayException {
        AdminAuthUser user = adminAuthUserRepository.findByMobile(mobile).orElse(null);
        if (user != null) throw new FreewayException("User already Exists");

        MerchantUser mu = merchantUserBO.getUserByMobile(mobile);
        if (null != mu) throw new FreewayException("User already exists as merchant");

        if (StringUtils.hasText(email)) {
            user = adminAuthUserRepository.findByLogin(email.toLowerCase()).orElse(null);
            if (null != user) {
                throw new FreewayException("User email already Exists");
            }
        }

        AdminAuthUser adminAuthUser;
        adminAuthUser = new AdminAuthUser();
        adminAuthUser.setLogin(StringUtils.hasText(email) ? email.toLowerCase() : mobile);
        adminAuthUser.setName(name);
        adminAuthUser.setMerchantId(merchantUser.getId().toString());
        adminAuthUser.setMobile(mobile);
        adminAuthUser.setMobileVerified(Boolean.FALSE);
        adminAuthUser.setRole("STORE_USER");
        if (!StringUtils.isEmpty(userType)) {
            if ("STORE_USER".equals(userType) && Util.isNotNull(merchantUser.getParentMerchant())) {
                adminAuthUser.setMerchantId(merchantUser.getParentMerchant());
            }
            String[] userTypes = userType.split("_");
            String promoter = userTypes[userTypes.length - 1];
            if ("PROMOTER".equals(promoter)) {
                MerchantUser promoterMerchant = merchantUserBO.getUserByMobile(merchantUser.getMobile() + "_" + userTypes[0]);
                if (Util.isNotNull(promoterMerchant)) {
                    merchantUser = promoterMerchant;
                    String promoterMerchantId = promoterMerchant.getId().toString();
                    adminAuthUser.setMerchantId(promoterMerchantId);
                }
                adminAuthUser.setPartner(userTypes[0]);
                adminAuthUser.setRole("PARTNER_SALES");
            }
        }
        adminAuthUser.setReferralId(Util.generateUniqueNumber());
        adminAuthUser.setPermissions(Collections.singletonList("STORE_USER"));
        adminAuthUser.setName(name);
        adminAuthUser.setConsent(false);
        adminAuthUser.setStatus(StoreUserStatus.INACTIVE.toString().toLowerCase());
        adminAuthUser = adminAuthUserRepository.save(adminAuthUser);
        Params merchantParams = merchantUser.getParams();
        if (Util.isNotNull(merchantParams.getLeadOwnerIds())) {
            merchantParams.setLeadOwnerIds(Arrays.asList(adminAuthUser.getId().toString()));
        }
        if (Util.isNotNull(merchantParams.getLeadOwnerId())) {
            merchantParams.setLeadOwnerId(adminAuthUser.getId().toString());
        }
        authUserBO.createAuthUser(adminAuthUser.getMobile(), adminAuthUser.getId().toString(), STORE_USER, false,
                false);
        return StoreUserResponse.builder()
                .id(adminAuthUser.getId().toString())
                .name(adminAuthUser.getName())
                .mobile(adminAuthUser.getMobile())
                .status(adminAuthUser.getStatus())
                .referralId(adminAuthUser.getReferralId())
                .email(adminAuthUser.getLogin())
                .DOB(adminAuthUser.getDOB())
                .partner(adminAuthUser.getPartner())
                .role(adminAuthUser.getRole())
                .createdDate(adminAuthUser.getCreatedDate())
                .build();
    }

    private void upload(AdminAuthUser auu, MultipartFile file, String type, Boolean isFront, String merchantId)
            throws IOException {
        if (null != file && file.getSize() > 0) {
            String path = "/tmp/" + file.getOriginalFilename();
            Path filepath = Paths.get(path);
            try (OutputStream os = Files.newOutputStream(filepath)) {
                os.write(file.getBytes());
            }
            String key =
                    "merchants/" + merchantId + "/storeUsers/" + auu.getMobile() + "/" + file.getOriginalFilename();
            s3UploadService.upload(key, new File(path), file.getContentType());
            List<String> retValues = s3UploadService.getPreSignedURL(key);
            String url = retValues.get(0);
            String expiry = retValues.get(1);
            DocumentInfo di = new DocumentInfo(url, file.getOriginalFilename(), "", type, expiry, key, "", "", "", "");
            if (Boolean.TRUE.equals(isFront)) {
                ParseDocResponse parseDocResponse = parseDocAndMask(key, type);
                if (null == parseDocResponse || !StringUtils.hasText(parseDocResponse.getName()) ||
                        !StringUtils.hasText(parseDocResponse.getDOB()))
                    throw new FreewayException("Not able to verify document please upload a clear image");
                if (!parseDocResponse.getName().equalsIgnoreCase(auu.getName()))
                    throw new FreewayException("Name is not same as in the document");
                if (!parseDocResponse.getDOB().equalsIgnoreCase(auu.getDOB()))
                    throw new FreewayException("DOB is not same as in the document");
            }
            List<DocumentInfo> documents = null == auu.getDocuments() ? new ArrayList<>() : auu.getDocuments();
            documents.add(di);
            auu.setDocuments(documents);
        }
    }

    public List<StoreUserResponse> getStoreUsers(String mid) {
        List<StoreUserResponse> list = new ArrayList<>();
        try {
            List<AdminAuthUser> adminAuthUserList =
                    adminAuthUserRepository.findByMerchantIdAndPermissions(mid, "STORE_USER").orElse(new ArrayList<>());
            List<AdminAuthUser> partnerSalesAdminAuthUserList =
                    adminAuthUserRepository.findByMerchantIdAndPermissions(mid, "PARTNER_SALES").orElse(new ArrayList<>());
            adminAuthUserList.addAll(partnerSalesAdminAuthUserList);
            MerchantUser user = merchantUserBO.getUserById(mid);
            if (Util.isNotNull(user.getPartnerMerchants())) {
                for (String merchantId : user.getPartnerMerchants()) {
                    List<AdminAuthUser> partnerAdminAuthUserList =
                            adminAuthUserRepository.findByMerchantIdAndPermissions(merchantId, "STORE_USER").orElse(new ArrayList<>());
                    partnerSalesAdminAuthUserList =
                            adminAuthUserRepository.findByMerchantIdAndPermissions(merchantId, "PARTNER_SALES").orElse(new ArrayList<>());
                    adminAuthUserList.addAll(partnerAdminAuthUserList);
                    adminAuthUserList.addAll(partnerSalesAdminAuthUserList);
                }
            }
            StoreUserTransaction storeUserTransaction;
            adminAuthUserList.sort(Comparator.comparing(AdminAuthUser::getCreatedDate).reversed());
            for (AdminAuthUser adminAuthUser : adminAuthUserList) {
                storeUserTransaction = paymentServiceBO.getTransaction(adminAuthUser.getId().toString(),
                        StoreUserTransactionStatusReq.builder().status("success").build());
                list.add(StoreUserResponse.builder()
                        .id(adminAuthUser.getId().toString())
                        .name(adminAuthUser.getName())
                        .mobile(adminAuthUser.getMobile())
                        .status(adminAuthUser.getStatus())
                        .referralId(adminAuthUser.getReferralId())
                        .email(adminAuthUser.getLogin())
                        .DOB(adminAuthUser.getDOB())
                        .createdDate(adminAuthUser.getCreatedDate())
                        .transactionCount(getCount(storeUserTransaction))
                        .transactionTotal(getTransactionTotal(storeUserTransaction))
                        .userType(adminAuthUser.getRole())
                        .partner(adminAuthUser.getPartner())
                        .build());
            }
            return list;
        } catch (Exception e) {
            LOGGER.error("Exception occurred while fetching store users for merchantId: {}", mid);
        }
        return new ArrayList<>();
    }

    Integer getCount(StoreUserTransaction storeUserTransaction) {
        if (storeUserTransaction != null && !CollectionUtils.isEmpty(storeUserTransaction.getTransactionResponses())) {
            return storeUserTransaction.getTransactionResponses().size();
        }
        return 0;
    }

    Float getTransactionTotal(StoreUserTransaction storeUserTransaction) {
        Float total = 0f;
        if (storeUserTransaction != null && !CollectionUtils.isEmpty(storeUserTransaction.getTransactionResponses())) {
            for (TransactionResponse transactionResponse : storeUserTransaction.getTransactionResponses()) {
                total = total + transactionResponse.getAmount();
            }
        }
        return total;
    }

    public StoreUserResponse editStoreUser(String sid, StoreUserRequest request) {

        AdminAuthUser adminAuthUser =
                adminAuthUserRepository.findById(sid).orElseThrow(() -> new FreewayException("User not exists."));
        if (!StringUtils.isEmpty(request.getEmail())) {
            AdminAuthUser checkEmail =
                    adminAuthUserRepository.findByLogin(request.getEmail().toLowerCase()).orElse(null);
            if (checkEmail != null && !checkEmail.getId().toString().equals(adminAuthUser.getId().toString())) {
                throw new FreewayException("User email already Exists");
            }
            adminAuthUser.setLogin(request.getEmail().toLowerCase());
        }
        if (StringUtils.hasText(request.getName())) adminAuthUser.setName(request.getName());
        if (StringUtils.hasText(request.getDOB())) adminAuthUser.setDOB(request.getDOB());
        if (StoreUserStatus.INACTIVE.equals(request.getStatus()))
            adminAuthUser.setStatus(request.getStatus().toString().toLowerCase());
        merchantSessionBO.logoutStoreUserIfExists(sid);


        adminAuthUserRepository.save(adminAuthUser);
        return StoreUserResponse.builder()
                .id(adminAuthUser.getId().toString())
                .name(adminAuthUser.getName())
                .mobile(adminAuthUser.getMobile())
                .status(adminAuthUser.getStatus())
                .referralId(adminAuthUser.getReferralId())
                .email(adminAuthUser.getLogin())
                .createdDate(adminAuthUser.getCreatedDate())
                .build();
    }

    public void updateStoreUserDetails(String storeUserId, StoreUserUpdateRequest request) {
        AdminAuthUser adminAuthUser = adminAuthUserRepository.findById(storeUserId)
                .orElseThrow(() -> new FreewayException("Store user does not exists."));
        if (null != request.getAccountDetails()) adminAuthUser.setAccountDetails(request.getAccountDetails());
        if (null != request.getConsent()) adminAuthUser.setConsent(request.getConsent());
        adminAuthUserRepository.save(adminAuthUser);
        if (null != adminAuthUser.getAccountDetails() &&
                StringUtils.hasText(adminAuthUser.getAccountDetails().getAccountNumber()) &&
                StringUtils.hasText(adminAuthUser.getAccountDetails().getIfscCode())) {
            BankAccountAuthResp bankAccountAuthResp = digitalIdentityService.verifyAccount(BankAccountAuthReq.builder()
                    .accountNumber(adminAuthUser.getAccountDetails().getAccountNumber()).ifsc(adminAuthUser.getAccountDetails().getIfscCode())
                    .provider(MerchantConstants.KARZA).source(Source.MERCHANTMS).build());
            if (!StringUtils.isEmpty(bankAccountAuthResp) &&
                    "success".equals(bankAccountAuthResp.getStatus())) {
                adminAuthUser.setAccountDetails(AccountDetails.builder()
                        .isVerified(Boolean.TRUE)
                        .accountNumber(adminAuthUser.getAccountDetails().getAccountNumber())
                        .beneficiaryName(adminAuthUser.getAccountDetails().getBeneficiaryName())
                        .vpa(adminAuthUser.getAccountDetails().getVpa())
                        .ifscCode(adminAuthUser.getAccountDetails().getIfscCode())
                        .build());
                MerchantPennydropDetails merchantPennydropDetails = MerchantPennydropDetails.builder()
                        .merchantId(StringUtils.hasText(adminAuthUser.getMerchantId()) ? adminAuthUser.getMerchantId() : adminAuthUser.getId().toString())
                        .bankAccountAuthResp(bankAccountAuthResp)
                        .acc(adminAuthUser.getAccountDetails().getAccountNumber())
                        .ifsc(adminAuthUser.getAccountDetails().getIfscCode()).build();
                merchantPennydropDetails.setCreatedDate(Instant.now());
                merchantUserBO.createAccountPennyDrop(merchantPennydropDetails);
                adminAuthUserRepository.save(adminAuthUser);

            }
        }

    }

    private ParseDocResponse parseDocAndMask(String key, String type) {
        ParseDocResponse parseDocResponse = ntbCoreService.parseDocAndMask(key, type);
        if (parseDocResponse != null && ("success").equalsIgnoreCase(parseDocResponse.getStatus())) {
            LOGGER.info("Success in parsing Document: {}", type);
        } else {
            LOGGER.error("Exception occurred in parsing document: {}", type);
        }
        return parseDocResponse;
    }

    public void createAdminUser(AdminAuthUser adminAuthUser) {
        adminAuthUserRepository.save(adminAuthUser);
    }

    public AdminAuthUser findAdminAuthUserByMobileAndEmail(String mobile, String email) {
        return adminAuthUserRepository.findByMobileAndEmail(mobile, email).orElse(null);
    }

    public List<AdminAuthUser> getRoList() {
        return adminAuthUserRepository.findByPartnerExists(Boolean.TRUE).orElse(new ArrayList<>());
    }

    public AdminAuthUser findAdminAuthUserByMobile(String mobile) {
        return adminAuthUserRepository.findByMobile(mobile).orElse(null);
    }

    public long getCountByReporter(String leadOwnerId) {
        return adminAuthUserRepository.findCountByReporter(leadOwnerId);
    }

    public List<AdminAuthUser> findByIdOrMerchantId(String id, String role, List<String> status) {
        return merchantUserBO.findByIdOrMerchantId(id, role, status);
    }

    public BasicResponse saveAdminAuthUser(AdminAuthUser adminAuthUser, Boolean sendPasswordLink) {
        return merchantUserBO.saveAdminAuthUser(adminAuthUser, sendPasswordLink);
    }


    public void checkNumber(String password) {
        char[] chars = password.toCharArray();
        StringBuilder sb = new StringBuilder();
        for (char c : chars) {
            if (Character.isDigit(c)) {
                sb.append(c);
            }
        }
        if (sb.length() == 0) {
            throw new FreewayException("Password must have at least one number");
        }
    }

    public void checkAlphabet(String password) {
        char[] chars = password.toCharArray();
        StringBuilder sb = new StringBuilder();
        for (char c : chars) {
            if (Character.isAlphabetic(c)) {
                sb.append(c);
            }
        }
        if (sb.length() == 0) {
            throw new FreewayException("Password must have at least one alphabet");
        }
    }

    public void checkSpecialCharacter(String password) {
        Pattern pattern = Pattern.compile("[@_!#$%^&*()<>?/\\|}{~:]");
        Matcher matcher = pattern.matcher(password);
        boolean isStringContainsSpecialCharacter = matcher.find();
        if (!isStringContainsSpecialCharacter) {
            throw new FreewayException("Password must have at least one alphabet from: [@_!#$%^&*()<>?/\\\\|}{~:]");
        }
    }

    public void checkPasswordRegex(String password) {
        if (password.length() < 7) {
            throw new FreewayException("Password must of minimum 7 characters");
        }
        if (password.length() > 30) {
            throw new FreewayException("Password must of maximum 30 characters");
        }
        checkNumber(password);
        checkAlphabet(password);
        checkSpecialCharacter(password);
    }

    public void checkPasswordValidity(AdminAuthUser adminAuthUser, Boolean isChangePassword) {
        if (!isChangePassword && Objects.nonNull(adminAuthUser.getPasswordPolicy()) &&
                Objects.nonNull(adminAuthUser.getPasswordPolicy().getLastPwdModifiedDate())) {
            Integer days = DateUtil.getDays(Date.from(Instant.now()),
                    Date.from(adminAuthUser.getPasswordPolicy().getLastPwdModifiedDate()));
            if (days > 90) {
                throw new FreewayException("You password is expired. Please change your password");
            }
        } else {
            PasswordPolicy passwordPolicy = adminAuthUser.getPasswordPolicy();
            if (Objects.isNull(passwordPolicy)) {
                passwordPolicy = new PasswordPolicy();
            }
            passwordPolicy.setLastPwdModifiedDate(Instant.now());
            adminAuthUser.setPasswordPolicy(passwordPolicy);
            adminAuthUser.setLastModifiedDate(Instant.now());
            adminAuthUserRepository.save(adminAuthUser);
        }
    }

    public void checkPasswordHistory(String password, AdminAuthUser adminAuthUser, Boolean isLogin) {
        if (!isLogin && Objects.nonNull(adminAuthUser.getPasswordPolicy()) &&
                Objects.nonNull(adminAuthUser.getPasswordPolicy().getPasswords())) {
            List<Password> passwords = adminAuthUser.getPasswordPolicy().getPasswords();
            for (Password passwd : passwords) {
                String dbPassword = password + passwd.getSalt();
                String hashed = Util.getMd5(dbPassword);
                if (hashed.equals(dbPassword)) {
                    throw new FreewayException("New password cannot be same as last 4 used passwords");
                }
            }
        }
    }

    public void checkLocked(AdminAuthUser adminAuthUser) {
        if ("locked".equals(adminAuthUser.getStatus())) {
            if (Objects.nonNull(adminAuthUser.getPasswordPolicy()) &&
                    Objects.nonNull(adminAuthUser.getPasswordPolicy().getAccountLockedAt())) {
                Integer seconds = DateUtil.getSeconds(Date.from(Instant.now()),
                        Date.from(adminAuthUser.getPasswordPolicy().getAccountLockedAt()));
                if (seconds < 1800) {
                    adminAuthUser.setLastModifiedDate(Instant.now());
                    adminAuthUser.setStatus("active");
                    adminAuthUserRepository.save(adminAuthUser);
                    throw new FreewayException("Your account has been locked");
                }
            }

        }
    }

    private void checkPassword(String password, AdminAuthUser adminAuthUser, Boolean isLogin,
                               Boolean isChangePassword) {
        checkPasswordRegex(password);
        checkPasswordValidity(adminAuthUser, isChangePassword);
        checkPasswordHistory(password, adminAuthUser, isLogin);
        checkLocked(adminAuthUser);
    }

    public void updatePasswordAttemptCount(AdminAuthUser adminAuthUser, String status, String deviceToken) {
        if (Objects.nonNull(adminAuthUser.getPasswordPolicy()) &&
                Objects.nonNull(adminAuthUser.getPasswordPolicy().getInvalidAttempts()) && "failed".equals(status)) {
            adminAuthUser.setLastModifiedDate(Instant.now());
            adminAuthUser.getPasswordPolicy()
                    .setInvalidAttempts(adminAuthUser.getPasswordPolicy().getInvalidAttempts() + 1);
            adminAuthUserRepository.save(adminAuthUser);
        } else {
            PasswordPolicy passwordPolicy = adminAuthUser.getPasswordPolicy();
            if (Objects.isNull(passwordPolicy)) {
                passwordPolicy = new PasswordPolicy();
            }
            passwordPolicy.setInvalidAttempts(0);
            adminAuthUser.setLastModifiedDate(Instant.now());
            if (!StringUtils.isEmpty(deviceToken)) {
                adminAuthUser.setDeviceToken(deviceToken);
            }
            adminAuthUser.setPasswordPolicy(passwordPolicy);
            adminAuthUserRepository.save(adminAuthUser);
        }
    }

    public Boolean checkPasswordAttempts(AdminAuthUser adminAuthUser) {
        if (Objects.nonNull(adminAuthUser.getPasswordPolicy()) &&
                Objects.nonNull(adminAuthUser.getPasswordPolicy().getInvalidAttempts()) &&
                adminAuthUser.getPasswordPolicy().getInvalidAttempts() > 6) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public void lockUser(AdminAuthUser adminAuthUser) {
        PasswordPolicy passwordPolicy = adminAuthUser.getPasswordPolicy();
        if (Objects.isNull(passwordPolicy)) {
            passwordPolicy = new PasswordPolicy();
        }
        passwordPolicy.setAccountLockedAt(Instant.now());
        adminAuthUser.setLastModifiedDate(Instant.now());
        adminAuthUser.setStatus("locked");
        adminAuthUser.setPasswordPolicy(passwordPolicy);
        adminAuthUserRepository.save(adminAuthUser);
    }

    private Boolean verifySession(MerchantSession merchantSession) {
        if (Objects.isNull(merchantSession)) {
            return Boolean.FALSE;
        }
        if (Objects.nonNull(merchantSession.getLastActivityDate())) {
            Integer minutes =
                    DateUtil.getMinutes(Date.from(Instant.now()), Date.from(merchantSession.getLastActivityDate()));
            if (minutes > 30) {
                return Boolean.FALSE;
            }
        }
        return !(Objects.nonNull(merchantSession.getInvalid()) && merchantSession.getInvalid());
    }

    private String createSession(String userName) {
        Pageable pageable = new OffsetBasedPageRequest(1, 0, new Sort(Sort.Direction.DESC, "createdDate"));
        if (!concurrentLogin) {
            List<MerchantSession> merchantSessions = merchantSessionBO.findByUser(userName, pageable);
            LOGGER.info("merchant sessions: {}", merchantSessions);
            boolean activeSession = Boolean.FALSE;
            for (MerchantSession merchantSession : merchantSessions) {
                if (verifySession(merchantSession)) {
                    activeSession = Boolean.TRUE;
                    merchantSession.setInvalid(Boolean.TRUE);
                    merchantSessionBO.saveMerchantSession(merchantSession);
                }
            }
            if (activeSession) {
                return null;
            }
        }
        MerchantSession merchantSession = new MerchantSession();
        merchantSession.setUser(userName);
        merchantSession.setCreatedDate(Instant.now());
        merchantSession.setLastActivityDate(Instant.now());
        merchantSession.setVersion(VERSION);
        merchantSession.setInvalid(Boolean.FALSE);
        merchantSessionBO.saveMerchantSession(merchantSession);
        return merchantSessionBO.findByUser(userName, pageable).get(0).getId().toString();
    }

    private void updateSessionWithToken(String sessionId, String token) {
        MerchantSession merchantSession = merchantSessionBO.findById(sessionId);
        if (Objects.nonNull(merchantSession)) {
            merchantSession.setToken(Util.md5(token));
            merchantSession.setVersion(VERSION);
            merchantSession.setInvalid(false);
            merchantSessionBO.saveMerchantSession(merchantSession);
        }
    }

    private void resendMOtp(String userName, String mobile) {
        AdminAuthUser adminAuthUser = adminAuthUserRepository.findByLogin(userName).orElse(null);
        if (Objects.nonNull(adminAuthUser)) {
            String otp = StringUtils.hasText(adminAuthUser.getMotp()) ? adminAuthUser.getMotp()
                    : Util.generateOtp(isProduction);
            adminAuthUser.setMotp(otp);
            adminAuthUser.setMobile(mobile);
            adminAuthUser.setLastModifiedDate(Instant.now());
            adminAuthUserRepository.save(adminAuthUser);
            notificationService.sendOTP(otp, mobile, Boolean.FALSE, null, Boolean.FALSE);
        }
    }

    private Set<String> getPermission(AdminAuthUser adminAuthUser) {
        List<String> permissions = new ArrayList<>();
        Map<String, List<String>> permissionEnum = getPermissions();
        for (String key : permissionEnum.keySet()) {
            if (permissionEnum.get(key).contains(adminAuthUser.getRole())) {
                permissions.add(key);
            }
        }
        if (adminAuthUser.getName().contains("simplilearn")) {
            adminAuthUser.getPermissions().remove("VIEW_PAYMENT_LINKS");
        }
        for (String i : adminAuthUser.getPermissions()) {
            if (!adminAuthUser.getExcludePermissions().contains(i)) {
                permissions.add(i);
            }
        }
        return permissions.stream().collect(Collectors.toSet());
    }

    private LoginResponse getpaymentToken(AdminAuthUser adminAuthUser, String userName, String deviceToken) {
        if ("active".equals(adminAuthUser.getStatus())) {
            if (Boolean.FALSE.equals(adminAuthUser.getMobileVerified())) {
                if (StringUtils.hasText(adminAuthUser.getMobile())) {
                    resendMOtp(userName, adminAuthUser.getMobile());
                }
            }
            String sessionId = createSession(userName);
            if (!StringUtils.hasText(sessionId)) {
                throw new FreewayException("Concurrent login detected. All active sessions will be logged out.");
            }
            String token = jwtTokenBO.generateToken(adminAuthUser.getLogin(), deviceToken, Arrays.asList(SALES),
                    adminAuthUser.getId().toString());
            updateSessionWithToken(sessionId, token);
            Boolean sotpflow = Boolean.FALSE;
            return LoginResponse.builder()
                    .token(token)
                    .mobile(adminAuthUser.getMobile())
                    .otpFlow(Boolean.FALSE.equals(adminAuthUser.getMobileVerified()))
                    .sotpflow(sotpflow)
                    .build();
        } else {
            throw new FreewayException("Your account has been locked");
        }
    }

    public LoginResponse login(LoginRequest loginRequest, String remoteAddress, String deviceToken)
            throws IllegalAccessException {
        String userName = loginRequest.getUserName().toLowerCase();
        String password = loginRequest.getPassword();
        AdminAuthUser adminAuthUser = adminAuthUserRepository.findByLogin(userName).orElse(null);
        if (Objects.isNull(adminAuthUser)) {
            throw new FreewayException("Invalid user");
        }
        if ("locked".equals(adminAuthUser.getStatus())) {
            throw new FreewayException("Your account has been locked");
        }
        if ("ADMIN".equals(adminAuthUser.getRole())) {
            if (Boolean.TRUE.equals(adminAuthUser.getUseVpn()) && !ALLOWED_IPS_FOR_ADMINS.contains(remoteAddress)) {
                throw new FreewayException("User must connect to VPN before login.");
            }
        }
//        checkPassword(password, adminAuthUser, Boolean.TRUE, Boolean.FALSE);
        if (Objects.isNull(adminAuthUser.getSalt())) {
            return null;
        }
        String salt = adminAuthUser.getSalt();
        String dbPassword = password + salt;
        String hashed = Util.getMd5(dbPassword);
        if (adminAuthUser.getPassword().equals(hashed)) {
            updatePasswordAttemptCount(adminAuthUser, "success", deviceToken);
            LoginResponse loginResponse = getpaymentToken(adminAuthUser, userName, deviceToken);
            loginResponse.setCode(0);
            return loginResponse;
        } else {
            if (checkPasswordAttempts(adminAuthUser)) {
                lockUser(adminAuthUser);
            } else {
                updatePasswordAttemptCount(adminAuthUser, "failed", deviceToken);
            }
        }
        throw new FreewayException("Invalid user name or password");
    }

    public BasicResponse logout(String storeUserId) {
        merchantSessionBO.logoutStoreUser(storeUserId);
        return BasicResponse.builder().status(Status.SUCCESS).statusMsg("Successfully Logged Out").build();
    }

    public AdminAuthUser findByLogin(String login) {
        return adminAuthUserRepository.findByLogin(login).orElse(null);
    }
}
