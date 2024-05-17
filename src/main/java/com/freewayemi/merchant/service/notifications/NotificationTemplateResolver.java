package com.freewayemi.merchant.service.notifications;

import com.freewayemi.merchant.bo.AdminAuthUserBO;
import com.freewayemi.merchant.bo.MerchantUserBO;
import com.freewayemi.merchant.bo.SalesAgentBO;
import com.freewayemi.merchant.commons.entity.Params;
import com.freewayemi.merchant.dto.NotificationEvent;
import com.freewayemi.merchant.dto.sales.SalesUserProfile;
import com.freewayemi.merchant.entity.AdminAuthUser;
import com.freewayemi.merchant.entity.MerchantUser;
import com.freewayemi.merchant.entity.NotificationTemplate;
import com.freewayemi.merchant.pojos.ResolvedNotification;
import com.freewayemi.merchant.type.AppType;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class NotificationTemplateResolver {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationTemplateResolver.class);

    private final MerchantUserBO merchantUserBO;
    private final SalesAgentBO salesAgentBO;
    private final AdminAuthUserBO adminAuthUserBO;
    private final ExpressionResolver expressionResolver;

    @Autowired
    public NotificationTemplateResolver(MerchantUserBO merchantUserBO, SalesAgentBO salesAgentBO,
                                        AdminAuthUserBO adminAuthUserBO, ExpressionResolver expressionResolver) {
        this.merchantUserBO = merchantUserBO;
        this.salesAgentBO = salesAgentBO;
        this.adminAuthUserBO = adminAuthUserBO;
        this.expressionResolver = expressionResolver;
    }

    public ResolvedNotification resolve(NotificationEvent event, NotificationTemplate template) {
        ResolvedNotification response = ResolvedNotification.builder().resolveSuccess(false).build();

        MerchantUser merchant = merchantUserBO.getUserByMerchantIdOrDisplayId(
                StringUtils.isNotBlank(event.getMerchantId()) ? event.getMerchantId() : event.getDisplayId());

        response.setMerchantId(merchant.getId().toString());

        // Preparing root object map for resolving template expressions
        Map<String, Object> rootObject = new HashMap<>();
        MerchantUser partnerMerchant = null;
        // If notification event is for partner merchant then storing parent merchant in 'merchant' variable as
        // most of the required data is part of parent merchant.
        if (CollectionUtils.isEmpty(merchant.getPartners()) && StringUtils.isNotBlank(merchant.getParentMerchant())) {
            LOGGER.debug("Received notification for partner merchant: {} with parent: {}", merchant.getId().toString(),
                    merchant.getParentMerchant());
            MerchantUser parentMerchant = merchantUserBO.getUserById(merchant.getParentMerchant());
            if (parentMerchant != null) {
                partnerMerchant = merchant;
                merchant = parentMerchant;
            }
        }
        rootObject.put("merchant", merchant);
        rootObject.put("partnerMerchant", partnerMerchant);
        if (AppType.MERCHANT_APP.equals(template.getAppType())) {
            AdminAuthUser merchantAuthUser = adminAuthUserBO.findAdminAuthUserByMobile(merchant.getMobile());
            rootObject.put("merchantAuthUser", merchantAuthUser);
        }
        if (AppType.SALES_APP.equals(template.getAppType())) {
            String leadOwnerId = Optional.ofNullable(merchant.getParams()).map(Params::getLeadOwnerId).orElse(null);
            response.setLeadOwnerId(leadOwnerId);
            SalesUserProfile salesUserProfile = StringUtils.isNotBlank(leadOwnerId) ?
                    salesAgentBO.getSalesUserProfile(leadOwnerId) : null;
            rootObject.put("salesUserProfile", salesUserProfile);
        }
        rootObject.put("eventData", event.getEventData());

        // Preparing channels info map containing expression to get resolved value
        Map<String, Object> channelsInfo = createChannelsInfoExpressionMap(template);

        // Resolving template expressions
        response.setChannelsInfo(expressionResolver.resolveTemplateExpressionMap(channelsInfo, rootObject));
        response.setData(expressionResolver.resolveTemplateExpressionMap(template.getData(), rootObject));
        response.setResolveSuccess(true);

        // checking condition if any, for sending notification
        boolean sendNotification = true;
        if (StringUtils.isNotBlank(template.getCondition())) {
            rootObject.put("channelsInfo", response.getChannelsInfo());
            rootObject.put("data", response.getData());
            Object result = expressionResolver.evaluateExpression(template.getCondition(), rootObject, Object.class);
            sendNotification = checkConditionResult(result);
        }
        response.setSendNotification(sendNotification);

        return response;
    }

    private boolean checkConditionResult(Object result) {
        if (result instanceof Boolean) {
            return (Boolean) result;
        }
        if (result instanceof String) {
            return "true".equalsIgnoreCase(result.toString().trim());
        }
        if (result instanceof Collection) {
            return CollectionUtils.isNotEmpty((Collection<?>) result);
        }
        return false;
    }

    private Map<String, Object> createChannelsInfoExpressionMap(NotificationTemplate template) {
        Map<String, Object> channelsInfo = new HashMap<>();
        boolean isSalesApp = AppType.SALES_APP.equals(template.getAppType());
        for (String channel: template.getChannels()) {
            switch (channel) {
                case "push":
                    channelsInfo.put("device",
                            isSalesApp ? "#{salesUserProfile.deviceToken}" : "#{merchant.deviceToken}");
                    break;
                case "email":
                    channelsInfo.put("email", isSalesApp ? "#{salesUserProfile.email}" : "#{merchant.email}");
                    break;
                case "sms":
                case "whatsapp":
                    channelsInfo.put("mobile",
                            isSalesApp ? "#{salesUserProfile.mobile}" : "#{merchant.mobile}");
                    break;
                default:
                    LOGGER.error("Ignoring unknown channel {} for templateId: {}", channel, template.getId().toString());
            }
        }
        return channelsInfo;
    }

}
