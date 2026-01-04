# 인증/인가 구현 계획 (JWT)

작성일: 2026-01-04

## 체크리스트
- [ ] 로그인/토큰 발급 API 설계 및 구현 (`POST /api/auth/login`)
- [ ] 토큰 재발급 API 설계 및 구현 (`POST /api/auth/refresh`)
- [ ] Security 예외 매핑(`AuthenticationException`, `AccessDeniedException`) → ApiResponse 포맷
- [ ] Freeboard 권한 부족/비로그인 테스트 추가
- [ ] 통합 테스트(@SpringBootTest + Security)로 JWT 발급→CRUD 시나리오 검증
- [ ] 문서/정책 업데이트 반영

## 설계 메모
- 권한 매핑: `User.Authority` → `ROLE_{AUTH}` (NORMAL/ARMBAND/ADMIN)
- 상태 체크: UserStatus ACTIVE만 로그인 성공
- 토큰: accessToken 우선, refreshToken 여부는 구현 시 결정
- CSRF: REST API용 disable
- 응답 포맷: `ApiResponse(success/message/data/errorCode)`

