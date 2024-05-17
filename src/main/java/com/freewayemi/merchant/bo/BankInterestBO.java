package com.freewayemi.merchant.bo;

import com.freewayemi.merchant.BankInterestResponse;
import com.freewayemi.merchant.commons.dto.BrandInfo;
import com.freewayemi.merchant.commons.dto.offer.BankInterestDto;
import com.freewayemi.merchant.commons.dto.offer.InterestPerTenureDto;
import com.freewayemi.merchant.commons.exception.FreewayCustomException;
import com.freewayemi.merchant.commons.exception.FreewayException;
import com.freewayemi.merchant.commons.type.*;
import com.freewayemi.merchant.commons.utils.Util;
import com.freewayemi.merchant.dto.request.BankInterestRequest;
import com.freewayemi.merchant.dto.request.InterestPerTenureRequest;
import com.freewayemi.merchant.entity.BankInterest;
import com.freewayemi.merchant.entity.InterestPerTenure;
import com.freewayemi.merchant.repository.BankInterestRepository;
import org.apache.commons.lang.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class BankInterestBO {

    private final static Logger logger = LoggerFactory.getLogger(BankInterestBO.class);

    private final BankInterestRepository bankInterestRepository;

    @Autowired
    public BankInterestBO(BankInterestRepository bankInterestRepository) {
        this.bankInterestRepository = bankInterestRepository;
    }

    public BankInterestDto getBankInterestByBrandInfo(BrandInfo brandInfo) {
        if (Util.isNotNull(brandInfo) && StringUtils.hasText(brandInfo.getBrandId())) {
            BankInterest bankInterestOnBrand = getBankInterestByBrandInfo(brandInfo.getBrandId());
            if (Util.isNotNull(bankInterestOnBrand)) {
                return convertToBankInterestCardDto(bankInterestOnBrand, null, brandInfo.getBrandDisplayId());
            }
        }
        return null;
    }

    public BankInterestDto getBankInterestByMerchantId(String merchantId, String merchantDisplayId) {
        if (StringUtils.hasText(merchantId)) {
            BankInterest bankInterestOnMerchant = getBankInterestByMerchantId(merchantId);
            if (Util.isNotNull(bankInterestOnMerchant)) {
                return convertToBankInterestCardDto(bankInterestOnMerchant, merchantDisplayId, null);
            }
        }
        return null;
    }

    private BankInterest getBankInterestByBrandInfo(String brandId) {
        return bankInterestRepository.findByBrandId(brandId).orElse(null);
    }

    private BankInterest getBankInterestByMerchantId(String merchantId) {
        return bankInterestRepository.findByMerchantId(merchantId).orElse(null);
    }

    private BankInterestDto convertToBankInterestCardDto(BankInterest bankInterest, String merchantDisplayId, String brandDisplayId) {
        return BankInterestDto.builder()
                .merchantDisplayId(merchantDisplayId)
                .brandDisplayId(brandDisplayId)
                .isActive(bankInterest.getIsActive())
                .interestPerTenureDtos(convertToInterestPerTenureDtos(bankInterest.getInterestPerTenures(),
                        Util.isNotNull(bankInterest.getBankInterestType()) ?
                                bankInterest.getBankInterestType() : StringUtils.hasText(merchantDisplayId) ?
                                BankInterestTypeEnum.CUSTOM_MERCHANT : BankInterestTypeEnum.CUSTOM_BRAND))
//                .validFrom(StringUtils.hasText(bankInterest.getValidFrom()) ?
//                        DateUtil.convertStringToInstantInUtc("dd/MM/yyyy",
//                                DateUtil.getDateInGenericFormat(bankInterest.getValidFrom(), "dd/MM/yyyy")) : null)
//                .validTo(StringUtils.hasText(bankInterest.getValidTo()) ?
//                        DateUtil.convertStringToInstantInUtc("dd/MM/yyyy",
//                                DateUtil.getDateInGenericFormat(bankInterest.getValidTo(), "dd/MM/yyyy")) : null)
                .validFrom(bankInterest.getValidFrom())
                .validTo(bankInterest.getValidTo())
                .build();
    }

    private List<InterestPerTenureDto> convertToInterestPerTenureDtos(List<InterestPerTenure> interestPerTenures, BankInterestTypeEnum bankInterestTypeEnum) {
        if (!CollectionUtils.isEmpty(interestPerTenures)) {
            return interestPerTenures.stream().map(interestPerTenure -> InterestPerTenureDto.builder()
                    .cardInterestId(interestPerTenure.getCardInterestId())
                    .bankEnum(interestPerTenure.getBankCode())
                    .brandIrr(interestPerTenure.getBrandIrr())
                    .irr(interestPerTenure.getIrr())
                    .calculationType(interestPerTenure.getCalculationType())
                    .tenure(interestPerTenure.getTenure())
                    .cardType(interestPerTenure.getCardType())
                    .maxAmount(interestPerTenure.getMaxAmount())
                    .tenureInDays(interestPerTenure.getTenureInDays())
                    .minAmount(interestPerTenure.getMinAmount())
                    .providerSchemeCode(interestPerTenure.getProviderSchemeCode())
                    .providerDetailedSchemeCode(interestPerTenure.getProviderDetailedSchemeCode())
                    .isActive(interestPerTenure.getIsActive())
//                    .validFrom(StringUtils.hasText(interestPerTenure.getValidFrom()) ?
//                            DateUtil.convertStringToInstantInUtc("dd/MM/yyyy",
//                                    DateUtil.getDateInGenericFormat(interestPerTenure.getValidFrom(), "dd/MM/yyyy")) : null)
//                    .validTo(StringUtils.hasText(interestPerTenure.getValidTo()) ?
//                            DateUtil.convertStringToInstantInUtc("dd/MM/yyyy",
//                                    DateUtil.getDateInGenericFormat(interestPerTenure.getValidTo(), "dd/MM/yyyy")) : null)
                    .validFrom(interestPerTenure.getValidFrom())
                    .validTo(interestPerTenure.getValidTo())
                    .bankPfInPercentage(interestPerTenure.getBankPfInPercentage())
                    .bankPfMaxAmount(interestPerTenure.getBankPfMaxAmount())
                    .bankPfFlatAmount(interestPerTenure.getBankPfFlatAmount())
                    .bankInterestTypeEnum(bankInterestTypeEnum)
                    .build()).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    public List<InterestPerTenureDto> getInterestPerTenureDtoByBankAndCardType(BankInterestDto bankInterestDto, CardTypeEnum cardType, BankEnum bankCode) {
        Instant currentDate = Instant.now();
        if (BooleanUtils.isFalse(bankInterestDto.getIsActive()) ||
                CollectionUtils.isEmpty(bankInterestDto.getInterestPerTenureDtos()) ||
                !Util.isUnderValidTimePeriod(currentDate, bankInterestDto.getValidFrom(), bankInterestDto.getValidTo())) {
            return null;
        }
        return bankInterestDto.getInterestPerTenureDtos().stream()
                .filter(interestPerTenureDto -> !BooleanUtils.isFalse(interestPerTenureDto.getIsActive()))
                .filter(interestPerTenureDto -> Util.isUnderValidTimePeriod(currentDate, interestPerTenureDto.getValidFrom(), interestPerTenureDto.getValidTo()))
                .filter(interestPerTenureDto -> cardType.equals(interestPerTenureDto.getCardType())
                        && bankCode.equals(interestPerTenureDto.getBankEnum())).collect(Collectors.toList());
    }

    public InterestPerTenureDto getInterestPerTenureDtoByBankAndCardTypeAndTenure(BankInterestDto bankInterestDto, CardTypeEnum cardType, BankEnum bankCode, Integer tenure) {
        Instant currentDate = Instant.now();
        if (BooleanUtils.isFalse(bankInterestDto.getIsActive()) ||
                CollectionUtils.isEmpty(bankInterestDto.getInterestPerTenureDtos()) ||
                !Util.isUnderValidTimePeriod(currentDate, bankInterestDto.getValidFrom(), bankInterestDto.getValidTo())) {
            return null;
        }
        List<InterestPerTenureDto> interestPerTenureDtos = bankInterestDto.getInterestPerTenureDtos().stream()
                .filter(interestPerTenureDto -> !BooleanUtils.isFalse(interestPerTenureDto.getIsActive()))
                .filter(interestPerTenureDto -> Util.isUnderValidTimePeriod(currentDate, interestPerTenureDto.getValidFrom(), interestPerTenureDto.getValidTo()))
                .filter(interestPerTenureDto -> cardType.equals(interestPerTenureDto.getCardType())
                        && bankCode.equals(interestPerTenureDto.getBankEnum())
                        && tenure.equals(interestPerTenureDto.getTenure())
                ).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(interestPerTenureDtos)) {
            return null;
        }
        if (interestPerTenureDtos.size() > 1) {
            //LOGGER.error(TransactionCode.FAILED_183.getDashboardStatusMsg() + " merchant display id: " + bankInterestCardDto.getMerchantDisplayId() + " brand display id: " + bankInterestCardDto.getBrandDisplayId())
            throw new FreewayCustomException(TransactionCode.FAILED_184);
        }
        return interestPerTenureDtos.get(0);
    }


    public BankInterestResponse createOrUpdateBankInterest(BankInterestRequest bankInterestRequest) {
        BankInterestResponse response = null;
        try {
            validateBankInterestRequest(bankInterestRequest);
            BankInterest bankInterest = getBankInterestByBrandIdOrMerchantId(bankInterestRequest.getBrandId(), bankInterestRequest.getMerchantId());
            if (Util.isNull(bankInterest)) {
                bankInterest = createBankInterest(bankInterestRequest);
            } else {
                updateBankInterest(bankInterest, bankInterestRequest);
            }
            bankInterestRepository.save(bankInterest);
            response = new BankInterestResponse(0, "Success", "Successful ", convertToBankInterestCardDto(bankInterest, "", ""));
        } catch (FreewayException e) {
            logger.error("FreewayException occurred as: ", e);
            response = new BankInterestResponse(20, "failed", e.getMessage(), null);
        } catch (Exception e) {
            logger.error("Exception occurred as: ", e);
        }
        if (Util.isNull(response)) {
            response = new BankInterestResponse(20, "failed", "Failed to insert or update the file.", null);
        }
        return response;
    }

    private void updateBankInterest(BankInterest bankInterest, BankInterestRequest bankInterestRequest) {
        bankInterest.setIsActive((Boolean) compareObjectField(bankInterest.getIsActive(), bankInterestRequest.getIsActive()));
        bankInterest.setValidFrom((Instant) compareObjectField(bankInterest.getValidFrom(), bankInterestRequest.getValidFrom()));
        bankInterest.setValidTo((Instant) compareObjectField(bankInterest.getValidTo(), bankInterestRequest.getValidTo()));
        bankInterest.setUpdatedBy(bankInterestRequest.getUpdatedBy());
        bankInterest.setInterestPerTenures(convertInterestPerTenureDtoListToInterestPerTenureList(bankInterestRequest.getInterestPerTenures()));
    }

    private BankInterest createBankInterest(BankInterestRequest bankInterestRequest) {
        return convertBankInterestRequestToBankInterest(bankInterestRequest);
    }

    private void validateBankInterestRequest(BankInterestRequest bankInterestRequest) {
        Set<String> uniqueEntries = new HashSet<>();
        if (Util.isNull(bankInterestRequest)) {
            throw new FreewayException("Invalid request!");
        }
        if (Util.isNull(bankInterestRequest.getIsActive())) {
            throw new FreewayException("Bank interest has no active flag!");
        }
        if (StringUtils.isEmpty(bankInterestRequest.getBrandId()) && StringUtils.isEmpty(bankInterestRequest.getMerchantId())) {
            throw new FreewayException("Bank interest is not attached with any brand or merchant!");
        }
        if (!CollectionUtils.isEmpty(bankInterestRequest.getInterestPerTenures())) {
            bankInterestRequest.getInterestPerTenures().forEach(interestPerTenureRequest -> {
                if (Util.isNull(interestPerTenureRequest.getIsActive())) {
                    throw new FreewayException("Bank interest has no active flag!");
                }
                if (Util.isNull(interestPerTenureRequest.getBankCode())) {
                    throw new FreewayException("Bank code should not be null!");
                }
                if (Util.isNull(interestPerTenureRequest.getCardType())) {
                    throw new FreewayException("Card type should not be null!");
                }
                if (Util.isNull(interestPerTenureRequest.getTenure())) {
                    throw new FreewayException("Invalid tenure!");
                }
                if (Util.isNull(interestPerTenureRequest.getIrr()) && Util.isNull(interestPerTenureRequest.getBrandIrr())) {
                    throw new FreewayException("Invalid bank interest value!");
                }
                String cardTypeBankCodeTenure = interestPerTenureRequest.getCardType().getCardType() +
                        interestPerTenureRequest.getBankCode().getCode() + interestPerTenureRequest.getTenure();
                if (BooleanUtils.isTrue(interestPerTenureRequest.getIsActive())) {
                    if (uniqueEntries.contains(cardTypeBankCodeTenure)) {
                        throw new FreewayException("Invalid request multiple active entries found for bank: " + interestPerTenureRequest.getBankCode().getCode()
                                + " card type: " + interestPerTenureRequest.getCardType().getCardType()
                                + " tenure: " + interestPerTenureRequest.getTenure());
                    }
                    uniqueEntries.add(cardTypeBankCodeTenure);
                }
            });
        }
    }

    public String compareStringField(String oldValue, String newValue) {
        if (StringUtils.isEmpty(oldValue)) {
            if (StringUtils.hasText(newValue)) {
                return newValue;
            }
        } else {
            if (!oldValue.equalsIgnoreCase(newValue)) {
                return newValue;
            }
        }
        return oldValue;
    }

    public Object compareObjectField(Object oldValue, Object newValue) {
        if (Util.isNull(oldValue)) {
            if (Util.isNotNull(newValue)) {
                return newValue;
            }
        } else {
            if (!oldValue.equals(newValue)) {
                return newValue;
            }
        }
        return oldValue;
    }

    public BankInterestResponse getBankInterest(BankInterestRequest bankInterestRequest) {
        BankInterestResponse response = null;
        BankInterest bankInterest = getBankInterestByBrandIdOrMerchantId(bankInterestRequest.getBrandId(), bankInterestRequest.getMerchantId());
        if (Util.isNotNull(bankInterest)) {
            response = new BankInterestResponse(0, "Success", "Successful ", convertToBankInterestCardDto(bankInterest, "", ""));
        }
        if (Util.isNull(response)) {
            response = new BankInterestResponse(21, "failed", "Bank interests not found!", null);
        }
        return response;
    }

    private BankInterest getBankInterestByBrandIdOrMerchantId(String brandId, String merchantId) {
        BankInterest bankInterest = null;
        if (StringUtils.hasText(brandId)) {
            bankInterest = bankInterestRepository.findByBrandId(brandId).orElse(null);
        } else if (StringUtils.hasText(merchantId)) {
            bankInterest = bankInterestRepository.findByMerchantId(merchantId).orElse(null);
        }
        return bankInterest;
    }

    public List<BankEnum> getBanks() {
        return Arrays.asList(BankEnum.values());
    }

    public List<CardTypeEnum> getCardTypes() {
        return Arrays.asList(CardTypeEnum.values());
    }

    public List<Integer> getEmiTenures() {
        return Arrays.stream(EmiTenureEnum.values()).map(EmiTenureEnum::getMonth).sorted().collect(Collectors.toList());
    }

    private BankInterest convertBankInterestRequestToBankInterest(BankInterestRequest bankInterestRequest) {
        BankInterest bankInterest = new BankInterest();
        bankInterest.setBrandId(bankInterestRequest.getBrandId());
        bankInterest.setMerchantId(bankInterestRequest.getMerchantId());
        bankInterest.setValidTo(bankInterestRequest.getValidTo());
        bankInterest.setValidFrom(bankInterestRequest.getValidFrom());
        bankInterest.setIsActive(bankInterestRequest.getIsActive());
        bankInterest.setInterestPerTenures(convertInterestPerTenureDtoListToInterestPerTenureList(bankInterestRequest.getInterestPerTenures()));
        bankInterest.setCreatedBy(bankInterestRequest.getCreatedBy());
        bankInterest.setUpdatedBy(bankInterestRequest.getUpdatedBy());
        return bankInterest;
    }

    private List<InterestPerTenure> convertInterestPerTenureDtoListToInterestPerTenureList(List<InterestPerTenureRequest> interestPerTenureRequests) {
        if (CollectionUtils.isEmpty(interestPerTenureRequests)) {
            return null;
        }
        return interestPerTenureRequests.stream().map(interestPerTenureDto -> {
            InterestPerTenure interestPerTenure = new InterestPerTenure();
            interestPerTenure.setCardInterestId(interestPerTenureDto.getCardInterestId());
            interestPerTenure.setBankCode(interestPerTenureDto.getBankCode());
            interestPerTenure.setCardType(interestPerTenureDto.getCardType());
            interestPerTenure.setTenure(interestPerTenureDto.getTenure());
            interestPerTenure.setTenureInDays(interestPerTenureDto.getTenureInDays());

            interestPerTenure.setIrr(interestPerTenureDto.getIrr());
            interestPerTenure.setBrandIrr(interestPerTenureDto.getBrandIrr());

            interestPerTenure.setProviderSchemeCode(interestPerTenureDto.getProviderSchemeCode());
            interestPerTenure.setProviderDetailedSchemeCode(interestPerTenureDto.getProviderDetailedSchemeCode());

            interestPerTenure.setIsActive(interestPerTenureDto.getIsActive());

            interestPerTenure.setValidFrom(interestPerTenureDto.getValidFrom());
            interestPerTenure.setValidTo(interestPerTenureDto.getValidTo());

            interestPerTenure.setMaxAmount(interestPerTenureDto.getMaxAmount());
            interestPerTenure.setMinAmount(interestPerTenureDto.getMinAmount());
            interestPerTenure.setBankPfFlatAmount(interestPerTenureDto.getBankPfFlatAmount());
            interestPerTenure.setBankPfMaxAmount(interestPerTenureDto.getBankPfMaxAmount());
            interestPerTenure.setBankPfInPercentage(interestPerTenureDto.getBankPfInPercentage());

            return interestPerTenure;
        }).collect(Collectors.toList());
    }

}
