package com.freewayemi.merchant.bo;

import com.freewayemi.merchant.commons.exception.FreewayException;
import com.freewayemi.merchant.commons.utils.EncryptionUtil;
import com.freewayemi.merchant.commons.utils.paymentConstants;
import com.freewayemi.merchant.commons.utils.Util;
import com.freewayemi.merchant.entity.MerchantSession;
import com.freewayemi.merchant.repository.MerchantSessionsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class MerchantSessionBO {

    private static final Logger LOGGER = LoggerFactory.getLogger(MerchantSessionBO.class);


    private final MerchantSessionsRepository merchantSessionsRepository;
    private final String paymentSecretKey;

    @Autowired
    public MerchantSessionBO(MerchantSessionsRepository merchantSessionsRepository,
                             @Value("${payment.secret.key}") String paymentSecretKey) {
        this.merchantSessionsRepository = merchantSessionsRepository;
        this.paymentSecretKey = paymentSecretKey;
    }


    public void logoutUser(String merchantId) {
        List<MerchantSession> sessions = merchantSessionsRepository.findByMerchantIdAndInvalid(merchantId, false)
                .orElseThrow(() -> new FreewayException("No merchant found"));
        for (MerchantSession session : sessions) {
            session.setInvalid(true);
            merchantSessionsRepository.save(session);
        }
    }

    public void logoutStoreUser(String userId) {
        List<MerchantSession> sessions = merchantSessionsRepository.findByUserAndInvalid(userId, false)
                .orElseThrow(() -> new FreewayException("No user found"));
        for (MerchantSession session : sessions) {
            session.setInvalid(true);
            merchantSessionsRepository.save(session);
        }
    }

    public void logoutStoreUserIfExists(String userId) {
        List<MerchantSession> sessions = merchantSessionsRepository.findByUserAndInvalid(userId, false)
                .orElse(new ArrayList<>());
        for (MerchantSession session : sessions) {
            session.setInvalid(true);
            merchantSessionsRepository.save(session);
        }
    }

    public void saveSession(String merchantId, String mobile, String jwtToken) {
        saveSession(merchantId, mobile, jwtToken, "");
    }

    public void saveSession(String merchantId, String mobile, String jwtToken, String userId) {
        Optional<List<MerchantSession>> optional = !StringUtils.isEmpty(mobile) ?
                merchantSessionsRepository.findByMerchantIdAndMobileAndInvalid(merchantId, mobile, false) :
                merchantSessionsRepository.findByMerchantIdAndInvalid(merchantId, false);
        if (optional.isPresent()) {
            List<MerchantSession> sessions = optional.get();
            if (!sessions.isEmpty()) {
                for (MerchantSession session : sessions) {
                    session.setInvalid(true);
                    if (!"9000000021".equals(mobile)) {
                        merchantSessionsRepository.save(session);
                    }
                }
            }
        }
        MerchantSession newMerchantSession = new MerchantSession();
        newMerchantSession.setMerchantId(merchantId);
        newMerchantSession.setInvalid(false);
        newMerchantSession.setMobile(mobile);
        newMerchantSession.setToken(Util.md5(jwtToken));
        newMerchantSession.setMobileVerified(true);
        newMerchantSession.setCreatedDate(Instant.now());
        newMerchantSession.setLastActivityDate(Instant.now());
        newMerchantSession.setUser(userId);
        try {
            newMerchantSession.setPassword(
                    EncryptionUtil.encrypt(jwtToken, paymentSecretKey, paymentConstants.payment_IV_KEY, "Hex", null));
        } catch (Exception e) {
            throw new FreewayException("Oops, something went wrong!");
        }
        merchantSessionsRepository.save(newMerchantSession);
    }

    public void saveSession(String brandDisplayId, String jwtToken) {
        Optional<List<MerchantSession>> optionalMerchantSessionList = merchantSessionsRepository.findByBrandDisplayIdAndInvalid(brandDisplayId, false);
        if (optionalMerchantSessionList.isPresent()) {
            List<MerchantSession> merchantSessionList = optionalMerchantSessionList.get();
            if (!merchantSessionList.isEmpty()) {
                for (MerchantSession merchantSession : merchantSessionList) {
                    merchantSession.setInvalid(true);
                    merchantSessionsRepository.save(merchantSession);
                }
            }
        }
        MerchantSession merchantSession = new MerchantSession();
        merchantSession.setInvalid(false);
        merchantSession.setBrandDisplayId(brandDisplayId);
        merchantSession.setToken(Util.md5(jwtToken));
        merchantSession.setCreatedDate(Instant.now());
        merchantSession.setLastActivityDate(Instant.now());
        try {
            merchantSession.setPassword(
                    EncryptionUtil.encrypt(jwtToken, paymentSecretKey, paymentConstants.payment_IV_KEY, "Hex", null));
        } catch (Exception e) {
            throw new FreewayException("Oops, something went wrong!");
        }
        merchantSessionsRepository.save(merchantSession);
    }

    public Optional<List<MerchantSession>> findByMerchantIdAndInvalid(String merchantId, Boolean invalid) {
        return merchantSessionsRepository.findByMerchantIdAndInvalid(merchantId, invalid);
    }

    public String getToken(MerchantSession merchantSession) {
        try {
            return EncryptionUtil.decrypt(merchantSession.getPassword(), paymentSecretKey, paymentConstants.payment_IV_KEY,
                    "Hex", null);
        } catch (Exception e) {
            throw new FreewayException("Oops, something went wrong!");
        }
    }

    public MerchantSession getMerchantSession(String token) {
        if (StringUtils.hasText(token)) {
            token = token.substring(7);
            Optional<MerchantSession> merchantSession = merchantSessionsRepository.findByToken(Util.md5(token));
            LOGGER.info("merchantSession: {} {}", Util.md5(token), merchantSession);
            return merchantSession.orElse(null);
        }
        return null;
    }

    public void saveMerchantSession(MerchantSession session) {
        merchantSessionsRepository.save(session);
    }

    public List<MerchantSession> findByUser(String userName, Pageable pageable) {
        return merchantSessionsRepository.findByUser(userName, pageable).orElse(new ArrayList<>());
    }

    public MerchantSession findById(String id) {
        return merchantSessionsRepository.findById(id).orElse(null);
    }
}
