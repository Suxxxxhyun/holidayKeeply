package com.holidaykeeply.api.dto;

import java.time.LocalDate;

import com.holidaykeeply.global.common.annotation.ExistsCountryName;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class HolidayDeleteDto {
	public record Request(

		@NotBlank(message = "countryName은 필수입니다.")
		@ExistsCountryName
		String countryName,

		@NotNull(message = "year는 필수입니다.")
		LocalDate year
	) {}
}
