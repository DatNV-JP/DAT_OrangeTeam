package com.orange.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.*;
import org.hibernate.search.engine.backend.types.Searchable;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "product_detail")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Indexed
public class ProductDetail extends BaseEntity{

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id")
    private Product product;

    @NotNull
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Size(max = 1000)
    @Column(name = "images", length = 1000)
    private String images;

    @NotNull
    @Column(name = "price_input", nullable = false)
    private Double priceInput;
    @NotNull
    @Column(name = "price_default", nullable = false)
    private Double priceDefault;

    @GenericField(sortable = Sortable.YES, searchable = Searchable.YES)
    @Column(name = "price_sale")
    private Double priceSale;


    @Column(name = "max_sale")
    private Double maxSale;

    @Column(name = "status")
    @GenericField(sortable = Sortable.YES)
    private Boolean status;

    @OneToMany(mappedBy = "productDetail")
    @JsonIgnore
    @IndexedEmbedded
    private Set<OrderDetail> orderDetails;

    @ManyToMany
    @JoinTable(name = "product_detail_variation_option", joinColumns = {@JoinColumn(name = "product_detail_id")},
            inverseJoinColumns = {@JoinColumn(name = "variation_option_id")})
    @IndexedEmbedded
    private Set<VariationOption> variationOptions = new HashSet<>();

    @ManyToMany( fetch = FetchType.EAGER)
    @JoinTable(name = "product_detail_promotion", joinColumns = {@JoinColumn(name = "product_detail_id")},
            inverseJoinColumns = {@JoinColumn(name = "promotion_id")})
    @JsonIgnore
    private Set<Promotion> promotionSet = new HashSet<>();
    public ProductDetail(Long productDetailId) {
        this.setId(productDetailId);
    }
}