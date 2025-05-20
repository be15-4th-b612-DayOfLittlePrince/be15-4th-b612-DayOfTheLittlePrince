package littleprince.member.query.controller;

import io.swagger.v3.oas.annotations.Operation;
import littleprince.common.dto.ApiResponse;
import littleprince.config.security.model.CustomUserDetail;
import littleprince.member.query.dto.response.MemberInfoResponse;
import littleprince.member.query.service.MemberQueryService;
import littleprince.member.query.service.MemberQueryServiceImpl;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberQueryController {

    private final MemberQueryService memberQueryService;

    @GetMapping("/info")
    @Operation(summary = "사용자 정보 조회", description = "사용자의 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<MemberInfoResponse>> getItemList(
            @AuthenticationPrincipal CustomUserDetail customUserDetail
    ) {
        MemberInfoResponse response = memberQueryService.getMemberInfo(customUserDetail);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

}
