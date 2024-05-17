package com.freewayemi.merchant.service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.xspec.S;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.RawMessage;
import com.amazonaws.services.simpleemail.model.SendRawEmailRequest;
import com.freewayemi.merchant.commons.bo.NotificationService;
import com.freewayemi.merchant.dto.request.EmailRequest;
import com.freewayemi.merchant.entity.MerchantConfigs;
import com.freewayemi.merchant.repository.MerchantConfigsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

@Service
public class EmailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailService.class);
    private final String fromEmail;
    private final String awsAccessKey;
    private final String awsSecretKey;
    private final String region;
    public EmailService(@Value("${aws.ses.from.email}") String fromEmail,
                        @Value("${aws.access.key}") String awsAccessKey,
                        @Value("${aws.secret.key}") String awsSecretKey,
                        @Value("${aws.region}") String region) {
        this.fromEmail = fromEmail;
        this.awsAccessKey = awsAccessKey;
        this.awsSecretKey = awsSecretKey;
        this.region = region;
    }

    public void sendEmail(EmailRequest emailRequest) {
        try {
            javax.mail.Session session = Session.getDefaultInstance(new Properties());
            MimeMessage message = new MimeMessage(session);
            message.setSubject(emailRequest.getSubject(), "UTF-8");
            message.setFrom(new InternetAddress(fromEmail));
            if(Objects.nonNull(emailRequest.getToEmailIds())) {
                message.addRecipients(Message.RecipientType.TO, InternetAddress.parse(emailRequest.getToEmailIds()));
            }
            if(Objects.nonNull(emailRequest.getCcEmailIds())) {
                message.addRecipients(Message.RecipientType.CC, InternetAddress.parse(emailRequest.getCcEmailIds()));
            }
            MimeMultipart msgBody = new MimeMultipart("alternative");
            MimeBodyPart wrap = new MimeBodyPart();
            MimeBodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(emailRequest.getHtmlBody(), "text/html; charset=UTF-8");
            msgBody.addBodyPart(htmlPart);
            wrap.setContent(msgBody);
            MimeMultipart msg = new MimeMultipart("mixed");
            message.setContent(msg);
            msg.addBodyPart(wrap);
            if (!StringUtils.isEmpty(emailRequest.getAttachment())) {
                MimeBodyPart att = new MimeBodyPart();
                DataSource fds = new FileDataSource(emailRequest.getAttachment());
                att.setDataHandler(new DataHandler(fds));
                att.setFileName(fds.getName());
                msg.addBodyPart(att);
            }
            BasicAWSCredentials basicAWSCredentials = new BasicAWSCredentials(awsAccessKey, awsSecretKey);
            AmazonSimpleEmailService client = AmazonSimpleEmailServiceClientBuilder.standard()
                    .withCredentials(new AWSStaticCredentialsProvider(basicAWSCredentials))
                    .withRegion(region)
                    .build();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            message.writeTo(outputStream);
            RawMessage rawMessage = new RawMessage(ByteBuffer.wrap(outputStream.toByteArray()));
            SendRawEmailRequest rawEmailRequest = new SendRawEmailRequest(rawMessage);
            client.sendRawEmail(rawEmailRequest);
            LOGGER.info("Email send successfully");
        } catch (Exception ex) {
            LOGGER.error("Exception occurred while sending email: ", ex);
        }
    }

}
