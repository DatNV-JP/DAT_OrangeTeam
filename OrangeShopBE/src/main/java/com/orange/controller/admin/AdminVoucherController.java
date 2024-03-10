package com.orange.controller.admin;

import com.orange.exception.GlobalException;
import com.orange.utils.SortUtils;
import com.orange.common.payload.Result;
import com.orange.domain.dto.VoucherDto;
import com.orange.exception.EntityIsEmptyException;
import com.orange.payload.request.CheckVoucherRequest;
import com.orange.services.IVoucherService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("/admin/voucher")
@RequiredArgsConstructor
public class AdminVoucherController {

    private final IVoucherService voucherService;

    @GetMapping("")
    public Result<?> getAllVoucher(@RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "20") int size,
                                   @RequestParam(defaultValue = "startDate,desc") String[] sort,
                                   @RequestParam(defaultValue = "1") Long status) {
        return Result.result(
                HttpStatus.OK.value(),
                "Lấy dữ liệu thành công",
                voucherService.fillAllForAdmin(status, PageRequest.of(page, size, SortUtils.getSort(sort))));
    }

    @GetMapping("/search")
    public Result<?> searchVoucher(@RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "20") int size,
                                   @RequestParam(defaultValue = "createDate,desc") String[] sort,
                                   @RequestParam(required = false) Integer status,
                                   @RequestParam(required = false) String keyWord) {
        return Result.result(
                HttpStatus.OK.value(),
                "Lấy dữ liệu thành công",
                voucherService.search(status, keyWord, PageRequest.of(page, size, SortUtils.getSort(sort))));
    }

    @GetMapping("/get-voucher-for-user")
    public Result<?> getAllVoucherFoUser(@RequestParam(required = false) Long userId) {
        return Result.result(
                HttpStatus.OK.value(),
                "Lấy dữ liệu thành công",
                voucherService.adminFindVoucherForUser(userId));
    }

    @GetMapping("/users-by-group")
    public Result<?> getAllUserByGroup(@RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "20") int size,
                                       @RequestParam(defaultValue = "id,desc") String[] sort,
                                       @RequestParam(defaultValue = "0") Long groupId) {
        return Result.result(
                HttpStatus.OK.value(),
                "Lấy dữ liệu thành công",
                voucherService.fillAllUserByGroupForAdmin(groupId, PageRequest.of(page, size, SortUtils.getSort(sort))));
    }

    @GetMapping("/get-groups")
    public Result<?> getGroups() {
        return Result.result(
                HttpStatus.OK.value(),
                "Lấy dữ liệu thành công",
                voucherService.fillAllGroupCustomer());
    }

    @GetMapping("/by-id")
    public Result<?> getVoucherById(@RequestParam Optional<Long> id) {
        if (id.isPresent()) {
            return Result.result(
                    HttpStatus.OK.value(),
                    "Lấy dữ liệu thành công",
                    voucherService.findById(id.get()));
        } else {
            throw GlobalException.throwException("error.missing.parameter");
        }
    }

    @PostMapping("/check-voucher")
    public Result<?> checkVoucher(@RequestBody CheckVoucherRequest checkVoucherRequest) {
        return voucherService.checkVoucher(checkVoucherRequest);
    }

    @PostMapping("/create")
    public Result<?> createVoucher(@RequestBody VoucherDto voucherDto) {
        return Result.result(
                HttpStatus.OK.value(),
                "Tạo khuyến mãi thành công",
                voucherService.create(voucherDto));
    }

    @PutMapping("/update")
    public Result<?> updateVoucher(@RequestBody VoucherDto voucherDto) {
        return Result.result(
                HttpStatus.OK.value(),
                "Cập nhật khuyến mãi thành công",
                voucherService.update(voucherDto));
    }

    @PutMapping("/reactivate/{id}")
    public Result<?> reactivateVoucher(@PathVariable Optional<Long> id) {
        if (id.isPresent()) {
        return Result.result(
                HttpStatus.OK.value(),
                "Tái kích hoạt thành công",
                voucherService.reactivate(id.get()));
        } else {
            throw new EntityIsEmptyException("Id trống");
        }
    }

    @DeleteMapping("/delete/{id}")
    public Result<?> deleteVoucher(@PathVariable Long id) {
        return Result.result(
                HttpStatus.OK.value(),
                "Xóa khuyến mãi thành công",
                voucherService.delete(id));
    }

    @GetMapping("/reindex")
    public Result<?> reindexProduct() throws InterruptedException {
        voucherService.indexData();
        return Result.success();
    }
}
