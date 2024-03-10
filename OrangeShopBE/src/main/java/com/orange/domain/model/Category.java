package com.orange.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.*;
import javax.validation.constraints.Size;
import lombok.*;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "category")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Indexed
public class Category extends BaseEntity{
    @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JoinColumn(name = "id_parent_category")
    @JsonIgnore
    private Category parentCategory;

    @Size(max = 145)
    @Column(name = "name", length = 145)
    @FullTextField(name = "name_search")
    @KeywordField(sortable = Sortable.YES)
    private String name;

    @Column(name = "status")
    @GenericField(sortable = Sortable.YES)
    @JsonIgnore
    private Boolean status;

    @OneToMany(mappedBy = "category")
    @JsonIgnore
    private Set<Product> products;

    @OneToMany(mappedBy = "category")
    @JsonIgnore
    private Set<Variation> variations;

    @OneToMany(mappedBy = "parentCategory")
    private Set<Category> children;

    public Category(Long categoryId) {
        this.setId(categoryId);
    }
}