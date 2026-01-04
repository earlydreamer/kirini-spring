# 자유게시판 1차 구현 - Service 단계

작성일: 2026-01-04

## 목표
- CRUD 비즈니스 로직, 권한/검증, 조회수 증가 처리

## 적용 파일
- `src/main/java/dev/earlydreamer/kirini/service/FreeboardService.java`

## 주요 로직
- 생성: 제목/내용 필수 검증, 기본값 설정(read=0, recommend=0, notify=COMMON, deleted=MAINTAINED, writeTime=now)
- 조회: 삭제되지 않은 글 단건 조회 + 조회수 증가(increaseReadCount 쿼리 후 엔티티 readCount 수동 증가)
- 목록: 삭제되지 않은 글 페이징 반환(Page → FreeboardResponse 매핑)
- 수정: 작성자 또는 ADMIN/ARMBAND만, 제목/내용 null 허용, 내용 공백 거부, modifyTime 업데이트
- 삭제: 논리 삭제(deleteStatus=DELETED), 권한 동일

## 예외/권한
- `BusinessException` 사용: NOT_FOUND, FORBIDDEN, TITLE_REQUIRED, CONTENTS_REQUIRED 등 코드 부여
- 권한 체크: 작성자 동일 or Authority ADMIN/ARMBAND

## 변경 사항 (JWT 인증 반영)
- 서비스 메서드 accountId/authority는 인증 컨텍스트에서 주입됨(Authentication → JwtUser)
- 권한 체크 로직 동일(작성자 또는 ADMIN/ARMBAND)

## 스니펫
```java
if (!canModify(freeboard, accountId, authority)) {
    throw new BusinessException("수정 권한이 없습니다.", "FORBIDDEN");
}
```
