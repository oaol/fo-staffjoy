package fo.staffjoy.common.exception;

/**
 * 
 * @author bryce
 * @Date Aug 18, 2019
 */
public class StaffjoyException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public StaffjoyException(String message, Throwable cause) {
        super(message, cause);
    }

    public StaffjoyException(String message) {
        super(message);
    }

    /**
     * 用异常处理流程因为需要记录栈针会消耗性能，屏蔽战阵
     */
    @Override
    public Throwable fillInStackTrace() {
        return this;
    }
}
