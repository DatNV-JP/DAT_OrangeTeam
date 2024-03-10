package com.orange.domain.dto;

import com.orange.domain.model.ERole;
import lombok.Data;

import java.io.Serializable;

/**
 * A DTO for the {@link com.orange.domain.model.Authority} entity
 */
@Data
public class AuthorityDTO extends BaseEntityDTO implements Serializable {
    private final ERole name;
    private final Boolean status;
}