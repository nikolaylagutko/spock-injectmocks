/**
 * Copyright 2015 Nikolay Lagutko <nikolay.lagutko@mail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gerzog.spock.injectmock;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.gerzog.spock.injectmock.api.InjectMock;
import org.gerzog.spock.injectmock.internal.FieldInjector;
import org.gerzog.spock.injectmock.internal.IInjector;
import org.gerzog.spock.injectmock.internal.MethodInjector;
import org.spockframework.runtime.GroovyRuntimeUtil;
import org.spockframework.runtime.InvalidSpecException;
import org.spockframework.runtime.extension.IMethodInterceptor;
import org.spockframework.runtime.extension.IMethodInvocation;
import org.spockframework.runtime.model.FieldInfo;

/**
 * Method Interceptor for Spec's Setup method.
 *
 * Initializes injectable fields and then run Setup method itself so it's
 * possible to work with this fields in Setup method
 *
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
public class InjectMocksMethodInterceptor implements IMethodInterceptor {

	private final List<Class<? extends Annotation>> supportedAnnotations;

	private final FieldInfo subjectField;

	private final List<FieldInfo> injectableFields;

	public InjectMocksMethodInterceptor(final List<Class<? extends Annotation>> supportedAnnotations, final FieldInfo subjectField, final List<FieldInfo> injectableFields) {
		this.supportedAnnotations = supportedAnnotations;
		this.subjectField = subjectField;
		this.injectableFields = injectableFields;
	}

	@Override
	public void intercept(final IMethodInvocation invocation) throws Throwable {
		inject(invocation.getInstance());

		invocation.proceed();
	}

	private void inject(final Object specInstance) {
		final Object subject = defineSubject(specInstance);

		initializeInjectables(specInstance);

		final List<IInjector> injectors = defineInjectors(subject);

		injectors.forEach(injector -> injector.inject(specInstance, subject, injectableFields));
	}

	private Object defineSubject(final Object spec) {
		final Object subject = subjectField.readValue(spec);

		if (subject == null) {
			throw new InvalidSpecException("@Subject field is not initialized!");
		}

		return subject;
	}

	private void validateInjectors(final List<IInjector> injectors) {
		final List<FieldInfo> missingFields = injectableFields.stream().filter(field -> hasInjector(field, injectors)).collect(Collectors.toList());

		if (!missingFields.isEmpty()) {
			throw new InvalidSpecException("Fields <" + toString(missingFields) + "> that cannot be injected.\nPlease verify that @Subject contain field/methods for injection with correct annotations.");
		}
	}

	private String toString(final List<FieldInfo> fieldInfo) {
		return fieldInfo.stream().map(field -> field.getName()).collect(Collectors.joining(", "));
	}

	private boolean hasInjector(final FieldInfo field, final List<IInjector> injectors) {
		return !injectors.stream().anyMatch(injector -> injector.getPropertyName().equals(field.getName()));
	}

	private List<IInjector> defineInjectors(final Object subject) {
		final List<IInjector> result = new ArrayList<>();

		result.addAll(defineMethodInjectors(subject));
		result.addAll(defineFieldInjectors(subject));

		validateInjectors(result);

		return result;
	}

	private List<IInjector> defineFieldInjectors(final Object subject) {
		return createInjectors(subject.getClass()::getDeclaredFields, field -> new FieldInjector(field));
	}

	private List<IInjector> defineMethodInjectors(final Object subject) {
		return createInjectors(subject.getClass()::getMethods, method -> new MethodInjector(method));
	}

	private <T extends AccessibleObject> List<IInjector> createInjectors(final Supplier<T[]> elementProducer, final Function<T, IInjector> injectorProducer) {
		return Stream.of(elementProducer.get()).filter(this::isAnnotated).map(injectorProducer).filter(this::isInjectablePresent).collect(Collectors.toList());
	}

	private boolean isInjectablePresent(final IInjector injector) {
		return injectableFields.stream().anyMatch(field -> field.getName().equals(injector.getPropertyName()));
	}

	private <T extends AccessibleObject> boolean isAnnotated(final T element) {
		return supportedAnnotations.stream().anyMatch(annotation -> element.isAnnotationPresent(annotation));
	}

	private void initializeInjectables(final Object specInstance) {
		injectableFields.forEach(field -> initializeInjectable(field, specInstance));
	}

	private void initializeInjectable(final FieldInfo field, final Object specInstance) {
		final InjectMock annotation = field.getAnnotation(InjectMock.class);

		switch (annotation.instantiateAs()) {
		case SPY:
			spy(specInstance, field);
			break;
		case MOCK:
			mock(specInstance, field);
			break;
		case CUSTOM:
			// leave field value as is
			break;
		default:
			throw new IllegalStateException("InstantiationType <" + annotation.instantiateAs() + " is not supported");
		}
	}

	private void mock(final Object specInstance, final FieldInfo field) {
		applyMockingMethod(specInstance, field, "Mock");
	}

	private void spy(final Object specInstance, final FieldInfo field) {
		applyMockingMethod(specInstance, field, "Spy");
	}

	private void applyMockingMethod(final Object specInstance, final FieldInfo field, final String method) {
		final Object mockValue = GroovyRuntimeUtil.invokeMethod(specInstance, method + "Impl", field.getName(), field.getType());

		field.writeValue(specInstance, mockValue);
	}
}
