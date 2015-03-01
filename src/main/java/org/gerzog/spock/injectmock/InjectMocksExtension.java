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
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.gerzog.spock.injectmock.api.InjectMock;
import org.spockframework.runtime.InvalidSpecException;
import org.spockframework.runtime.extension.IGlobalExtension;
import org.spockframework.runtime.model.FieldInfo;
import org.spockframework.runtime.model.SpecInfo;
import org.spockframework.util.ReflectionUtil;

import spock.lang.Subject;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
public class InjectMocksExtension implements IGlobalExtension {

	private static final String[] DEFAULT_ANNOTATION_CLASSES = {
		// java's @Resource
		"javax.annotation.Resource",
		// javax' @Inject
		"javax.inject.Inject",
		// guice's @Inject
		"com.google.Inject",
		// spring's @Autowired
		"org.springframework.beans.factory.annotation.Autowired",
		// spring's @Required
	"org.springframework.beans.factory.annotation.Required" };

	private static final List<Class<? extends Annotation>> SUPPORTED_ANNOTATIONS;

	static {
		@SuppressWarnings("unchecked")
		List<Class<? extends Annotation>> annotations = Stream.of(DEFAULT_ANNOTATION_CLASSES).map(className -> (Class<? extends Annotation>) ReflectionUtil.loadClassIfAvailable(className)).filter(clazz -> clazz != null).collect(Collectors.toList());

		SUPPORTED_ANNOTATIONS = Collections.unmodifiableList(annotations);
	}

	@Override
	public void visitSpec(final SpecInfo spec) {
		List<FieldInfo> injectables = getInjectableFields(spec);

		if (!injectables.isEmpty()) {
			FieldInfo subject = getSubjectField(spec);

			spec.addInterceptor(new InjectMocksMethodInterceptor(getSupportedAnnotations(), subject, injectables));
		}
	}

	private List<Class<? extends Annotation>> getSupportedAnnotations() {
		return SUPPORTED_ANNOTATIONS;
	}

	private List<FieldInfo> getInjectableFields(final SpecInfo spec) {
		return getAnnotatedFields(spec, InjectMock.class);
	}

	private FieldInfo getSubjectField(final SpecInfo spec) {
		List<FieldInfo> candidates = getAnnotatedFields(spec, Subject.class);

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
