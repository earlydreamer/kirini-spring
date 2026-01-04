package dev.earlydreamer.kirini.dto.response;

import dev.earlydreamer.kirini.domain.Freeboard;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class FreeboardResponse {
    private Integer id;
    private String title;
    private String contents;
    private Integer readCount;
    private Integer recommendCount;
    private LocalDateTime writeTime;
    private LocalDateTime modifyTime;
    private Freeboard.NotifyType notifyType;
    private Freeboard.DeleteStatus deleteStatus;
    private Integer accountId;

    public static FreeboardResponse from(Freeboard entity) {
        return FreeboardResponse.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .contents(entity.getContents())
                .readCount(entity.getReadCount())
                .recommendCount(entity.getRecommendCount())
                .writeTime(entity.getWriteTime())
                .modifyTime(entity.getModifyTime())
                .notifyType(entity.getNotifyType())
                .deleteStatus(entity.getDeleteStatus())
                .accountId(entity.getUser() != null ? entity.getUser().getId() : null)
                .build();
    }
}

