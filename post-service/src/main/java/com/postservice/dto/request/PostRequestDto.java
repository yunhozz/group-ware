package com.postservice.dto.request;

import com.postservice.common.enums.PostType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@AllArgsConstructor
public class PostRequestDto {

    @NotBlank
    @Size(min = 5, max = 20)
    private String title;

    @NotBlank
    @Size(min = 10, max = 1500)
    private String content;

    @NotNull
    private PostType postType; // MUST_READ, NOTICE, REPORT

    private List<MultipartFile> files;
}