package com.authserver.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JoinRequestDto {

    @Email
    @NotBlank
    private String email;

    @NotBlank
    @Size(min = 6, max = 12)
    private String password;

    @NotBlank
    private String name;

    private String imageUrl;
}