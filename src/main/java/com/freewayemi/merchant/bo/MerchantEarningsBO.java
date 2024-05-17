package com.freewayemi.merchant.bo;

import com.freewayemi.merchant.commons.bo.NotificationService;
import com.freewayemi.merchant.commons.exception.FreewayException;
import com.freewayemi.merchant.commons.utils.Util;
import com.freewayemi.merchant.dto.response.MerchantEarningsResponse;
import com.freewayemi.merchant.dto.response.PocketDetails;
import com.freewayemi.merchant.dto.response.ScratchCardResponse;
import com.freewayemi.merchant.entity.Earning;
import com.freewayemi.merchant.entity.MerchantUser;
import com.freewayemi.merchant.enums.EarningState;
import com.freewayemi.merchant.enums.EarningType;
import com.freewayemi.merchant.repository.MerchantEarningsRepository;
import com.freewayemi.merchant.repository.MerchantUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Component
public class MerchantEarningsBO {
    private final MerchantEarningsRepository merchantEarningsRepository;
    private final MerchantUserRepository merchantUserRepository;
    private final NotificationService notificationService;
    public static long REFERREDBY = 200l;
    public static long REFERRED = 100l;

    @Autowired
    public MerchantEarningsBO(MerchantEarningsRepository merchantEarningsRepository,
                              MerchantUserRepository merchantUserRepository,
                              NotificationService notificationService) {
        this.merchantEarningsRepository = merchantEarningsRepository;
        this.merchantUserRepository = merchantUserRepository;
        this.notificationService = notificationService;
    }

    public MerchantEarningsResponse getMerchantEarnings(String merchantId, String partner) {
        MerchantUser user = merchantUserRepository.findById(merchantId).orElse(null);
        MerchantUser partnerUser = null;
        String userMobile = user.getMobile();
        List<String> partners = user.getPartners();
        if (Util.isNotNull(partner) && Util.isNotNull(partners)) {
            try {
                if (Util.isNotNull(partners) && partners.contains(partner)) {
                    partnerUser = merchantUserRepository.findByMobileAndIsDeleted(userMobile + "_" + partner, false).orElse(null);
                    user = partnerUser;
                    merchantId = user.getId().toString();
                }
            } catch (Exception e) {
                throw new FreewayException(userMobile + " doesn't have any partner merchant with partner: " + partner);
            }
        }
        Optional<List<Earning>> responses = merchantEarningsRepository.findByMerchantId(merchantId);

        List<Earning> currentEarnings =
                responses.get().stream().filter(o -> o.getEarningState().equals(EarningState.scratched))
                        .filter(o -> o.getExpiryDate().truncatedTo(ChronoUnit.DAYS)
                                .isAfter(Instant.now().truncatedTo(ChronoUnit.DAYS)))
                        .collect(Collectors.toList());

        List<Earning> redeemedEarnings =
                responses.get().stream().filter(o -> o.getEarningState().equals(EarningState.redeemed))
                        .collect(Collectors.toList());

        List<Earning> expiredEarnings = responses.get().stream()
                .filter(o -> !o.getEarningState().equals(EarningState.redeemed))
                .filter(o -> o.getExpiryDate().truncatedTo(ChronoUnit.DAYS)
                        .isBefore(Instant.now().truncatedTo(ChronoUnit.DAYS)))
                .collect(Collectors.toList());

        List<Earning> scratchCards =
                responses.get().stream().filter(o -> o.getEarningState().equals(EarningState.unScratched))
                        .filter(o -> o.getExpiryDate().truncatedTo(ChronoUnit.DAYS)
                                .isAfter(Instant.now().truncatedTo(ChronoUnit.DAYS)))
                        .collect(Collectors.toList());

        PocketDetails pocketDetails = getPocketDetails(merchantId);
        return MerchantEarningsResponse.builder().currentEarnings(currentEarnings).expiredEarnings(expiredEarnings)
                .redeemedEarnings(redeemedEarnings).earnings(responses.get()).pocketDetails(pocketDetails)
                .scratchCards(scratchCards).build();
    }

    public PocketDetails getPocketDetails(String merchantId) {
        Optional<List<Earning>> optional = merchantEarningsRepository.findByMerchantId(merchantId);
        if (optional.isPresent()) {
            List<Earning> earnings = optional.get();
            Instant expiredDate = Instant.now().plus(7, ChronoUnit.DAYS);

            float redeemedAmount =
                    (float) earnings.stream().filter(o -> o.getEarningState().equals(EarningState.redeemed))
                            .mapToDouble(Earning::getAmount).sum();

            float totalAmount =
                    (float) earnings.stream().filter(o -> !o.getEarningState().equals(EarningState.unScratched))
                            .mapToDouble(Earning::getAmount).sum();

            float currentAmount =
                    (float) earnings.stream().filter(o -> !o.getEarningState().equals(EarningState.redeemed))
                            .filter(o -> !o.getEarningState().equals(EarningState.unScratched))
                            .filter(o -> o.getExpiryDate().truncatedTo(ChronoUnit.DAYS)
                                    .isAfter(expiredDate.truncatedTo(ChronoUnit.DAYS)))
                            .mapToDouble(Earning::getAmount)
                            .sum();

            float expiringAmount =
                    (float) earnings.stream().filter(o -> !o.getEarningState().equals(EarningState.redeemed))
                            .filter(o -> o.getExpiryDate().truncatedTo(ChronoUnit.DAYS)
                                    .isBefore(expiredDate.truncatedTo(ChronoUnit.DAYS)))
                            .filter(o -> o.getExpiryDate().truncatedTo(ChronoUnit.DAYS)
                                    .isAfter(Instant.now().truncatedTo(ChronoUnit.DAYS)))
                            .mapToDouble(Earning::getAmount).sum();


            List<Earning> scratchCards = new ArrayList<>();//getScratchCardList(merchantId, earnings);

            return PocketDetails.builder().totalEarnings(totalAmount).currentEarnings(currentAmount)
                    .earningsRedeemed(redeemedAmount).expiringEarnings(expiringAmount).scratchCards(scratchCards)
                    .build();
        }
        return PocketDetails.builder().totalEarnings(0f).currentEarnings(0f)
                .earningsRedeemed(0f).expiringEarnings(0f).scratchCards(Collections.singletonList(daily(merchantId)))
                .build();
    }

    private List<Earning> getScratchCardList(String merchantId, List<Earning> earnings) {
        List<Earning> scratchCards = earnings.stream()
                .filter(o -> o.getExpiryDate().truncatedTo(ChronoUnit.DAYS)
                        .isAfter(Instant.now().truncatedTo(ChronoUnit.DAYS)))
                .filter(o -> o.getEarningState().equals(EarningState.unScratched)).collect(Collectors.toList());

        ScratchCardResponse resp = isScratchCard(merchantId);
        if (!resp.isScratched()) {
            scratchCards.add(daily(merchantId));
        }
        return scratchCards;
    }

    private Earning daily(String merchantId) {
        Earning earning = new Earning();
        earning.setScratchCardId(EarningType.daily.name());
        earning.setEarningState(EarningState.unScratched);
        earning.setEarningType(EarningType.daily);
        earning.setMerchantId(merchantId);
        return earning;
    }


    public void createMerchantEarnings(String merchantId, String referalMerchantId, String type, long amount) {
        Earning earnings = new Earning();
        earnings.setScratchCardId(UUID.randomUUID().toString());
        earnings.setAmount(amount);
        earnings.setMerchantId(merchantId);
        earnings.setReferralMerchantId(referalMerchantId);
        earnings.setEarningType(EarningType.valueOf(type));
        earnings.setEarningState(EarningState.unScratched);
        earnings.setCreatedDate(Instant.now());
        earnings.setExpiryDate(ZonedDateTime.now(ZoneOffset.UTC).plusYears(1).toInstant());
        merchantEarningsRepository.save(earnings);
    }

    public ScratchCardResponse isScratchCard(String merchantId) {
        List<Earning> earnings =
                merchantEarningsRepository.findByMerchantIdAndEarningType(merchantId, EarningType.daily)
                        .orElse(new ArrayList<>());
        if (earnings.isEmpty()) {
            return ScratchCardResponse.builder().isScratched(false).build();
        }
        earnings.sort(Comparator.comparing(Earning::getCreatedDate).reversed());
        Instant date = earnings.get(0).getCreatedDate().truncatedTo(ChronoUnit.DAYS);
        if (date.equals(Instant.now().truncatedTo(ChronoUnit.DAYS))) {
            return ScratchCardResponse.builder().isScratched(true).build();
        }
        return ScratchCardResponse.builder().isScratched(false).build();
    }

    public ScratchCardResponse scratchCard(String merchantId, String scratchCardId, String partner) {
        MerchantUser user = merchantUserRepository.findById(merchantId).orElse(null);
        MerchantUser partnerUser = null;
        String userMobile = user.getMobile();
        List<String> partners = user.getPartners();
        if (Util.isNotNull(partner) && Util.isNotNull(partners)) {
            try {
                if (Util.isNotNull(partners) && partners.contains(partner)) {
                    partnerUser = merchantUserRepository.findByMobileAndIsDeleted(userMobile + "_" + partner, false).orElse(null);
                    user = partnerUser;
                    merchantId = user.getId().toString();
                }
            } catch (Exception e) {
                throw new FreewayException(userMobile + " doesn't have any partner merchant with partner: " + partner);
            }
        }
        if (scratchCardId.equals("daily")) {
            ScratchCardResponse resp = isScratchCard(merchantId);
            if (!resp.isScratched()) {
                int amount = getAmount(merchantId);
                Earning scratchCard = new Earning();
                scratchCard.setScratchCardId(UUID.randomUUID().toString());
                scratchCard.setAmount(amount);
                scratchCard.setMerchantId(merchantId);
                scratchCard.setEarningType(EarningType.daily);
                scratchCard.setEarningState(EarningState.scratched);
                scratchCard.setCreatedDate(Instant.now());
                scratchCard.setScratchedDate(Instant.now());
                scratchCard.setExpiryDate(Instant.now().plus(30, ChronoUnit.DAYS));
                merchantEarningsRepository.save(scratchCard);
                return ScratchCardResponse.builder().amount(amount).isScratched(true).build();
            }
        } else {
            Optional<Earning> optional = merchantEarningsRepository.findByScratchCardId(scratchCardId);
            if (optional.isPresent()) {
                Earning earning = optional.get();
                earning.setEarningState(EarningState.scratched);
                earning.setScratchedDate(Instant.now());
                merchantEarningsRepository.save(earning);
                return ScratchCardResponse.builder().amount(earning.getAmount()).isScratched(true).build();
            }
        }

        return null;
    }

    private int getAmount(String merchantId) {
        List<Earning> earnings = merchantEarningsRepository.findByMerchantId(merchantId).orElse(new ArrayList<>());

        Boolean initialUser = false;
        if (earnings.isEmpty() || earnings.size() == 1 || earnings.size() == 2) {
            initialUser = true;
        }

        LocalDate now = LocalDate.now();
        LocalDate startDate = now.withDayOfMonth(1);
        Instant startOfTheMonth = startDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Boolean noScratchinWeek = true;
        Boolean noScratchinEightDays = true;
        Instant weekBefore = Instant.now().truncatedTo(ChronoUnit.DAYS).minus(7, ChronoUnit.DAYS);
        Instant eightDaysBefore = Instant.now().truncatedTo(ChronoUnit.DAYS).minus(8, ChronoUnit.DAYS);

        List<Earning> lastweekData = earnings.stream().filter(o -> !o.getEarningState().equals(EarningState.redeemed))
                .filter(o -> o.getEarningType().equals(EarningType.daily))
                .filter(o -> o.getCreatedDate().truncatedTo(ChronoUnit.DAYS)
                        .isBefore(Instant.now().truncatedTo(ChronoUnit.DAYS)))
                .filter(o -> o.getCreatedDate().truncatedTo(ChronoUnit.DAYS).isAfter(weekBefore))
                .collect(Collectors.toList());


        for (Earning earning : lastweekData) {
            if (!earning.getEarningState().equals(EarningState.unScratched)) {
                noScratchinWeek = false;
                break;
            }
        }
        List<Earning> lastweekDataPlusoneDay =
                earnings.stream().filter(o -> !o.getEarningState().equals(EarningState.redeemed))
                        .filter(o -> o.getCreatedDate().truncatedTo(ChronoUnit.DAYS)
                                .isBefore(Instant.now().truncatedTo(ChronoUnit.DAYS).minus(1, ChronoUnit.DAYS)))
                        .filter(o -> o.getCreatedDate().truncatedTo(ChronoUnit.DAYS).isAfter(eightDaysBefore))
                        .collect(Collectors.toList());
        for (Earning earning : lastweekDataPlusoneDay) {
            if (!earning.getEarningState().equals(EarningState.unScratched)) {
                noScratchinEightDays = false;
                break;
            }
        }
        float totalMonthAmount =
                (float) earnings.stream().filter(o -> !o.getEarningState().equals(EarningState.redeemed))
                        .filter(o -> o.getCreatedDate().truncatedTo(ChronoUnit.DAYS)
                                .isBefore(Instant.now().truncatedTo(ChronoUnit.DAYS)))
                        .filter(o -> o.getCreatedDate().truncatedTo(ChronoUnit.DAYS)
                                .isAfter(startOfTheMonth.truncatedTo(ChronoUnit.DAYS)))
                        .mapToDouble(o -> o.getAmount()).sum();

        int randomNum = ThreadLocalRandom.current().nextInt(0, 1 + 1);

        if ((noScratchinEightDays || noScratchinWeek || initialUser) && totalMonthAmount < 150) {
            return ThreadLocalRandom.current().nextInt(10, 20 + 1);
        } else if (randomNum == 1 && totalMonthAmount < 150) {
            return ThreadLocalRandom.current().nextInt(5, 10 + 1);
        } else {
            return 0;
        }
    }

    @Async
    public void updateEarnings(String merchantId, String transactionId) {
        Optional<MerchantUser> optional = merchantUserRepository.findById(merchantId);
        if (optional.isPresent()) {
            MerchantUser mu = optional.get();
            MerchantUser referee = merchantUserRepository.findByReferralCode(mu.getReferredBy())
                    .orElseThrow(() -> new FreewayException("Referee not found"));
            Earning referredTo = merchantEarningsRepository
                    .findByMerchantIdAndReferralMerchantId(merchantId, referee.getId().toString())
                    .orElseThrow(() -> new FreewayException("No entry found for merchant id" + merchantId));
            Earning referredBy = merchantEarningsRepository
                    .findByMerchantIdAndReferralMerchantId(referee.getId().toString(), merchantId)
                    .orElseThrow(() -> new FreewayException("No entry found for merchant id" + referee));
            if (referredTo != null && referredBy != null) {
                referredTo.setEarningState(EarningState.redeemable);
                referredTo.setRedeemableDate(Instant.now());
                referredTo.setTxnId(transactionId);
                merchantEarningsRepository.save(referredTo);
                feedAnalytics(mu, referredTo);
                referredBy.setEarningState(EarningState.redeemable);
                referredBy.setRedeemableDate(Instant.now());
                referredTo.setTxnId(transactionId);
                merchantEarningsRepository.save(referredBy);
                feedAnalytics(mu, referredBy);
            }
        }
    }

    @Async
    public void updateDailyEarnings(String merchantId, String transactionId) {
        Optional<MerchantUser> opt = merchantUserRepository.findById(merchantId);
        if (opt.isPresent()) {
            MerchantUser mu = opt.get();
            Optional<List<Earning>>
                    optional = merchantEarningsRepository.findByMerchantIdAndEarningType(merchantId, EarningType.daily);
            if (optional.isPresent()) {
                List<Earning> earnings = optional.get();
                for (Earning earning : earnings) {
                    if (EarningState.scratched.equals(earning.getEarningState()) &&
                            Instant.now().isBefore(earning.getExpiryDate())) {
                        earning.setEarningState(EarningState.redeemable);
                        earning.setRedeemableDate(Instant.now());
                        earning.setTxnId(transactionId);
                        merchantEarningsRepository.save(earning);
                        feedAnalytics(mu, earning);
                    }
                }
            }
        }
    }

    private void feedAnalytics(MerchantUser mu, Earning earning) {
        try {
            Map<String, String> map = new HashMap<>();
            map.put("project", "merchant");
            map.put("mobile", mu.getMobile());
            map.put("time", String.valueOf(System.currentTimeMillis()));
            map.put("name", "scratchcard.redeemable.1.0");
            Map<String, String> eventProps = new HashMap<>();
            eventProps.put("amount", String.valueOf(earning.getAmount()));
            eventProps.put("earningState", earning.getEarningState().name());
            eventProps.put("earningType", earning.getEarningType().name());
            eventProps.put("expiryDate", null == earning.getExpiryDate() ? null : earning.getExpiryDate().toString());
            eventProps.put("scratchedDate", null == earning.getScratchedDate() ? null : earning.getScratchedDate().toString());
            eventProps.put("redeemedDate", null == earning.getRedeemedDate() ? null : earning.getRedeemedDate().toString());
            eventProps.put("redeemableDate", null == earning.getRedeemableDate() ? null : earning.getRedeemableDate().toString());
            Map<String, String> userProps = new HashMap<>();
            userProps.put("mobile", mu.getMobile());
            userProps.put("email", mu.getEmail());
            notificationService.sendAnalyticsFeed(map, eventProps, userProps);
        } catch (Exception e) {

        }
    }

    public Long getLastRedeemedAmount(String merchantId){
        Optional<List<Earning>> responses = merchantEarningsRepository.findByMerchantId(merchantId);
        long redeemedAmount = 0;
        if(responses.isPresent()) {
            for (Earning earning : responses.get()) {
                if (EarningState.redeemed.equals(earning.getEarningState()) && !StringUtils.isEmpty(earning.getToBeNotified()) && earning.getToBeNotified()) {
                    redeemedAmount += earning.getAmount();
                }
            }
        }
        return redeemedAmount;
    }

    public void earningsNotified(String merchantId){
        Optional<List<Earning>> responses = merchantEarningsRepository.findByMerchantId(merchantId);
        if(responses.isPresent()) {
            for (Earning earning : responses.get()) {
                if (EarningState.redeemed.equals(earning.getEarningState()) && !StringUtils.isEmpty(earning.getToBeNotified()) && earning.getToBeNotified()) {
                    earning.setToBeNotified(false);
                    earning.setNotifiedAt(Instant.now());
                    merchantEarningsRepository.save(earning);
                }
            }
        }
    }

}
