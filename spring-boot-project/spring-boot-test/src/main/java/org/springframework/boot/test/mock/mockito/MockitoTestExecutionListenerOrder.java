/*
 * Copyright 2012-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.boot.test.mock.mockito;

import org.springframework.core.Ordered;

public enum MockitoTestExecutionListenerOrder {

	/**
	 * Order of {@link MockitoTestExecutionListener}.
	 */
	MOCKITO(1_950),

	/**
	 * Order of {@link ResetAllMocksTestExecutionListener}.
	 */
	RESET_ALL_MOCKS(Ordered.LOWEST_PRECEDENCE - 110),

	/**
	 * Order of {@link ResetMocksTestExecutionListener}.
	 */
	RESET_MOCKS(Ordered.LOWEST_PRECEDENCE - 100),

	/**
	 * Order of {@link MockitoStrictStubsTestExecutionListener}.
	 */
	MOCKITO_STRICT_STUBS(Ordered.LOWEST_PRECEDENCE - 80);

	private final int value;

	MockitoTestExecutionListenerOrder(int value) {
		this.value = value;
	}

	public int getValue() {
		return this.value;
	}

}
