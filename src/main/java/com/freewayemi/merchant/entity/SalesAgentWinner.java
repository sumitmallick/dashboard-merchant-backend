package com.freewayemi.merchant.entity;

import com.freewayemi.merchant.commons.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "sales_agent_winners")
@Data
@EqualsAndHashCode(callSuper = true)
public class SalesAgentWinner extends BaseEntity {
    private String email;
    private Boolean active;
}
