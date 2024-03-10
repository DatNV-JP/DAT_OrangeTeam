package com.orange.payload.request.ghn;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GHNCalculateFeeRequest {

    @JsonProperty("from_district_id")
    private Integer fromDistrictId = 3440;

//    @JsonProperty("service_id")
//    private Integer serviceId = 53321;

    @JsonProperty("service_type_id")
    private Integer serviceTypeId = 2;

    @JsonProperty("to_district_id")
    private Integer toDistrictId;

    @JsonProperty("to_ward_code")
    private String toWardCode;

    @JsonProperty("height")
    private Integer height = 50;

    @JsonProperty("length")
    private Integer length = 20;

    @JsonProperty("weight")
    private Integer weight = 200;

    @JsonProperty("width")
    private Integer width = 20;

    @JsonProperty("insurance_value")
    private Integer insuranceValue;

    @JsonProperty("coupon")
    private String coupon;
}
