package com.orange.controller.admin;

import com.orange.exception.GlobalException;
import com.orange.utils.SortUtils;
import com.orange.common.payload.Page;
import com.orange.common.payload.Result;
import com.orange.domain.dto.OrderDTO;
import com.orange.exception.EntityInvalid;
import com.orange.payload.request.UpdateOrderStatus;
import com.orange.services.IOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("/admin/order")
@RequiredArgsConstructor
//@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminOrderController {

    private final IOrderService orderService;

    @GetMapping("")
    public Result<?> getAllOrdersForAdmin(@RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "20") int size,
                                          @RequestParam(defaultValue = "createDate,desc") String[] sort,
                                          @RequestParam Optional<Long> statusId) {
        Page<?> pages;
        if (statusId.isPresent()) {
            pages = this.orderService.fillAllByStatusForAdmin(statusId.get(), PageRequest.of(page, size, SortUtils.getSort(sort)));
        } else {
            pages = this.orderService.fillAllForAdmin(PageRequest.of(page, size, SortUtils.getSort(sort)));
        }
        return Result.result(HttpStatus.OK.value(), "Lấy dữ liệu order thành công!", pages);
    }

    @GetMapping("/search")
    public Result<?> searchOrder(@RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "20") int size,
                                 @RequestParam(defaultValue = "createDate,desc") String[] sort,
                                 @RequestParam(required = false) String keyWord,
                                 @RequestParam Optional<Long> statusId){
        return Result.result(
                HttpStatus.OK.value(),
                "Lấy dữ liệu thành công",
                orderService.search(keyWord, statusId, PageRequest.of(page, size, SortUtils.getSort(sort))));
    }

    @GetMapping("/filter")
    public Result<?> filterOrder(@RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "20") int size,
                                 @RequestParam(defaultValue = "createDate,desc") String[] sort,
                                 @RequestParam(required = false) String keyWord,
                                 @RequestParam(required = false) Long statusId,
                                 @RequestParam(required = false) String createDate,
                                 @RequestParam(required = false) Long id) {
        return Result.result(
                HttpStatus.OK.value(),
                "Lấy dữ liệu thành công",
                orderService.filter(null, id, keyWord, statusId, createDate, PageRequest.of(page, size, SortUtils.getSort(sort))));
    }

    @GetMapping("/order-detail")
    public Result<?> findById(@RequestParam Optional<Long> id) {
        if (id.isPresent()){
            return Result.result(HttpStatus.OK.value(), "Lấy dữ liệu thành công", orderService.findOrderViewById(id.get()));
        } else {
            throw GlobalException.throwException("error.missing.parameter");
        }
    }
    @PostMapping
    public Result<?> createOrder(@RequestBody OrderDTO orderDTO) {
        OrderDTO result = this.orderService.create(orderDTO);
        return Result.result(HttpStatus.OK.value(), "Tạo mới order thành công!", result);
    }

    @PutMapping("/update")
    public Result<?> updateOrder(@RequestBody OrderDTO orderDTO) {
        OrderDTO result = this.orderService.update(orderDTO);
        return Result.result(HttpStatus.OK.value(), "Cập nhật order thành công!", result);
    }

    @PutMapping("/update-status")
    public Result<?> updateOrderStatus(@RequestBody UpdateOrderStatus orderStatus) {
        OrderDTO result = this.orderService.updateOrderStatus(orderStatus);
        return Result.result(HttpStatus.OK.value(), "Cập nhật trạng thái order thành công!", result);
    }

    @PutMapping("/confirm-And-Shipping/{orderId}")
    public Result<?> confirmAndOrderShipping(@PathVariable Long orderId) {
        OrderDTO result = this.orderService.confirmAndOrderShipping(orderId);
        return Result.result(HttpStatus.OK.value(), "Đã xác nhận đơn hàng và gửi yêu cầu đến bên vận chuyển!", result);
    }
    @PutMapping("/approve-cancel-order/{orderId}")
    public Result<?> cancelOrder(@PathVariable Long orderId) {
        OrderDTO result = this.orderService.approveCancelOrder(orderId);
        return Result.result(HttpStatus.OK.value(), "Đã xác nhận hủy đơn hàng thành công!", result);
    }

    @PutMapping("/approve-return-order/{orderId}")
    public Result<?> returnOrder(@PathVariable Long orderId) {
        OrderDTO result = this.orderService.approveReturnOrder(orderId);
        return Result.result(HttpStatus.OK.value(), "Cập nhật trạng thái order thành công!", result);
    }

    @GetMapping("/reindex")
    public Result<?> reindexProduct() throws InterruptedException {
        orderService.indexData();
        return Result.success();
    }
}
