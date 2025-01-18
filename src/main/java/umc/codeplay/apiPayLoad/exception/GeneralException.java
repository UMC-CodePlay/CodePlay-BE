package umc.codeplay.apiPayLoad.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

import umc.codeplay.apiPayLoad.code.BaseErrorCode;
import umc.codeplay.apiPayLoad.code.ErrorReasonDTO;

@Getter
@AllArgsConstructor
public class GeneralException extends RuntimeException {

    private BaseErrorCode code;

    public ErrorReasonDTO getErrorReason() {
        return this.code.getReason();
    }

    public ErrorReasonDTO getErrorReasonHttpStatus() {
        return this.code.getReasonHttpStatus();
    }
}
