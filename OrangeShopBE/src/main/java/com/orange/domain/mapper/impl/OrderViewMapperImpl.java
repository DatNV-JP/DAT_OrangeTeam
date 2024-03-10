package com.orange.domain.mapper.impl;

import com.orange.domain.dto.OrderViewDTO;
import com.orange.domain.mapper.IAddressMapper;
import com.orange.domain.mapper.IOrderDetailViewMapper;
import com.orange.domain.mapper.IOrderViewMapper;
import com.orange.domain.mapper.IUserPaymentMethodMapper;
import com.orange.domain.model.Order;
import com.orange.domain.model.Voucher;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderViewMapperImpl implements IOrderViewMapper {

    private final ModelMapper modelMapper;
    private final IUserPaymentMethodMapper userPaymentMethodMapper;
    private final IAddressMapper addressMapper;
    private final IOrderDetailViewMapper orderDetailViewMapper;

    @Override
    public Order toEntity(OrderViewDTO dto) {
        Order entity = modelMapper.map(dto, Order.class);
        entity.setOrderDetails(orderDetailViewMapper.toEntitySet(dto.getOrderDetailViews()));
        if (dto.getVoucherId() != null && dto.getVoucherId() > 0) {
            entity.setVoucher(new Voucher(dto.getVoucherId()));
        }
        if (dto.getAddress() != null && dto.getAddress().getId() != null){
            entity.setAddress(addressMapper.toEntity(dto.getAddress()));
        }
//        entity.setUserPaymentMethod(userPaymentMethodMapper.toEntity(dto.get));

        return entity;
    }

    @Override
    public OrderViewDTO toDto(Order entity) {
        OrderViewDTO dto = modelMapper.map(entity, OrderViewDTO.class);
        dto.setOrderDetailViews(orderDetailViewMapper.toDtoSet(entity.getOrderDetails()));
        if (entity.getAddress() != null && entity.getAddress().getId() != null){
            dto.setAddress(addressMapper.toDto(entity.getAddress()));
        }
        if (entity.getVoucher() != null && entity.getVoucher().getId() > 0) {
            dto.setVoucherId(entity.getVoucher().getId());
        }
        dto.setProductQuantity(entity.getOrderDetails().size());

        return dto;
    }
}
