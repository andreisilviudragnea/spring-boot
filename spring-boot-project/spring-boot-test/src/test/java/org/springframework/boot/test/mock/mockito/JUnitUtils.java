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

import java.util.ArrayList;
import java.util.List;

import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;

import static org.assertj.core.api.Assertions.assertThat;

public final class JUnitUtils {

	private JUnitUtils() {

	}

	public static List<TestReport> runTestClass(Class<?> testClass) {
		LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
				.selectors(DiscoverySelectors.selectClass(testClass)).build();

		List<TestReport> testReports = new ArrayList<>();

		Launcher launcher = LauncherFactory.create();

		launcher.registerTestExecutionListeners(new TestExecutionListener() {
			@Override
			public void executionFinished(TestIdentifier testIdentifier, TestExecutionResult testExecutionResult) {
				if (testIdentifier.getDisplayName().endsWith("()")) {
					testReports.add(new TestReport(testIdentifier, testExecutionResult));
				}
			}
		});

		launcher.execute(request);

		return testReports;
	}

	public static void assertPassedTest(List<JUnitUtils.TestReport> testReports, int index) {
		JUnitUtils.TestReport testReport = testReports.get(index);
		assertThat(testReport.getTestExecutionResult().getStatus()).isEqualTo(TestExecutionResult.Status.SUCCESSFUL);
	}

	public static void assertFailedTest(List<JUnitUtils.TestReport> testReports, int index,
			Class<? extends Throwable> throwableClass) {
		JUnitUtils.TestReport testReport = testReports.get(index);
		Throwable throwable = testReport.getTestExecutionResult().getThrowable().get();
		assertThat(throwable).isInstanceOf(throwableClass);
		assertThat(throwable.getSuppressed()).isEmpty();
	}

	public static class TestReport {

		private final TestIdentifier testIdentifier;

		private final TestExecutionResult testExecutionResult;

		public TestReport(TestIdentifier testIdentifier, TestExecutionResult testExecutionResult) {
			this.testIdentifier = testIdentifier;
			this.testExecutionResult = testExecutionResult;
		}

		public TestIdentifier getTestIdentifier() {
			return this.testIdentifier;
		}

		public TestExecutionResult getTestExecutionResult() {
			return this.testExecutionResult;
		}

	}

}
