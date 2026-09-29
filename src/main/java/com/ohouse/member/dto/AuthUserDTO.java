package com.ohouse.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class AuthUserDTO {

    private Integer memberId;
    private String id;
    private String name;
    private String role;

}