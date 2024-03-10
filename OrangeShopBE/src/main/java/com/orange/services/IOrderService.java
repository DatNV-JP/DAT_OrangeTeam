package com.orange.services;

import com.orange.common.payload.Page;
import com.orange.domain.dto.OrderDTO;
import com.orange.domain.dto.OrderViewDTO;
import com.orange.domain.model.Order;
import com.orange.domain.model.User;
import com.orange.payload.request.CancelOrderRequest;
import com.orange.payload.request.ReturnOrderRequest;
import com.orange.payload.request.UpdateOrderStatus;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

public interface IOrderService extends BaseService<OrderDTO, Long> {

    OrderDTO create(OrderDTO dto, Long userId);

    OrderDTO createForUser(OrderDTO dto);

    Page<?> filter(Long uId, Long id, String keyWord, Long statusId, String createDate, Pageable pageable);

    Page<?> fillAllByStatus(Long oStatusId, Pageable pageable);

    Page<?> fillAllByStatusForAdmin(Long oStatusId, Pageable pageable);

    Page<?> fillAllForAdmin(Pageable pageable);

    OrderDTO findOneByUserAndOrderId(Long oId,User user);

    @Transactional
    OrderDTO updateOrderStatus(UpdateOrderStatus orderStatus);

    OrderDTO approveReturnOrder(Long id);

    OrderDTO confirmAndOrderShipping(Long id);

    OrderDTO approveCancelOrder(Long id);

    OrderDTO requestCancelOrder(CancelOrderRequest cancelOrderRequest);

    String undoCancelOrderRequest(Long orderId);

    OrderDTO requestReturnOrder(ReturnOrderRequest returnOrderRequest);

    OrderViewDTO findOrderViewById(Long id);

    void trackingPageNotificationToCustomer(Order order, String content, String title);

    void trackingPageNotificationToAdmin(Order order, String content, String title);

    Page<?> search(String keyWord, Optional<Long> statusId, Pageable pageable);

    void indexData() throws InterruptedException;
}
