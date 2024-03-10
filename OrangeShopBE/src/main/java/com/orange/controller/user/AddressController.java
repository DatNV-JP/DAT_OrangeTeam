package com.orange.controller.user;


import com.orange.exception.GlobalException;
import com.orange.utils.AccountUtils;
import com.orange.common.payload.Page;
import com.orange.common.payload.Result;
import com.orange.exception.EntityIsEmptyException;
import com.orange.payload.request.UserAddressRequest;
import com.orange.payload.response.UserAddressResponse;
import com.orange.services.IAddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("/address")
@RequiredArgsConstructor
public class AddressController {

    private final IAddressService addressService;
    private final AccountUtils accountUtils;

    @GetMapping("")
    public Result<?> getAllAddress(@RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "20") int size) {
        Page<?> pages = this.addressService.fillAll(PageRequest.of(page, size));
        return Result.result(HttpStatus.OK.value(), "Lấy dữ liệu adddress thành công!", pages);
    }

    @GetMapping("/by-user")
    public Result<?> getAddressByUser(@RequestParam(required = false) Long id) {
        List<UserAddressResponse> list;
        if (id == null) {
            list = this.addressService.fillAddressByUser(accountUtils.getUser().getId());
        } else {
            list = this.addressService.fillAddressByUser(id);
        }
        return Result.result(HttpStatus.OK.value(), "Lấy dữ liệu adddress thành công!", list);
    }

    @PostMapping("/add-user-address")
    public Result<?> addAddressForUser(@RequestBody Optional<UserAddressRequest> addressRequest) {
        if (addressRequest.isPresent()) {
                UserAddressResponse result = addressService.addUserAddress(accountUtils.getUser(), addressRequest.get());
                return Result.result(HttpStatus.OK.value(), "Thêm adddress thành công!", result);
        } else {
            throw GlobalException.throwException("address.user.notfound");
        }
    }
    @PutMapping("/set-default")
    public Result<?> setDefaultAddressForUser(@RequestBody Optional<Long> addressId){
        if (addressId.isPresent()) {
            UserAddressResponse result = addressService.setDefaultAddressForUser(accountUtils.getUser(), addressId.get());
            return Result.result(HttpStatus.OK.value(), "Cập nhật địa chỉ mặc định thành công!", result);
        } else {
            throw GlobalException.throwException("address.user.id.notfound");
        }
    }

    @PutMapping("/update")
    public Result<?> updateUserAddress(@RequestBody Optional<UserAddressRequest> addressRequest){
        if (addressRequest.isPresent()) {
            UserAddressResponse result = addressService.updateAddressForUser(accountUtils.getUser(), addressRequest.get());
            return Result.result(HttpStatus.OK.value(), "Cập nhật địa chỉ thành công!", result);
        } else {
            throw GlobalException.throwException("address.user.notfound");
        }
    }

    @DeleteMapping("/delete/{id}")
    public Result<?> deleteAddressForUser(@PathVariable Optional<Long> id){
        if (id.isPresent()) {
            UserAddressResponse result = addressService.deleteUserAddress(accountUtils.getUser(), id.get());
            return Result.result(HttpStatus.OK.value(), "Xóa địa chỉ thành công!", result);
        } else {
            throw GlobalException.throwException("address.user.id.notfound");
        }
    }

}
