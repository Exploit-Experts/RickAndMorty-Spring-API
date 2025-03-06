package com.rickmorty.handlers;

import com.rickmorty.Models.CustomErrorResponse;
import com.rickmorty.Models.ValidationErrorResponse;
import com.rickmorty.exceptions.*;
import com.rickmorty.exceptions.ConflictException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<CustomErrorResponse> handlerUserNotFound(UserNotFoundException ex) {
        return new ResponseEntity<>(new CustomErrorResponse(ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<CustomErrorResponse> handlePageNotFoundException(NotFoundException ex) {
        return new ResponseEntity<>(new CustomErrorResponse(ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidIdException.class)
    public ResponseEntity<CustomErrorResponse> handleInvalidIdException(InvalidIdException ex) {
        return new ResponseEntity<>(new CustomErrorResponse(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(LocationNotFoundException.class)
    public ResponseEntity<CustomErrorResponse> handleResourceNotFoundException(LocationNotFoundException ex) {
        return new ResponseEntity<>(new CustomErrorResponse(ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RewriteErrorException.class)
    public ResponseEntity<CustomErrorResponse> handleRewriteErrorException(RewriteErrorException ex) {
        return new ResponseEntity<>(new CustomErrorResponse(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<CustomErrorResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        String message = "Parâmetro " + ex.getName() + " inválido";
        return new ResponseEntity<>(new CustomErrorResponse(message), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<CustomErrorResponse> handleNoResourceFoundException(NoResourceFoundException ex) {
        return new ResponseEntity<>(new CustomErrorResponse("Rota inexistente"), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<String> handleNoHandlerFoundException(NoHandlerFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("A rota " + ex.getRequestURL() + " não foi encontrada.");
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<CustomErrorResponse> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        return new ResponseEntity<>(new CustomErrorResponse(ex.getMessage()), HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(CharacterNotFoundException.class)
    public ResponseEntity<CustomErrorResponse> handleCharacterNotFoundException(CharacterNotFoundException ex) {
        return new ResponseEntity<>(new CustomErrorResponse(ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EpisodeNotFoundException.class)
    public ResponseEntity<CustomErrorResponse> handleEpisodeNotFoundException(EpisodeNotFoundException ex) {
        return new ResponseEntity<>(new CustomErrorResponse(ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidParameterException.class)
    public ResponseEntity<CustomErrorResponse> handleInvalidParameterException(InvalidParameterException ex) {
        CustomErrorResponse error = new CustomErrorResponse(ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ValidationErrorException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationErrorException(ValidationErrorException ex) {
        log.error("Erro de validação: " + ex.getErrors());
        return new ResponseEntity<>(new ValidationErrorResponse(ex.getErrors()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<CustomErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        String message = "Erro no corpo da requisição: o JSON está mal formatado ou contém valores inválidos.";
        CustomErrorResponse error = new CustomErrorResponse(message);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<CustomErrorResponse> handleConflictException(ConflictException ex) {
        return new ResponseEntity<>(new CustomErrorResponse(ex.getMessage()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(FavoriteAlreadyExists.class)
    public ResponseEntity<CustomErrorResponse> handleFavoriteAlreadyExists(FavoriteAlreadyExists ex) {
        return new ResponseEntity<>(new CustomErrorResponse(ex.getMessage()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(FavoriteNotFound.class)
    public ResponseEntity<CustomErrorResponse> handleFavoriteNotFound(FavoriteNotFound ex) {
        return new ResponseEntity<>(new CustomErrorResponse(ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CustomErrorResponse> handleException(Exception ex) {
        log.error("Um erro inesperado aconteceu: " + ex.getMessage());
        return new ResponseEntity<>(new CustomErrorResponse("Ocorreu um erro inesperado."), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
