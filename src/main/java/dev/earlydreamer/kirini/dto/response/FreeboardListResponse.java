package dev.earlydreamer.kirini.dto.response;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Builder
public class FreeboardListResponse {
    private List<FreeboardResponse> items;
    private int currentPage;
    private int totalPages;
    private int pageSize;
    private long totalCount;

    public static FreeboardListResponse from(Page<FreeboardResponse> page) {
        return FreeboardListResponse.builder()
                .items(page.getContent())
                .currentPage(page.getNumber() + 1)
                .totalPages(page.getTotalPages())
                .pageSize(page.getSize())
                .totalCount(page.getTotalElements())
                .build();
    }
}

