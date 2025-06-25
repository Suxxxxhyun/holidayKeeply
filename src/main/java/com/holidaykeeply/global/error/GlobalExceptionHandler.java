package com.holidaykeeply.global.error;

import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.holidaykeeply.global.common.response.Response;

@RestControllerAdvice(annotations = { RestController.class })
public class GlobalExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Response<Map<String, String>>> handleValidationException(
			final MethodArgumentNotValidException ex) {
		Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getFieldErrors().forEach(error -> {
			errors.put(error.getField(), error.getDefaultMessage());
		});

		final ExceptionDto response = new ExceptionDto(ErrorCode.NOT_VALID_ERROR, errors.toString());
		return ResponseEntity.badRequest().body(Response.fail(response));
	}

	@ExceptionHandler(NoSuchElementException.class)
	public ResponseEntity<String> handleNoSuchElementException(NoSuchElementException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body("해당 데이터를 찾을 수 없습니다.");
	}

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body("서버 내부 오류가 발생했습니다.");
	}
}
