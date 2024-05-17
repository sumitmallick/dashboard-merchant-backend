package com.freewayemi.merchant.commons.dto.ntbservices.validators;

public interface ValidationErrorMessages {

    interface CreateLoanValidationMessage {
        String PAN_VALIDATION_MESSAGE = "Pan Details cannot be blank";

        String PINCODE_VALIDATION_MESSAGE = "Pin code Details cannot be blank";
        String PROVIDER_NOT_PRESENT = "Unable to find provider";
        String TITLE_NOT_PRESENT = "Title needs to be present";
        String FIRSTNAME_NOT_PRESENT = "FirstName cannot be empty";
        String LASTNAME_NOT_PRESENT = "LastName cannot be empty";
        String DOB_INVALID="DOB has to be of ddMMyyyy";
        String MOBILE_NUMBER_PATTERN="Mobile Number has to be number of 10 digits";
        String EMAIL_ID_PATTERN="Email Id needs to be alpahnumerics.";
        String EMAIL_ID_EMPTY="Email Id canot be blank";
        String PURCHASE_AMOUNT_NOT_EMPTY="Purchase Amount cannot be empty";
        String PURCHASE_AMOUNT_POSITIVE_INTEGER="Purchase Amount has to be positive integer";
        String NET_SALARY_POSITIVE_INTEGER="NetSalary has to be positive integer";
        String NET_SALARY="Net salary cannot be empty";
        String GENDER_NOT_PROVIDED="Gender cannot be empty";
        String MOBILE_NUMBER_NOT_PROVIDED="Mobile Number cannot be empty";
        String PINCODE_NOT_PRESENT="Pincode not present";
        String APPLICANT_NAME_BLANK="Applicant name blank issue";
        String NAME_NOT_PRESENT = "Name cannot be empty";

        String LOCATION_NOT_PRESENT = "Provide location access.";
    }

    interface CibilValidationMessage{
        String PROSPECT_ID_NOT_PRESENT="ProspectID not present";
    }

    interface UpdateProspectMessage{
        String AMOUNT="Amount has to be positive value";
        String EMI_AMOUNT="EMI Amount has to be positive value";
        String FATHER_NAME="Father Name cannot be empty";
        String MOTHER_NAME="Mother Name cannot be empty";
        String GENDER="Gender cannot be empty";
        String TENURE="Tenure has to be >= 3";
        String ROI = "ROI has to be positive value";
        String PRINCIPAL = "Principal has to be positive value";
        String ACC_NAME="Account Name cannot be empty";
        String ACC_NO="Account No cannot be empty";
        String ACC_IFSC="Ifsc code cannot be empty";
        String MOBILE_NO="Mobile number cannot be empty";
    }

    interface DisbursalProspectMessage{
        String AMOUNT="Amount has to be a positive value";
        String GST_AMOUNT = "GST Amount has to be a positive value";
        String PROCESSING_FEE= "Processing Fee has to be a positive value";
        String TENURE="Tenure has to be a positive value";
        String ROI="ROI has to be a positive value";
    }
}
