package fo.staffjoy.common.exception;

/**
 * Business Service Exception TODO http status code
 * 
 * @author bryce
 * @Date Aug 25, 2019
 */
public class ServiceException extends StaffjoyException {

    private static final long serialVersionUID = 2359767895161832954L;

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

}
