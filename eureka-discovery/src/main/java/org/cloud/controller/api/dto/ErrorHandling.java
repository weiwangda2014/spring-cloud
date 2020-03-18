package org.cloud.controller.api.dto;

import javassist.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ErrorHandling {

    private static final Logger log = LoggerFactory.getLogger(ErrorHandling.class);

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<SystemMessageDTO> illegalArgument(Exception e) {
        log.error("Error: ", e);

        return new ResponseEntity<SystemMessageDTO>(new SystemMessageDTO(
                HttpStatus.BAD_REQUEST,
                "invalid_input",
                e.getLocalizedMessage()
        ), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<SystemMessageDTO> NotFound(Exception e) {
        log.error("Not Found Error: ", e);

        return new ResponseEntity<SystemMessageDTO>(new SystemMessageDTO(
                HttpStatus.NOT_FOUND,
                "resource_not_found",
                e.getLocalizedMessage()
        ), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<SystemMessageDTO> generalError(Exception e) {
        log.error("General error: ", e);

        return new ResponseEntity<SystemMessageDTO>(new SystemMessageDTO(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "internal_error",
                e.getLocalizedMessage()
        ), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

