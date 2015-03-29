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
package org.gerzog.spock.injectmock.internal.accessors;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.spockframework.runtime.InvalidSpecException;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
public class MethodAccessor extends AbstractSingleTypeAccessor {

	private static final String SETTER_PREFIX = "set";

	@Override
	public boolean exists(final Class<?> clazz, final String name, final Class<?> type) {
		return getDeclaredMethod(clazz, type, toMethodName(name)) != null;
	}

	@Override
	protected void internalSet(final Object target, final String name, final Object value) {
		final String methodName = toMethodName(name);
		final Method method = getDeclaredMethod(target.getClass(), value.getClass(), methodName);

		if (method != null) {
			final boolean original = method.isAccessible();
			try {
				method.setAccessible(true);
				method.invoke(target, value);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				throw new InvalidSpecException("Cannot write injectable value to method <" + target.getClass().getSimpleName() + "." + methodName + ">", e);
			} finally {
				method.setAccessible(original);
			}
		}
	}

	private static String toMethodName(final String propertyName) {
		return SETTER_PREFIX + StringUtils.capitalize(propertyName);
	}

	private static Method getDeclaredMethod(final Class<?> clazz, final Class<?> type, final String name) {
		Method result = null;

		if (type != null) {
			try {
				result = clazz.getDeclaredMethod(name, type);
			} catch (NoSuchMethodException e) {
				result = null;
			}

			if (result == null) {
				result = Stream.of(clazz.getDeclaredMethods()).filter(method -> {
					return (method.getName().equals(name)) && (method.getParameterCount() == 1) && (method.getParameterTypes()[0].isAssignableFrom(type));
				}).findFirst().orElse(null);
			}

			if ((result == null) && (clazz.getSuperclass() != null)) {
				result = getDeclaredMethod(clazz.getSuperclass(), type, name);
			}
		}

		return result;
	}
}
