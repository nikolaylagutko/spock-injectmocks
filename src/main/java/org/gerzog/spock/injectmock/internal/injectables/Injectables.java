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
package org.gerzog.spock.injectmock.internal.injectables;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import org.gerzog.spock.injectmock.api.Injectable;
import org.gerzog.spock.injectmock.injections.IInjectable;
import org.gerzog.spock.injectmock.mocking.api.Mock;
import org.gerzog.spock.injectmock.mocking.api.Spy;
import org.gerzog.spock.injectmock.mocking.api.Stub;
import org.spockframework.runtime.model.FieldInfo;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
public final class Injectables {

	private static final Map<Class<? extends Annotation>, Function<FieldInfo, IInjectable>> injectableProducers;

	static {
		Map<Class<? extends Annotation>, Function<FieldInfo, IInjectable>> map = new HashMap<>();

		map.put(Mock.class, MockInjectable::new);
		map.put(Spy.class, SpyInjectable::new);
		map.put(Stub.class, StubInjectable::new);

		injectableProducers = Collections.unmodifiableMap(map);
	}

	private Injectables() {

	}

	public static Optional<IInjectable> forField(final FieldInfo field) {
		IInjectable result = null;

		if (field.isAnnotationPresent(Injectable.class)) {
			Optional<IInjectable> injectable = injectableProducers.entrySet().stream().filter(entry -> field.isAnnotationPresent(entry.getKey())).map(entry -> entry.getValue().apply(field)).findFirst();

			result = injectable.orElseGet(() -> new CustomInjectable(field));
		}

		return Optional.ofNullable(result);
	}
}
