package com.orange.payload.request.ghn;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class GHNFee implements Serializable {
    @JsonProperty("CODFailedFee")
    private Double CODFailedFee;
    @JsonProperty("CODFee")
    private Double CODFee;
    @JsonProperty("Coupon")
    private Double Coupon;
    @JsonProperty("DeliverRemoteAreasFee")
    private Double DeliverRemoteAreasFee;
    @JsonProperty("DocumentReturn")
    private Double DocumentReturn;
    @JsonProperty("DoubleCheck")
    private Double DoubleCheck;
    @JsonProperty("Insurance")
    private Double Insurance;
    @JsonProperty("MainService")
    private Double MainService;
    @JsonProperty("PickRemoteAreasFee")
    private Double PickRemoteAreasFee;
    @JsonProperty("R2S")
    private Double R2S;
    @JsonProperty("Return")
    private Double Return;
    @JsonProperty("StationDO")
    private Double StationDO;
    @JsonProperty("StationPU")
    private Double StationPU;
    @JsonProperty("Total")
    private Double Total;
}
