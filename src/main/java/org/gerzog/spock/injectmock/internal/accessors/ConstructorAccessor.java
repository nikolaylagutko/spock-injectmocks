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

import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.gerzog.spock.injectmock.injections.IAccessor;
import org.gerzog.spock.injectmock.utils.InjectMocksUtils;
import org.spockframework.runtime.InvalidSpecException;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
public class ConstructorAccessor implements IAccessor {

	@Override
	public boolean exists(final Class<?> clazz, final String name, final Class<?>... types) {
		return ConstructorUtils.getAccessibleConstructor(clazz, types) != null;
	}

	@Override
	public Object apply(final Object target, final String name, final Object value) {
		try {
			Object[] values = (Object[]) value;
			Class<?>[] types = InjectMocksUtils.toClassArray(values, Object::getClass);
			return ConstructorUtils.invokeExactConstructor((Class<?>) target, values, types);
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
			throw new InvalidSpecException("Cannot create <" + target + "> object by constructor args <" + value + ">", e);
		}
	}
}
