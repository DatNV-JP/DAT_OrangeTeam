package com.orange.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.*;
import org.hibernate.search.engine.backend.types.Searchable;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField;

import java.util.Set;

@Entity
@Table(name = "variation")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Indexed
public class Variation extends BaseEntity{

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Category category;

    @Size(max = 105)
    @NotNull
    @Column(name = "name", nullable = false, length = 105)
    @KeywordField(name = "name_search", sortable = Sortable.YES)
    private String name;

    @Column(name = "status")
    @GenericField(sortable = Sortable.YES)
    private Boolean status;
    @OneToMany(mappedBy = "variation")
    @JsonIgnore
    private Set<VariationOption> variationOptions;
}