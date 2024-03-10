package com.orange.controller.user;

import com.orange.utils.AccountUtils;
import com.orange.utils.SortUtils;
import com.orange.common.payload.Result;
import com.orange.payload.request.CheckVoucherRequest;
import com.orange.services.IVoucherService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/voucher")
@RequiredArgsConstructor
public class VoucherController {

    private final IVoucherService voucherService;

    @GetMapping("")
    public Result<?> getAllVoucher(@RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "20") int size,
                                   @RequestParam(defaultValue = "startDate,desc") String[] sort) {

        return Result.result(
                HttpStatus.OK.value(),
                "Lấy mã giảm giá cho khách hàng thành công",
                voucherService.fillAll(PageRequest.of(page, size, SortUtils.getSort(sort))));
    }

    @PostMapping("/check-voucher")
    public Result<?> checkVoucher(@RequestBody CheckVoucherRequest checkVoucherRequest) {
        checkVoucherRequest.setUsername(AccountUtils.getUsername());
        return voucherService.checkVoucher(checkVoucherRequest);
    }
}
