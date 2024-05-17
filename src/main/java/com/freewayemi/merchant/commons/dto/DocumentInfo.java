package com.freewayemi.merchant.commons.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;

@Data
public class DocumentInfo {
    private final String url;
    private final String filename;
    private final String name;
    private final String type;
    private final String expiry;
    private final String key;
    private final String docStatus;
    private final String remark;
    private final String panNumber;
    private final String documentOwnerType;

    @CreatedDate
    private Instant createdDate;
    @LastModifiedDate
    private Instant lastModifiedDate;

    @JsonCreator
    public DocumentInfo(@JsonProperty("url") String url,
                        @JsonProperty("filename") String filename,
                        @JsonProperty("name") String name,
                        @JsonProperty("type") String type,
                        @JsonProperty("expiry") String expiry,
                        @JsonProperty("key") String key,
                        @JsonProperty("docStatus") String docStatus,
                        @JsonProperty("remark") String remark,
                        @JsonProperty("panNumber") String panNumber,
                        @JsonProperty("documentOwnerType") String documentOwnerType){
        this.url = url;
        this.filename = filename;
        this.name = name;
        this.type = type;
        this.expiry = expiry;
        this.key = key;
        this.docStatus = docStatus;
        this.remark = remark;
        this.panNumber = panNumber;
        this.documentOwnerType = documentOwnerType;
    }
}
