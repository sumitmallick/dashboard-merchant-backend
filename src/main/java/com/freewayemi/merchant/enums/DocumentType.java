package com.freewayemi.merchant.enums;

public enum DocumentType {
    AADHAR_CARD("AADHAR_CARD"),
    VOTER_ID("VOTER_ID"),
    DRIVING_LICENSE("DRIVING_LICENSE"),
    BANK_PASSBOOK("BANK_PASSBOOK"),
    PASSPORT("PASSPORT");



    private final String documentType;

    DocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getDocumentType() {
        return documentType;
    }

    public static DocumentType getByDocumentType(String documentType) {
        for (DocumentType type: values()) {
            if (type.getDocumentType().equals(documentType)) {
                return type;
            }
        }
        return null;
    }
}
