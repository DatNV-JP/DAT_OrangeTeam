package com.orange.services;

import com.orange.domain.dto.AddressDTO;
import com.orange.domain.model.User;
import com.orange.payload.request.UserAddressRequest;
import com.orange.payload.response.UserAddressResponse;

import java.util.List;

public interface IAddressService extends BaseService<AddressDTO, Long>{
    List<UserAddressResponse> fillAddressByUser(Long userId);

    UserAddressResponse addUserAddress(User user, UserAddressRequest addUserAddressRequest);

    UserAddressResponse setDefaultAddressForUser(User user, Long addressId);

    UserAddressResponse deleteUserAddress(User user, Long addressId);

    UserAddressResponse updateAddressForUser(User user, UserAddressRequest userAddressRequest);
}
