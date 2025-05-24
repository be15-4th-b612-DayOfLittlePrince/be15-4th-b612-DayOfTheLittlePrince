package littleprince.common.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public enum GlobalErrorCode implements ErrorCode{

    // 그 외 기타 오류
    VALIDATION_ERROR("11001", "입력 값 검증 오류입니다.", HttpStatus.BAD_REQUEST),
    INTERNAL_SERVER_ERROR("11002", "내부 서버 오류 입니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    UNAUTHORIZED_REQUEST("11003", "유효하지 않은 요청입니다.", HttpStatus.UNAUTHORIZED),
    ACCESS_TOKEN_EXPIRED("11004", "Access Token이 만료되었습니다.", HttpStatus.UNAUTHORIZED),   // 401
    REFRESH_TOKEN_EXPIRED("11005", "Refresh Token이 만료되었습니다.", HttpStatus.UNAUTHORIZED), // 401
    INVALID_TOKEN("11006", "토큰이 유효하지 않습니다.", HttpStatus.BAD_REQUEST),
    ACCESS_DENIED("10007", "해당 페이지에 대한 조회 권한이 없습니다.", HttpStatus.FORBIDDEN), // 400
    SEND_EMAIL_FAILED("10007", "이메일 전송에 실패하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
}