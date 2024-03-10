package com.orange.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.*;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;

import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "`order`")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Indexed
public class Order extends BaseEntity{

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @IndexedEmbedded
    private User user;

    @Size(max = 105)
    @NotNull
    @Column(name = "consignee_name", nullable = false, length = 105)
    @FullTextField
    private String consigneeName;

    @NotNull
    @Column(name = "consignee_phone", nullable = false)
    private String consigneePhone;

    @Column(name = "consignee_email")
    private String consigneeEmail;

    @Column(name = "to_address")
    private String toAddress;

    @Column(name = "to_ward")
    private String toWard;

    @Column(name = "to_district")
    private String toDistrict;

    @Column(name = "to_province")
    private String toProvince;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "estimated_delivery_time")
    private Date estimatedDeliveryTime;

    @Column(name = "completed_time")
    private Date completedTime;

    @Column(name = "cancel_time")
    private Date cancelTime;

    @Column(name = "wait_for_pay_time")
    private Date waitForPayTime;

    @Column(name = "transporting_time")
    private Date transportingTime;
    @Column(name = "delivery_in_progress_time")
    private Date deliveryInProgressTime;

    @Column(name = "wait_for_confirmation_time")
    private Date waitForConfirmationTime;

    @Column(name = "confirmed_time")
    private Date confirmedTime;

    @Column(name = "wait_for_cancel_time")
    private Date waitForCancelTime;

    @Column(name = "return_time")
    private Date returnTime;

    @Column(name = "wait_for_return_time")
    private Date waitForReturnTime;

    @Column(name = "packaging_time")
    private Date packagingTime;

    @Column(name = "pay_time")
    private Date payTime;

    @Column(name = "cancel_note", length = 500)
    private String cancelNote;

    @Column(name = "shipping_fee")
    private Double shippingFee;

    @Column(name = "voucher_discount")
    private Double voucherDiscount;

    @Column(name = "pre_order_status")
    private Long preOrderStatus;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private PaymentType paymentType;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private ShippingMethod shippingMethod;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private OrderStatus orderStatus;

    @Column(name = "order_total")
    private Double orderTotal;

    @Column(name = "shipping_code")
    @FullTextField
    private String shippingCode;
    @ManyToOne(fetch = FetchType.LAZY)
    private Address address;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voucher_id")
    private Voucher voucher;

    @OneToMany(mappedBy = "order")
    @JsonIgnore
    private Set<OrderDetail> orderDetails;

    public Order(Long orderId) {
        this.setId(orderId);
    }
}