package com.holidaykeeply.api.dto;

import java.time.LocalDate;

import com.querydsl.core.annotations.QueryProjection;

import lombok.Getter;

public class HolidayInfoDto {

	@Getter
	public static class Response{
		Long id;
		String localName;
		String name;
		String country;
		boolean fixed;
		boolean global;
		String launchYear;
		LocalDate localDate;

		@QueryProjection
		public Response(
			Long id,
			String localName,
			String name,
			String country,
			boolean fixed,
			boolean global,
			String launchYear,
			LocalDate localDate
		){
			this.id = id;
			this.localName = localName;
			this.name = name;
			this.country = country;
			this.fixed = fixed;
			this.global = global;
			this.launchYear = launchYear;
			this.localDate = localDate;
		}
	}
}
