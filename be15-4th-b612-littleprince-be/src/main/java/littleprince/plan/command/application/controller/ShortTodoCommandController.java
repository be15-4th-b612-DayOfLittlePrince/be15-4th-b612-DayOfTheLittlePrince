package littleprince.plan.command.application.controller;

import jakarta.validation.Valid;
import littleprince.common.dto.ApiResponse;
import littleprince.config.security.model.CustomUserDetail;
import littleprince.plan.command.application.dto.request.CreateShortTodoRequestDto;
import littleprince.plan.command.service.ShortTodoCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/plans/short-todos")
public class ShortTodoCommandController {
    private final ShortTodoCommandService shortTodoCommandService;

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createShortTodo(
            @AuthenticationPrincipal CustomUserDetail userDetail,
            @RequestBody @Valid CreateShortTodoRequestDto dto
            ){
        shortTodoCommandService.create(userDetail.getMemberId(), dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(null));
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<ApiResponse<Void>> deleteShortTodo(
            @AuthenticationPrincipal CustomUserDetail userDetail,
            @PathVariable Long taskId
    ) {
        shortTodoCommandService.deleteShortTodo(userDetail.getMemberId(), taskId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
