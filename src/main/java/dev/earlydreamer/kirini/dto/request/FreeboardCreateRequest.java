package dev.earlydreamer.kirini.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FreeboardCreateRequest {

    @NotBlank(message = "제목은 필수입니다.")
    @Size(min = 1, max = 50, message = "제목은 1~50자여야 합니다.")
    private String title;

    @NotBlank(message = "내용은 필수입니다.")
    private String contents;
}

