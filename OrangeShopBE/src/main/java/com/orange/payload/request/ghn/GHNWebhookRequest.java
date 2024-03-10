package com.orange.payload.request.ghn;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.Date;

@Data
public class GHNWebhookRequest implements Serializable {
    @JsonProperty("CODAmount")
    private Double CODAmount;
    @JsonProperty("CODTransferDate")
    private Date CODTransferDate;
    @JsonProperty("ClientOrderCode")
    private String ClientOrderCode;
    @JsonProperty("ConvertedWeight")
    private Integer ConvertedWeight;
    @JsonProperty("Description")
    private String Description;
    @JsonProperty("Height")
    private Integer Height;
    @JsonProperty("IsPartialReturn")
    private Boolean IsPartialReturn;
    @JsonProperty("Length")
    private Integer Length;
    @JsonProperty("OrderCode")
    private String OrderCode;
    @JsonProperty("PartialReturnCode")
    private String PartialReturnCode;
    @JsonProperty("PaymentType")
    private Integer PaymentType;
    @JsonProperty("Reason")
    private String Reason;
    @JsonProperty("ReasonCode")
    private String ReasonCode;
    @JsonProperty("ShipperName")
    private String ShipperName;
    @JsonProperty("ShipperPhone")
    private String ShipperPhone;
    @JsonProperty("ShopID")
    private String ShopID;
    @JsonProperty("Status")
    private String Status;
    @JsonProperty("Time")
    private Date Time;
    @JsonProperty("Type")
    private String Type;
    @JsonProperty("Warehouse")
    private String Warehouse;
    @JsonProperty("Weight")
    private Double Weight;
    @JsonProperty("Width")
    private Double Width;
    @JsonProperty("Fee")
    private GHNFee Fee;
}
