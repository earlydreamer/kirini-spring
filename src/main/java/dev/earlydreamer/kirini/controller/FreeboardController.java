package dev.earlydreamer.kirini.controller;

import dev.earlydreamer.kirini.domain.User;
import dev.earlydreamer.kirini.dto.request.FreeboardCreateRequest;
import dev.earlydreamer.kirini.dto.request.FreeboardUpdateRequest;
import dev.earlydreamer.kirini.dto.response.ApiResponse;
import dev.earlydreamer.kirini.dto.response.FreeboardListResponse;
import dev.earlydreamer.kirini.dto.response.FreeboardResponse;
import dev.earlydreamer.kirini.security.JwtUser;
import dev.earlydreamer.kirini.service.FreeboardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/freeboard")
@RequiredArgsConstructor
public class FreeboardController {

    private final FreeboardService freeboardService;

    @PostMapping
    public ResponseEntity<ApiResponse<FreeboardResponse>> create(
            Authentication authentication,
            @Valid @RequestBody FreeboardCreateRequest request,
            @RequestHeader(name = "X-Forwarded-For", required = false) String forwardedFor,
            @RequestHeader(name = "X-Real-IP", required = false) String realIp,
            @RequestHeader(name = "Remote-Addr", required = false) String remoteAddr
    ) {
        JwtUser jwtUser = requireJwtUser(authentication);
        String ip = pickClientIp(forwardedFor, realIp, remoteAddr);
        FreeboardResponse response = freeboardService.create(jwtUser.accountId(), request, ip);
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
            Authentication authentication,
            @Valid @RequestBody FreeboardUpdateRequest request
    ) {
        JwtUser jwtUser = requireJwtUser(authentication);
        FreeboardResponse response = freeboardService.update(id, jwtUser.accountId(), request, jwtUser.authority());
        return ResponseEntity.ok(ApiResponse.success("게시글이 수정되었습니다.", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Integer id,
            Authentication authentication
    ) {
        JwtUser jwtUser = requireJwtUser(authentication);
        freeboardService.delete(id, jwtUser.accountId(), jwtUser.authority());
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

    private JwtUser requireJwtUser(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null || !(authentication.getPrincipal() instanceof JwtUser jwtUser)) {
            throw new dev.earlydreamer.kirini.exception.BusinessException("인증이 필요합니다.", "UNAUTHORIZED");
        }
        return jwtUser;
    }
}
