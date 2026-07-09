package com.example.web;

import java.util.ArrayList;
import java.util.List;
import org.opentmf.commons.patch.JsonPatchException;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import com.example.common.dto.ErrorDto;
import com.example.common.dto.ErrorResponseDto;
import com.example.common.exceptions.DuplicateRoleException;
import com.example.common.exceptions.DuplicateUsernameException;
import com.example.common.exceptions.ResourceNotFoundException;
import com.example.common.exceptions.UnprocessableContentException;
import com.example.mapper.ErrorMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RestControllerAdvice
@Log4j2
@RequiredArgsConstructor
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    private final ErrorMapper mapper;

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponseDto> handleAuthentication(AuthenticationException ex,
            HttpServletRequest request) {
        log.info("Authentication error: {}", ex.getMessage());
        return new ResponseEntity<ErrorResponseDto>(
                mapper.toDto(ex, HttpStatus.UNAUTHORIZED.value(), request.getRequestURI()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ErrorResponseDto> handleAuthorizationDenied(AuthorizationDeniedException ex,
            HttpServletRequest request) {
        log.warn("Authorization error: {}", ex.getMessage());
        return new ResponseEntity<ErrorResponseDto>(
                mapper.toDto(ex, HttpStatus.FORBIDDEN.value(), request.getRequestURI()), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponseDto> handleBadCredential(BadCredentialsException ex,
            HttpServletRequest request) {
        log.info("Bad credentials: {}", ex.getMessage());
        return new ResponseEntity<ErrorResponseDto>(
                mapper.toDto(ex, "Username or password is incorrect", HttpStatus.UNAUTHORIZED.value(),
                        request.getRequestURI()),
                HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(DuplicateUsernameException.class)
    public ResponseEntity<ErrorResponseDto> handleDuplicateUsername(DuplicateUsernameException ex,
            HttpServletRequest request) {
        log.info("Duplicate username: {}", ex.getMessage());
        return new ResponseEntity<ErrorResponseDto>(
                mapper.toDto(ex, HttpStatus.CONFLICT.value(), request.getRequestURI()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(DuplicateRoleException.class)
    public ResponseEntity<ErrorResponseDto> handleDuplicateRole(DuplicateRoleException ex, HttpServletRequest request) {
        log.info("Duplicate role: {}", ex.getMessage());
        return new ResponseEntity<ErrorResponseDto>(
                mapper.toDto(ex, HttpStatus.CONFLICT.value(), request.getRequestURI()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleResourceNotFound(ResourceNotFoundException ex,
            HttpServletRequest request) {
        log.info("Resource not found: {}", ex.getMessage());
        return new ResponseEntity<ErrorResponseDto>(
                mapper.toDto(ex, HttpStatus.NOT_FOUND.value(), request.getRequestURI()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UnprocessableContentException.class)
    public ResponseEntity<ErrorResponseDto> handleUnprocessableContent(UnprocessableContentException ex,
            HttpServletRequest request) {
        log.info("Failed to process input: {}", ex.getMessage());
        return new ResponseEntity<ErrorResponseDto>(
                mapper.toDto(ex, HttpStatus.UNPROCESSABLE_CONTENT.value(), request.getRequestURI()),
                HttpStatus.UNPROCESSABLE_CONTENT);
    }

    @ExceptionHandler(JsonPatchException.class)
    public ResponseEntity<ErrorResponseDto> handleJsonPatch(JsonPatchException ex,
            HttpServletRequest request) {
        log.info("JSON Patch Failed: {}", ex.getMessage());
        return new ResponseEntity<ErrorResponseDto>(
                mapper.toDto(ex, HttpStatus.BAD_REQUEST.value(), request.getRequestURI()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDto> handleIllegalArgument(IllegalArgumentException ex,
            HttpServletRequest request) {
        log.info("Illegal argument: {}", ex.getMessage());
        return new ResponseEntity<ErrorResponseDto>(
                mapper.toDto(ex, HttpStatus.BAD_REQUEST.value(), request.getRequestURI()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConversionFailedException.class)
    public ResponseEntity<ErrorResponseDto> handleConversionFailed(ConversionFailedException ex,
            HttpServletRequest request) {
        log.info("Conversion failed: {}", ex.getMessage());
        return new ResponseEntity<ErrorResponseDto>(
                mapper.toDto(ex, HttpStatus.BAD_REQUEST.value(), request.getRequestURI()), HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
            HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        List<ErrorDto> errors = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.add(new ErrorDto(fieldName, errorMessage));
        });
        log.info("Invalid Method Argument: {}", ex.getMessage());

        return new ResponseEntity<Object>(
                mapper.toDto(ex, "Validation failed for one or more fields", HttpStatus.BAD_REQUEST.value(),
                        request.getDescription(false).replace("uri=", ""), errors),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ErrorResponseDto> handleGenericException(Exception ex, HttpServletRequest request) {
        log.error("An unexpected error has occurred. Name: {}, Message: {}", ex.getClass().getSimpleName(),
                ex.getMessage(), ex);
        return new ResponseEntity<ErrorResponseDto>(
                mapper.toDto(ex, HttpStatus.INTERNAL_SERVER_ERROR.value(), request.getRequestURI()),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
