package com.orange.controller.user;

import com.orange.domain.dto.NotificationDTO;
import com.orange.domain.model.User;
import com.orange.exception.GlobalException;
import com.orange.services.impl.WebsocketNotificationService;
import com.orange.utils.AccountUtils;
import com.orange.utils.SortUtils;
import com.orange.common.payload.Page;
import com.orange.common.payload.Result;
import com.orange.domain.dto.OrderDTO;
import com.orange.exception.EntityIsEmptyException;
import com.orange.payload.request.CancelOrderRequest;
import com.orange.payload.request.ReturnOrderRequest;
import com.orange.services.IOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("/order")
@RequiredArgsConstructor
public class  OrderController {

    private final IOrderService orderService;
    private final AccountUtils accountUtils;
    private final WebsocketNotificationService wsn;

    @GetMapping("")
    public Result<?> getAllOrders(@RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "20") int size,
                                  @RequestParam(defaultValue = "createDate,desc") String[] sort,
                                  @RequestParam Optional<Long> statusId) {
        Page<?> pages;
        if (statusId.isPresent()) {
            pages = this.orderService.fillAllByStatus(statusId.get(), PageRequest.of(page, size, SortUtils.getSort(sort)));
        } else {
            pages = this.orderService.fillAll(PageRequest.of(page, size, SortUtils.getSort(sort)));
        }
        return Result.result(HttpStatus.OK.value(), "Lấy dữ liệu order thành công!", pages);
    }

    @GetMapping("/filter")
    public Result<?> filterOrder(@RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "20") int size,
                                 @RequestParam(defaultValue = "createDate,desc") String[] sort,
                                 @RequestParam(required = false) String keyWord,
                                 @RequestParam(required = false) Long statusId,
                                 @RequestParam(required = false) String createDate,
                                 @RequestParam(required = false) Long id) throws ParseException {
        if (accountUtils.getUserOrAnonymousUser() == null) {
            throw new UsernameNotFoundException("Khong the xem don hang neu chua dang nhap");
        }
        return Result.result(
                HttpStatus.OK.value(),
                "Lấy dữ liệu thành công",
                orderService.filter(accountUtils.getUserId(), id, keyWord, statusId, createDate, PageRequest.of(page, size, SortUtils.getSort(sort))));
    }

    @GetMapping("/order-detail")
    public Result<?> getOrderById(@RequestParam(value = "id", defaultValue = "0") Optional<Long> id) {
        if (id.isPresent()) {
            OrderDTO orderDTO = this.orderService.findOneByUserAndOrderId(id.get(), accountUtils.getUserOrAnonymousUser());
            return Result.result(HttpStatus.OK.value(), "Lấy dữ liệu order thành công!", orderDTO);
        } else {
            throw GlobalException.throwException("common.not.blankOrNull.message");
        }
    }

    @PostMapping("/create-order")
    public Result<?> createOrder(@RequestBody OrderDTO orderDTO) {
        Long userId = accountUtils.getUserOrAnonymousUser() == null ? null: accountUtils.getUserId();
        OrderDTO result = this.orderService.create(orderDTO, userId);
        User user = accountUtils.getUserOrAnonymousUser();
        System.out.println(user);
//        if (user != null) {
//            wsn.pushAndSaveNotification(NotificationDTO.builder()
//                    .content("Tạo đơn hàng thành công!")
//                    .title("Thông báo đơn hàng: " + result.getId())
//                    .url("/user/tracking-page/" + result.getId())
//                    .isRead(false)
//                    .toTopic(user.getUsername())
//                    .topicUrl("/topic/" + user.getUsername())
//                    .build());
//        }
        return Result.result(HttpStatus.OK.value(), "Tạo mới order thành công!", result);
    }

    @PutMapping("/cancel-order")
    public Result<?> cancelOrder(@RequestBody CancelOrderRequest cancelOrderRequest) {
        OrderDTO result = this.orderService.requestCancelOrder(cancelOrderRequest);
        return Result.result(HttpStatus.OK.value(), "Tạo yêu cầu hủy đơn hàng thành công, đang chờ hệ thống xác nhận!", result);
    }

    @PutMapping("/undo-cancel/{id}")
    public Result<?> undoCancelOrderRequest(@PathVariable Optional<Long> id) {
        if (id.isPresent()) {
            return Result.result(HttpStatus.OK.value(), orderService.undoCancelOrderRequest(id.get()), null);
        } else {
            throw GlobalException.throwException("error.notfound");
        }
    }

    @PutMapping("/return-order")
    public Result<?> returnOrder(@RequestBody ReturnOrderRequest returnOrderRequest) {
        OrderDTO result = this.orderService.requestReturnOrder(returnOrderRequest);
        return Result.result(HttpStatus.OK.value(), "Tạo yêu cầu trả hàng thành công, đang chờ hệ thống xác nhận!", result);
    }
}
