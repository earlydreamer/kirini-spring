# 인증/인가 개요 (JWT 기반)

작성일: 2026-01-04

## 범위
- 전역 인증/인가를 JWT 기반으로 적용 (Authorization: Bearer)
- 사용자 권한/상태(User.Authority/UserStatus) 반영
- 공통 응답 포맷(ApiResponse)와 예외 매핑 통일

## 현재 적용
- 자유게시판에 JWT 필터 적용 (SecurityConfig, JwtAuthenticationFilter)
- 읽기 공개, 쓰기 인증 필요

## 앞으로 진행할 단계
- 로그인/토큰 발급·재발급 엔드포인트 추가
- 인증/인가 예외 응답 표준화
- 권한 부족/비로그인 테스트, 통합 테스트 추가

## 문서 구조
- 00-overview.md (본 문서)
- 01-plan.md (체크리스트)
- 02-jwt.md (구현 세부, 향후 업데이트)

