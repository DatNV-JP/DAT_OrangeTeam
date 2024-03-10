package com.orange.services;

import com.orange.domain.model.ShippingMethod;
import com.orange.domain.model.ghn.GHNProvince;
import com.orange.domain.model.ghn.GHNShippingOrder;
import com.orange.payload.request.ghn.GHNCalculateFeeRequest;
import com.orange.payload.response.GHNCalculateFeeResponse;
import com.orange.payload.response.GHNShippingOrderResponseWrapper;

import java.util.List;

public interface IShippingService {
    List<ShippingMethod> getAllShippingMethod();

    List<GHNProvince> getProvince();

    GHNShippingOrderResponseWrapper createShippingOrder(GHNShippingOrder ghnShippingOrder);

    GHNCalculateFeeResponse calculateFee(GHNCalculateFeeRequest calculateFeeRequest);
}
