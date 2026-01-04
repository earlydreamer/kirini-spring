package dev.earlydreamer.kirini.controller;

import dev.earlydreamer.kirini.domain.User;
import dev.earlydreamer.kirini.dto.request.FreeboardCreateRequest;
import dev.earlydreamer.kirini.dto.request.FreeboardUpdateRequest;
import dev.earlydreamer.kirini.dto.response.ApiResponse;
import dev.earlydreamer.kirini.dto.response.FreeboardListResponse;
import dev.earlydreamer.kirini.dto.response.FreeboardResponse;
import dev.earlydreamer.kirini.service.FreeboardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/freeboard")
@RequiredArgsConstructor
public class FreeboardController {

    private final FreeboardService freeboardService;

    // TODO: 인증 연동 시 accountId/authority는 SecurityContext에서 추출

    @PostMapping
    public ResponseEntity<ApiResponse<FreeboardResponse>> create(
            @RequestHeader(name = "X-Account-Id") Integer accountId,
            @RequestHeader(name = "X-Account-Authority", required = false) User.Authority authority,
            @Valid @RequestBody FreeboardCreateRequest request,
            @RequestHeader(name = "X-Forwarded-For", required = false) String forwardedFor,
            @RequestHeader(name = "X-Real-IP", required = false) String realIp,
            @RequestHeader(name = "Remote-Addr", required = false) String remoteAddr
    ) {
        String ip = pickClientIp(forwardedFor, realIp, remoteAddr);
        FreeboardResponse response = freeboardService.create(accountId, request, ip);
        return ResponseEntity.ok(ApiResponse.success("게시글이 등록되었습니다.", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FreeboardResponse>> getOne(@PathVariable Integer id) {
        FreeboardResponse response = freeboardService.getWithIncreaseReadCount(id);
        return ResponseEntity.ok(ApiResponse.success("게시글 조회에 성공했습니다.", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<FreeboardListResponse>> getList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        FreeboardListResponse response = freeboardService.getList(page, size);
        return ResponseEntity.ok(ApiResponse.success("게시글 목록 조회에 성공했습니다.", response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<FreeboardResponse>> update(
            @PathVariable Integer id,
            @RequestHeader(name = "X-Account-Id") Integer accountId,
            @RequestHeader(name = "X-Account-Authority", required = false) User.Authority authority,
            @Valid @RequestBody FreeboardUpdateRequest request
    ) {
        FreeboardResponse response = freeboardService.update(id, accountId, request, authority != null ? authority : User.Authority.NORMAL);
        return ResponseEntity.ok(ApiResponse.success("게시글이 수정되었습니다.", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Integer id,
            @RequestHeader(name = "X-Account-Id") Integer accountId,
            @RequestHeader(name = "X-Account-Authority", required = false) User.Authority authority
    ) {
        freeboardService.delete(id, accountId, authority != null ? authority : User.Authority.NORMAL);
        return ResponseEntity.ok(ApiResponse.success("게시글이 삭제되었습니다."));
    }

    private String pickClientIp(String forwardedFor, String realIp, String remoteAddr) {
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            // X-Forwarded-For가 다중 IP일 수 있으니 첫 번째 IP 사용
            int commaIdx = forwardedFor.indexOf(',');
            return commaIdx > 0 ? forwardedFor.substring(0, commaIdx).trim() : forwardedFor.trim();
        }
        if (realIp != null && !realIp.isBlank()) {
            return realIp.trim();
        }
        return remoteAddr;
    }
}

