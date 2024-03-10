package com.orange.domain.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "`user_address`")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserAddress extends BaseEntity{

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User user;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Address address;

    @Column(name = "phone")
    private String phone;

    @Column(name = "name")
    private String name;

    @Column(name = "is_default")
    private Boolean isDefault;

    @Column(name = "status")
    private Boolean status;

}