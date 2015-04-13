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
package org.gerzog.spock.injectmock.utils;

import java.util.List;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Stream;

import org.spockframework.runtime.model.FieldInfo;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
public final class InjectMocksUtils {

	private InjectMocksUtils() {

	}

	public static <T> Class<?>[] toClassArray(final List<T> objects, final Function<T, Class<?>> classFunction) {
		return toClassArray(objects.stream(), classFunction);
	}

	public static <T> Class<?>[] toClassArray(final T[] objects, final Function<T, Class<?>> classFunction) {
		return toClassArray(Stream.of(objects), classFunction);
	}

	public static <T> Class<?>[] toClassArray(final Stream<T> objects, final Function<T, Class<?>> classFunction) {
		return toArray(objects, classFunction, Class<?>[]::new);
	}

	public static <T> Object[] toObjectArray(final List<T> objects, final Function<T, Object> converter) {
		return toArray(objects.stream(), converter, Object[]::new);
	}

	private static <T, C> C[] toArray(final Stream<T> objects, final Function<T, C> converter, final IntFunction<C[]> arrayGenerator) {
		return objects.map(converter).toArray(arrayGenerator);
	}

	public static Class<?> getTargetClass(final FieldInfo field, final Object instance) {
		Class<?> result = field.getType();

		if (result.equals(Object.class)) {
			result = instance.getClass();
		}

		return result;
	}

}
