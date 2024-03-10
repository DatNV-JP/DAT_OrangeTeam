package com.orange.domain.model;

import lombok.*;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "voucher")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Indexed
public class Voucher extends BaseEntity{

    @Size(max = 6)
    @NotNull
    @Column(name = "code", unique = true, nullable = false, length = 6)
    @FullTextField
    private String code;

    @Size(max = 500)
    @Column(name = "description", length = 500)
    private String description;

    @Size(max = 50)
    @NotNull
    @Column(name = "name", nullable = false, length = 50)
    @FullTextField(name = "name_search")
    private String name;

    @Column(name = "usage_limit")
    private Integer usageLimit;

    @Column(name = "used")
    private Integer used;

    @Column(name = "usage_limit_per_user")
    private Integer usageLimitPerUser = 1;

    @Column(name = "minimum_order_value")
    private Double minimumOrderValue;

    @Column(name = "start_date")
    @GenericField(sortable = Sortable.YES)
    private Instant startDate;

    @Column(name = "end_date")
    @GenericField(sortable = Sortable.YES)
    private Instant endDate;

    @Column(name = "discount_amount")
    private Double discountAmount;

    @Column(name = "max_discount_amount")
    private Double maxDiscountAmount;

    @Column(name = "status")
    @GenericField(sortable = Sortable.YES)
    private Integer status = 1;

    @Column(name = "is_percent")
    private Boolean isPercent;

    @Column(name = "is_global")
    private Boolean isGlobal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voucher_type_id")
    private VoucherType voucherType;


    @OneToMany(orphanRemoval = true)
    @JoinColumn(name = "voucher_id")
    private Set<Order> orders = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "user_voucher", joinColumns = {@JoinColumn(name = "voucher_id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id")})
    private Set<User> users = new HashSet<>();

    @OneToMany(orphanRemoval = true)
    @JoinColumn(name = "voucher_id")
    private Set<UserVoucher> userVouchers = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private GroupCustomer group;

    public Voucher(Long voucherId) {
        this.setId(voucherId);
    }
}