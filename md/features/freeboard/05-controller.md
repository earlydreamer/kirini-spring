# 자유게시판 1차 구현 - Controller & Exception 단계

작성일: 2026-01-04

## 목표
- REST 엔드포인트 제공, 공통 응답 포맷 적용, 예외 처리 연동

## 적용 파일
- `src/main/java/dev/earlydreamer/kirini/controller/FreeboardController.java`
- `src/main/java/dev/earlydreamer/kirini/exception/BusinessException.java`
- `src/main/java/dev/earlydreamer/kirini/exception/GlobalExceptionHandler.java`

## 엔드포인트
- POST `/api/freeboard` : 글 작성 
- GET `/api/freeboard/{id}` : 단건 조회(조회수 +1)
- GET `/api/freeboard?page&size` : 목록
- PUT `/api/freeboard/{id}` : 수정
- DELETE `/api/freeboard/{id}` : 논리 삭제

## 특징
- 응답 래퍼: `ApiResponse` (`success/message/data/errorCode`)
- IP 추출: `X-Forwarded-For` → `X-Real-IP` → `Remote-Addr` 순
- 예외 매핑: `BusinessException` → 400, Validation → 400, 기타 → 500

## 변경 사항 (JWT 인증 반영)
- 임시 헤더(`X-Account-*`) 제거, Spring Security Authentication(JWT)로 사용자 정보 주입
- JWT 헤더: `Authorization: Bearer {token}`
- 인증 필수: POST/PUT/DELETE `/api/freeboard` (읽기 GET은 공개)
- 사용 클래스: `JwtProvider`, `JwtAuthenticationFilter`, `SecurityConfig`

## 스니펫
```java
return ResponseEntity.ok(ApiResponse.success("게시글이 등록되었습니다.", response));
```
