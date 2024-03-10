package com.orange.domain.mapper.impl;

import com.orange.domain.model.Address;
import com.orange.domain.mapper.IAddressMapper;
import com.orange.domain.dto.AddressDTO;
import com.orange.domain.model.Village;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AddressMapperImpl implements IAddressMapper {

    private final ModelMapper modelMapper;
    @Override
    public Address toEntity(AddressDTO dto) {
        Address entity = modelMapper.map(dto, Address.class);
        return entity;
    }

    @Override
    public AddressDTO toDto(Address entity) {
        AddressDTO dto = modelMapper.map(entity, AddressDTO.class);
        return dto;
    }
}
