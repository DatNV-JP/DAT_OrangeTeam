package com.orange.domain.model;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "user_voucher")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserVoucher implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Id
    @ManyToOne
    @JoinColumn(name = "voucher_id")
    private Voucher voucher;
}