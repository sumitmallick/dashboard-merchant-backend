package com.freewayemi.merchant.bo;

import com.google.cloud.storage.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FirebaseBO {
    private static final Logger LOGGER = LoggerFactory.getLogger(HomeBO.class);

    private final StorageOptions storageOptions;
    private final String bucketName;
    private final String bucketUrl;

    @Autowired
    public FirebaseBO(StorageOptions storageOptions,
                      @Value("${FIREBASE_BUCKET_NAME}") String bucketName,
                      @Value("${FIREBASE_BUCKET_URL}") String bucketUrl) {
        this.storageOptions = storageOptions;
        this.bucketName = bucketName;
        this.bucketUrl = bucketUrl;
    }

    public List<String> getFolderContentUrlList(String folderName) {
        Storage storage = storageOptions.getService();
        List<String> urls = new ArrayList<>();
        {
            for (Blob blob : storage.list(bucketName).iterateAll()) {
                if (blob.getBlobId().getName().startsWith(folderName + "/") &&
                        !blob.getBlobId().getName().endsWith("/"))
                    urls.add(bucketUrl + blob.getBlobId().getName());
            }
        }
        return urls;
    }

    public String uploadImageByteArray(String path, String contentType, byte[] image) {
        Storage service = storageOptions.getService();
        BlobId blobId = BlobId.of(bucketName, path);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(contentType).build();
        service.create(blobInfo, image);
        return bucketUrl + path;
    }

    public String getBucketUrl() {
        return bucketUrl;
    }
}
