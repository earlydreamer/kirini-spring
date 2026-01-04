# JWT 기반 인증/인가 구현 세부

작성일: 2026-01-04

## 현재 반영 내용 (기반)
- `SecurityConfig`: stateless, JWT 필터 등록, freeboard GET permitAll, 나머지 기본 permitAll (추후 tighten 예정)
- `JwtProvider`: accessToken 발급/파싱 (subject=accountId, claim auth=Authority)
- `JwtAuthenticationFilter`: Authorization Bearer 토큰 검증 → SecurityContext 설정
- `FreeboardController`: Authentication(JwtUser)에서 accountId/authority 사용

## 예정 작업 (세부)
1) 로그인/토큰 발급
   - `AuthController#login`: email/password 검증 → accessToken 반환
   - 실패 시 401 + ApiResponse.error("인증 실패", "UNAUTHORIZED")
2) 토큰 재발급
   - `AuthController#refresh`: refreshToken 또는 만료된 accessToken 검증 후 새 accessToken
   - refresh 저장/블랙리스트 여부 결정
3) Security 예외 매핑 강화
   - EntryPoint/AccessDeniedHandler에서 ApiResponse 포맷으로 401/403 응답
   - `GlobalExceptionHandler` 보완
4) 인가 규칙 보강
   - `/api/freeboard/**` POST/PUT/DELETE 인증 필수로 명시, 기타는 정책에 따라 조정
5) 테스트
   - 단위: 인증 누락/권한 부족 MockMvc 테스트 추가
   - 통합: @SpringBootTest + JWT 발급 헬퍼로 CRUD 시나리오

## 엔드포인트 초안
- `POST /api/auth/login`
  - 요청: `{ "email": "...", "password": "..." }`
  - 응답: `{ success, data: { accessToken, expiresIn } }`
- `POST /api/auth/refresh` (미구현)

## 상태
- 로그인 엔드포인트 구현: `AuthController#login`
- SecurityConfig: `/api/auth/**` permitAll, stateless, JWT 필터
- UserDetailsService: `CustomUserDetailsService` + `SecurityUser`
