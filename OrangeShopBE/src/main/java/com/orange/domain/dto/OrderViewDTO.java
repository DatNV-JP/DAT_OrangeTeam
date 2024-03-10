package com.orange.domain.dto;

import com.orange.domain.model.OrderStatus;
import com.orange.domain.model.PaymentType;
import com.orange.domain.model.ShippingMethod;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Set;

@Data
public class OrderViewDTO {
    private Long id;
    private String createDate;
    private Integer productQuantity;
    private String consigneeName;
    private String consigneePhone;
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
    private OrderStatus orderStatus;
    private AddressDTO address;
    private PaymentType paymentType;
    private ShippingMethod shippingMethod;
    private String consigneeEmail;
    private String cancelNote;
    private Double shippingFee;
    private Double voucherDiscount;
    private Long preOrderStatus;
    private Long voucherId;
    private Set<OrderDetailViewDTO> orderDetailViews;
}
