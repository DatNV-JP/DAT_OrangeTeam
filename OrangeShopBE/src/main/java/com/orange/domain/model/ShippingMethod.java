package com.orange.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "shipping_method")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShippingMethod extends BaseEntity{
    @Size(max = 105)
    @NotNull
    @Column(name = "name", nullable = false, length = 105)
    private String name;

    @Column(name = "status")
    private Boolean status;


    public ShippingMethod(long id) {
        this.setId(id);
    }
}