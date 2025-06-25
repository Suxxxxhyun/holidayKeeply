package com.holidaykeeply.global.error;

import jakarta.validation.constraints.NotNull;

public record ExceptionDto(
	@NotNull ErrorCode code,

	@NotNull String message
) {
}
