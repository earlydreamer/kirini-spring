package dev.earlydreamer.kirini.service;

import dev.earlydreamer.kirini.domain.Freeboard;
import dev.earlydreamer.kirini.domain.Freeboard.DeleteStatus;
import dev.earlydreamer.kirini.domain.Freeboard.NotifyType;
import dev.earlydreamer.kirini.domain.User;
import dev.earlydreamer.kirini.dto.request.FreeboardCreateRequest;
import dev.earlydreamer.kirini.dto.request.FreeboardUpdateRequest;
import dev.earlydreamer.kirini.dto.response.FreeboardListResponse;
import dev.earlydreamer.kirini.dto.response.FreeboardResponse;
import dev.earlydreamer.kirini.exception.BusinessException;
import dev.earlydreamer.kirini.repository.FreeboardRepository;
import dev.earlydreamer.kirini.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FreeboardService {

    private final FreeboardRepository freeboardRepository;
    private final UserRepository userRepository;

    @Transactional
    public FreeboardResponse create(Integer accountId, FreeboardCreateRequest request, String authorIp) {
        // accountId는 인증에서 가져온 값
        if (request.getTitle() == null || request.getTitle().isBlank()) {
            throw new BusinessException("제목은 필수입니다.", "TITLE_REQUIRED");
        }
        if (request.getContents() == null || request.getContents().isBlank()) {
            throw new BusinessException("내용은 필수입니다.", "CONTENTS_REQUIRED");
        }

        User user = userRepository.findById(accountId)
                .orElseThrow(() -> new BusinessException("사용자를 찾을 수 없습니다."));

        Freeboard entity = new Freeboard();
        entity.setTitle(request.getTitle());
        entity.setContents(request.getContents());
        entity.setReadCount(0);
        entity.setRecommendCount(0);
        entity.setWriteTime(java.time.LocalDateTime.now());
        entity.setAuthorIp(authorIp);
        entity.setNotifyType(NotifyType.COMMON);
        entity.setDeleteStatus(DeleteStatus.MAINTAINED);
        entity.setUser(user);

        Freeboard saved = freeboardRepository.save(entity);
        return FreeboardResponse.from(saved);
    }

    @Transactional
    public FreeboardResponse getWithIncreaseReadCount(Integer id) {
        Freeboard freeboard = freeboardRepository.findByIdAndDeleteStatus(id, DeleteStatus.MAINTAINED)
                .orElseThrow(() -> new BusinessException("게시글을 찾을 수 없습니다."));

        freeboardRepository.increaseReadCount(id);
        // 동시성 단순화: 증가 후 다시 조회 없이 엔티티 readCount 수동 증가
        freeboard.setReadCount(freeboard.getReadCount() == null ? 1 : freeboard.getReadCount() + 1);
        return FreeboardResponse.from(freeboard);
    }

    public FreeboardListResponse getList(int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(page - 1, 0), size);
        Page<FreeboardResponse> result = freeboardRepository.findByDeleteStatus(DeleteStatus.MAINTAINED, pageable)
                .map(FreeboardResponse::from);
        return FreeboardListResponse.from(result);
    }

    @Transactional
    public FreeboardResponse update(Integer id, Integer accountId, FreeboardUpdateRequest request, User.Authority authority) {
        // accountId/authority는 인증에서 가져온 값
        Freeboard freeboard = freeboardRepository.findByIdAndDeleteStatus(id, DeleteStatus.MAINTAINED)
                .orElseThrow(() -> new BusinessException("게시글을 찾을 수 없습니다.", "NOT_FOUND"));

        if (!canModify(freeboard, accountId, authority)) {
            throw new BusinessException("수정 권한이 없습니다.", "FORBIDDEN");
        }

        if (request.getTitle() != null && !request.getTitle().isBlank()) {
            freeboard.setTitle(request.getTitle());
        }
        if (request.getContents() != null) {
            if (request.getContents().isBlank()) {
                throw new BusinessException("내용은 공백일 수 없습니다.", "CONTENTS_REQUIRED");
            }
            freeboard.setContents(request.getContents());
        }
        freeboard.setModifyTime(java.time.LocalDateTime.now());

        return FreeboardResponse.from(freeboard);
    }

    @Transactional
    public void delete(Integer id, Integer accountId, User.Authority authority) {
        // accountId/authority는 인증에서 가져온 값
        Freeboard freeboard = freeboardRepository.findByIdAndDeleteStatus(id, DeleteStatus.MAINTAINED)
                .orElseThrow(() -> new BusinessException("게시글을 찾을 수 없습니다.", "NOT_FOUND"));

        if (!canModify(freeboard, accountId, authority)) {
            throw new BusinessException("삭제 권한이 없습니다.", "FORBIDDEN");
        }

        freeboard.setDeleteStatus(DeleteStatus.DELETED);
    }

    private boolean canModify(Freeboard freeboard, Integer accountId, User.Authority authority) {
        boolean isOwner = freeboard.getUser() != null && freeboard.getUser().getId().equals(accountId);
        boolean isManager = authority == User.Authority.ADMIN || authority == User.Authority.ARMBAND;
        return isOwner || isManager;
    }
}
