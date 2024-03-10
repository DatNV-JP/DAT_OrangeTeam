package com.orange.domain.dto;

import com.orange.domain.model.GroupCustomer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.Instant;
import java.util.Set;

/**
 * A DTO for the {@link com.orange.domain.model.Voucher} entity
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoucherDto extends BaseEntityDTO implements Serializable {
    @Size(max = 6)
    @NotNull
    private String code;
    @Size(max = 500)
    private String description;
    @Size(max = 50)
    @NotNull
    private String name;
    private Integer usageLimit;
    private Integer usageLimitPerUser = 1;
    private Integer used;
    private Double minimumOrderValue;
    private Instant startDate;
    private Instant endDate;
    private Double discountAmount;
    private Double maxDiscountAmount;
    private Integer status;
    private Boolean isPercent;
    private Boolean isGlobal;
    private Long voucherTypeId;
    private Long groupId;
    private Set<Long> userIds;
}