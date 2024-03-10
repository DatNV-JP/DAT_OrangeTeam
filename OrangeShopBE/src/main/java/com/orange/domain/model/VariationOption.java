package com.orange.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.*;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField;

import java.util.Set;

@Entity
@Table(name = "variation_option")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Indexed
public class VariationOption extends BaseEntity{

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @IndexedEmbedded
    private Variation variation;

    @Size(max = 505)
    @Column(name = "value", length = 505)
    @KeywordField(sortable = Sortable.YES)
    private String value;

    @ManyToMany(mappedBy = "variationOptions")
    @JsonIgnore
    private Set<ProductDetail> productDetails;
}