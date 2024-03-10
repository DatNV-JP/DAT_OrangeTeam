package com.orange.domain.mapper.impl;

import com.orange.domain.dto.OrderDTO;
import com.orange.domain.mapper.IAddressMapper;
import com.orange.domain.mapper.IOrderDetailMapper;
import com.orange.domain.mapper.IOrderMapper;
import com.orange.domain.mapper.IUserMapper;
import com.orange.domain.model.Order;
import com.orange.domain.model.User;
import com.orange.domain.model.Voucher;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderMapperImpl implements IOrderMapper {

    private final ModelMapper modelMapper;
    private final IUserMapper userMapper;
    private final IAddressMapper addressMapper;
    private final IOrderDetailMapper orderDetailMapper;

    @Override
    public Order toEntity(OrderDTO dto) {
        Order entity = modelMapper.map(dto, Order.class);
        if (dto.getAddress() != null && dto.getAddress().getId() != null) {
            entity.setAddress(addressMapper.toEntity(dto.getAddress()));
        }
        entity.setUser(new User(dto.getUserId()));
        entity.setOrderDetails(orderDetailMapper.toEntitySet(dto.getOrderDetails()));
        if (dto.getVoucherId() != null && dto.getVoucherId() > 0) {
            entity.setVoucher(new Voucher(dto.getVoucherId()));
        }
        if (dto.getUserId() != null && dto.getUserId() > 0) {
            entity.setUser(new User(dto.getUserId()));
        } else {
            entity.setUser(null);
        }
        return entity;
    }

    @Override
    public OrderDTO toDto(Order entity) {
        OrderDTO dto = modelMapper.map(entity, OrderDTO.class);
        if (entity.getAddress() != null && entity.getAddress().getId() != null){
            dto.setAddress(addressMapper.toDto(entity.getAddress()));
        }
        dto.setOrderDetails(orderDetailMapper.toDtoSet(entity.getOrderDetails()));
        if (entity.getVoucher() != null && entity.getVoucher().getId() > 0) {
            dto.setVoucherId(entity.getVoucher().getId());
        }
        if (entity.getUser() != null && entity.getUser().getId() > 0) {
            dto.setUserId(entity.getUser().getId());
        }
        return dto;
    }
}
