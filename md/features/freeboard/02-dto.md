# 자유게시판 1차 구현 - DTO 단계

작성일: 2026-01-04

## 목표
- 게시글 작성/수정/조회에 필요한 요청/응답 DTO 정의

## 적용 파일
- `src/main/java/dev/earlydreamer/kirini/dto/request/FreeboardCreateRequest.java`
- `src/main/java/dev/earlydreamer/kirini/dto/request/FreeboardUpdateRequest.java`
- `src/main/java/dev/earlydreamer/kirini/dto/response/FreeboardResponse.java`
- `src/main/java/dev/earlydreamer/kirini/dto/response/FreeboardListResponse.java`

## 내용
- CreateRequest: 제목 1~50, 내용 NotBlank 검증
- UpdateRequest: 제목 1~50(optional), 내용은 null 허용하되 공백은 Service에서 거부
- Response: 엔티티 → 응답 변환(id, title, contents, readCount, recommendCount, writeTime, modifyTime, notifyType, deleteStatus, accountId)
- ListResponse: 페이지 메타 포함(currentPage, totalPages, pageSize, totalCount)

## 참고 코드 스니펫
```java
@Getter
@NoArgsConstructor
public class FreeboardCreateRequest {
    @NotBlank @Size(min = 1, max = 50)
    private String title;
    @NotBlank
    private String contents;
}
```

