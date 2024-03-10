package com.orange.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "village")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Village{

    @Id
    private String id;
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private District district;

    @Size(max = 45)
    @NotNull
    @Column(name = "name", nullable = false, length = 45)
    private String name;

    @OneToMany(mappedBy = "village")
    @JsonIgnore
    private Set<Address> addresses;

    public Village(String villageId) {
        this.setId(villageId);
    }
}