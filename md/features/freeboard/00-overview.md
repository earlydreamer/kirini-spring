# 자유게시판 1차 구현 개요 (게시글만)

작성일: 2026-01-04

## 범위
- 게시글 CRUD만 구현 (댓글/첨부/신고/공지캐시 등은 이후 단계)
- 조회 시 조회수 자동 증가
- 삭제는 논리 삭제(`freeboard_deleted` 변경)

## 스키마/도메인 기준값 (스키마 우선)
- 작성자 FK: `account_uid` (User 연관)
- 공지 플래그: `freeboard_notify` ENUM(`COMMON`,`NOTIFICATION`), 기본값 `COMMON`
- 삭제 플래그: `freeboard_deleted` ENUM(`MAINTAINED`,`DELETED`), 기본값 `MAINTAINED`
- 권한: `Authority` ENUM(`NORMAL`,`ARMBAND`,`ADMIN`)
- 카운터 기본값: `freeboard_read=0`, `freeboard_recommend=0`

## 필요한 계층별 설계 메모
- Entity: 현 구조 유지, Enum 필드 사용, 기본값은 Service에서 보정
- Repository: 페이징 목록(삭제되지 않은 글), 단건 조회, 조회수 증가용 업데이트 메서드
- DTO
  - Request: 작성/수정용(title 1~50, contents 필수)
  - Response: 글 정보 + 작성자 id/name(optional) + 카운트/상태/공지 여부
- Service: 권한 체크(작성자 또는 ADMIN/ARMBAND), 기본값 세팅, 단건 조회 시 조회수 증가 트랜잭션 처리
- Controller: `/api/freeboard` REST CRUD, 응답 포맷 `agents.md`(success/message/data)

## 권한/검증 정책
- 작성: 로그인 사용자(NORMAL 이상)
- 수정/삭제: 작성자 또는 ADMIN/ARMBAND
- 제목: 1~50자, 내용: 공백 불가
- 삭제: `freeboard_deleted`를 `DELETED`로 변경

## 예정 API 스케치
- `POST /api/freeboard` 글 작성
- `GET /api/freeboard/{id}` 단건 조회(조회수 +1)
- `GET /api/freeboard?page&size` 목록 조회(삭제되지 않은 글, 최신순)
- `PUT /api/freeboard/{id}` 글 수정
- `DELETE /api/freeboard/{id}` 글 삭제(논리)

## 다음 단계
1) DTO 정의(request/response)
2) Repository 구현
3) Service 구현(조회수 증가 포함)
4) Controller 구현
5) 간단 통합 테스트/시나리오 수동 점검

