package com.orange.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * A DTO for the {@link com.orange.domain.model.OrderStatus} entity
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusDTO extends BaseEntityDTO implements Serializable {
    @Size(max = 50)
    private String status;

    public OrderStatusDTO(Long id) {
        this.setId(id);
    }
}