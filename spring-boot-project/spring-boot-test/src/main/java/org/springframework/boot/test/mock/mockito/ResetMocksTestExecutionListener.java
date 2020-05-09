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

import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;
import org.springframework.test.context.support.AbstractTestExecutionListener;
import org.springframework.util.ClassUtils;

/**
 * {@link TestExecutionListener} to reset any mock beans that have been marked with a
 * {@link MockReset}.
 *
 * @author Phillip Webb
 * @since 1.4.0
 */
public class ResetMocksTestExecutionListener extends AbstractTestExecutionListener {

	private static final boolean MOCKITO_IS_PRESENT = ClassUtils.isPresent("org.mockito.MockSettings",
			ResetMocksTestExecutionListener.class.getClassLoader());

	@Override
	public int getOrder() {
		return MockitoTestExecutionListenerOrder.RESET_MOCKS.getValue();
	}

	@Override
	public void beforeTestMethod(TestContext testContext) throws Exception {
		if (MOCKITO_IS_PRESENT) {
			MockitoUtils.resetMocks(testContext.getApplicationContext(), MockReset.BEFORE);
		}
	}

	@Override
	public void afterTestMethod(TestContext testContext) throws Exception {
		if (MOCKITO_IS_PRESENT) {
			MockitoUtils.resetMocks(testContext.getApplicationContext(), MockReset.AFTER);
		}
	}

}
