package com.orange.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "order_status")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Indexed
public class OrderStatus extends BaseEntity implements Serializable {

    @Size(max = 50)
    @Column(name = "status", length = 50)
    private String status;

    @OneToMany(mappedBy = "orderStatus", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Order> orders = new HashSet<>();

    public OrderStatus(Long id) {
        this.setId(id);
    }
}