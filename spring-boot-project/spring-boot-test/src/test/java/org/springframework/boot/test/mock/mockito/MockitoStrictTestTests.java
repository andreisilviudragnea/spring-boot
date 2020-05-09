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

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.exceptions.misusing.UnnecessaryStubbingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.JUnitUtils.TestReport;
import org.springframework.boot.test.mock.mockito.example.ClassToMock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public class MockitoStrictTestTests {

	@Test
	void mockitoStrictTestBeanMethod() {
		List<TestReport> testReports = JUnitUtils.runTestClass(MockitoStrictTestBeanMethod.class);

		JUnitUtils.assertFailedTest(testReports, 0, UnnecessaryStubbingException.class);
		JUnitUtils.assertPassedTest(testReports, 1);
	}

	@Test
	void mockitoStrictTestDirtiesContext() {
		List<TestReport> testReports = JUnitUtils.runTestClass(MockitoStrictTestDirtiesContext.class);

		JUnitUtils.assertFailedTest(testReports, 0, UnnecessaryStubbingException.class);
		JUnitUtils.assertPassedTest(testReports, 1);
	}

	@MockitoStrictTest
	@TestMethodOrder(MethodOrderer.Alphanumeric.class)
	static class MockitoStrictTestBeanMethod {

		@Autowired
		private ClassToMock classToMock;

		@Test
		void test1() {
			assertThat(this.classToMock.method2ToMock("input")).isEqualTo(null);
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

	@MockitoStrictTest
	@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
	@TestMethodOrder(MethodOrderer.Alphanumeric.class)
	static class MockitoStrictTestDirtiesContext {

		@Autowired
		private ClassToMock classToMock;

		@Test
		void test1() {
			assertThat(this.classToMock.method2ToMock("input")).isEqualTo(null);
		}

		@Test
		void test2() {
			assertThat(this.classToMock.method1ToMock("input")).isEqualTo("output");
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

}
