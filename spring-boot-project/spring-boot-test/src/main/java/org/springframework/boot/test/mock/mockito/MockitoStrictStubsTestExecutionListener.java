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

import java.util.List;
import java.util.Set;

import org.mockito.Mockito;
import org.mockito.internal.junit.DefaultStubbingLookupListener;
import org.mockito.internal.junit.UnusedStubbingsFinder;
import org.mockito.listeners.StubbingLookupListener;
import org.mockito.quality.Strictness;

import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

public class MockitoStrictStubsTestExecutionListener extends AbstractTestExecutionListener {

	private Set<Object> mocks;

	private final StubbingLookupListener stubbingLookupListener = new DefaultStubbingLookupListener(
			Strictness.STRICT_STUBS);

	@Override
	public int getOrder() {
		return MockitoTestExecutionListenerOrder.MOCKITO_STRICT_STUBS.getValue();
	}

	@Override
	public void beforeTestMethod(TestContext testContext) throws Exception {
		this.mocks = MockitoUtils.collectAllMocks(testContext.getApplicationContext());

		for (Object mock : this.mocks) {
			getStubbingLookupListeners(mock).add(this.stubbingLookupListener);
		}
	}

	@Override
	public void afterTestMethod(TestContext testContext) throws Exception {
		for (Object mock : this.mocks) {
			getStubbingLookupListeners(mock).remove(this.stubbingLookupListener);
		}

		if (testContext.getTestException() != null) {
			return;
		}

		new UnusedStubbingsFinder().getUnusedStubbings(this.mocks).reportUnused();
		Mockito.validateMockitoUsage();
	}

	private static List<StubbingLookupListener> getStubbingLookupListeners(Object mock) {
		return Mockito.mockingDetails(mock).getMockCreationSettings().getStubbingLookupListeners();
	}

}
