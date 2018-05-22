package com.thewhite.news.api;

import com.whitesoft.api.dto.ErrorDTO;
import com.whitesoft.util.exceptions.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static org.springframework.http.HttpStatus.*;


/**
 * Created by Korovin Anatolii on 12.11.17.
 * <p>
 * Controller advice for exception handling in API's requests
 *
 * @author Korovin Anatolii
 * @version 1.0
 */
@ControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {


    @ResponseStatus(CONFLICT)
    @ExceptionHandler(WSStatusException.class)
    public @ResponseBody
    ErrorDTO processEntityStatusException(WSStatusException exception) {
        return new ErrorDTO(exception.getCode(), exception.getMessage());
    }

    @ResponseStatus(NOT_FOUND)
    @ExceptionHandler(WSNotFoundException.class)
    public @ResponseBody
    ErrorDTO processEntityNotFoundException(WSNotFoundException exception) {
        return new ErrorDTO(exception.getCode(), exception.getMessage());
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(WSArgumentException.class)
    public @ResponseBody
    ErrorDTO processEntityArgumentException(WSArgumentException exception) {
        return new ErrorDTO(exception.getCode(), exception.getMessage());
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public
    @ResponseBody
    ErrorDTO processIllegalArgumentException(IllegalArgumentException exception) {
        return new ErrorDTO(400, "Неверные параметры запроса.");
    }

    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ExceptionHandler(RuntimeException.class)
    public @ResponseBody
    ErrorDTO processRuntimeException(RuntimeException exception) {
        return new ErrorDTO(0, exception.getMessage());
    }

    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ExceptionHandler(WSInternalException.class)
    public @ResponseBody
    ErrorDTO processEntityInternalException(WSInternalException exception) {
        return new ErrorDTO(exception.getCode(), exception.getMessage());
    }

    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public @ResponseBody
    ErrorDTO processEntityConditionInternalException() {
        return new ErrorDTO(666, "Не удалось сохранить изменения в базе.");
    }

    @ResponseStatus(FORBIDDEN)
    @ExceptionHandler(AccessDeniedException.class)
    public @ResponseBody
    ErrorDTO processAccessDeniedException(AccessDeniedException exception) {
        return new ErrorDTO(403, "Недостаточно прав.");
    }

    @ResponseStatus(FORBIDDEN)
    @ExceptionHandler(WSAccessDeniedException.class)
    public @ResponseBody
    ErrorDTO processAccessDeniedException(WSAccessDeniedException exception) {
        return new ErrorDTO(exception.getCode(), exception.getMessage());
    }

    @ResponseStatus(FORBIDDEN)
    @ExceptionHandler(WSForbiddenException.class)
    public @ResponseBody
    ErrorDTO processWsForbiddenException(WSForbiddenException exception) {
        return new ErrorDTO(exception.getCode(), exception.getMessage());
    }
}
