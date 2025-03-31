package com.infinity.commerce.auth_service.pojo;

import com.infinity.commerce.auth_service.enums.Role;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class    UserDetails {
    private String username ;
    private List<Role> roles;
}
