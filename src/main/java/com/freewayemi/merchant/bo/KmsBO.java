package com.freewayemi.merchant.bo;

import com.amazonaws.encryptionsdk.AwsCrypto;
import com.amazonaws.encryptionsdk.kms.KmsMasterKeyProvider;
import com.amazonaws.services.kms.AWSKMSClientBuilder;
import com.freewayemi.merchant.commons.exception.FreewayException;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class KmsBO {

    private static final Logger LOGGER = LoggerFactory.getLogger(KmsBO.class);

    private final KmsMasterKeyProvider provider;

    @Autowired
    public KmsBO(AWSKMSClientBuilder awskmsClientBuilder, @Value("${aws.kms.arn}") String masterKey) {
        this.provider = KmsMasterKeyProvider.builder().withClientBuilder(awskmsClientBuilder)
                .withKeysForEncryption(masterKey).build();
    }

    public String encrypt(String data) {
        final AwsCrypto awsCrypto = new AwsCrypto();
        return Hex.encodeHexString(awsCrypto.encryptData(provider, data.getBytes()).getResult());
    }

    public String decrypt(String ciphertext) {
        final AwsCrypto awsCrypto = new AwsCrypto();
        try {
            return new String(awsCrypto.decryptData(provider, Hex.decodeHex(ciphertext.toCharArray())).getResult());
        } catch (DecoderException e) {
            LOGGER.error("DecoderException occurred: ", e);
            throw new FreewayException("Something went wrong");
        }
    }
}
