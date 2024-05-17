package com.freewayemi.merchant.service;

import com.freewayemi.merchant.bo.ReferralCodeBO;
import com.freewayemi.merchant.commons.bo.NtbCoreService;
import com.freewayemi.merchant.commons.bo.S3UploadService;
import com.freewayemi.merchant.commons.dto.Address;
import com.freewayemi.merchant.commons.dto.DocumentInfo;
import com.freewayemi.merchant.commons.dto.ParseDocResponse;
import com.freewayemi.merchant.commons.dto.karza.GstAuthReq;
import com.freewayemi.merchant.commons.entity.Params;
import com.freewayemi.merchant.commons.exception.FreewayException;
import com.freewayemi.merchant.commons.utils.paymentConstants;
import com.freewayemi.merchant.commons.utils.Util;
import com.freewayemi.merchant.dto.AddressResponse;
import com.freewayemi.merchant.dto.GstDetailsInfo;
import com.freewayemi.merchant.dto.PanDocumentRequest;
import com.freewayemi.merchant.dto.request.MerchantOnboardingRequest;
import com.freewayemi.merchant.dto.request.PartnerInfo;
import com.freewayemi.merchant.dto.response.MerchantUserResponse;
import com.freewayemi.merchant.entity.BrandGst;
import com.freewayemi.merchant.entity.MerchantGSTDetails;
import com.freewayemi.merchant.entity.MerchantUser;
import com.freewayemi.merchant.pojos.APIResponse;
import com.freewayemi.merchant.pojos.pan.PanDetailsRequest;
import com.freewayemi.merchant.pojos.pan.PanDetailsResponse;
import com.freewayemi.merchant.repository.BrandGstRepository;
import com.freewayemi.merchant.repository.MerchantGstAuthInfoRepository;
import com.freewayemi.merchant.repository.MerchantUserRepository;
import com.freewayemi.merchant.type.MerchantConstants;
import com.freewayemi.merchant.type.Source;
import com.freewayemi.merchant.utils.MerchantStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.freewayemi.merchant.utils.Constants.payment_PARTNER;

@Service
public class MerchantOnboardingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MerchantLeadService.class);
    private final DigitalIdentityService digitalIdentityService;
    private final BrandGstRepository brandGSTRepository;
    private final MerchantUserRepository merchantUserRepository;
    private final S3UploadService s3UploadService;
    private final NtbCoreService ntbCoreService;
    private final MerchantGstAuthInfoRepository merchantGstAuthInfoRepository;
    private final ReferralCodeBO referralCodeBO;

    @Autowired
    public MerchantOnboardingService(
            DigitalIdentityService digitalIdentityService,
            BrandGstRepository brandGSTRepository,
            MerchantUserRepository merchantUserRepository,
            S3UploadService s3UploadService,
            NtbCoreService ntbCoreService, MerchantGstAuthInfoRepository merchantGstAuthInfoRepository,
            ReferralCodeBO referralCodeBO) {
        this.digitalIdentityService = digitalIdentityService;
        this.brandGSTRepository = brandGSTRepository;
        this.merchantUserRepository = merchantUserRepository;
        this.s3UploadService = s3UploadService;
        this.ntbCoreService = ntbCoreService;
        this.merchantGstAuthInfoRepository = merchantGstAuthInfoRepository;
        this.referralCodeBO = referralCodeBO;

    }

//    private void onboardingRequestValidation(String leadOwnerId, MerchantOnboardingRequest merchantOnboardingRequest) {
//        if (!ValidationUtil.validateMobileNumber(merchantOnboardingRequest.getMobile())) {
//            throw new FreewayException("Invalid Mobile Number");
//        }
//    }

    public APIResponse onboardingMerchant(MerchantUser merchantUser, MerchantOnboardingRequest merchantOnboardingRequest, String onboardingStage, String deviceToken) {
        MerchantUser childMerchant = null;
        String partnerValue = payment_PARTNER;
        if (MerchantStatus.registered.name().equals(merchantUser.getStatus()) && !CollectionUtils.isEmpty(merchantUser.getPartners())) {
            childMerchant = merchantUserRepository.findByMobileAndIsDeleted(merchantUser.getMobile() + "_" + merchantUser.getPartners().get(0), false).orElseThrow(() -> new FreewayException("Merchant user is not found"));
            partnerValue = merchantUser.getPartners().get(0);
        }
        com.freewayemi.merchant.dto.request.PartnerInfo partner = referralCodeBO.getPartnerInfo(partnerValue);

        switch (onboardingStage) {
            case paymentConstants.ONBOARDING_STAGE_1:
                LOGGER.info("At ONBOARDING_STAGE_1: {}", paymentConstants.ONBOARDING_STAGE_1);
                APIResponse errorResponse = new APIResponse(null, null, null, null);
                if ((Objects.nonNull(childMerchant) && MerchantStatus.registered.name().equals(childMerchant.getStatus())) || MerchantStatus.registered.name().equals(merchantUser.getStatus())) {
                    GstDetailsInfo gstDetailsInfo = new GstDetailsInfo();
                    if (Util.isNotNull(merchantOnboardingRequest.getGst()) && Util.isNotNull(merchantOnboardingRequest.getGst().equals(""))) {
                        String gst = merchantOnboardingRequest.getGst();
                        List<BrandGst> brandGSTResponse = brandGSTRepository.findByGst(gst).get();
                        com.freewayemi.merchant.dto.gst.GstAuthResp gstAuthResp =
                                digitalIdentityService.verifyGst(GstAuthReq.builder().gstin(gst).provider(MerchantConstants.KARZA).source(Source.MERCHANTMS).build());
                        if (Util.isNotNull(gstAuthResp)) {
                            MerchantGSTDetails merchantGSTDetails = new MerchantGSTDetails();
                            merchantGSTDetails.setMerchantId(merchantUser.getId().toString());
                            merchantGSTDetails.setGst(gst);
                            merchantGSTDetails.setGstAuthResp(gstAuthResp);
                            merchantGSTDetails.setCreatedDate(Instant.now());
                            merchantGSTDetails.setLastModifiedDate(Instant.now());
                            merchantGstAuthInfoRepository.save(merchantGSTDetails);
                            if (Objects.nonNull(gstAuthResp.getAddressesOfPrincipalPlaceOfBusiness())) {
                                String address =
                                        Objects.equals(gstAuthResp.getAddressesOfPrincipalPlaceOfBusiness().getCompleteAddress(), "NA")
                                                ? "" : gstAuthResp.getAddressesOfPrincipalPlaceOfBusiness().getCompleteAddress();
                                if (!StringUtils.hasText(address)) {
                                    address =
                                            Objects.equals(gstAuthResp.getAddressesOfPrincipalPlaceOfBusiness().getNatureOfBusinessAtAddress(), "NA")
                                                    ? "" : gstAuthResp.getAddressesOfPrincipalPlaceOfBusiness().getNatureOfBusinessAtAddress();
                                }
                                String[] addressList = address.split(",");
                                String pincode = "";
                                if (addressList.length > 0) {
                                    pincode = addressList[addressList.length - 1];
                                } else {
                                    pincode = "";
                                }
                                if (pincode.contains("pin")) {
                                    String[] pincodeList = pincode.split(":");
                                    if (pincodeList.length > 0) {
                                        pincode = pincodeList[pincodeList.length - 1];
                                    }
                                }
                                String line1 = "", line2 = "", city = "", state = "";
                                if (addressList.length > 4) {
                                    line1 = String.join(",",
                                            Arrays.copyOfRange(addressList, 0, addressList.length - 4));
                                } else {
                                    line1 = address;
                                }
                                if (addressList.length > 3) {
                                    line2 = addressList[addressList.length - 4].trim();
                                }
                                if (addressList.length > 2) {
                                    city = addressList[addressList.length - 3].trim();
                                }
                                if (addressList.length > 1) {
                                    state = addressList[addressList.length - 2].trim();
                                }
                                gstDetailsInfo.setStatus(Boolean.TRUE);
                                gstDetailsInfo.setNames(gstAuthResp.getMemberNames());
                                gstDetailsInfo.setAddress(address);
                                gstDetailsInfo.setLine1(line1);
                                gstDetailsInfo.setLine2(line2);
                                gstDetailsInfo.setCity(city);
                                gstDetailsInfo.setState(state);
                                gstDetailsInfo.setPincode(pincode);
                                gstDetailsInfo.setBusinessName(gstAuthResp.getLegalNameOfBusiness());
                                gstDetailsInfo.setType(gstAuthResp.getConstitutionOfBusiness());
                                gstDetailsInfo.setGst(gstAuthResp.getGst());
                                gstDetailsInfo.setPhoneNumber(gstAuthResp.getGstContact().getMobileNumber());

                                merchantUser.setBusinessName(gstAuthResp.getLegalNameOfBusiness());
                            }
                            Params params = new Params();
                            if (Objects.nonNull(merchantUser.getParams())) {
                                params = merchantUser.getParams();
                            }
                            if (brandGSTResponse.isEmpty()) {
                                merchantUser.setGst(gstAuthResp.getGst());
                            } else {
                                LOGGER.info("brandGSTResponse: {}", brandGSTResponse);
                                List<String> brandIds = new ArrayList<>();
                                brandGSTResponse.forEach(brands -> {
                                    if (!brands.getBrandId().isEmpty() && !brandIds.contains(brands.getBrandId())) {
                                        brandIds.add(brands.getBrandId());
                                    }
                                });
                                merchantUser.setGst(gst);
                                if (!brandIds.isEmpty()) {
                                    params.setBrandIds(brandIds);
                                    params.setBrandId(brandIds.get(0));
                                    merchantUser.setParams(params);
                                }
                            }
                            params.setCheckoutVersion("v2");
                            merchantUser.setParams(params);
                            String pinCode = gstDetailsInfo.getPincode();
                            String city = "";
                            String state = "";
                            AddressResponse response = digitalIdentityService.getPostalAddress(pinCode);
                            if (Util.isNotNull(response) && Util.isNotNull(response.getCity()) && Util.isNotNull(response.getState())) {
                                city = response.getCity();
                                state = response.getState();
                            }
                            Address address = new Address(gstDetailsInfo.getPincode(), city, gstDetailsInfo.getLine1(), gstDetailsInfo.getLine2(), state, "", null, "KARZA", true, null);
                            merchantUser.setAddress(address);
                            if (Util.isNotNull(deviceToken) && !deviceToken.equals("")) {
                                merchantUser.setDeviceToken(deviceToken);
                            }
                            if (Objects.nonNull(childMerchant)) {
                                childMerchant.setStage(paymentConstants.ONBOARDING_STAGE_1);
                                childMerchant.setStatus(MerchantStatus.registered.name());
                            }else{
                                merchantUser.setStage(paymentConstants.ONBOARDING_STAGE_1);
                                merchantUser.setStatus(MerchantStatus.registered.name());
                            }
                            merchantUserRepository.save(merchantUser);
                            if(Objects.nonNull(childMerchant)){
                                merchantUserRepository.save(childMerchant);
                            }
                            String nextStage = Util.getNextOnboardingStage(partner, onboardingStage);
                            MerchantUserResponse merchantUserResponse = new MerchantUserResponse(merchantUser, null, null, null, null, gstDetailsInfo, null, null, null);
                            merchantUserResponse.setNextOnboardingStage(nextStage);
                            return new APIResponse(200, "SUCCESS", "Successfully verified GST for merchant: " + merchantUser.getDisplayId(), merchantUserResponse);
                        }
                        return new APIResponse(210, "ERROR", "Couldn't fetch GST details for GST: " + merchantOnboardingRequest.getGst(), new MerchantUserResponse(merchantUser, null, null, null, null, null, null, null, null));
                    }
                    return new APIResponse(211, "ERROR", "Enter valid GST", new MerchantUserResponse(merchantUser, null, null, null, null, null, null, null, null));
                }
                return new APIResponse(212, "ERROR", "Merchant's status is not " + MerchantStatus.registered.name(), new MerchantUserResponse(merchantUser, null, null, null, null, null, null, null, null));

            case paymentConstants.ONBOARDING_STAGE_2:
                LOGGER.info("At ONBOARDING_STAGE_2: {}", paymentConstants.ONBOARDING_STAGE_2);
                if ((Objects.nonNull(childMerchant) && MerchantStatus.registered.name().equals(childMerchant.getStatus())) || MerchantStatus.registered.name().equals(merchantUser.getStatus())) {
                    if (Util.isNotNull(merchantOnboardingRequest.getOwnership()) && !merchantOnboardingRequest.getOwnership().equals("")) {
                        merchantUser.setOwnership(merchantOnboardingRequest.getOwnership());
                        if (Objects.nonNull(childMerchant)) {
                            childMerchant.setStage(paymentConstants.ONBOARDING_STAGE_2);
                            childMerchant.setStatus(MerchantStatus.registered.name());
                        }else{
                            merchantUser.setStage(paymentConstants.ONBOARDING_STAGE_2);
                            merchantUser.setStatus(MerchantStatus.registered.name());
                        }
                        merchantUserRepository.save(merchantUser);
                        if(Objects.nonNull(childMerchant)){
                            merchantUserRepository.save(childMerchant);
                        }
                        String nextStage = Util.getNextOnboardingStage(partner, onboardingStage);
                        MerchantUserResponse merchantUserResponse = new MerchantUserResponse(merchantUser, null, null, null, null, null, null, null, null);
                        merchantUserResponse.setNextOnboardingStage(nextStage);
                        return new APIResponse(201, "SUCCESS", "ownership details added for merchant: ." + merchantUser.getDisplayId(), merchantUserResponse);
                    }
                    return new APIResponse(225, "ERROR", "ownership details can not be empty.", new MerchantUserResponse(merchantUser, null, null, null, null, null, null, null, null));
                }
                return new APIResponse(224, "ERROR", "Merchant is not in " + paymentConstants.ONBOARDING_STAGE_2, new MerchantUserResponse(merchantUser, null, null, null, null, null, null, null, null));

            case paymentConstants.ONBOARDING_STAGE_3:
                LOGGER.info("At ONBOARDING_STAGE_3: {}", paymentConstants.ONBOARDING_STAGE_3);
                if ((Objects.nonNull(childMerchant) && MerchantStatus.registered.name().equals(childMerchant.getStatus())) || MerchantStatus.registered.name().equals(merchantUser.getStatus())) {
                    if (Util.isNotNull(merchantOnboardingRequest.getPanDetails())) {
                        String pan = "";
                        List<DocumentInfo> documents = null == merchantUser.getDocuments() ? new ArrayList<>() : merchantUser.getDocuments();
                        for (PanDocumentRequest panData : merchantOnboardingRequest.getPanDetails()) {
                            if (Util.isNotNull(panData.getPan()) && !panData.getPan().equals("")) {
                                PanDetailsResponse panDetailsResponse =
                                        digitalIdentityService.getPANDetails(PanDetailsRequest.builder().panNumber(panData.getPan()).source(Source.MERCHANTMS).build());
                                if (Util.isNotNull(panDetailsResponse)) {
                                    LOGGER.info("panDetailsResponse: {}", panDetailsResponse);
                                    DocumentInfo document = new DocumentInfo(
                                            "", "", "identity", panData.getType(), "", "", "UPLOADED", "", panData.getPan(), ""
                                    );
                                    if (Util.isNotNull(panData.getType()) && !panData.getType().equals("")) {
                                        document = new DocumentInfo(
                                                "", "", "identity", panData.getType(), "", "", "UPLOADED", "", panData.getPan(), "");
                                    }
                                    documents.add(document);
                                    pan = panData.getPan();
                                }
                            }
                        }
                        merchantUser.setDocuments(documents);
                        merchantUser.setPan(pan);
                        if (Objects.nonNull(childMerchant)) {
                            childMerchant.setStage(paymentConstants.ONBOARDING_STAGE_3);
                            childMerchant.setStatus(MerchantStatus.registered.name());
                        }else{
                            merchantUser.setStage(paymentConstants.ONBOARDING_STAGE_3);
                            merchantUser.setStatus(MerchantStatus.registered.name());
                        }
                        merchantUserRepository.save(merchantUser);
                        if(Objects.nonNull(childMerchant)){
                            merchantUserRepository.save(childMerchant);
                        }
                        String nextStage = Util.getNextOnboardingStage(partner, onboardingStage);
                        MerchantUserResponse merchantUserResponse = new MerchantUserResponse(merchantUser, null, null, null, null, null, null, null, null);
                        merchantUserResponse.setNextOnboardingStage(nextStage);
                        return new APIResponse(200, "SUCCESS", "Successfully verified PAN for merchant: " + merchantUser.getDisplayId(), merchantUserResponse);
                    }
                    return new APIResponse(222, "ERROR", "PAN details can not be empty.", new MerchantUserResponse(merchantUser, null, null, null, null, null, null, null, null));
                }
                return new APIResponse(223, "ERROR", "Merchant's status is not " + MerchantStatus.registered.name(), new MerchantUserResponse(merchantUser, null, null, null, null, null, null, null, null));

            default:
                return null;
        }
    }

    private void upload(MerchantUser merchantUser, MultipartFile file, String type, Boolean isFront, String merchantId)
            throws IOException {
        if (null != file && file.getSize() > 0) {
            String path = "/tmp/" + file.getOriginalFilename();
            Path filepath = Paths.get(path);
            try (OutputStream os = Files.newOutputStream(filepath)) {
                os.write(file.getBytes());
            }
            String key =
                    "merchants/" + merchantId + "/merchantUsers/" + merchantUser.getMobile() + "/" + file.getOriginalFilename();
            s3UploadService.upload(key, new File(path), file.getContentType());
            List<String> retValues = s3UploadService.getPreSignedURL(key);
            String url = retValues.get(0);
            String expiry = retValues.get(1);
            DocumentInfo di = new DocumentInfo(url, file.getOriginalFilename(), "", type, expiry, key, "UPLOADED", "", "", "");
            if (isFront) {
                ParseDocResponse parseDocResponse = parseDocAndMask(key, type);
                if (null == parseDocResponse ||
                        !StringUtils.hasText(parseDocResponse.getName()) ||
                        !StringUtils.hasText(parseDocResponse.getDOB()))
                    throw new FreewayException("Not able to verify document please upload a clear image");
//                if (!parseDocResponse.getName().equalsIgnoreCase(merchantUser.getName()))
//                    throw new FreewayException("Name is not same as in the document");
//                if (!parseDocResponse.getDOB().equalsIgnoreCase(merchantUser.getDOB()))
//                    throw new FreewayException("DOB is not same as in the document");
            }
            List<DocumentInfo> documents = null == merchantUser.getDocuments() ? new ArrayList<>() : merchantUser.getDocuments();
            documents.add(di);
            merchantUser.setDocuments(documents);
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
}
