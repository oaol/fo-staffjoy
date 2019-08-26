package fo.staffjoy.common.error;

import org.springframework.web.bind.annotation.ExceptionHandler;

import fo.staffjoy.common.auth.PermissionDeniedException;
import fo.staffjoy.common.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;

@Deprecated
@Slf4j
//@RestControllerAdvice
public class GlobalExceptionTranslator {

//    @ExceptionHandler(ServiceException.class)
//    public BaseResponse handleError(ServiceException e) {
//        log.error("Service Exception", e);
//        return BaseResponse
//                .builder()
//                .code(e.getResultCode())
//                .message(e.getMessage())
//                .build();
//    }
//
//    @ExceptionHandler(PermissionDeniedException.class)
//    public BaseResponse handleError(PermissionDeniedException e) {
//        log.error("Permission Denied", e);
//        return BaseResponse
//                .builder()
//                .code(e.getResultCode())
//                .message(e.getMessage())
//                .build();
//    }
}
