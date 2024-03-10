package com.orange.domain.dto;

import com.orange.domain.model.Authority;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

/**
 * A DTO for the {@link com.orange.domain.model.User} entity
 */
@Data
public class UserDTO extends BaseEntityDTO implements Serializable {
    @Size(max = 50)
    @NotNull
    private String username;
    @Size(max = 105)
    @NotNull
    private String passwordHash;
    @Size(max = 105)
    @NotEmpty
    private String email;
    private String phone;
    @Size(max = 45)
    private String firstName;
    @Size(max = 45)
    private String lastName;
    private Boolean activate;
    private List<Authority> roles;
    private String avatar;
}