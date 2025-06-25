package com.holidaykeeply.domain.entity;

import java.time.LocalDate;
import java.util.List;

import org.hibernate.annotations.Comment;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Holiday extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "holiday_id")
	private Long id;

	@Comment("공휴일 영어 이름")
	@Column(nullable = false)
	private String name;

	@Comment("공휴일 현지어 이름")
	@Column(nullable = false)
	private String localName;

	@Comment("공휴일 날짜")
	@Column(nullable = false)
	private LocalDate date;

	@Comment("해당 공휴일이 매년 고정되는지 여부")
	@Column(nullable = false)
	private boolean fixed;

	@Comment("해당 공휴일이 전 세계적으로 공통인지 여부")
	@Column(nullable = false)
	private boolean global;

	@Comment("공휴일이 처음 시행된 연도 (nullable)")
	private String launchYear;

	@ElementCollection
	private List<String> counties;

	@ElementCollection
	private List<String> types;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "country_id")
	private Country country;

	public void addCountry(LocalDate year, Country country) {
		this.country = country;
		this.date = LocalDate.of(year.getYear(), this.date.getMonth(), this.date.getDayOfMonth());
	}
}
