package com.orange.payload.response;

import com.orange.domain.model.Village;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserAddressResponse {
    private Long id;
    private Village village;
    private String addressLine1;
    private String phone;
    private String name;
    private Boolean isDefault;
}
