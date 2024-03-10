package com.orange.services;

import com.orange.domain.dto.ProductDetailDTO;
import com.orange.domain.model.ModelProcedure.*;

import java.util.List;

public interface IStoreProcedureService {
    OgetCountAllStatus getCountAllStatus(String fromDate, String toDate);
    List<OgetTopProductByStt> getTopProducts(Integer status, String fromDate, String toDate, Integer top);
    OgetTotalAmountQuantity getTotalAmountByStatus(String fromDate, String toDate, int status);

    List<OGetCountOrders> getCountOrdersByWeek();

    List<Double> getTotalAmountByMonth();

    List<ProductDetailDTO> getTopProductDetails(String fromDate, String toDate, Integer top, Integer status);

    OgetOrderByUserID getOrderByUserID(Long userId);
}
