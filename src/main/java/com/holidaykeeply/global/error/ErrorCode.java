package com.holidaykeeply.global.error;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
	NOT_VALID_ERROR(HttpStatus.BAD_REQUEST, "A-001", "잘못된 요청입니다."),
	EXTERNAL_API_ERROR(HttpStatus.SERVICE_UNAVAILABLE, "A-002", "외부 API 호출 중 오류가 발생했습니다."),
	EXTERNAL_API_TIMEOUT(HttpStatus.REQUEST_TIMEOUT, "A-003", "외부 API 호출 시간이 초과되었습니다."),
	EXTERNAL_API_NOT_FOUND(HttpStatus.NOT_FOUND, "A-004", "요청한 데이터를 찾을 수 없습니다.");


	private final HttpStatus httpStatus;
	private final String errorCode;
	private final String message;
}
