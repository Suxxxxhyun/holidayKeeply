package com.holidaykeeply.fixture;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.introspector.FieldReflectionArbitraryIntrospector;

public class FixtureMonkeyUtils {
	private static final FixtureMonkey DEFAULT = FixtureMonkey.builder()
		.objectIntrospector(FieldReflectionArbitraryIntrospector.INSTANCE)
		.build();

	private FixtureMonkeyUtils() {} // 인스턴스화 방지

	public static FixtureMonkey getDefault() {
		return DEFAULT;
	}
}
