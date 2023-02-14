package com.postservice.dto.request;

import com.postservice.common.enums.PostType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostUpdateRequestDto {

    @NotBlank
    @Size(min = 5, max = 20)
    private String title;

    @NotBlank
    @Size(min = 10, max = 1500)
    private String content;

    @NotNull
    private PostType postType; // MUST_READ, NOTICE, REPORT
}