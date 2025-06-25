package com.holidaykeeply.api.dto;

import java.time.LocalDate;

import com.holidaykeeply.global.common.annotation.ExistsCountryCode;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

public class HolidayUpsertDto {

	public record Request(
		@NotBlank(message = "countryCode는 필수입니다.")
		@ExistsCountryCode
		String countryCode,

		@NotNull(message = "year는 필수입니다.")
		LocalDate year
	) {}
}
