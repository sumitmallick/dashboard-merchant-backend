package com.freewayemi.merchant.dto.response;

import com.freewayemi.merchant.commons.dto.AppContentCard;
import com.freewayemi.merchant.commons.dto.TransactionResponse;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class HomeResponse {
    private final Boolean showTxnSummary;
    private final Boolean showInfoSection;
    private final Boolean showDataDashboard;
    private final Boolean showKnoSection;
    private final Boolean showStatSection;
    private final Boolean showTxnList;
    private final Boolean showEmiInfo;
    private final Boolean showWelcomeScreens;
    private final Boolean showSettlementTab;
    private final Boolean showPocketDetails;
    private final Boolean showLastRedeemedAmount;
    private final Boolean showBanners;
    private final Boolean showDailyEarningPromo;
    private final Boolean showOtherUserEarningPromo;
    private final Boolean showConsumerNumberPromo;
    private final Boolean showOtherUserNumberPromo;
    private final Boolean showUpdateAccount;
    private final Boolean showStoreUserStats;
    private final TxnSummary txnSummary;
    private final AppContentCard infoSection;
    private final DataDashBoard dataDashBoard;
    private final List<AppContentCard> knoSections;
    private final List<AppContentCard> statSections;
    private final List<AppContentCard> banners;
    private final List<TransactionResponse> txnList;
    private final EmiInfo emiInfo;
    private final PocketDetails pocketDetails;
    private final Long lastRedeemedAmount;
    private final StoreUserHomeData storeUserHomeData;


}
