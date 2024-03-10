package com.orange.controller.admin;

import com.orange.common.payload.Result;
import com.orange.domain.dto.ProductDetailDTO;
import com.orange.domain.model.ModelProcedure.*;
import com.orange.services.IStoreProcedureService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("admin/procedure")
@RequiredArgsConstructor
public class AdminStoreProcedureController {
    private final IStoreProcedureService procedureService;

    @GetMapping("/get-count-all-status-by-date")
    public Result<?> getOrderStatus(
            @RequestParam(value= "fromDate",defaultValue = "2021-1-1") String fromDate,
            @RequestParam(value= "toDate",defaultValue = "2024-1-1") String toDate) {

        return Result.result(HttpStatus.OK.value(), "Lấy dữ liệu thành công!", procedureService.getCountAllStatus(fromDate, toDate));
    }

//    @GetMapping("/top-product-by-stt")
//    public Result<?> getTopProducts(@RequestParam(value= "status",defaultValue="1")             Integer status,
//                                         @RequestParam(value= "fromDate",defaultValue="2021-1-1")    String fromDate,
//                                         @RequestParam(value= "toDate",defaultValue="2023-1-1")      String toDate,
//                                         @RequestParam(value= "top",defaultValue="10")               Integer top) {
//        List<OgetTopProductByStt> result = procedureService.getTopProducts(status, fromDate, toDate, top);
//        return Result.result(HttpStatus.OK.value(), "Lấy dữ liệu thành công!",result);
//    }

    @GetMapping("/total-amount-by-stt")
    public Result<?> getTotalAmountByStatus(
            @RequestParam(value = "fromDate",required = false) String fromDate,
            @RequestParam(value = "toDate",required = false) String toDate,
            @RequestParam(value = "status") int status) {
        OgetTotalAmountQuantity result = procedureService.getTotalAmountByStatus(fromDate, toDate, status);
        return Result.result(HttpStatus.OK.value(), "Lấy dữ liệu thành công!",result);

    }

    @GetMapping("/get-count-orders-by-week")
    public Result<?> getCountOrdersByWeek(){
        List<OGetCountOrders> result = procedureService.getCountOrdersByWeek();
        return Result.result(HttpStatus.OK.value(), "Lấy dữ liệu thành công!",result);
    }

    @GetMapping("/get-total-amount-by-month")
    public Result<?> getTotalAmountByMonth(){
        List<Double> result = procedureService.getTotalAmountByMonth();
        return Result.result(HttpStatus.OK.value(), "Lấy dữ liệu thành công!",result);
    }

    @GetMapping("/get-top-productdetail")
    public Result<?> getTopProductsBy(
                                    @RequestParam(value= "status",defaultValue="1")             Integer status,
                                    @RequestParam(value= "fromDate",required = false)           String fromDate,
                                    @RequestParam(value= "toDate",required = false)             String toDate,
                                    @RequestParam(value= "top",defaultValue="8")               Integer top){
        List<ProductDetailDTO> result = procedureService.getTopProductDetails(fromDate,toDate,top,status);
        return Result.result(HttpStatus.OK.value(), "Lấy dữ liệu thành công!",result);
    }

}
