package dev.earlydreamer.kirini.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FreeboardUpdateRequest {

    @Size(min = 1, max = 50, message = "제목은 1~50자여야 합니다.")
    private String title;

    private String contents;
}

