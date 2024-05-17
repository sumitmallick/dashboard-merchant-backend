package com.freewayemi.merchant.commons.type;

public enum TransactionCode {

    SUCCESS(0, TransactionStatus.success, "Transaction successful", "Transaction successful"),
    INITIATED(1, TransactionStatus.initiated, "Transaction in progress", "Transaction in progress"),
    PROCESSING(2, TransactionStatus.processing, "Processing", "Processing"),
    EXPIRED(3, TransactionStatus.expired, "Session expired", "Session expired"),
    CANCELLED(4, TransactionStatus.cancelled, "Cancelled by user", "You have cancelled the transaction."),
    INITIATED_WITH_PAYMENT_OPTIONS(5, TransactionStatus.initiated, "Transaction in progress", "Transaction in progress"),
    DP_PENDING(6, TransactionStatus.processing, "Down payment Pending", "Down payment Pending"),
    LOADED(7, TransactionStatus.loaded, "Transaction page loaded", "Transaction page loaded"),
    PROCESSING_21(21, TransactionStatus.processing, "The last 4 digits of the HDFC Bank Debit Card entered do not match with the bank records.", "The last 4 digits of the HDFC Bank Debit Card entered do not match with the bank records."),
    PROCESSING_22(22, TransactionStatus.processing, "The ICICI Bank Debit Card details entered does not match with the bank records. If you have another ICICI bank debit card you can try using it.", "The ICICI Bank Debit Card details entered does not match with the bank records. Please ask your customer to check and enter the card details again."),
    PROCESSING_23(23, TransactionStatus.processing, "The Axis Bank Debit Card details entered does not match with the bank records. If you have another Axis bank debit card you can try using it.", "The Axis Bank Debit Card details entered does not match with the bank records. Please ask your customer to check and enter the card details again."),
    PROCESSING_24(24, TransactionStatus.processing, "Please enter correct PAN card number linked to your bank account.", "The PAN card number entered does not match with the bank records."),
    PROCESSING_26(26, TransactionStatus.processing, "Invalid OTP entered", "The OTP entered by your customer does not match with the one sent by the bank. Please ask your customer to enter the correct OTP or resend the payment link."),
    PROCESSING_27(27, TransactionStatus.processing, "Transaction in progress", "Redirected to complete the loan steps"),

    FAILED_20(20, TransactionStatus.failed, "Transaction failure. Please try again.", "Transaction failure"),
    FAILED_22(22, TransactionStatus.failed, "Limit exceeded", "Transaction amount is greater than your available limit."),
    FAILED_23(23, TransactionStatus.failed, "Authentication failed", "Authentication failed"),
    FAILED_24(24, TransactionStatus.failed, "Not Eligible", "Looks like you are not approved from any of our lenders"),
    FAILED_25(25, TransactionStatus.failed, "OTP verification failed", "Oh! You have reached maximum limit to validate your OTP. Kindly try again."),
    FAILED_27(27, TransactionStatus.failed, "Invalid minimum transaction amount", "Invalid minimum transaction amount", "Transaction amount is lower than Bank's limit of Rs %s. Please retry with a higher amount."),
    FAILED_28(28, TransactionStatus.failed, "Invalid maximum transaction amount", "Invalid maximum transaction amount", "Bank's transaction limit exceeded. Please try with a lower amount lesser than %s."),
    FAILED_29(29, TransactionStatus.failed, "Looks like your debit card is not active. Please try again with another card", "It looks like the ICICI Bank Debit Card being used for the payment is not active. Please ask your customer to connect to his bank and get the card activated."),
    FAILED_30(30, TransactionStatus.failed, "We did not get any response from the bank. Please retry again.", "No Response from Bank"),
    FAILED_31(31, TransactionStatus.failed, "Your card is expired. Please try again with a valid card.", "Card is expired."),
    FAILED_32(32, TransactionStatus.failed, "We are observing connectivity issues with the Bank. Please try again after some time.", "Oops! We are seeing some timeout issues at bank's side. Happens rarely but generally gets resolved in 30 minutes."),
    FAILED_33(33, TransactionStatus.failed, "You do not have sufficient limit in your account to complete the payment. Please complete payment using another method.", "You do not have sufficient limit in your account to complete the payment. Please complete payment using another method."),
    FAILED_34(34, TransactionStatus.failed, "Bank has not enabled EMI for this card. Please try with another card.", "Bank has not enabled EMI for this card. Please try with another card."),
    FAILED_35(35, TransactionStatus.failed, "The CVV you entered was incorrect. Kindly enter correct CVV", "The CVV you entered was incorrect. Kindly enter correct CVV"),
    FAILED_36(36, TransactionStatus.failed, "Your bank has declined the transaction. Please ensure you enter the correct OTP for authentication.", "Your customer's OTP verification with bank is failed. Please ask him to try again and enter correct OTP sent by bank."),
    FAILED_37(37, TransactionStatus.failed, "We are observing technical issues with the Bank. Please try again after some time.", "We are observing technical issues with the Bank. Please try again after some time."),
    FAILED_38(38, TransactionStatus.failed, "You card is inactive, please call your bank to activate it", "You card is inactive, please call your bank to activate it"),
    FAILED_39(39, TransactionStatus.failed, "We are observing technical issues with the Bank. Please try again using another payment option", "We are observing technical issues with the Bank. Please try again using another payment option"),
    FAILED_40(40, TransactionStatus.failed, "You have either cancelled transaction on Bank OTP page or Please ensure you enter the correct OTP for authentication.", "Your customer's OTP verification with bank is failed. Please ask him to try again and enter correct OTP sent by bank."),
    FAILED_41(41, TransactionStatus.failed, "Invalid VPA", "Invalid VPA"),
    FAILED_42(42, TransactionStatus.failed, "Please enter correct VPA", "Please enter correct VPA"),
    FAILED_43(43, TransactionStatus.failed, "Invalid PIN", "Invalid PIN"),
    FAILED_44(44, TransactionStatus.failed, "The mobile number entered by you is not registered with Bank. Please try again with the mobile number registered with Bank.", "The mobile number entered by you is not registered with Bank. Please try again with the mobile number registered with Bank."),
    FAILED_45(45, TransactionStatus.failed, "You have reached the limit of the number of loans that can be availed through your Bank in this month. Please use another option to complete the payment.", "You have reached the limit of the number of loans that can be availed through your Bank in this month. Please use another option to complete the payment."),
    FAILED_46(46, TransactionStatus.failed, "Your transaction is declined by Bank as you are not eligible for doing online category transaction.", "Your transaction is declined as you are not eligible for doing online category transaction."),
    FAILED_47(47, TransactionStatus.failed, "Your transaction is declined as you are not eligible for Kotak DCEMI. Please check your DCEMI eligibility by sending SMS “DCEMI <last 4 digits of your debit card number>” to 5676788 from your registered mobile number.", "Your transaction is declined as you are not eligible for Kotak DCEMI. Please check your DCEMI eligibility by sending SMS “DCEMI <last 4 digits of your debit card number>” to 5676788 from your registered mobile number."),
    FAILED_48(48, TransactionStatus.failed, "Technical error, please retry later.", "Technical error, please retry later."),
    FAILED_49(49, TransactionStatus.failed, "Your transaction is declined as you do not have sufficient limit for this DCEMI transaction. Please check your DCEMI eligibility by sending SMS “DCEMI <last 4 digits of your debit card number>” to 5676788 from your registered mobile number.", "Your transaction is declined as you do not have sufficient limit for this DCEMI transaction. Please check your DCEMI eligibility by sending SMS “DCEMI <last 4 digits of your debit card number>” to 5676788 from your registered mobile number."),
    FAILED_50(50, TransactionStatus.failed, "Your transaction is declined as you have exhausted your DCEMI purchasing limits.", "Your transaction is declined as you have exhausted your DCEMI purchasing limits."),
    FAILED_51(51, TransactionStatus.failed, "Your bank has declined this transaction. Please ensure your card is active and has sufficient limit to perform this transaction. If the problem persists, please speak to your bank representative.", "The transaction is being declined by the bank."),
    FAILED_52(52, TransactionStatus.failed, "Your bank is rejecting this transaction. Please ensure you have entered the correct card details. If the problem persists, please speak to your bank representative.", "Your bank is rejecting this transaction. Please ensure you have entered the correct card details. If the problem persists, please speak to your bank representative."),
    FAILED_53(53, TransactionStatus.failed, "We are observing technical issues with the Bank. Please try again after some time.", "Payments through HDFC bank have not been enabled for you yet. Please contact your payment sales representative for further queries."),
    FAILED_54(54, TransactionStatus.failed, "Refund after 90 days is not allowed by bank", "Refund after 90 days is not allowed by bank"),
    FAILED_55(55, TransactionStatus.failed, "You do not have sufficient limit in your account to complete the payment. Please transfer a minimum amount of 1 rupee and continue with the payment.", "Your customer does not have any money in his account to proceed with the payment. Please ask him/her to transfer a minimum amount of 1 rupee and continue with the payment."),
    FAILED_56(56, TransactionStatus.failed, "We are observing technical issues with the Bank. Please try again after some time.", "Payments through HDFC bank have not been enabled for you yet. Please contact your payment sales representative for further queries."),
    FAILED_57(57, TransactionStatus.failed, "Emi is not available for the card used in the transaction", "Emi is not available for the card used in the transaction"),
    FAILED_58(58, TransactionStatus.failed, "You have exceeded the transaction limit for ICICI Cardless EMI. Please use another option to complete the payment.", "Your customer have exceeded the transaction limit for ICICI Cardless EMI. Customer need to use another option to complete the payment."),
    FAILED_59(59, TransactionStatus.failed, "You have reached the limit of the number of loans that can be availed through your Bank. Please use another option to complete the payment.", "Customer has reached the limit of the number of loans that can be availed through your Bank. Please ask the customer to use another payment option."),
    FAILED_60(60, TransactionStatus.failed, "Your debit card is blocked by the bank. Please use another option to complete the payment.", "Customer's debit card is blocked by the bank. Please ask the customer to use another payment option."),
    DP_SUCCESS(60, TransactionStatus.processing, "Down payment Success", "Down payment Success"),
    FAILED_61(61, TransactionStatus.failed, "Your transaction is declined as your address registered with the bank is not updated. Please contact your bank to update the same.", "Your transaction is declined as your address registered with the bank is not updated. Please contact your bank to update the same."),
    DP_PROCESSING(61, TransactionStatus.processing, "Down payment Initiated", "Down payment Initiated"),
    FAILED_62(62, TransactionStatus.failed, "You have entered wrong card expiry date or year. Please check and try again.", "Your customer had entered invalid card expiry month or year. Please ask him to check and enter correct details as mentioned on the card and try again."),
    FAILED_63(63, TransactionStatus.processing, "Entered serial number is incorrect or offer is not available, please try again with correct or another serial number", "The customer has entered incorrect serial number or offer is not available on the serial number, ask to try again with correct or another serial number"),
    FAILED_84(84, TransactionStatus.failed, "Entered serial number is incorrect or offer is not available, please try again with correct or another serial number", "Entered serial number is incorrect or offer is not available, please try again with correct or another serial number"),
    FAILED_64(64, TransactionStatus.failed, "Selected brand model is invalid", "Selected brand model is invalid"),
    FAILED_65(65, TransactionStatus.failed, "Selected brand model and entered serial number are invalid", "Selected brand model and entered serial number are invalid"),
    FAILED_66(66, TransactionStatus.failed, "Selected brand model and entered serial number are invalid or already claimed", "Selected brand model and entered serial number are invalid or already claimed"),
    FAILED_67(67, TransactionStatus.failed, "Selected brand model and entered serial number are invalid", "Selected brand model and entered serial number are invalid"),
    FAILED_68(68, TransactionStatus.processing, "The brand network is unreachable, please try again later", "The brand network is unreachable, please try again later"),
    FAILED_69(69, TransactionStatus.failed, "No Cost EMI transaction limit on your card. Please use another card to get the No Cost EMI.", "No Cost EMI transaction limit on your card. Please use another card to get the No Cost EMI."),

    REFUND_70(70, TransactionStatus.refundRequested, "Refund request has been accepted to process", "Refund request has been accepted to process"),
    REFUND_71(71, TransactionStatus.failed, "Refund is restricted", "You are not allowed to initiate refund from Dashboard"),
    REFUND_72(72, TransactionStatus.failed, "Refund is restricted", "You are not allowed to initiate refund using API"),
    REFUND_73(73, TransactionStatus.fundsTransferRequiredByMerchant, "You are required to transfer funds to process this refund", "Funds to process this refund with bank"),
    REFUND_74(74, TransactionStatus.fundsReceivedFromMerchant, "payment has received funds to process this refund with bank", "payment has received funds to process this refund with bank"),
    REFUND_75(75, TransactionStatus.verifyingFundReceived, "payment to verify the funds to process refund with bank", "payment to verify the funds to process refund with bank"),
    REFUND_76(76, TransactionStatus.fundReceivedDetailNotMatching, "The detail of funds transferred needs to be rechecked", "The detail of funds transferred needs to be rechecked"),
    REFUND_77(77, TransactionStatus.refundCancelled, "Refund has been cancelled", "Refund has been cancelled"),
    REFUND_78(78, TransactionStatus.toBeSentToBank, "Refund is scheduled to process with bank", "Refund is scheduled to process with bank"),
    REFUND_79(79, TransactionStatus.filePrepared, "Refund file is prepared to send to bank", "Refund file is prepared to send to bank"),
    REFUND_80(80, TransactionStatus.sentToBank, "Refund is sent to the bank for processing", "Refund is sent to the bank for processing"),
    REFUND_81(81, TransactionStatus.bankAccepted, "Bank has accepted to process refund", "Bank has accepted to process refund"),
    REFUND_82(82, TransactionStatus.fundsTransferredByMerchant, "Funds are transferred to process refund with bank", "Funds are transferred to process refund with bank"),
    REFUND_83(83, TransactionStatus.fundsReturned, "Funds returned back to the merchant", "Funds returned back to the merchant"),
    REFUND_84(84, TransactionStatus.forceCompleted, "Force competed", "Force competed"),
    REFUND_85(85, TransactionStatus.failed, "Partial refund is not allowed by the bank", "Partial refund is not allowed by the bank"),
    REFUND_86(86, TransactionStatus.failed, "Refund transaction not found", "Refund transaction not found"),
    REFUND_87(87, TransactionStatus.failed, "Transaction not found against requested refund", "Transaction not found against requested refund"),
    REFUND_88(88, TransactionStatus.failed, "Refund inquiry is not available for selected bank", "Refund inquiry is not available for selected bank"),
    REFUND_89(89, TransactionStatus.sentToBank, "Down payment of amount Rs. %s will be refunded to the payment source account within 3-5 working days. Request you to please check your account after 5 days.", "Down payment of amount Rs. %s will be refunded to the payment source account within 3-5 working days. Request you to please check your account after 5 days."),

    REFUND_90(90, TransactionStatus.reversed, "Duplicate transaction found, transaction reversed.", "Duplicate transaction found, transaction reversed."),

    FAILED_101(101, TransactionStatus.failed, "Transaction limit exceeded. Please try with lower amount", "Limit exceeded"),
    FAILED_102(102, TransactionStatus.failed, "Invalid encrypt decrypt type", "Invalid encrypt decrypt type"),
    FAILED_103(103, TransactionStatus.failed, "Transaction limit exceeded. Please try with lower amount", "Limit exceeded"),
    FAILED_104(104, TransactionStatus.failed, "Sorry. Your KYC was unsuccessful. Please retry to complete the purchase.", "Sorry. Your KYC was unsuccessful. Please retry to complete the purchase."),
    FAILED_105(105, TransactionStatus.failed, "Sorry, your KYC was declined by the bank. Make sure your Aadhaar number matches with bank's records. Please retry to complete the purchase.", "Sorry, your KYC was declined by the bank. Make sure your Aadhaar number matches with bank's records. Please retry to complete the purchase."),
    FAILED_106(106, TransactionStatus.failed, "Sorry, you are not eligible for HDFC EMI.", "Sorry, you are not eligible for HDFC EMI."),
    FAILED_107(107, TransactionStatus.failed, "Sorry, you are not eligible for HDFC Flexipay.", "Sorry, you are not eligible for HDFC Flexipay."),
    FAILED_108(108, TransactionStatus.failed, "Sorry. Your KYC was unsuccessful due to session expiry. Please retry to complete the purchase.", "Sorry. Your KYC was unsuccessful due to session expiry. Please retry to complete the purchase."),
    EXPIRED_109(109, TransactionStatus.expired, "Transaction not attempted by customer", "Sorry, the transaction has timed out due to inactivity. Please re-initiate the payment."),
    FAILED_110(110, TransactionStatus.failed, "Offer is not available on this tenure please select a lower tenure.", "Offer is not available on this tenure please select a lower tenure."),
    PROCESSING_111(111, TransactionStatus.processing, "Sorry, loan application is incomplete. Please retry with complete application.", "Sorry, loan application is incomplete. Please retry with complete application."),
    FAILED_112(112, TransactionStatus.failed, "Sorry, you are not approved from IIFL Finance.", "Sorry, you are not approved from IIFL Finance."),
    FAILED_113(113, TransactionStatus.failed, "BIN cannot be empty", "BIN cannot be empty"),
    FAILED_114(114, TransactionStatus.failed, "BIN details not found", "BIN details not found"),
    FAILED_115(115, TransactionStatus.failed, "Merchant not approved.", "Merchant not approved."),
    FAILED_116(116, TransactionStatus.failed, "Incorrect request body", "Incorrect request body"),
    FAILED_117(117, TransactionStatus.failed, "Refund payout is not enabled", "Refund payout is not enabled"),
    FAILED_118(118, TransactionStatus.failed, "Successful transaction not available", "Successful transaction not available"),
    FAILED_119(119, TransactionStatus.failed, "Payout amount is higher than subvention amount", "Payout amount is higher than subvention amount"),
    FAILED_121(121, TransactionStatus.failed, "Transaction is already successful", "Transaction is already successful"),
    FAILED_122(122, TransactionStatus.failed, "Transaction not found", "Transaction not found"),
    FAILED_123(123, TransactionStatus.failed, "Invalid maximum transaction Amount", "Invalid maximum transaction Amount", "Transaction amount should be less than %s"),
    FAILED_124(124, TransactionStatus.failed, "Invalid minimum transaction Amount", "Invalid minimum transaction Amount", "Transaction amount should be more than %s"),
    FAILED_125(125, TransactionStatus.failed, "Duplicate process transaction request", "Duplicate process transaction request"),
    FAILED_126(126, TransactionStatus.failed, "Daily transaction limit for customer has reached", "Daily transaction limit for customer has reached"),
    FAILED_127(127, TransactionStatus.failed, "Weekly transaction limit for customer has reached", "Weekly transaction limit for customer has reached"),
    FAILED_128(128, TransactionStatus.failed, "Daily transaction limit for customer has reached", "Daily transaction limit for customer has reached"),
    FAILED_129(129, TransactionStatus.failed, "Monthly transaction limit for merchant has reached", "Daily transaction limit for merchant has reached"),
    FAILED_130(130, TransactionStatus.failed, "Weekly transaction limit for merchant has reached", "Weekly transaction limit for merchant has reached"),
    FAILED_131(131, TransactionStatus.failed, "Monthly transaction limit for merchant has reached", "Monthly transaction limit for merchant has reached"),
    FAILED_132(132, TransactionStatus.failed, "Transaction type is not Valid", "Transaction type is not Valid"),
    FAILED_133(133, TransactionStatus.failed, "Transaction amount is null or non positive", "Transaction amount is null or non positive"),
    FAILED_134(134, TransactionStatus.failed, "Invalid payment details as no vpa or account number is present", "Invalid payment details as no vpa or account number is present"),
    FAILED_135(135, TransactionStatus.failed, "Payout transaction is not in initiated state", "Payout transaction is not in initiated state"),
    FAILED_136(136, TransactionStatus.failed, "Payout transaction is already processed", "Payout transaction is already processed"),
    FAILED_137(137, TransactionStatus.failed, "Payout transaction is already initiated", "Payout transaction is already initiated"),
    FAILED_138(138, TransactionStatus.failed, "Payout already initiated or processed", "Payout already initiated or processed"),
    FAILED_139(139, TransactionStatus.failed, "Invalid customer VPA", "Invalid customer VPA"),
    PENDING_WITH_BANK(140, TransactionStatus.pending_with_bank, "Settlement initiated with the bank", "Settlement initiated with the bank"),
    REVERSED(141, TransactionStatus.reversed, "Settlement initiated with the bank", "Settlement initiated with the bank"),
    FAILED_142(142, TransactionStatus.failed, "Only one refund payout transaction allowed", "Only one refund payout transaction allowed"),
    FAILED_143(143, TransactionStatus.failed, "Signature validation failed.", "Signature validation failed."),
    FAILED_144(144, TransactionStatus.failed, "Selected brand model and entered serial number is already claimed", "Selected brand model and entered serial number is already claimed"),
    FAILED_145(145, TransactionStatus.failed, "Get delivery order details is not allowed", "Get delivery order details is not allowed"),
    FAILED_146(146, TransactionStatus.failed, "Your previous loan is in process, kindly retry after the loan is disbursed.", "Your previous loan is in process, kindly retry after the loan is disbursed."),
    FAILED_147(147, TransactionStatus.failed, "Transaction already in progress.", "Transaction already in progress."),
    FAILED_148(148, TransactionStatus.processing, "Selected product is not mapped with GSTIN number at brands end", "Selected product is not mapped with GSTIN number at brands end"),
    FAILED_149(149, TransactionStatus.failed, "Serial number already getting verified please wait", "Serial number already getting verified please wait"),
    FAILED_150(150, TransactionStatus.failed, "Additional cashback transaction limit on your card. Please use another card to get the Additional cashback.", "Serial number already getting verified please wait"),

    DISPUTE_151(151, TransactionStatus.open, "Dispute has been opened", "Dispute has been opened"),
    DISPUTE_152(152, TransactionStatus.lost, "You lost the dispute", "You lost the dispute"),
    DISPUTE_153(153, TransactionStatus.won, "You won the dispute", "You won the dispute"),
    DISPUTE_154(154, TransactionStatus.underReview, "Your dispute is under review", "Your dispute is under review"),
    DISPUTE_155(155, TransactionStatus.closed, "Your dispute has been closed", "Your dispute has been closed"),
    FAILED_156(156, TransactionStatus.failed, "Down payment will be refunded to your account within 3-5 working days. Request you to please check your account after 5 days.", "Down payment will be refunded to your account within 3-5 working days. Request you to please check your account after 5 days."),
    FAILED_157(157, TransactionStatus.failed, "Card information is not provided", "Card information is not provided"),
    FAILED_158(158, TransactionStatus.failed, "Invalid bank code", "Invalid bank code"),
    FAILED_159(159, TransactionStatus.failed, "Invalid Card Type", "Invalid Card Type"),
    FAILED_160(160, TransactionStatus.failed, "Invalid Emi tenure", "Invalid Emi tenure"),
    FAILED_161(161, TransactionStatus.failed, "Subvention is given but serial number is not verified", "Subvention is given but serial number is not verified"),
    FAILED_162(162, TransactionStatus.failed, "Invalid emi option", "Invalid emi option"),
    FAILED_163(163, TransactionStatus.failed, "Product doesn't exist", "Product doesn't exist"),
    FAILED_164(164, TransactionStatus.failed, "Card offer is not available", "Card offer is not available"),
    FAILED_165(165, TransactionStatus.failed, "Subvention is not available for selected card offer", "Subvention is not available for selected card offer"),
    FAILED_166(166, TransactionStatus.failed, "Invalid subvention amount", "Invalid subvention amount"),
    FAILED_167(167, TransactionStatus.failed, "Model number or brand id not found", "Model number or brand id not found"),
    DISPUTE_168(168, TransactionStatus.pendingWithMerchant, "Dispute has been opened", "Dispute has been opened"),
    DISPUTE_169(169, TransactionStatus.underReview, "The issuing bank will review documents uploaded for the dispute", "The issuing bank will review documents uploaded for the dispute"),
    DISPUTE_170(170, TransactionStatus.underpaymentReview, "payment to review the document uploaded by the merchant", "payment to review the document uploaded by the merchant"),
    DISPUTE_171(171, TransactionStatus.merchantWon, "Merchant won the dispute", "Merchant won the dispute"),
    DISPUTE_172(172, TransactionStatus.merchantLost, "Merchant lost the dispute", "Merchant lost the dispute"),
    DISPUTE_173(173, TransactionStatus.failed, "Refund not allowed for the transaction where dispute is raised", "Refund not allowed for the transaction where dispute is raised"),
    DISPUTE_174(174, TransactionStatus.sentToBank, "Dispute document has been sent to bank", "Dispute document has been sent to bank"),
    FAILED_175(175, TransactionStatus.failed, "Your Loan is expired.", "Your Loan is expired."),
    FAILED_176(176, TransactionStatus.failed, "Your bank is rejecting this transaction. Please ensure your card is active. If the problem persists, please speak to your bank representative.", "Your bank is rejecting this transaction. Please ensure your card is active. If the problem persists, please speak to your bank representative."),
    FAILED_177(177, TransactionStatus.failed, "Your bank is rejecting this transaction due to wrong card expiry month or year. Please check and try again. If the problem persists, please speak to your bank representative.", "Your bank is rejecting this transaction due to wrong card expiry month or year. Please check and try again. If the problem persists, please speak to your bank representative."),
    FAILED_178(178, TransactionStatus.failed, "Your bank is rejecting this transaction due to card withdrawal limit is exceeded. Please try again with another card or speak to your bank representative.", "Your bank is rejecting this transaction due to card withdrawal limit is exceeded. Please try again with another card or speak to your bank representative."),
    FAILED_179(179, TransactionStatus.failed, "Your bank is rejecting this transaction. Please try again with another card or speak to your bank representative.", "Your bank is rejecting this transaction. Please ensure your card is active. If the problem persists, please speak to your bank representative."),
    FAILED_180(180, TransactionStatus.failed, "Invalid dealer code", "Invalid Partner Id"),
    FAILED_181(181, TransactionStatus.failed, "Your account is inactive. Please get in touch with the bank.", "Your customer's account is inactive. Please ask your customer to get in touch with the bank."),
    FAILED_182(182, TransactionStatus.failed, "Refund after 30 days is not allowed by bank", "Refund after 30 days is not allowed by bank"),
    FAILED_183(183, TransactionStatus.failed, "No offer found on this product.", "No offer found on this product."),
    FAILED_184(184, TransactionStatus.failed, "Unable to find suitable offer for you please contact support.", "Bank interests rates are not configured correctly for this txn, kindly update the interests on dashboard and ask customer retry fresh transaction."),
    FAILED_185(185, TransactionStatus.failed, "Refund after 90 days is not allowed by the bank", "Refund after 90 days is not allowed by the bank"),
    FAILED_186(186, TransactionStatus.failed, "No agreement is available", "No agreement is available"),
    FAILED_187(187, TransactionStatus.failed, "Unable to generate KFS, please contact support care@getpayment.com", "Failed to generate agreement"),
    FAILED_188(188, TransactionStatus.failed, "KFS agreement is expired, please retry", "Your customer has generated multiple kfs agreements for different banks ask customer to select bank and tenure again and go ahead with the transaction."),
    FAILED_189(189, TransactionStatus.failed, "Unable to generate agreement, please contact support care@getpayment.com", "Failed to generate agreement"),

    FAILED_201(201, TransactionStatus.failed, "Error occurred. Please retry.", "Error occurred. Please retry."),
    FAILED_202(202, TransactionStatus.failed, "The product with this IMEI number is already sold. Please enter a different IMEI number.", "The product with this IMEI number is already sold. Please enter a different IMEI number."),
    FAILED_203(203, TransactionStatus.failed, "The product with this IMEI number is already sold. Please ask merchant to re-share link with a different IMEI number.", "The product with this IMEI number is already sold. Please ask merchant to re-share link with a different IMEI number."),
    FAILED_204(204, TransactionStatus.failed, "IMEI does not belong to the merchant.", "IMEI does not belong to the merchant."),
    FAILED_205(205, TransactionStatus.failed, "The model Name and IMEI does not match. Please re-enter the correct IMEI number", "The model Name and IMEI does not match. Please re-enter the correct IMEI number"),
    FAILED_206(206, TransactionStatus.failed, "Error occurred: State Code not mapped.", "Error occurred: State Code not mapped."),
    FAILED_207(207, TransactionStatus.failed, "Please enter the correct IMEI number. The accepted IMEI is IMEI 1.", "Please enter the correct IMEI number. The accepted IMEI is IMEI 1."),
    FAILED_208(208, TransactionStatus.failed, "Error occurred: Dealer Code incorrect.", "Error occurred: Dealer Code incorrect."),

    FAILED_210(210, TransactionStatus.failed, "Serial number does not exist. Please enter correct serial number", "Serial number does not exist. Please enter correct serial number"),
    FAILED_211(211, TransactionStatus.failed, "Model and Serial Number do not match. Please enter correct serial number", "Model and Serial Number do not match. Please enter correct serial number"),
    FAILED_212(212, TransactionStatus.failed, "Invalid dealer number", "Invalid dealer number"),
    FAILED_213(213, TransactionStatus.failed, "Serial Number has already been sold", "Serial Number has already been sold"),
    FAILED_214(214, TransactionStatus.failed, "Serial Number has not been sold", "Serial Number has not been sold"),
    FAILED_215(215, TransactionStatus.failed, "Serial does number does not exist. Please enter correct serial number.", "Serial does number does not exist. Please enter correct serial number."),
    FAILED_216(216, TransactionStatus.failed, "Model and Serial Number do not match. Please enter correct serial number.", "Model and Serial Number do not match. Please enter correct serial number."),
    FAILED_217(217, TransactionStatus.failed, "Product with the entered serial number is already sold. Please enter serial number of a different product.", "Product with the entered serial number is already sold. Please enter serial number of a different product."),
    FAILED_218(218, TransactionStatus.failed, "Please try again by entering the serial number.", "Please try again by entering the serial number."),
    FAILED_219(219, TransactionStatus.failed, "Server is facing some issues. Please retry after sometime.", "Server is facing some issues. Please retry after sometime."),
    FAILED_220(220, TransactionStatus.failed, "No providers available.", "No providers available"),
    ;
    private final Integer code;
    private final String status;
    private String statusMsg;
    private String dashboardStatusMsg;
    private String template;

    TransactionCode(Integer code, String statusMsg) {
        this.code = code;
        this.status = TransactionStatus.failed.name();
        this.statusMsg = statusMsg;
    }

    TransactionCode(Integer code, TransactionStatus status, String statusMsg) {
        this.code = code;
        this.status = status.name();
        this.statusMsg = statusMsg;
    }

    TransactionCode(Integer code, TransactionStatus status, String statusMsg, String dashboardStatusMsg) {
        this.code = code;
        this.status = status.name();
        this.statusMsg = statusMsg;
        this.dashboardStatusMsg = dashboardStatusMsg;
    }

    TransactionCode(Integer code, TransactionStatus status, String statusMsg, String dashboardStatusMsg, String template) {
        this.code = code;
        this.status = status.name();
        this.statusMsg = statusMsg;
        this.dashboardStatusMsg = dashboardStatusMsg;
        this.template = template;
    }

    public Integer getCode() {
        return code;
    }

    public String getStatusMsg() {
        return statusMsg;
    }

    public void setStatusMsg(String statusMsg) {
        this.statusMsg = statusMsg;
    }

    public String getStatus() {
        return status;
    }

    public String getDashboardStatusMsg() {
        return dashboardStatusMsg;
    }

    public String getTemplate() {
        return template;
    }

    public static TransactionCode getByCode(Integer code) {
        for (TransactionCode transactionCode : TransactionCode.values()) {
            if (transactionCode.getCode().equals(code)) {
                return transactionCode;
            }
        }
        return TransactionCode.FAILED_20;
    }

}
