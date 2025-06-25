package com.holidaykeeply.global.common.response;

import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.holidaykeeply.global.error.ExceptionDto;

public record Response<T>(
	@JsonIgnore
	HttpStatus httpStatus,
	boolean success,
	@Nullable T data,
	@Nullable ExceptionDto error
) {
	public static <T> Response<T> ok(@Nullable final T data) {
		return new Response<>(HttpStatus.OK, true, data, null);
	}

	public static <T> Response<T> created(@Nullable final T data) {
		return new Response<>(HttpStatus.CREATED, true, data, null);
	}

	public static <T> Response<T> fail(final ExceptionDto e) {
		return new Response<>(e.code().getHttpStatus(), false,null, e);
	}
}
