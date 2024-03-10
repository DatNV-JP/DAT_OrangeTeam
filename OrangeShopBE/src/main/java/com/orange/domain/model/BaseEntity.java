package com.orange.domain.model;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @GenericField(sortable = Sortable.YES)
    private Long id;

    @Column
    @CreatedBy
    private String createBy;

    @Column
    @CreatedDate
    @GenericField(sortable = Sortable.YES)
    private Date createDate;

    @Column
    @LastModifiedDate
    private Date modifiedDate;

    @Column
    @LastModifiedBy
    private String modifiedBy;

}
