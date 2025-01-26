package umc.codeplay.apiPayLoad.code.status;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

import umc.codeplay.apiPayLoad.code.BaseErrorCode;
import umc.codeplay.apiPayLoad.code.ErrorReasonDTO;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {

    // 가장 일반적인 응답
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON400", "잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON401", "인증이 필요합니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),

    MEMBER_NOT_FOUND(HttpStatus.BAD_REQUEST, "MEMBER400", "유저를 찾을 수 없습니다."),
    MEMBER_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "MEMBER401", "유저가 이미 존재합니다."),

    NOT_AUTHORIZED(HttpStatus.BAD_REQUEST, "AUTH400", "인증되지 않은 요청입니다."),
    ID_OR_PASSWORD_WRONG(HttpStatus.BAD_REQUEST, "AUTH401", "아이디 혹은 비밀번호가 잘못되었습니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.BAD_REQUEST, "AUTH402", "유효하지 않은 리프레시 토큰입니다."),
    OAUTH_TOKEN_REQUEST_FAILED(HttpStatus.BAD_REQUEST, "AUTH403", "외부인증 토큰 요청에 실패했습니다."),
    OAUTH_USERINFO_REQUEST_FAILED(HttpStatus.BAD_REQUEST, "AUTH404", "외부인증 유저 정보 요청에 실패했습니다."),
    AUTHORIZATION_METHOD_ERROR(HttpStatus.BAD_REQUEST, "AUTH405", "인증 방식이 잘못되었습니다."),
    INVALID_OAUTH_PROVIDER(HttpStatus.BAD_REQUEST, "AUTH406", "유효하지 않은 OAuth 제공자입니다."),

    AWS_SERVICE_UNAVAILABLE(HttpStatus.BAD_REQUEST, "AWS400", "AWS S3에 파일을 업로드할 수 없습니다."),
    AWS_METHOD_NOT_ALLOWED(
            HttpStatus.METHOD_NOT_ALLOWED,
            "AWS405",
            "AWS S3 presigned url에서 해당 method는 허용되지 않습니다."),

    MUSIC_NOT_FOUND(HttpStatus.BAD_REQUEST, "MUSIC400", "음원을 찾을 수 없습니다."),

    LIKE_NOT_FOUND(HttpStatus.BAD_REQUEST, "LIKE400", "해당 좋아요를 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ErrorReasonDTO getReason() {
        return ErrorReasonDTO.builder().message(message).code(code).isSuccess(false).build();
    }

    @Override
    public ErrorReasonDTO getReasonHttpStatus() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .httpStatus(httpStatus)
                .build();
    }
}
