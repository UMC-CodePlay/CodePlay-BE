package umc.codeplay.apiPayLoad.exception.handler;

import umc.codeplay.apiPayLoad.code.BaseErrorCode;
import umc.codeplay.apiPayLoad.exception.GeneralException;

public class GeneralHandler extends GeneralException {

    public GeneralHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
