package com.freewayemi.merchant.commons.bo;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.HttpMethod;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.freewayemi.merchant.commons.exception.FreewayException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Component
public class S3UploadService {

    private static final Logger LOGGER = LoggerFactory.getLogger(S3UploadService.class);

    private final AmazonS3 amazonS3;
    private final String bucketName;
    private final String secureBucketName;

    String region = "ap-south-1";

    public List<String> getPreSignedURL(String objectKey) {
        URL url;
        List<String> retValues = new ArrayList<String>();
        try {
            // Set the presigned URL to expire after 7 days
            Date currentDate = new Date();
            Calendar c = Calendar.getInstance();
            c.setTime(currentDate);
            c.add(Calendar.DATE, 7);
            long expTimeMillis = c.getTimeInMillis();

            Date expiration = new Date();
            expiration.setTime(expTimeMillis);

            // Generate the presigned URL.
            GeneratePresignedUrlRequest generatePresignedUrlRequest =
                    new GeneratePresignedUrlRequest(bucketName, objectKey)
                            .withMethod(HttpMethod.GET)
                            .withExpiration(expiration);


            url = amazonS3.generatePresignedUrl(generatePresignedUrlRequest);

            retValues.add(url.toString());
            retValues.add(expiration.toString());
        } catch (AmazonServiceException e) {
            LOGGER.error("AmazonServiceException occurred while generating url: ", e);
            throw new FreewayException("AmazonServiceException while generating url");
        } catch (SdkClientException e) {
            LOGGER.error("SdkClientException occurred while while generating url: ", e);
            throw new FreewayException("SdkClientException while generating url");
        }

        return retValues;

    }

    @Autowired
    private S3UploadService(AmazonS3 amazonS3, @Value("${aws.s3.bucket.name}") String bucketName,
                            @Value("${aws.s3.secure.bucket.name}") String secureBucketName) {
        this.amazonS3 = amazonS3;
        this.bucketName = bucketName;
        this.secureBucketName = secureBucketName;
    }

    public void upload(String s3FileKey, File file, String contentType) {
        upload(s3FileKey, file, contentType, false);
    }

    public void upload(String s3FileKey, File file, String contentType, Boolean isSecure) {
        try {
            PutObjectRequest request = new PutObjectRequest(isSecure ? secureBucketName : bucketName, s3FileKey, file);
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(contentType);
            request.setMetadata(metadata);
            PutObjectResult putObjectResult = amazonS3.putObject(request);
            LOGGER.info("ETag is: {}", putObjectResult.getETag());
        } catch (AmazonServiceException e) {
            LOGGER.error("AmazonServiceException occurred while uploading file to S3: ", e);
            throw new FreewayException("AmazonServiceException while generating file");
        } catch (SdkClientException e) {
            LOGGER.error("SdkClientException occurred while uploading file to S3: ", e);
            throw new FreewayException("SdkClientException while generating file");
        }
    }

}
