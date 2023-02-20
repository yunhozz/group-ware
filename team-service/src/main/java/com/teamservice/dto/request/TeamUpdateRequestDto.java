package com.teamservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TeamUpdateRequestDto {

    @NotBlank
    @Size(min = 2, max = 15)
    private String name;

    private String imageUrl;
}