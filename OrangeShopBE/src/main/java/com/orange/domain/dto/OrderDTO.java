package com.orange.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.orange.domain.model.OrderStatus;
import com.orange.domain.model.PaymentType;
import com.orange.domain.model.ShippingMethod;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;

/**
 * A DTO for the {@link com.orange.domain.model.Order} entity
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OrderDTO extends BaseEntityDTO implements Serializable {

    @Size(max = 105)
    @NotNull
    private String consigneeName;
    @NotNull
    private String consigneePhone;
    private String consigneeEmail;
    private String toAddress;
    private String toWard;
    private String toDistrict;
    private String toProvince;
    private String description;
    private Date estimatedDeliveryTime;
    private Date completedTime;
    private Date cancelTime;
    private Date waitForPayTime;
    private Date deliveryInProgressTime;
    private Date waitForConfirmationTime;
    private Date confirmedTime;
    private Date waitForCancelTime;
    private Date returnTime;
    private Date waitForReturnTime;
    private Date packagingTime;
    private Double orderTotal;
    private String shippingCode;
    private Long userId;
    private PaymentType paymentType;
    private ShippingMethod shippingMethod;
    private OrderStatus orderStatus;
    private AddressDTO address;
    private String cancelNote;
    private Long voucherId;
    private Double shippingFee;
    private Double voucherDiscount;
    private Long preOrderStatus;
    private Set<OrderDetailDTO> orderDetails;
}