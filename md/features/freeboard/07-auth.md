# 자유게시판 인증·인가 문서 (JWT)

작성일: 2026-01-04

## 1. 현재 적용된 내용
- 보안 방식: JWT 기반 무상태 인증 (Authorization: Bearer)
- 설정: `SecurityConfig` (CSRF disable, stateless, JwtAuthenticationFilter 등록, freeboard 읽기만 permitAll)
- 컴포넌트: `JwtProvider`(발급/파싱), `JwtAuthenticationFilter`(토큰 검증), `JwtUser`(principal)
- 컨트롤러: `FreeboardController`는 `Authentication`에서 accountId/authority 주입하여 작성/수정/삭제 시 사용

## 2. 응답/예외 정책 (권한 부족/비로그인)
- 인증 실패(비로그인, 토큰 무효): 401 반환 (Security entry point) → `ApiResponse` 포맷 적용 필요
- 권한 부족(작성자 아님 & ADMIN/ARMBAND 아님): `BusinessException("FORBIDDEN")` → 400/403 처리 여부 결정 (현재 400)
- 제안: `GlobalExceptionHandler`에 `AccessDeniedException`/`AuthenticationException` 매핑 추가하여 `ApiResponse.error`로 응답

## 3. 향후 구현 단계
1) **로그인/토큰 발급 API**
   - 엔드포인트 설계: `POST /api/auth/login` (payload: email(or id) + password) → JWT(accessToken) 반환
   - 인증 실패 응답: 401, `ApiResponse.error("인증 실패", "UNAUTHORIZED")`
   - PasswordEncoder 검증 + UserStatus 체크(ACTIVE만 허용)
2) **토큰 재발급 API**
   - 엔드포인트 설계: `POST /api/auth/refresh` (refreshToken 또는 재발급용 accessToken) → 새 accessToken 반환
   - 선택: refresh 저장소/블랙리스트 필요 여부 결정
3) **Security 예외 매핑 강화**
   - `AccessDeniedException` → 403, `AuthenticationException` → 401을 `GlobalExceptionHandler` 또는 `AuthenticationEntryPoint/AccessDeniedHandler`에서 `ApiResponse` 포맷으로 응답
4) **권한 검증 테스트 추가**
   - 단위: FreeboardController(MockMvc standalone)에서 인증 미설정 → 401/403 기대
   - 통합: `@SpringBootTest` + Security 필터 포함, 실제 JWT 발급 후 CRUD 호출 시나리오
5) **통합 테스트(@SpringBootTest + Security)**
   - 준비: 테스트용 User seed + JWT 발급 헬퍼
   - 시나리오: 로그인 → 토큰 획득 → 생성/조회/수정/삭제 요청, 토큰 누락/권한 부족 케이스 포함

## 4. 엔드포인트 설계 초안 (제안)
- `POST /api/auth/login`
  - 요청: `{ "email": "...", "password": "..." }`
  - 응답: `{ "success": true, "data": { "accessToken": "...", "expiresIn": 3600 } }`
- `POST /api/auth/refresh`
  - 요청: `{ "refreshToken": "..." }`
  - 응답: 새 accessToken (동일 포맷)

## 5. 해야 할 작업 체크리스트
- [ ] `AuthController` 추가(login/refresh)
- [ ] `GlobalExceptionHandler`에 Auth/AccessDenied 매핑 추가 (`ApiResponse` 포맷)
- [ ] FreeboardController 테스트에 인증 실패/권한 부족 케이스 추가
- [ ] 통합 테스트(@SpringBootTest + Security) 작성 (JWT 발급 포함)
- [ ] 문서 반영: 02-implementation/05-controller/06-test에 인증 예외 응답 및 테스트 시나리오 업데이트

## 6. 참고
- JWT Secret: `security.jwt.secret` (Base64) / 만료 `security.jwt.expiration-ms`
- 역할 매핑: `User.Authority` → `ROLE_{AUTH}` (NORMAL/ARMBAND/ADMIN)
- 상태 체크: UserStatus가 ACTIVE가 아니면 로그인 실패 처리 권장

