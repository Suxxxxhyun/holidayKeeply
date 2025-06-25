package com.holidaykeeply.api.dto;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import com.holidaykeeply.global.common.annotation.ExistsCountryName;
import com.holidaykeeply.global.common.annotation.ValidDateRange;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@ValidDateRange
public record SearchCondition(
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	@NotNull(message = "startDate는 필수입니다.")
	LocalDate startDate,

	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	@NotNull(message = "endDate는 필수입니다.")
	LocalDate endDate,

	@NotBlank(message = "countryName은 필수입니다.")
	@ExistsCountryName
	String countryName
) {
}
