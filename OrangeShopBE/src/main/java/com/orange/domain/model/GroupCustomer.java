package com.orange.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "group_customer")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GroupCustomer extends BaseEntity{

    @Size(max = 100)
    @Column(name = "group_name", length = 100)
    private String groupName;

    @Column(name = "required_conditions")
    private Double requiredConditions;

    @OneToMany(mappedBy = "group")
    @JsonIgnore
    private Set<Voucher> vouchers = new LinkedHashSet<>();

    public GroupCustomer(Long groupId) {
        this.setId(groupId);
    }
}