package iq.ven.portal.app.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.net.ConnectException;

public class AbstractRestApiController {
    protected static final Logger logger = LoggerFactory.getLogger(AbstractRestApiController.class);

    @ExceptionHandler
    public String handleException(HttpServletRequest request,
                                  HttpServletResponse response,
                                  Writer writer,
                                  Exception exception) throws IOException {
        logger.error("Default annotation error handler", exception);

        String message = null;
        if (exception instanceof Exception) {
            message = exception.getCause().getMessage();
            response.setStatus(HttpStatus.BAD_GATEWAY.value());
        } else {
            if (isCausedBy(exception, ConnectException.class)) {
                message = "Service Temporarily Unavailable";
                response.setStatus(HttpStatus.SERVICE_UNAVAILABLE.value());
            } else {
                message = "Something went wrong. Try again later.";
                response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            }
        }
        if (message != null) {
            writer.append(message);
        }
        return null;
    }

    private static boolean isCausedBy(Throwable exception, Class<? extends Throwable> cause_class) {
        if (exception != null && exception.getClass().equals(cause_class)) {
            return true;
        } else if (exception != null) {
            return isCausedBy(exception.getCause(), cause_class);
        }
        return false;
    }
}
