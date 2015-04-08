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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.gerzog.spock.injectmock.injections.IInjectable;
import org.gerzog.spock.injectmock.injections.IInjector;
import org.gerzog.spock.injectmock.internal.injectables.Injectables;
import org.gerzog.spock.injectmock.internal.injectors.Injector;
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

	private final FieldInfo subjectField;

	private final List<FieldInfo> injectableFields;

	public InjectMocksMethodInterceptor(final FieldInfo subjectField, final List<FieldInfo> injectableFields) {
		this.subjectField = subjectField;
		this.injectableFields = injectableFields;
	}

	@Override
	public void intercept(final IMethodInvocation invocation) throws Throwable {
		inject(invocation.getInstance());

		invocation.proceed();
	}

	private void inject(final Object specInstance) {
		final IInjector injector = createInjector();

		injector.inject(specInstance, createInjectables());
	}

	private List<IInjectable> createInjectables() {
		return injectableFields.stream().map(field -> Injectables.forField(field)).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
	}

	private IInjector createInjector() {
		return new Injector(subjectField);
	}

}
