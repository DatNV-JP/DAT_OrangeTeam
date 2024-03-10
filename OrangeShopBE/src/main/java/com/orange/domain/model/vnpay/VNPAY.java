package com.orange.domain.model.vnpay;

public enum VNPAY {
    VNPAY_URL("vnp_PayUrl", "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html"),
    VNPAY_TRANSACTION_URL("vnpay_TransactionURL", "https://sandbox.vnpayment.vn/merchant_webapi/api/transaction"),
//    VNPAY_RETURN_URL("vnp_ReturnUrl", "https://sanvadio.buzz/after-payment"),
    VNPAY_RETURN_URL("vnp_ReturnUrl", "http://localhost:9527/after-payment"),
    VNPAY_API_URL("vnp_apiUrl", "https://sandbox.vnpayment.vn/merchant_webapi/api/transaction"),
    VNPAY_TMN_CODE("vnp_TmnCode", "669H6BK0"),
    VNPAY_VERSION("vnp_Version", "2.1.0"),
    VNPAY_COMMAND("vnp_Command", "pay"),
    VNPAY_ORDER_TYPE("vnp_OrderType", "200000"),
    VNPAY_LOCALE_DEFAULT("vnp_Locale", "vn"),
    VNPAY_HASH_SECRET("vnp_HashSecret", "IUWLDHERMTXMUUFNJDPEKTTYXQVPUIUG"),
    ;

    private final String key;
    private final String value;

    VNPAY(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
