package com.freewayemi.merchant.service;

import com.freewayemi.merchant.commons.bo.S3UploadService;
import com.freewayemi.merchant.commons.dto.karza.GstAuthenticationResponse;
import com.freewayemi.merchant.commons.utils.DateUtil;
import com.freewayemi.merchant.commons.utils.Util;
import com.freewayemi.merchant.dto.gst.GstAuthResp;
import com.freewayemi.merchant.entity.MerchantGSTDetails;
import com.freewayemi.merchant.entity.MerchantPennydropDetails;
import com.freewayemi.merchant.entity.MerchantUser;
import com.freewayemi.merchant.pojos.GenerateAgreementResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class GenerateAgreementService {
    private static final Logger LOGGER = LoggerFactory.getLogger(GenerateAgreementService.class);

    private static final String MERCHANT_ONBOARDING_AGREEMENT_KEY_FORMAT = "merchant_onboardings/%s";

    private final S3UploadService s3UploadService;

    @Autowired
    public GenerateAgreementService(S3UploadService s3UploadService) {
        this.s3UploadService = s3UploadService;
    }

    public String getAgreementTemplate(String type) {
        switch (type) {
            case "ONBOARDING":
                return "onboarding_agreement/merchant_onboarding_agreement";
        }
        return null;
    }

    public Context getOnBoardingContextData(MerchantUser merchantUser, MerchantPennydropDetails merchantPennydropDetails, MerchantGSTDetails merchantGSTDetails) {
        try {
            List<String> entityTypes = Arrays.asList("proprietorship", "partnership", "llp", "public limited", "private limited", "huf");
            Context context = new Context();
            context.setVariable("name", merchantUser.getFirstName() + " " + merchantUser.getLastName());
            context.setVariable("gst", merchantUser.getGst());
            context.setVariable("pan", merchantUser.getPan());
            String address = "";
            String city = "";
            String gstName = "";
            if (StringUtils.hasText(merchantUser.getFirstName()) && StringUtils.hasText(merchantUser.getLastName())) {
                gstName = merchantUser.getFirstName() + " " + merchantUser.getLastName();
            } else if (StringUtils.hasText(merchantUser.getFirstName())) {
                gstName = merchantUser.getFirstName();
            } else if (StringUtils.hasText(merchantUser.getLastName())) {
                gstName = merchantUser.getLastName();
            }
            String gstMobile = merchantUser.getMobile();
            String gstEmail = merchantUser.getEmail();
            if (Objects.nonNull(merchantGSTDetails.getGstAuthResp())) {
                GstAuthResp gstAuthResp = merchantGSTDetails.getGstAuthResp();
                context.setVariable("legalName", gstAuthResp.getLegalNameOfBusiness());
                context.setVariable("shopName", gstAuthResp.getTradeName());
                context.setVariable("registrationDate", gstAuthResp.getGstRegistrationDate());
                String entityType = gstAuthResp.getConstitutionOfBusiness();
                if (StringUtils.hasText(entityType)) {
                    if ("limited liability partnership".equalsIgnoreCase(entityType) || "llp".equalsIgnoreCase(entityType)) {
                        entityType = "LLP";
                    } else if ("hindu undivided family".equalsIgnoreCase(entityType) || "huf".equalsIgnoreCase(entityType)) {
                        entityType = "HUF";
                    } else {
                        if (!entityTypes.contains(entityType.toLowerCase())) {
                            entityType = "Others";
                        }
                    }
                } else {
                    entityType = "others";
                }
                context.setVariable("entityType", entityType);
                String consent = "";
                if ("huf".equalsIgnoreCase(entityType) || "proprietorship".equalsIgnoreCase(entityType)) {
                    consent = "Proprietor";
                } else if ("partnership".equalsIgnoreCase(entityType) || "llp".equalsIgnoreCase(entityType)) {
                    consent = "Partner";
                }
                if ("public limited".equalsIgnoreCase(entityType) || "private limited".equalsIgnoreCase(entityType)) {
                    consent = "Director";
                }
                context.setVariable("consent", consent);
                context.setVariable("gst", gstAuthResp.getGst());
                if (Objects.nonNull(gstAuthResp.getAddressesOfPrincipalPlaceOfBusiness())) {
                    address = Objects.equals(gstAuthResp.getAddressesOfPrincipalPlaceOfBusiness().getCompleteAddress(), "NA")
                            ? "" : gstAuthResp.getAddressesOfPrincipalPlaceOfBusiness().getCompleteAddress();
                    if (!StringUtils.hasText(address)) {
                        address =
                                Objects.equals(gstAuthResp.getAddressesOfPrincipalPlaceOfBusiness().getNatureOfBusinessAtAddress(), "NA")
                                        ? "" : gstAuthResp.getAddressesOfPrincipalPlaceOfBusiness().getNatureOfBusinessAtAddress();
                    }
                    String[] addressList = address.split(",");
                    city = addressList.length > 2 ? addressList[addressList.length - 3].trim() : "";
                }
                context.setVariable("address", address);

                if (Objects.nonNull(gstAuthResp.getGstContact())) {
                    context.setVariable("gstName", StringUtils.hasText(gstName) ? gstName : gstAuthResp.getGstContact().getName());
                    context.setVariable("gstMobile", StringUtils.hasText(gstMobile) ? gstMobile : gstAuthResp.getGstContact().getMobileNumber());
                    context.setVariable("gstEmail", StringUtils.hasText(gstEmail) ? gstEmail : gstAuthResp.getGstContact().getEmail());
                } else {
                    context.setVariable("gstName", gstName);
                    context.setVariable("gstMobile", gstMobile);
                    context.setVariable("gstEmail", gstEmail);
                }
            }else if (Objects.nonNull(merchantGSTDetails.getGstAuthenticationResponse())) {
                GstAuthenticationResponse gstAuthenticationResponse = merchantGSTDetails.getGstAuthenticationResponse();
                if (Objects.nonNull(gstAuthenticationResponse)) {
                    GstAuthenticationResponse.Result result = merchantGSTDetails.getGstAuthenticationResponse().getResult();
                    context.setVariable("legalName", result.getLgnm());
                    context.setVariable("shopName", result.getTradeNam());
                    context.setVariable("registrationDate", result.getRgdt());
                    String entityType = result.getCtb();
                    if (StringUtils.hasText(entityType)) {
                        if ("limited liability partnership".equalsIgnoreCase(entityType) || "llp".equalsIgnoreCase(entityType)) {
                            entityType = "LLP";
                        } else if ("hindu undivided family".equalsIgnoreCase(entityType) || "huf".equalsIgnoreCase(entityType)) {
                            entityType = "HUF";
                        } else {
                            if (!entityTypes.contains(entityType.toLowerCase())) {
                                entityType = "Others";
                            }
                        }
                    } else {
                        entityType = "others";
                    }
                    context.setVariable("entityType", entityType);
                    String consent = "";
                    if ("huf".equalsIgnoreCase(entityType) || "proprietorship".equalsIgnoreCase(entityType)) {
                        consent = "Proprietor";
                    } else if ("partnership".equalsIgnoreCase(entityType) || "llp".equalsIgnoreCase(entityType)) {
                        consent = "Partner";
                    }
                    if ("public limited".equalsIgnoreCase(entityType) || "private limited".equalsIgnoreCase(entityType)) {
                        consent = "Director";
                    }
                    context.setVariable("consent", consent);
                    context.setVariable("gst", result.getGstin());
                    if (Objects.nonNull(result.getPradr())) {
                        address = Objects.equals(result.getPradr().getAdr(), "NA")
                                ? "" : result.getPradr().getAdr();
                        if (!StringUtils.hasText(address)) {
                            address =
                                    Objects.equals(result.getPradr().getNtr(), "NA")
                                            ? "" : result.getPradr().getNtr();
                        }
                        String[] addressList = address.split(",");
                        city = addressList.length > 2 ? addressList[addressList.length - 3].trim() : "";
                    }
                    context.setVariable("address", address);

                    if (Objects.nonNull(result.getContacted())) {
                        context.setVariable("gstName", StringUtils.hasText(gstName) ? gstName : result.getContacted().getName());
                        context.setVariable("gstMobile", StringUtils.hasText(gstMobile) ? gstMobile : result.getContacted().getMobNum());
                        context.setVariable("gstEmail", StringUtils.hasText(gstEmail) ? gstEmail : result.getContacted().getEmail());
                    } else {
                        context.setVariable("gstName", gstName);
                        context.setVariable("gstMobile", gstEmail);
                        context.setVariable("gstEmail", gstMobile);
                    }
                }
            } else {
                context.setVariable("gstName", gstName);
                context.setVariable("gstMobile", gstMobile);
                context.setVariable("gstEmail", gstEmail);
            }
            context.setVariable("city", city);
            if (Objects.nonNull(merchantUser.getAddress())) {
                context.setVariable("line1", merchantUser.getAddress().getLine1());
                context.setVariable("line2", merchantUser.getAddress().getLine2());
                context.setVariable("city", StringUtils.hasText(merchantUser.getAddress().getCity()) ? merchantUser.getAddress().getCity() : city);
                context.setVariable("state", merchantUser.getAddress().getState());
                context.setVariable("pinCode", merchantUser.getAddress().getPincode());
            }
            if (Objects.nonNull(merchantPennydropDetails.getBankAccountAuthResp())) {
                context.setVariable("accountHolderName", merchantPennydropDetails.getBankAccountAuthResp().getAccountName());
            }
            context.setVariable("accountNumber", merchantPennydropDetails.getAcc());
            context.setVariable("ifscCode", merchantPennydropDetails.getIfsc());
            String category = "";
            if (StringUtils.hasText(merchantUser.getCategory())) {
                category = merchantUser.getCategory();
                if (category.contains("&")) {
                    category = category.replace("&", "and");
                }
            }
            context.setVariable("category", category);
            context.setVariable("merchantServiceAggrement", "https://www.getpayment.com/merchant-service-agreement");
            context.setVariable("date", DateUtil.getFormattedDate(Date.from(Instant.now()), "dd/MM/yyyy"));
            return context;
        } catch (Exception ex) {
            LOGGER.info("exception while generating the agreement: " + ex);
        }
        return null;

    }

    public GenerateAgreementResponse generateMerchantOnboardingAgreement(MerchantUser merchantUser,
                                                                         MerchantPennydropDetails merchantPennydropDetails,
                                                                         MerchantGSTDetails merchantGSTDetails,
                                                                         String type) throws IOException {
        final String documentType = "merchant_onboarding_agreement";
        final String documentContent = "pdf";
        GenerateAgreementResponse generateAgreementResponse =
                new GenerateAgreementResponse(20,
                        "FAILED", "No agreement is available", null, null,
                        null);
        String template = getAgreementTemplate(type);
        LOGGER.info("template: {}", template);
        if (StringUtils.hasText(template)) {
            Context context = getOnBoardingContextData(merchantUser, merchantPennydropDetails, merchantGSTDetails);
            String agreementHTMLContent = Util.parseThymeleafTemplate(template, context);
            String merchantOnBoardingAgreementFileName = System.currentTimeMillis() + "_merchant_onboarding_agreement.pdf";
            String merchantOnboardingAgreementS3BucketKey = String.format(MERCHANT_ONBOARDING_AGREEMENT_KEY_FORMAT, merchantUser.getId().toString(), merchantOnBoardingAgreementFileName);
            String merchantOnboardingDocumentPath = "/tmp/" + merchantOnBoardingAgreementFileName;
            Util.generatePdfFromHtml(agreementHTMLContent, merchantOnboardingDocumentPath);
            convertHTMLToPdfAndUploadToS3Bucket(merchantOnboardingDocumentPath, merchantOnboardingAgreementS3BucketKey);
            String merchantOnBoardingAgreementUrl = getS3UrlFromS3Key(merchantOnboardingAgreementS3BucketKey);
            generateAgreementResponse = new GenerateAgreementResponse(0,
                    "SUCCESS", "AgreementDetails generated successfully", documentType,
                    merchantOnBoardingAgreementUrl, documentContent);
        }
        return generateAgreementResponse;
    }

    private void convertHTMLToPdfAndUploadToS3Bucket(String merchantOnboardingDocumentPath,
                                                     String s3BucketKey) {
        File pdfDest = new File(merchantOnboardingDocumentPath);
        s3UploadService.upload(s3BucketKey, pdfDest, "application/pdf");
    }

    private String getS3UrlFromS3Key(String kfsS3BucketKey) {
        return s3UploadService.getPreSignedURL(kfsS3BucketKey).get(0);
    }

}
