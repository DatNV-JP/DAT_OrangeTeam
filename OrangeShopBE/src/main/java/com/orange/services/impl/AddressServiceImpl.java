package com.orange.services.impl;

import com.orange.utils.StringUtils;
import com.orange.common.payload.Page;
import com.orange.domain.dto.AddressDTO;
import com.orange.domain.mapper.IAddressMapper;
import com.orange.domain.model.*;
import com.orange.exception.*;
import com.orange.payload.request.UserAddressRequest;
import com.orange.payload.response.UserAddressResponse;
import com.orange.repositories.*;
import com.orange.services.IAddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AddressServiceImpl implements IAddressService {

    private final IAddressRepository addressRepository;
    private final IAddressMapper addressMapper;
    private final IVillageRepository villageRepository;
    private final IDistrictRepository districtRepository;
    private final ICityRepository cityRepository;
    private final IUserAddressRepository userAddressRepository;

    @Override
    public AddressDTO create(AddressDTO dto) {
        if (StringUtils.isNullOrEmpty(dto.getAddressLine1())) {
            throw GlobalException.throwException("address.user.address.notempty");
        }
        if (findByAddressDTO(dto).isPresent()) {
            throw GlobalException.throwException("address.user.address.exists");
        }

        Address address = checkVillageDistrictCity(dto);

        address = addressRepository.save(address);
        return addressMapper.toDto(address);
    }

    @Override
    public AddressDTO update(AddressDTO dto) {
        if (StringUtils.isNullOrEmpty(dto.getAddressLine1())) {
            throw GlobalException.throwException("address.user.address.notempty");
        }
        if (findByAddressDTO(dto).isPresent()) {
            Address address = checkVillageDistrictCity(dto);
            address = addressRepository.save(address);
            return addressMapper.toDto(address);
        } else {
            throw GlobalException.throwException("address.user.notfound");
        }

    }

    @Override
    public AddressDTO delete(AddressDTO dto) {
        Optional<Address> address = findByAddressDTO(dto);
        if (address.isPresent()) {
            Address deletedAddress = address.get();
            deletedAddress.setStatus(false);
            deletedAddress = addressRepository.save(deletedAddress);
            return addressMapper.toDto(deletedAddress);
        } else {
            throw GlobalException.throwException("address.user.notfound");
        }
    }

    @Override
    public Page<?> fillAll(Pageable pageable) {
        org.springframework.data.domain.Page<Address> result = this.addressRepository.findAll(pageable);
        List<AddressDTO> viewDTOList = result.getContent()
                .stream()
                .map(addressMapper::toDto)
                .collect(Collectors.toList());
        return Page.of(result.getSize(), result.getNumber(), result.getTotalPages(), Math.toIntExact(result.getTotalElements()), viewDTOList);
    }

    @Override
    public List<UserAddressResponse> fillAddressByUser(Long userId) {
        List<UserAddress> userAddresses = this.userAddressRepository.findAllByStatusIsTrueAndUser_Id(userId);
        if (userAddresses.isEmpty()) {
            throw GlobalException.throwException("address.user.notfound");
        }
        return userAddresses.stream()
                .map(ua -> UserAddressResponse.builder()
                        .id(ua.getAddress().getId())
                        .name(ua.getName())
                        .phone(ua.getPhone())
                        .addressLine1(ua.getAddress().getAddressLine1())
                        .village(ua.getAddress().getVillage())
                        .isDefault(ua.getIsDefault())
                        .build()).collect(Collectors.toList());
    }

    @Override
    public AddressDTO findById(Long id) {
        Address address = getAddressById(id);
        return addressMapper.toDto(address);
    }

    @Override
    public UserAddressResponse addUserAddress(User user, UserAddressRequest addUserAddressRequest) {
        UserAddress userAddress = new UserAddress();
        userAddress.setUser(user);

        Optional<Address> addressOptional = findByAddressDTO(addUserAddressRequest.getAddress());
        if (addressOptional.isPresent()) {
            userAddress.setAddress(addressOptional.get());
        } else {
            AddressDTO newAddressDTO = create(addUserAddressRequest.getAddress());
            userAddress.setAddress(addressMapper.toEntity(newAddressDTO));
        }
        Optional<UserAddress> userAddressOptional = this.userAddressRepository.findFirstByUser_IdAndAddress_Id(userAddress.getUser().getId(), userAddress.getAddress().getId());
        if (userAddressOptional.isPresent()) {
            throw GlobalException.throwException("address.user.address.exists");
        }
        userAddress.setStatus(true);
        userAddress.setCreateBy(user.getUsername());
        userAddress.setCreateDate(new Date());
        userAddress.setName(addUserAddressRequest.getName());
        userAddress.setPhone(addUserAddressRequest.getPhone());
        userAddress.setIsDefault(addUserAddressRequest.getIsDefault() != null && addUserAddressRequest.getIsDefault());
        if (userAddress.getIsDefault()) {
            this.userAddressRepository.updateAllDefaultAddressToFalse(userAddress.getUser().getId());
        }
        this.userAddressRepository.save(userAddress);
        return toUserAddressResponse(userAddress);
    }

    private static UserAddressResponse toUserAddressResponse(UserAddress userAddress) {
        return UserAddressResponse.builder()
                .isDefault(userAddress.getIsDefault())
                .id(userAddress.getAddress().getId())
                .name(userAddress.getName())
                .phone(userAddress.getPhone())
                .addressLine1(userAddress.getAddress().getAddressLine1())
                .village(userAddress.getAddress().getVillage())
                .build();
    }

    @Override
    public UserAddressResponse setDefaultAddressForUser(User user, Long addressId) {
        Optional<UserAddress> userAddressOptional = this.userAddressRepository.findFirstByUser_IdAndAddress_Id(user.getId(), addressId);
        if (userAddressOptional.isPresent()) {
            this.userAddressRepository.updateAllDefaultAddressToFalse(userAddressOptional.get().getId());
            UserAddress updated = userAddressOptional.get();
            updated.setIsDefault(true);
            this.userAddressRepository.save(updated);
            return toUserAddressResponse(updated);
        } else {
            throw GlobalException.throwException("address.user.notfound");
        }
        /*AtomicReference<UserAddress> userAddress = new AtomicReference<>();
        List<UserAddress> userAddresses = this.userAddressRepository.findUserAddressesByUser_Id(user.getId())
                .stream()
                .filter(ua -> (ua.getAddress().getId() == addressId) || ua.getIsDefault())
                .map(ua -> {
                    if (ua.getAddress().getId() == addressId) {
                        ua.setIsDefault(true);
                        userAddress.set(ua);
                        return ua;
                    } else {
                        ua.setIsDefault(false);
                        return ua;
                    }
                })
                .toList();
        this.userAddressRepository.saveAll(userAddresses);
        UserAddress updatedUseraddress = userAddress.get();*/
    }

    @Override
    public UserAddressResponse deleteUserAddress(User user, Long addressId) {
        Optional<UserAddress> userAddressOptional = this.userAddressRepository.findFirstByUser_IdAndAddress_Id(user.getId(), addressId);
        if (userAddressOptional.isPresent()) {
            UserAddress updated = userAddressOptional.get();
            updated.setStatus(false);
            this.userAddressRepository.save(updated);
            return toUserAddressResponse(updated);
        } else {
            throw GlobalException.throwException("address.user.notfound");
        }
    }

    @Override
    public UserAddressResponse updateAddressForUser(User user, UserAddressRequest userAddressRequest) {
        Optional<UserAddress> userAddressOptional = this.userAddressRepository.findFirstByUser_IdAndAddress_Id(user.getId(), userAddressRequest.getAddress().getId());
        if (userAddressOptional.isPresent()) {
            UserAddress oldUserAddress = userAddressOptional.get();
            String addressLine1 = oldUserAddress.getAddress().getAddressLine1();
            String addressLine2 = oldUserAddress.getAddress().getAddressLine2();
            String villageId = oldUserAddress.getAddress().getVillage().getId();
            Address addressOptional = addressRepository.find_by_address(userAddressRequest.getAddress().getAddressLine1() ,userAddressRequest.getAddress().getVillage().getId()).orElse(null);
            if (addressOptional != null) {
                oldUserAddress.setAddress(addressOptional);
            } else {
                AddressDTO newAddressDTO = userAddressRequest.getAddress();
                newAddressDTO.setId(null);
                oldUserAddress.setAddress(addressMapper.toEntity(create(newAddressDTO)));
            }
            oldUserAddress.setStatus(true);
            oldUserAddress.setModifiedBy(user.getUsername());
            oldUserAddress.setModifiedDate(new Date());
            oldUserAddress.setName(userAddressRequest.getName());
            oldUserAddress.setPhone(userAddressRequest.getPhone());
            oldUserAddress.setIsDefault(userAddressRequest.getIsDefault() == null? false : userAddressRequest.getIsDefault() );
            if (oldUserAddress.getIsDefault()) {
                this.userAddressRepository.updateAllDefault(user.getId(),oldUserAddress.getId());
            }
            System.out.println("xxxx");
            UserAddress newUserAddress = this.userAddressRepository.save(oldUserAddress);
            System.out.println(newUserAddress.getIsDefault());
            return toUserAddressResponse(newUserAddress);
        } else {
            throw GlobalException.throwException("address.user.notfound");
        }
    }

        private Address getAddressById (Long id){
            return this.addressRepository.findById(id)
                    .orElseThrow(() -> GlobalException.throwException("address.user.notfound"));
        }


        private Optional<Address> findByAddressDTO (AddressDTO dto){
            Long id = dto.getId();
            if (id != null) {
                return addressRepository.findById(id);
            } else {
                String addressLine1 = dto.getAddressLine1();
                String addressLine2 = dto.getAddressLine2();
                String villageId = dto.getVillage().getId();
                return addressRepository.findByAddressLine1AndAddressLine2AndVillageId(addressLine1, addressLine2, villageId);
            }
        }

        private Address checkVillageDistrictCity (AddressDTO dto){

            Address address = addressMapper.toEntity(dto);

            Village village = address.getVillage();
            District district = village.getDistrict();
            City city = district.getCity();

            // Kiểm tra xem City đã tồn tại hay chưa
            Optional<City> cityOptional = cityRepository.findById(city.getId());
            if (cityOptional.isEmpty()) {
                // Nếu City chưa tồn tại, tạo mới City
                city = cityRepository.save(city);
            } else {
                // Nếu City đã tồn tại, lấy thông tin của nó
                city = cityOptional.get();
            }

            // Cập nhật lại District với thông tin mới nhất của City
            district.setCity(city);

            // Kiểm tra xem District đã tồn tại hay chưa
            Optional<District> districtOptional = districtRepository.findById(district.getId());
            if (districtOptional.isEmpty()) {
                // Nếu District chưa tồn tại, tạo mới District
                district = districtRepository.save(district);
            } else {
                // Nếu District đã tồn tại, lấy thông tin của nó
                district = districtOptional.get();
            }

            // Cập nhật lại Village với thông tin mới nhất của District
            village.setDistrict(district);

            // Kiểm tra xem Village đã tồn tại hay chưa
            Optional<Village> villageOptional = villageRepository.findById(village.getId());
            if (villageOptional.isEmpty()) {
                // Nếu Village chưa tồn tại, tạo mới Village
                village = villageRepository.save(village);
            } else {
                // Nếu Village đã tồn tại, lấy thông tin của nó
                village = villageOptional.get();
            }

            // Cập nhật lại địa chỉ với thông tin mới nhất của Village
            address.setVillage(village);

        /*// Kiểm tra xem Address đã tồn tại hay chưa
        Optional<Address> addressOptional = addressRepository.findById(address.getId());
        if (addressOptional.isEmpty()) {
            // Nếu Address chưa tồn tại, tạo mới Address
            address = addressRepository.save(address);
        } else {
            // Nếu Address đã tồn tại, lấy thông tin của nó
            address = addressOptional.get();
        }*/
            return address;
        }
}

