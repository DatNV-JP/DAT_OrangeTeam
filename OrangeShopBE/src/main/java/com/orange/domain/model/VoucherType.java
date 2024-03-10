package com.orange.domain.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "voucher_type")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VoucherType extends BaseEntity{

    @Size(max = 255)
    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(orphanRemoval = true)
    @JoinColumn(name = "voucher_type_id")
    private Set<Voucher> vouchers = new LinkedHashSet<>();

    public VoucherType(Long id) {
        this.setId(id);
    }
}