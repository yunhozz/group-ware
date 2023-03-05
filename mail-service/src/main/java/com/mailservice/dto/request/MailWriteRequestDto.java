package com.mailservice.dto.request;

import com.mailservice.common.enums.SecurityRating;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Getter
@Setter
public class MailWriteRequestDto {

    @NotBlank
    @Size(min = 5, max = 100)
    private String title;

    @NotBlank
    @Size(max = 2000)
    private String content;

    private boolean isImportant;

    private MultipartFile[] files;

    // SecuritySetting
    private SecurityRating rating;
    private LocalDateTime validity;
}