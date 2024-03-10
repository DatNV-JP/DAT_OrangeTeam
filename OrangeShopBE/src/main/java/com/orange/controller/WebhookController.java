package com.orange.controller;

import com.orange.services.IOrderService;
import com.orange.utils.JsonUtils;
import com.orange.common.payload.Result;
import com.orange.common.template.EmailTemplate;
import com.orange.common.template.EmailTemplateOrder;
import com.orange.domain.model.*;
import com.orange.exception.EntityNotFoundException;
import com.orange.payload.request.ghn.GHNWebhookRequest;
import com.orange.repositories.IOrderRepository;
import com.orange.repositories.IProductDetailRepository;
import com.orange.repositories.IVoucherRepository;
import com.orange.services.impl.MaillerServiceImpl;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("webhook")
@RequiredArgsConstructor
public class WebhookController {
    private final IOrderRepository orderRepository;
    private final IVoucherRepository voucherRepository;
    private final IProductDetailRepository productDetailRepository;
    private final MaillerServiceImpl mailerService;
    private final IOrderService orderService;
    private static final Logger logger = LoggerFactory.getLogger(WebhookController.class);
    @PostMapping("webhook-ghn")
    public Result<?> handleWebhook(@RequestBody GHNWebhookRequest payload) {
        // Handle the webhook payload here
        Optional<Order> orderFind = orderRepository.findByShippingCodeLike(payload.getOrderCode());
        if (orderFind.isPresent()){
            Order order = orderFind.get();
            String status = payload.getStatus();
            switch (status) {
                case "transporting" -> {
                    order.setOrderStatus(new OrderStatus(EOrderStatus.TRANSPORTING.getId()));
                    order.setTransportingTime(new Date());
                    order.setModifiedBy("GHN");
                    order.setModifiedDate(new Date());
                    orderRepository.save(order);
                    if (order.getConsigneeEmail() != null) {
                        String body = EmailTemplateOrder.generateHtmlOrderAfterTransport(order);
                        mailerService.queue(order.getConsigneeEmail(), "[THÔNG BÁO] Xác nhận đơn hàng từ " + EmailTemplate.SHOP_NAME + " đã được bàn giao cho phía giao hàng", body);
                    }
                    if (order.getUser() != null) {
                        orderService.trackingPageNotificationToCustomer(order,
                                "Đơn hàng của bạn đã được bàn giao cho bên vận chuyển",
                                "[THÔNG BÁO] đơn hàng " + order.getId() + " đã được bàn giao");
                    }
                    orderService.trackingPageNotificationToAdmin(order,
                            "Đơn hàng " + order.getId() + " đã được bàn giao cho bên vận chuyển",
                            "[THÔNG BÁO] đơn hàng " + order.getId() + " đã được bàn giao");
                }
                case "delivering" -> {
                    order.setOrderStatus(new OrderStatus(EOrderStatus.DELIVERY_IN_PROGRESS.getId()));
                    order.setDeliveryInProgressTime(new Date());
                    order.setModifiedBy("GHN");
                    order.setModifiedDate(new Date());
                    orderRepository.save(order);
                    if (order.getConsigneeEmail() != null) {
                        String body = EmailTemplateOrder.generateHtmlOrderAfterDeliveryEmail(order);
                        mailerService.queue(order.getConsigneeEmail(), "[THÔNG BÁO] Xác nhận đơn hàng từ " + EmailTemplate.SHOP_NAME + " đang được giao tới Quý khách", body);
                    }
                    if (order.getUser() != null) {
                        orderService.trackingPageNotificationToCustomer(order,
                                "Đơn hàng " + order.getId() + " đang được giao tới bạn",
                                "[THÔNG BÁO] đơn hàng " + order.getId() + " đang giao");
                    }
                    orderService.trackingPageNotificationToAdmin(order,
                            "Đơn hàng " + order.getId() + " đang được giao tới tới khách hàng của bạn",
                            "[THÔNG BÁO] từ dịch vụ vận chuyển Giao Hàng Nhanh");
                }
                case "delivered" -> {
                    order.setOrderStatus(new OrderStatus(EOrderStatus.COMPLETED.getId()));
                    order.setCompletedTime(new Date());
                    order.setModifiedBy("GHN");
                    order.setModifiedDate(new Date());
                    orderRepository.save(order);
                    if (order.getConsigneeEmail() != null) {
                        String body = EmailTemplateOrder.generateHtmlOrderAfterCompleted(order);
                        mailerService.queue(order.getConsigneeEmail(), "[THÔNG BÁO] Giao hàng thành công từ  " + EmailTemplate.SHOP_NAME, body);
                    }
                    if (order.getUser() != null) {
                        orderService.trackingPageNotificationToCustomer(order,
                                "Đơn hàng " + order.getId() + " của bạn đã được giao thành công",
                                "[THÔNG BÁO] đơn hàng " + order.getId() + " đã được giao");
                    }
                    orderService.trackingPageNotificationToAdmin(order,
                            "Đơn hàng " + order.getId() + " đã giao hàng thành công",
                            "[THÔNG BÁO] từ dịch vụ vận chuyển Giao Hàng Nhanh");
                }
                case "delivery_fail" -> {
                    order.setOrderStatus(new OrderStatus(EOrderStatus.DELIVERY_IN_PROGRESS.getId()));
                    order.setDeliveryInProgressTime(new Date());
                    order.setModifiedBy("GHN");
                    order.setModifiedDate(new Date());
                    orderRepository.save(order);
                    if (order.getConsigneeEmail() != null) {
                        String body = EmailTemplateOrder.generateHtmlOrderAfterCompleted(order);
                        mailerService.queue(order.getConsigneeEmail(), "[THÔNG BÁO] Giao hàng không thành công từ  " + EmailTemplate.SHOP_NAME, body);
                    }
                    if (order.getUser() != null) {
                        orderService.trackingPageNotificationToCustomer(order,
                                "Đơn hàng " + order.getId() + " của bạn không giao thành công",
                                "[THÔNG BÁO] đơn hàng " + order.getId() + " không giao thành công");
                    }
                    orderService.trackingPageNotificationToAdmin(order,
                            "Đơn hàng " + order.getId() + " đã giao hàng không thành công",
                            "[THÔNG BÁO] từ dịch vụ vận chuyển Giao Hàng Nhanh");
                }
                case "returned" -> {
                    order.setOrderStatus(new OrderStatus(EOrderStatus.CANCEL.getId()));
                    order.setCancelTime(new Date());
                    order.setModifiedBy("GHN");
                    order.setModifiedDate(new Date());
                    orderRepository.save(order);
                    List<ProductDetail> productDetails = order.getOrderDetails().stream()
                            .map(od -> {
                                ProductDetail pd = od.getProductDetail();
                                pd.setQuantity(pd.getQuantity() + od.getQuantity());
                                return pd;
                            }).toList();
                    this.productDetailRepository.saveAll(productDetails);
                    if (order.getVoucher() != null) {
                        Voucher reSetQtyVoucher = order.getVoucher();
                        reSetQtyVoucher.setUsageLimit(reSetQtyVoucher.getUsageLimit() + 1);
                        voucherRepository.save(reSetQtyVoucher);
                    }
                    if (order.getConsigneeEmail() != null) {
                        String body = EmailTemplateOrder.generateHtmlOrderCancelByDeleveriFail(order);
                        mailerService.queue(order.getConsigneeEmail(), "[THÔNG BÁO] Hủy đơn hàng do giao hàng không thành công  " + EmailTemplate.SHOP_NAME, body);
                    }
                    if (order.getUser() != null) {
                        orderService.trackingPageNotificationToCustomer(order,
                                "Đơn hàng " + order.getId() + " của bạn đã bị hủy do giao hàng không thành công",
                                "[THÔNG BÁO] hủy đơn hàng " + order.getId());
                    }
                    orderService.trackingPageNotificationToAdmin(order,
                            "Đơn hàng " + order.getId() + " đã bị hủy do giao hàng không thành công",
                            "[THÔNG BÁO] từ dịch vụ vận chuyển Giao Hàng Nhanh");
                }
                default -> {
                }
            }
            logger.info("From Webhook GHN: " + JsonUtils.toJson(payload));
            return Result.result(HttpStatus.OK.value(), "Thanh cong", payload);
        } else {
            logger.info(JsonUtils.toJson(payload));
            logger.info("Khong tim thay don hang co ma shipping code: " + payload.getOrderCode());
            throw new EntityNotFoundException("Khong tim thay don hang co ma shipping code: " + payload.getOrderCode());
        }
    }
}
