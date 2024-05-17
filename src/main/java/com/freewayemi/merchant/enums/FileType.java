package com.freewayemi.merchant.enums;

public enum FileType {
    PAN("PAN"),
    ADDRESS_PROOF("ADDRESS_PROOF"),
    BUSINESS_DOCUMENTS("BUSINESS_DOCUMENTS"),
    SETTLEMENT_ACCOUNT_PROOF("SETTLEMENT_ACCOUNT_PROOF");

    private final String fileType;

    FileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFileType() {
        return fileType;
    }

    public static FileType getByFIleType(String fileType) {
        for (FileType type: values()) {
            if (type.getFileType().equals(fileType)) {
                return type;
            }
        }
        return null;
    }
}
