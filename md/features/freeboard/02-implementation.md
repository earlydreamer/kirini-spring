# 자유게시판 1차 구현 기록 (게시글 CRUD)

작성일: 2026-01-04

> 이 문서는 단계별 세부 구현 문서(02~06)로 분할되었습니다. 각 단계는 user-registration 문서 구조를 따라 구성됩니다.
>
> - 02-DTO: `02-dto.md`
> - 03-Repository: `03-repository.md`
> - 04-Service: `04-service.md`
> - 05-Controller & Exception: `05-controller.md`
> - 06-Test: `06-test.md`

## 요약
- 게시글 CRUD 구현 완료 (첨부/댓글 제외)
- 조회 시 조회수 자동 증가, 삭제는 논리 삭제
- 권한: 작성자 또는 ADMIN/ARMBAND만 수정·삭제
- 임시 인증 헤더 사용(`X-Account-Id`, `X-Account-Authority`)

## 다음 읽을 거리
- 상세 단계별 구현은 위 링크된 문서를 참고하세요.
