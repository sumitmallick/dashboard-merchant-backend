package com.freewayemi.merchant.commons.dto.ntbservices.cibil;

public enum InputType {
    OTP,MOBILE_NUMBER;

    private static final String OTP_QUEUE="OTP_IDM_Email_Queue";
    private static final String ALTERNATE_MOBILE_QUEUE="OTP_AlternateEmail_Entry_Queue";
    private static final String OTP_QUEUE_ALTERNATE_EMAIL="OTP_IDM_AlternateEmail_Queue";

    public static InputType getInputType(String queueType){
        if(OTP_QUEUE.equalsIgnoreCase(queueType) || OTP_QUEUE_ALTERNATE_EMAIL.equalsIgnoreCase(queueType)) {
            return InputType.OTP;
        }
        if(ALTERNATE_MOBILE_QUEUE.equalsIgnoreCase(queueType)){
            return InputType.MOBILE_NUMBER;
        }
        return null;
    }
}
