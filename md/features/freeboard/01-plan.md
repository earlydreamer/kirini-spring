# 자유게시판 1차 구현 계획 (게시글만)

작성일: 2026-01-04

## 체크리스트
- [ ] DTO 정의 (request/response)
- [ ] Repository 구현 (목록 페이징, 단건 조회, 조회수 증가)
- [ ] Service 구현 (CRUD, 권한, 기본값, 조회수 증가 트랜잭션)
- [ ] Controller 구현 (REST API, 응답 포맷 준수)
- [ ] 수동 시나리오 점검

## 설계 상세
### 1) DTO
- `FreeboardCreateRequest`: title(1~50), contents(비어있지 않음)
- `FreeboardUpdateRequest`: title(optional, 1~50), contents(optional, 공백 불가)
- `FreeboardResponse`: id, title, contents, readCount, recommendCount, writeTime, modifyTime, notifyType, deleteStatus, accountId(optional), authorName(optional)
- `FreeboardListResponse`: 목록 + 페이징 메타(currentPage, totalPages, pageSize, totalCount)

### 2) Repository (Spring Data JPA)
- `FreeboardRepository extends JpaRepository<Freeboard, Integer>`
  - `Page<Freeboard> findByDeleteStatus(DeleteStatus deleteStatus, Pageable pageable);`
  - `Optional<Freeboard> findByIdAndDeleteStatus(Integer id, DeleteStatus status);`
  - `@Modifying @Query` 조회수 증가: `UPDATE Freeboard f SET f.readCount = f.readCount + 1 WHERE f.id = :id`

### 3) Service
- 기본값 세팅: notify=COMMON, deleteStatus=MAINTAINED, readCount/recommendCount=0
- 권한: 작성자 또는 ADMIN/ARMBAND만 수정/삭제
- 조회: 단건 조회 시 조회수 증가 후 엔티티 반환
- 삭제: deleteStatus=DELETED로 변경(논리 삭제)

### 4) Controller (REST)
- base path: `/api/freeboard`
- 요청/응답 포맷: `agents.md` success/message/data 사용
- 엔드포인트
  - POST `/` 작성 (로그인 필요)
  - GET `/{id}` 단건 조회(+조회수 증가)
  - GET `/` 목록 조회(page,size)
  - PUT `/{id}` 수정 (작성자 또는 관리자)
  - DELETE `/{id}` 삭제 (작성자 또는 관리자)

### 5) 권한/검증
- 제목 1~50, 내용 not blank
- 권한: 엔티티 `Authority` 기준(NORMAL/ARMBAND/ADMIN)

## 진행 순서
1. DTO 클래스 추가
2. Repository 추가
3. Service 추가
4. Controller 추가
5. 수동 점검 및 필요시 테스트 코드 추가

