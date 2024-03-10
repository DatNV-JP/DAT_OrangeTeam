package com.orange.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.*;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField;

import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "`user`")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Indexed
public class User extends BaseEntity implements Serializable {

    @Size(max = 50)
    @NotNull
    @Column(name = "username", nullable = false, length = 50)
    @FullTextField(name = "username_search")
    @KeywordField(sortable = Sortable.YES)
    private String username;

    @Size(max = 105)
    @NotNull
    @Column(name = "password_hash", nullable = false, length = 105)
    private String passwordHash;

    @Size(max = 105)
    @Column(name = "email", length = 105)
    private String email;

    @Size(max = 105)
    @Column(name = "avatar", length = 105)
    private String avatar;

    @Column(name = "phone")
    private String phone;

    @Size(max = 45)
    @Column(name = "first_name", length = 45)
    @FullTextField(name = "firstName_search")
    @KeywordField(sortable = Sortable.YES)
    private String firstName;

    @Size(max = 45)
    @Column(name = "last_name", length = 45)
    @FullTextField(name = "lastName_search")
    @KeywordField(sortable = Sortable.YES)
    private String lastName;

    @Column(name = "activate")
    @GenericField(sortable = Sortable.YES)
    private Boolean activate;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private Set<UserPaymentMethod> userPaymentMethods;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private Set<UserReview> userReviews;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private Set<UserAddress> userAddresses;

    @ManyToMany
    @JoinTable(name = "account_authority", joinColumns = {@JoinColumn(name = "account_id")},
            inverseJoinColumns = {@JoinColumn(name = "role_id")})
    private Set<Authority> roles = new HashSet<>();

    @ManyToMany(mappedBy = "users")
    private Set<Voucher> vouchers = new HashSet<>();

    @OneToMany(orphanRemoval = true)
    @JoinColumn(name = "user_id")
    private Set<UserVoucher> userVouchers = new HashSet<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private OrderStat orderStat;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private Set<Order> orders = new HashSet<>();


    public User(Long userID) {
        this.setId(userID);
    }
}