package dev.earlydreamer.kirini.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.earlydreamer.kirini.domain.Freeboard;
import dev.earlydreamer.kirini.domain.User;
import dev.earlydreamer.kirini.dto.request.FreeboardCreateRequest;
import dev.earlydreamer.kirini.dto.request.FreeboardUpdateRequest;
import dev.earlydreamer.kirini.dto.response.FreeboardResponse;
import dev.earlydreamer.kirini.exception.GlobalExceptionHandler;
import dev.earlydreamer.kirini.security.JwtUser;
import dev.earlydreamer.kirini.service.FreeboardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class FreeboardControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private FreeboardService freeboardService;

    @InjectMocks
    private FreeboardController freeboardController;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(freeboardController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .defaultRequest(get("/").principal(auth(1, User.Authority.NORMAL)))
                .build();
    }

    private Authentication auth(Integer accountId, User.Authority authority) {
        JwtUser jwtUser = new JwtUser(accountId, authority);
        return new UsernamePasswordAuthenticationToken(jwtUser, null,
                List.of(new SimpleGrantedAuthority("ROLE_" + authority.name())));
    }

    private FreeboardResponse sampleResponse(Integer id) {
        return FreeboardResponse.builder()
                .id(id)
                .title("title")
                .contents("content")
                .readCount(1)
                .recommendCount(0)
                .writeTime(LocalDateTime.now())
                .modifyTime(LocalDateTime.now())
                .notifyType(Freeboard.NotifyType.COMMON)
                .deleteStatus(Freeboard.DeleteStatus.MAINTAINED)
                .accountId(1)
                .build();
    }

    @Test
    @DisplayName("게시글 생성 후 단건 조회 및 삭제")
    void createReadDelete() throws Exception {
        var createReq = new FreeboardCreateRequest();
        var titleField = FreeboardCreateRequest.class.getDeclaredField("title");
        titleField.setAccessible(true);
        titleField.set(createReq, "hello");
        var contentsField = FreeboardCreateRequest.class.getDeclaredField("contents");
        contentsField.setAccessible(true);
        contentsField.set(createReq, "world");

        Mockito.when(freeboardService.create(eq(1), any(FreeboardCreateRequest.class), any()))
                .thenReturn(sampleResponse(1));
        Mockito.when(freeboardService.getWithIncreaseReadCount(1)).thenReturn(sampleResponse(1));
        Mockito.doNothing().when(freeboardService).delete(eq(1), eq(1), any(User.Authority.class));

        String body = objectMapper.writeValueAsString(createReq);

        mockMvc.perform(post("/api/freeboard")
                        .principal(auth(1, User.Authority.NORMAL))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1));

        mockMvc.perform(get("/api/freeboard/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.readCount").value(1));

        mockMvc.perform(delete("/api/freeboard/1").principal(auth(1, User.Authority.NORMAL)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("게시글 수정")
    void updatePost() throws Exception {
        var createReq = new FreeboardCreateRequest();
        var titleField = FreeboardCreateRequest.class.getDeclaredField("title");
        titleField.setAccessible(true);
        titleField.set(createReq, "title");
        var contentsField = FreeboardCreateRequest.class.getDeclaredField("contents");
        contentsField.setAccessible(true);
        contentsField.set(createReq, "content");

        var updateReq = new FreeboardUpdateRequest();
        var uTitle = FreeboardUpdateRequest.class.getDeclaredField("title");
        uTitle.setAccessible(true);
        uTitle.set(updateReq, "new title");

        Mockito.when(freeboardService.create(eq(1), any(FreeboardCreateRequest.class), any()))
                .thenReturn(sampleResponse(1));
        var updated = FreeboardResponse.builder()
                .id(1)
                .title("new title")
                .contents("content")
                .readCount(1)
                .recommendCount(0)
                .writeTime(LocalDateTime.now())
                .modifyTime(LocalDateTime.now())
                .notifyType(Freeboard.NotifyType.COMMON)
                .deleteStatus(Freeboard.DeleteStatus.MAINTAINED)
                .accountId(1)
                .build();
        Mockito.when(freeboardService.update(eq(1), eq(1), any(FreeboardUpdateRequest.class), any()))
                .thenReturn(updated);

        String body = objectMapper.writeValueAsString(createReq);
        String updateBody = objectMapper.writeValueAsString(updateReq);

        mockMvc.perform(post("/api/freeboard")
                        .principal(auth(1, User.Authority.NORMAL))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());

        mockMvc.perform(put("/api/freeboard/1")
                        .principal(auth(1, User.Authority.NORMAL))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("new title"));
    }

    @Test
    @DisplayName("비인증 사용자는 작성 불가")
    void createRequiresAuth() throws Exception {
        FreeboardCreateRequest createReq = new FreeboardCreateRequest();
        var titleField = FreeboardCreateRequest.class.getDeclaredField("title");
        titleField.setAccessible(true);
        titleField.set(createReq, "hello");
        var contentsField = FreeboardCreateRequest.class.getDeclaredField("contents");
        contentsField.setAccessible(true);
        contentsField.set(createReq, "world");

        String body = objectMapper.writeValueAsString(createReq);

        mockMvc.perform(post("/api/freeboard")
                        .with(request -> { request.setUserPrincipal(null); return request; })
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("권한 부족 시 수정 불가")
    @WithMockUser(username = "2", roles = {"NORMAL"})
    void updateForbidden() throws Exception {
        // given existing post owner id = 1, but user=2 tries to edit
        var updateReq = new FreeboardUpdateRequest();
        var uTitle = FreeboardUpdateRequest.class.getDeclaredField("title");
        uTitle.setAccessible(true);
        uTitle.set(updateReq, "new title");

        Mockito.when(freeboardService.update(eq(1), eq(2), any(FreeboardUpdateRequest.class), any()))
                .thenThrow(new dev.earlydreamer.kirini.exception.BusinessException("삭제 권한이 없습니다.", "FORBIDDEN"));

        String updateBody = objectMapper.writeValueAsString(updateReq);

        mockMvc.perform(put("/api/freeboard/1")
                        .principal(auth(2, User.Authority.NORMAL))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBody))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("FORBIDDEN"));
    }
}
