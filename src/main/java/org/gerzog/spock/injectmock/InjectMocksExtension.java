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
import java.util.List;
import java.util.stream.Collectors;

import org.gerzog.spock.injectmock.api.Injectable;
import org.spockframework.runtime.InvalidSpecException;
import org.spockframework.runtime.extension.AbstractGlobalExtension;
import org.spockframework.runtime.model.FieldInfo;
import org.spockframework.runtime.model.SpecInfo;

import spock.lang.Subject;

/**
 * Extension entry point.
 *
 * Make some verifications on Spec's content and registers method interceptor
 * for setup method.
 *
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
public class InjectMocksExtension extends AbstractGlobalExtension {

	@Override
	public void visitSpec(final SpecInfo spec) {
		final List<FieldInfo> injectables = getInjectableFields(spec);

		if (!injectables.isEmpty()) {
			final FieldInfo subject = getSubjectField(spec);

			spec.addSetupInterceptor(new InjectMocksMethodInterceptor(subject, injectables));
		}
	}

	private List<FieldInfo> getInjectableFields(final SpecInfo spec) {
		return getAnnotatedFields(spec, Injectable.class);
	}

	private FieldInfo getSubjectField(final SpecInfo spec) {
		final List<FieldInfo> candidates = getAnnotatedFields(spec, Subject.class);

		if (candidates.isEmpty()) {
			throw new InvalidSpecException("There is no field with @Subject annotation to inject @InjectMock values");
		} else if (candidates.size() > 1) {
			throw new InvalidSpecException("Impossible to inject @InjectMock values to multiple @Subject objects");
		}

		return candidates.get(0);
	}

	private List<FieldInfo> getAnnotatedFields(final SpecInfo spec, final Class<? extends Annotation> annotation) {
		return spec.getAllFields().stream().filter(field -> field.isAnnotationPresent(annotation)).collect(Collectors.toList());
	}

}
