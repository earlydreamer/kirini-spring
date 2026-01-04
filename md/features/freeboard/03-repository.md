# 자유게시판 1차 구현 - Repository 단계

작성일: 2026-01-04

## 목표
- 삭제되지 않은 게시글 목록/단건 조회, 조회수 증가 쿼리 제공

## 적용 파일
- `src/main/java/dev/earlydreamer/kirini/repository/FreeboardRepository.java`

## 내용
- `findByDeleteStatus(DeleteStatus, Pageable)`: 삭제되지 않은 목록 페이징 조회
- `findByIdAndDeleteStatus(Integer, DeleteStatus)`: 삭제되지 않은 단건 조회
- `increaseReadCount(@Param("id"))`: 조회수 +1 업데이트(@Modifying)

## 비고
- 삭제 플래그 Enum: `MAINTAINED`만 조회 대상으로 사용

