package com.orange.domain.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Entity
@Table(name = "order_stats")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderStat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "total_orders")
    private Integer totalOrders;

    @Column(name = "total_amount", precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @NotNull
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

}