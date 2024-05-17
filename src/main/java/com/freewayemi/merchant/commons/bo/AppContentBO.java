package com.freewayemi.merchant.commons.bo;

import com.freewayemi.merchant.commons.dto.AppContentCard;
import com.freewayemi.merchant.commons.entity.AppContents;
import com.freewayemi.merchant.commons.entity.Params;
import com.freewayemi.merchant.commons.repository.AppContentsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class AppContentBO {
    private final AppContentsRepository appContentsRepository;

    @Autowired
    public AppContentBO(AppContentsRepository appContentsRepository) {
        this.appContentsRepository = appContentsRepository;
    }

    public List<AppContentCard> getStatSection() {
        Optional<List<AppContents>> appContentsOp = appContentsRepository
                .findAllByContentTypeAndActive("stat", true);
        return appContentsOp.map(contents -> contents.stream().map(appContents -> AppContentCard.builder()
                .icon(appContents.getIcon())
                .landing(appContents.getLanding())
                .text(appContents.getText())
                .subText(appContents.getSubText())
                .title(appContents.getTitle())
                .type(appContents.getType())
                .build())
                .collect(Collectors.toList())).orElse(null);
    }

    public Optional<AppContents> findByContentTypeAndActive(String info, boolean b) {
        return appContentsRepository.findByContentTypeAndActive("info", true);
    }

    public Optional<List<AppContents>> findAllByContentTypeAndActiveAndCategory(String knowledge_custom, boolean b,
                                                                                String category) {
        return appContentsRepository.findAllByContentTypeAndActiveAndCategory(knowledge_custom, b, category);
    }

    public Optional<List<AppContents>> findAllByContentTypeAndActiveAndUserStatusOrUserStatus(String knowledge,
                                                                                              boolean b, String status,
                                                                                              String referred) {
        return appContentsRepository
                .findAllByContentTypeAndActiveAndUserStatusOrUserStatus(knowledge, b, status, referred);
    }

    public Optional<List<AppContents>> findAllByContentTypeAndActiveAndUserStatus(String knowledge, boolean b,
                                                                                  String status) {
        return appContentsRepository.findAllByContentTypeAndActiveAndUserStatus(knowledge, b, status);
    }

    public AppContents findNudge(String entityId) {
        List<AppContents> list =
                appContentsRepository.findByEntityIdAndActiveAndContentType(entityId, true, "nudge").orElse(null);
        if (null != list && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    public AppContents findNudgeBaseCategoryAndBrandId(String merchantCategory, Params params) {
        DateTimeFormatter formatter = DateTimeFormatter
                .ofPattern("yyyy-MM-dd")
                .withZone(ZoneOffset.systemDefault());
        String date = formatter.format(Instant.now());
        LocalDate localDate = LocalDate.parse(date);
        Instant from = localDate.atStartOfDay(ZoneId.of("Asia/Kolkata")).toInstant();
        List<AppContents> contents = appContentsRepository.findByCategoryAndActiveAndContentTypeAndExpiry(merchantCategory, true, "nudge", from).orElse(null);
        if (null != contents && contents.size() > 0) {
            List<AppContents> output = new ArrayList<>();
            List<String> brandIds = CollectionUtils.isEmpty(params.getBrandIds()) ? StringUtils.hasText(params.getBrandId()) ? Collections.singletonList(params.getBrandId()) : null : params.getBrandIds();
            for (AppContents appContents : contents) {
                if (!CollectionUtils.isEmpty(brandIds) && !CollectionUtils.isEmpty(appContents.getBrandIds())) {
                    Optional<AppContents> optional = contents.stream().filter(content ->
                            !CollectionUtils.isEmpty(content.getBrandIds()) &&
                                    content.getBrandIds().containsAll(brandIds)).findFirst();
                    if (optional.isPresent()) output.add(appContents);
                } else {
                    output.add(appContents);
                }
            }
            return output.get(0);
        }
        return null;
    }

    public List<AppContents> findNudgeBaseCategory(String merchantCategory) {
        DateTimeFormatter formatter = DateTimeFormatter
                .ofPattern("yyyy-MM-dd")
                .withZone(ZoneOffset.systemDefault());
        String date = formatter.format(Instant.now());
        LocalDate localDate = LocalDate.parse(date);
        Instant from = localDate.atStartOfDay(ZoneId.of("Asia/Kolkata")).toInstant();
        return appContentsRepository.findByCategoryAndActiveAndContentTypeAndExpiry(merchantCategory, true, "nudge", from).orElse(null);
    }

}
