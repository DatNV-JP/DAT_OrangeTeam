package com.orange.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.*;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.*;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "promotion")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Indexed
public class Promotion extends BaseEntity{
    @Size(max = 205)
    @NotNull
    @Column(name = "name", nullable = false, length = 205)
    @FullTextField(name = "name_search")
    @KeywordField(sortable = Sortable.YES)
    private String name;

    @Size(max = 505)
    @Column(name = "description", length = 505)
    private String description;

    @NotNull
    @Column(name = "start_date", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd hh",timezone = "Asia/Ho_Chi_Minh")
    @GenericField(sortable = Sortable.YES)
    private Date startDate;

    @NotNull
    @Column(name = "end_date", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd hh",timezone = "Asia/Ho_Chi_Minh")
    @GenericField(sortable = Sortable.YES)
    private Date endDate;

    @NotNull
    @Column(name = "discount", nullable = false)
    private Integer discount;

    @Size(max = 50)
    @Column(name = "discount_code", nullable = false, length = 50)
    private String discountCode;

    @Column(name = "status")
    @GenericField(sortable = Sortable.YES)
    private Boolean status = true;

    @NotNull
    @Column(name = "is_percent", nullable = false)
    private Boolean isPercent;
    @NotNull
    @Column(name = "is_date", nullable = false)
    private Boolean isDate;

    @ManyToMany( fetch = FetchType.EAGER)
    @JoinTable(name = "product_detail_promotion", joinColumns = {@JoinColumn(name = "promotion_id")},
            inverseJoinColumns = {@JoinColumn(name = "product_detail_id")})
    @JsonIgnore
    private Set<ProductDetail> productDetails = new HashSet<>();
}