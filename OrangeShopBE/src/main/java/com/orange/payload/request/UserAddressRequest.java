package com.orange.payload.request;

import com.orange.domain.dto.AddressDTO;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserAddressRequest {
    private AddressDTO address;
    private String phone;
    private String name;
    private Boolean isDefault;
}
