package com.orange.controller.user;

import com.orange.utils.AccountUtils;
import com.orange.common.payload.Result;
import com.orange.domain.model.ModelProcedure.OgetOrderByUserID;
import com.orange.services.IStoreProcedureService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@RequestMapping("user/procedure")
@RequiredArgsConstructor
public class StoreProcedure {
    private final IStoreProcedureService procedureService;
    private final AccountUtils accountUtils;

    @GetMapping("/get-order-status-by-id")
    public Result<?> getOrderStatusByid() {
        OgetOrderByUserID result = procedureService.getOrderByUserID(accountUtils.getUserId());
        return Result.result(HttpStatus.OK.value(), "Lấy dữ liệu thành công!",result);
    }
}
