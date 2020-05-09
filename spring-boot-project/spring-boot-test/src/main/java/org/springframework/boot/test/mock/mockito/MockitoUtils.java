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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import org.mockito.Mockito;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

public final class MockitoUtils {

	private MockitoUtils() {

	}

	public static Set<Object> collectAllMocks(ApplicationContext applicationContext) {
		Set<Object> allMockBeans = new HashSet<>();
		doWithSingletonBeans(applicationContext, (bean) -> {
			if (Mockito.mockingDetails(bean).isMock()) {
				allMockBeans.add(bean);
			}
		});
		return allMockBeans;
	}

	public static void resetMocks(ApplicationContext applicationContext, MockReset reset) {
		doWithSingletonBeans(applicationContext, (bean) -> {
			if (reset.equals(MockReset.get(bean))) {
				Mockito.reset(bean);
			}
		});
	}

	public static void resetAllMocks(ApplicationContext applicationContext) {
		doWithSingletonBeans(applicationContext, (bean) -> {
			if (Mockito.mockingDetails(bean).isMock()) {
				Mockito.reset(bean);
			}
		});
	}

	public static void doWithSingletonBeans(ApplicationContext applicationContext, Consumer<Object> beanConsumer) {
		if (!(applicationContext instanceof ConfigurableApplicationContext)) {
			return;
		}

		ConfigurableListableBeanFactory beanFactory = ((ConfigurableApplicationContext) applicationContext)
				.getBeanFactory();

		String[] names = beanFactory.getBeanDefinitionNames();
		Set<String> instantiatedSingletons = new HashSet<>(Arrays.asList(beanFactory.getSingletonNames()));
		for (String name : names) {
			BeanDefinition definition = beanFactory.getBeanDefinition(name);
			if (definition.isSingleton() && instantiatedSingletons.contains(name)) {
				Object bean = beanFactory.getSingleton(name);
				beanConsumer.accept(bean);
			}
		}

		try {
			MockitoBeans mockedBeans = beanFactory.getBean(MockitoBeans.class);
			for (Object mockedBean : mockedBeans) {
				beanConsumer.accept(mockedBean);
			}
		}
		catch (NoSuchBeanDefinitionException ex) {
			// Continue
		}

		if (applicationContext.getParent() != null) {
			doWithSingletonBeans(applicationContext.getParent(), beanConsumer);
		}
	}

}
