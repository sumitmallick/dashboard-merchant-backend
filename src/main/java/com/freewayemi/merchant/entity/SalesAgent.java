package com.freewayemi.merchant.entity;

import com.freewayemi.merchant.commons.entity.BaseEntity;
import com.freewayemi.merchant.dto.request.Account;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "sales_agents")
@Data
@EqualsAndHashCode(callSuper = true)
public class SalesAgent extends BaseEntity {
    private String firstName;
    private String lastName;
    private String email;
    private String mobile;
    private Account account;
}
