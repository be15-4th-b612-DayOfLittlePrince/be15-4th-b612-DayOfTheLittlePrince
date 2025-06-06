package littleprince.plan.query.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import littleprince.common.dto.ApiResponse;
import littleprince.config.security.model.CustomUserDetail;
import littleprince.plan.query.dto.response.ShortListResponse;
import littleprince.plan.query.dto.response.ShortListsAllResponse;
import littleprince.plan.query.dto.response.ShortPlanDateResponse;
import littleprince.plan.query.service.ShortListQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Date;

@Tag(name = "단기 투두 리스트")
@RestController
@RequestMapping("/plan")
@RequiredArgsConstructor
public class ShortListQueryController {

    private final ShortListQueryService planQueryService;

    /* 단기 리스트 조회 */
    @GetMapping("/short/{date}/todo")
    @Operation(summary = "단기 투두 조회",description = "사용자는 본인이 작성한 해당날짜에 단기 플랜을 조회할 수 있다.")
    public ResponseEntity<ApiResponse<ShortListResponse>> getShortList(
            @AuthenticationPrincipal CustomUserDetail customUserDetail,
            @PathVariable Date date
    )
    {
        Long memberId =  customUserDetail.getMemberId();

        ShortListResponse response = planQueryService.getShortList(memberId, date);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /* 단기 플랜 날짜 여부 조회 */
    @GetMapping("/short")
    @Operation(summary = "단기 플랜 투두 여부 조회",description = "사용자는 본인이 작성한 어느 날짜에 단기 플랜을 작성했는지 확인할 수 있다.")
    public ResponseEntity<ApiResponse<ShortPlanDateResponse>> getShortDates(
            @AuthenticationPrincipal CustomUserDetail customUserDetail
    )
    {
        Long memberId = customUserDetail.getMemberId();

        ShortPlanDateResponse response = planQueryService.getShortDates(memberId);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 단기 플랜 전체 조회 => task table 안에 있는 오늘 날짜 전체 조회(장기 플랜 내에 있는 것까지 포함한 모든 단기 리스트 조회)
    @GetMapping("/short/all")
    @Operation(summary = "단기 투두 전체 조회",description = "사용자는 본인이 작성한 해당날짜에 모든 단기플랜을 조회할 수 잇다.")
    public ResponseEntity<ApiResponse<ShortListsAllResponse>> getShortListsAll(
            @AuthenticationPrincipal CustomUserDetail customUserDetail
    ){
        Long memberId = customUserDetail.getMemberId();

        ShortListsAllResponse response = planQueryService.getShortListsAll(memberId);

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}