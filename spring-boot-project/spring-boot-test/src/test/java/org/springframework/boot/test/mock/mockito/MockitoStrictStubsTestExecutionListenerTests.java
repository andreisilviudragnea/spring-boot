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

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.exceptions.misusing.PotentialStubbingProblem;
import org.mockito.exceptions.misusing.UnfinishedStubbingException;
import org.mockito.exceptions.misusing.UnnecessaryStubbingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.example.ClassToMock;
import org.springframework.boot.test.mock.mockito.example.ClassUsingMock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestExecutionListeners.MergeMode;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public class MockitoStrictStubsTestExecutionListenerTests {

	@Test
	void potentialStubbingProblem() {
		List<JUnitUtils.TestReport> testReports = JUnitUtils.runTestClass(PotentialStubbingProblemTest.class);

		JUnitUtils.assertFailedTest(testReports, 0, PotentialStubbingProblem.class);
	}

	@Test
	void strictStubsTestMethod() {
		List<JUnitUtils.TestReport> testReports = JUnitUtils.runTestClass(StrictStubsTestMethod.class);

		JUnitUtils.assertFailedTest(testReports, 0, UnnecessaryStubbingException.class);
	}

	@Test
	void strictStubsBeforeEachMethod() {
		List<JUnitUtils.TestReport> testReports = JUnitUtils.runTestClass(StrictStubsBeforeEachMethod.class);

		JUnitUtils.assertFailedTest(testReports, 0, UnnecessaryStubbingException.class);
		JUnitUtils.assertFailedTest(testReports, 1, UnnecessaryStubbingException.class);
	}

	@Test
	void strictStubsNoFailure() {
		List<JUnitUtils.TestReport> testReports = JUnitUtils.runTestClass(StrictStubsNoFailure.class);

		JUnitUtils.assertPassedTest(testReports, 0);
		JUnitUtils.assertPassedTest(testReports, 1);
	}

	@Test
	void strictStubsBeanMethod() {
		List<JUnitUtils.TestReport> testReports = JUnitUtils.runTestClass(StrictStubsBeanMethod.class);

		JUnitUtils.assertFailedTest(testReports, 0, UnnecessaryStubbingException.class);
		JUnitUtils.assertPassedTest(testReports, 1);
	}

	@Test
	void strictStubsFailedTest() {
		List<JUnitUtils.TestReport> testReports = JUnitUtils.runTestClass(StrictStubsFailedTest.class);

		JUnitUtils.assertFailedTest(testReports, 0, AssertionError.class);
	}

	@Test
	void strictStubsAfterFailedTest() {
		List<JUnitUtils.TestReport> testReports = JUnitUtils.runTestClass(StrictStubsAfterFailedTest.class);

		JUnitUtils.assertFailedTest(testReports, 0, AssertionError.class);
		JUnitUtils.assertFailedTest(testReports, 1, UnnecessaryStubbingException.class);
	}

	@Test
	void mockCreationNotIntercepted() {
		List<JUnitUtils.TestReport> testReports = JUnitUtils.runTestClass(MockCreationNotIntercepted.class);

		JUnitUtils.assertPassedTest(testReports, 0);
	}

	@Test
	void strictStubsMockBean() {
		List<JUnitUtils.TestReport> testReports = JUnitUtils.runTestClass(StrictStubsMockBean.class);

		JUnitUtils.assertFailedTest(testReports, 0, UnnecessaryStubbingException.class);
	}

	@Test
	void invalidMockitoUsage() {
		List<JUnitUtils.TestReport> testReports = JUnitUtils.runTestClass(InvalidMockitoUsage.class);

		JUnitUtils.assertFailedTest(testReports, 0, UnfinishedStubbingException.class);
	}

	@ExtendWith(SpringExtension.class)
	@TestExecutionListeners(value = MockitoStrictStubsTestExecutionListener.class,
			mergeMode = MergeMode.MERGE_WITH_DEFAULTS)
	@TestMethodOrder(MethodOrderer.Alphanumeric.class)
	static class PotentialStubbingProblemTest {

		@Autowired
		private ClassToMock classToMock;

		@Autowired
		private ClassUsingMock classUsingMock;

		@Test
		void test1() {
			given(this.classToMock.method1ToMock("input")).willReturn("output");
			assertThat(this.classUsingMock.useMock("differentInput")).isEqualTo(null);
		}

		@Configuration
		public static class TestConfiguration {

			@Bean
			public ClassToMock classToMock() {
				return mock(ClassToMock.class);
			}

			@Bean
			public ClassUsingMock classUsingMock(ClassToMock classToMock) {
				return new ClassUsingMock(classToMock);
			}

		}

	}

	@ExtendWith(SpringExtension.class)
	@TestExecutionListeners(value = MockitoStrictStubsTestExecutionListener.class,
			mergeMode = MergeMode.MERGE_WITH_DEFAULTS)
	@TestMethodOrder(MethodOrderer.Alphanumeric.class)
	static class StrictStubsTestMethod {

		@Autowired
		private ClassToMock classToMock;

		@Test
		void test1() {
			given(this.classToMock.method2ToMock("input")).willReturn("output");

			given(this.classToMock.method1ToMock("input")).willReturn("output");
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
	@TestExecutionListeners(value = MockitoStrictStubsTestExecutionListener.class,
			mergeMode = MergeMode.MERGE_WITH_DEFAULTS)
	@TestMethodOrder(MethodOrderer.Alphanumeric.class)
	static class StrictStubsBeforeEachMethod {

		@Autowired
		private ClassToMock classToMock;

		@BeforeEach
		void setUp() {
			given(this.classToMock.method1ToMock("input1")).willReturn("output1");
			given(this.classToMock.method2ToMock("input2")).willReturn("output2");
		}

		@Test
		void test1() {
			assertThat(this.classToMock.method1ToMock("input1")).isEqualTo("output1");
		}

		@Test
		void test2() {
			assertThat(this.classToMock.method2ToMock("input2")).isEqualTo("output2");
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
	@TestExecutionListeners(value = MockitoStrictStubsTestExecutionListener.class,
			mergeMode = MergeMode.MERGE_WITH_DEFAULTS)
	@TestMethodOrder(MethodOrderer.Alphanumeric.class)
	static class StrictStubsNoFailure {

		@Autowired
		private ClassToMock classToMock;

		@BeforeEach
		void setUp() {
			given(this.classToMock.method1ToMock("input1")).willReturn("output1");
		}

		@Test
		void test1() {
			assertThat(this.classToMock.method1ToMock("input1")).isEqualTo("output1");
		}

		@Test
		void test2() {
			assertThat(this.classToMock.method1ToMock("input1")).isEqualTo("output1");
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
	@TestExecutionListeners(value = MockitoStrictStubsTestExecutionListener.class,
			mergeMode = MergeMode.MERGE_WITH_DEFAULTS)
	@TestMethodOrder(MethodOrderer.Alphanumeric.class)
	static class StrictStubsBeanMethod {

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

	@ExtendWith(SpringExtension.class)
	@TestExecutionListeners(value = MockitoStrictStubsTestExecutionListener.class,
			mergeMode = MergeMode.MERGE_WITH_DEFAULTS)
	@TestMethodOrder(MethodOrderer.Alphanumeric.class)
	static class StrictStubsFailedTest {

		@Autowired
		private ClassToMock classToMock;

		@BeforeEach
		void setUp() {
			given(this.classToMock.method2ToMock("input")).willReturn("output");
		}

		@Test
		void test1() {
			Assertions.fail("I failed");
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
	@TestExecutionListeners(value = MockitoStrictStubsTestExecutionListener.class,
			mergeMode = MergeMode.MERGE_WITH_DEFAULTS)
	@TestMethodOrder(MethodOrderer.Alphanumeric.class)
	static class StrictStubsAfterFailedTest {

		@Autowired
		private ClassToMock classToMock;

		@BeforeEach
		void setUp() {
			given(this.classToMock.method2ToMock("input")).willReturn("output");
		}

		@Test
		void test1() {
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
	@TestExecutionListeners(value = MockitoStrictStubsTestExecutionListener.class,
			mergeMode = MergeMode.MERGE_WITH_DEFAULTS)
	@TestMethodOrder(MethodOrderer.Alphanumeric.class)
	static class MockCreationNotIntercepted {

		@Mock
		private ClassToMock classToMock;

		@Test
		void test1() {
			given(this.classToMock.method2ToMock("input")).willReturn("output");

			given(this.classToMock.method1ToMock("input")).willReturn("output");
			assertThat(this.classToMock.method1ToMock("input")).isEqualTo("output");
		}

	}

	@ExtendWith(SpringExtension.class)
	@TestExecutionListeners(value = MockitoStrictStubsTestExecutionListener.class,
			mergeMode = MergeMode.MERGE_WITH_DEFAULTS)
	@TestMethodOrder(MethodOrderer.Alphanumeric.class)
	static class StrictStubsMockBean {

		@MockBean
		private ClassToMock classToMock;

		@Test
		void test1() {
			given(this.classToMock.method2ToMock("input")).willReturn("output");

			given(this.classToMock.method1ToMock("input")).willReturn("output");
			assertThat(this.classToMock.method1ToMock("input")).isEqualTo("output");
		}

	}

	@ExtendWith(SpringExtension.class)
	@TestExecutionListeners(value = MockitoStrictStubsTestExecutionListener.class,
			mergeMode = MergeMode.MERGE_WITH_DEFAULTS)
	@TestMethodOrder(MethodOrderer.Alphanumeric.class)
	static class InvalidMockitoUsage {

		@Autowired
		private ClassToMock classToMock;

		@Test
		void test1() {
			given(this.classToMock.method1ToMock("input"));
		}

		@Configuration
		public static class TestConfiguration {

			@Bean
			public ClassToMock classToMock() {
				return mock(ClassToMock.class);
			}

			@Bean
			public ClassUsingMock classUsingMock(ClassToMock classToMock) {
				return new ClassUsingMock(classToMock);
			}

		}

	}

}
