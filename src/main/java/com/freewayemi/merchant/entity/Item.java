package com.freewayemi.merchant.entity;

import com.freewayemi.merchant.commons.entity.BaseEntity;
import com.freewayemi.merchant.dto.Construct;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.*;

@Document(collection = "incentives")
@Data
@EqualsAndHashCode(callSuper = true)
public class Item extends BaseEntity {
    public String incentiveId;

    public String status;
    public Instant endDate;
    public Instant startDate;
    public List<Construct> construct;
    public String constructType;

    public String name;
}
