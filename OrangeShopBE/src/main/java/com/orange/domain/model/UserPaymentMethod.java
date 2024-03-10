package com.orange.domain.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigInteger;
import java.time.LocalDate;

@Entity
@Table(name = "`user_payment_method`")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserPaymentMethod extends BaseEntity{
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User user;

//    @NotNull
//    @ManyToOne(fetch = FetchType.LAZY, optional = false)
//    private PaymentType paymentType;

    @Size(max = 105)
    @Column(name = "provider", length = 105)
    private String provider;

    @Column(name = "account_number")
    private BigInteger accountNumber;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Column(name = "is_default")
    private Boolean isDefault;

    @Column(name = "status")
    private Boolean status;
}