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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.example.ClassToMock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestExecutionListeners.MergeMode;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public class ResetAllMocksTestExecutionListenerTests {

	@Test
	void resetAllMocksAfterFailedTest() {
		List<JUnitUtils.TestReport> testReports = JUnitUtils.runTestClass(ResetAllMocksAfterFailedTest.class);

		JUnitUtils.assertFailedTest(testReports, 0, AssertionError.class);
		JUnitUtils.assertPassedTest(testReports, 1);
	}

	@Test
	void resetAllMocksBeanMethod() {
		List<JUnitUtils.TestReport> testReports = JUnitUtils.runTestClass(ResetAllMocksBeanMethod.class);

		JUnitUtils.assertPassedTest(testReports, 0);
		JUnitUtils.assertPassedTest(testReports, 1);
	}

	@Test
	void resetAllMocksBeforeEachMethod() {
		List<JUnitUtils.TestReport> testReports = JUnitUtils.runTestClass(ResetAllMocksBeforeEachMethod.class);

		JUnitUtils.assertPassedTest(testReports, 0);
		JUnitUtils.assertPassedTest(testReports, 1);
	}

	@Test
	void resetAllMocksMockBean() {
		List<JUnitUtils.TestReport> testReports = JUnitUtils.runTestClass(ResetAllMocksMockBean.class);

		JUnitUtils.assertPassedTest(testReports, 0);
		JUnitUtils.assertPassedTest(testReports, 1);
	}

	@Test
	void resetAllMocksTestMethod() {
		List<JUnitUtils.TestReport> testReports = JUnitUtils.runTestClass(ResetAllMocksTestMethod.class);

		JUnitUtils.assertPassedTest(testReports, 0);
		JUnitUtils.assertPassedTest(testReports, 1);
	}

	@ExtendWith(SpringExtension.class)
	@TestExecutionListeners(value = ResetAllMocksTestExecutionListener.class, mergeMode = MergeMode.MERGE_WITH_DEFAULTS)
	@TestMethodOrder(MethodOrderer.Alphanumeric.class)
	static class ResetAllMocksAfterFailedTest {

		@Autowired
		private ClassToMock classToMock;

		@Test
		void test1() {
			given(this.classToMock.method1ToMock("input")).willReturn("output");
			fail("I failed");
		}

		@Test
		void test2() {
			assertThat(this.classToMock.method1ToMock("input")).isEqualTo(null);
		}

		@Configuration
		public static class TestConfiguration {

			@Bean
			public ClassToMock classToMock() {
				return mock(ClassToMock.class);
			}

		}

	}

	@ExtendWith(SpringExtension.class)
	@TestExecutionListeners(value = ResetAllMocksTestExecutionListener.class, mergeMode = MergeMode.MERGE_WITH_DEFAULTS)
	@TestMethodOrder(MethodOrderer.Alphanumeric.class)
	static class ResetAllMocksBeanMethod {

		@Autowired
		private ClassToMock classToMock;

		@Test
		void test1() {
			assertThat(this.classToMock.method1ToMock("input")).isEqualTo("output");
		}

		@Test
		void test2() {
			assertThat(this.classToMock.method1ToMock("input")).isEqualTo(null);
		}

		@Configuration
		public static class TestConfiguration {

			@Bean
			public ClassToMock classToMock() {
				ClassToMock classToMock = mock(ClassToMock.class);
				given(classToMock.method1ToMock("input")).willReturn("output");
				return classToMock;
			}

		}

	}

	@ExtendWith(SpringExtension.class)
	@TestExecutionListeners(value = ResetAllMocksTestExecutionListener.class, mergeMode = MergeMode.MERGE_WITH_DEFAULTS)
	@TestMethodOrder(MethodOrderer.Alphanumeric.class)
	static class ResetAllMocksBeforeEachMethod {

		@Autowired
		private ClassToMock classToMock;

		@BeforeEach
		void setUp() {
			given(this.classToMock.method1ToMock("input")).willReturn("output");
		}

		@Test
		void test1() {
			assertThat(this.classToMock.method1ToMock("input")).isEqualTo("output");
		}

		@Test
		void test2() {
			assertThat(this.classToMock.method1ToMock("input")).isEqualTo("output");
		}

		@Configuration
		public static class TestConfiguration {

			@Bean
			public ClassToMock classToMock() {
				return mock(ClassToMock.class);
			}

		}

	}

	@ExtendWith(SpringExtension.class)
	@TestExecutionListeners(value = ResetAllMocksTestExecutionListener.class, mergeMode = MergeMode.MERGE_WITH_DEFAULTS)
	@TestMethodOrder(MethodOrderer.Alphanumeric.class)
	static class ResetAllMocksMockBean {

		@MockBean
		private ClassToMock classToMock;

		@Test
		void test1() {
			given(this.classToMock.method1ToMock("input")).willReturn("output");
			assertThat(this.classToMock.method1ToMock("input")).isEqualTo("output");
		}

		@Test
		void test2() {
			assertThat(this.classToMock.method1ToMock("input")).isEqualTo(null);
		}

	}

	@ExtendWith(SpringExtension.class)
	@TestExecutionListeners(value = ResetAllMocksTestExecutionListener.class, mergeMode = MergeMode.MERGE_WITH_DEFAULTS)
	@TestMethodOrder(MethodOrderer.Alphanumeric.class)
	static class ResetAllMocksTestMethod {

		@Autowired
		private ClassToMock classToMock;

		@Test
		void test1() {
			given(this.classToMock.method1ToMock("input")).willReturn("output");
			assertThat(this.classToMock.method1ToMock("input")).isEqualTo("output");
		}

		@Test
		void test2() {
			assertThat(this.classToMock.method1ToMock("input")).isEqualTo(null);
		}

		@Configuration
		public static class TestConfiguration {

			@Bean
			public ClassToMock classToMock() {
				return mock(ClassToMock.class);
			}

		}

	}

}
