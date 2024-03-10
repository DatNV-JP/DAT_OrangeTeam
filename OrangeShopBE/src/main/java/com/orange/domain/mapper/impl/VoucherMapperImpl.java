package com.orange.domain.mapper.impl;

import com.orange.domain.dto.VoucherDto;
import com.orange.domain.mapper.IVoucherMapper;
import com.orange.domain.model.BaseEntity;
import com.orange.domain.model.GroupCustomer;
import com.orange.domain.model.Voucher;
import com.orange.domain.model.VoucherType;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class VoucherMapperImpl implements IVoucherMapper {

    private final ModelMapper modelMapper;

    @Override
    public Voucher toEntity(VoucherDto dto) {
        Voucher entity = modelMapper.map(dto, Voucher.class);
        if (dto.getGroupId() != null) {
            entity.setGroup(new GroupCustomer(dto.getGroupId()));
        }
//        entity.setVoucherType(new VoucherType(dto.getVoucherTypeId()));

        return entity;
    }

    @Override
    public VoucherDto toDto(Voucher entity) {
        VoucherDto dto = modelMapper.map(entity, VoucherDto.class);
//        dto.setVoucherTypeId(entity.getVoucherType().getId());
        if (entity.getGroup() != null) {
            dto.setGroupId(entity.getGroup().getId());
        }
        if (!entity.getUsers().isEmpty()) {
            Set<Long> ids = entity.getUsers().stream()
                    .map(BaseEntity::getId).collect(Collectors.toSet());
            dto.setUserIds(ids);
        }
        return dto;
    }
}
