package com.group5.taskFlow.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(JwtAuthenticationException.class)
  public ResponseEntity<?> handleJwtAuthenticationException(JwtAuthenticationException ex, WebRequest request) {
    ErrorDetails errorDetails = new ErrorDetails(new Date(), ex.getMessage(), request.getDescription(false));
    return new ResponseEntity<>(errorDetails, HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<?> handleEntityNotFoundException(EntityNotFoundException ex, WebRequest request) {
    ErrorDetails errorDetails = new ErrorDetails(new Date(), ex.getMessage(), request.getDescription(false));

    return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(EmailAlreadyExistsException.class)
  public ResponseEntity<?> handleEmailAlreadyExistsException(EmailAlreadyExistsException ex, WebRequest request) {
    ErrorDetails errorDetails = new ErrorDetails(new Date(), ex.getMessage(), request.getDescription(false));
    return new ResponseEntity<>(errorDetails, HttpStatus.CONFLICT);
  }

  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<?> handleBadCredentialsException(BadCredentialsException ex, WebRequest request) {
    ErrorDetails errorDetails = new ErrorDetails(new Date(), ex.getMessage(), request.getDescription(false));
    return new ResponseEntity<>(errorDetails, HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<?> globalExceptionHandler(Exception ex, WebRequest request) {
    ErrorDetails errorDetails = new ErrorDetails(new Date(), ex.getMessage(), request.getDescription(false));
    System.out.println("ERROR -- " + ex.getMessage() + "\n" + ex.getCause().toString());

    return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
    return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(EnumConstantNotPresentException.class)
  public ResponseEntity<ErrorDetails> handleEnumConstantNotPresentException(EnumConstantNotPresentException ex, WebRequest request) {
    ErrorDetails errorDetails = new ErrorDetails(new Date(), ex.getMessage(), request.getDescription(false));
    return new ResponseEntity<>(errorDetails, HttpStatus.NOT_ACCEPTABLE);
  }

  public static record ErrorDetails(
          Date timestamp,
          String message,
          String details

  ) {
  }
}
