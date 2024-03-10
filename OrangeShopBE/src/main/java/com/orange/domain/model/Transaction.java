package com.orange.domain.model;

import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.*;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Long transactionId;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @NotNull
    @Column(name = "transaction_ref")
    private String transactionRef;

    @NotNull
    @Column(name = "transaction_res_code")
    private String transationResCode;

    @NotNull
    @Column(name = "transaction_status")
    private Integer transactionStatus; // 0= pending, 1= success, 2= success

    @NotNull
    @Column(name = "transaction_no")
    private Long transactionNo;

    @Column(name = "bank_tran_no")
    private String bankTranNo;

    @NotNull
    @Column(name = "bank_code")
    private String bankCode;

    @NotNull
    @Column(name = "amount")
    private Double amount;

    @NotNull
    @Column(name = "currency_code")
    private String currencyCode = "VND";

    @Column(name = "pay_date")
    private Timestamp payDate;
}
