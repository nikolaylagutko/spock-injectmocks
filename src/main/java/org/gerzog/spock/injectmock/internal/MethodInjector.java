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
package org.gerzog.spock.injectmock.internal;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.commons.lang3.StringUtils;
import org.spockframework.runtime.InvalidSpecException;

/**
 * Injector implementation for method-based injection
 *
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
public class MethodInjector extends AbstractInjector<Method> {

	public MethodInjector(final Method accessible) {
		super(accessible);
	}

	@Override
	protected void inject(final Method accessible, final Object instance, final Object value) {
		try {
			accessible.invoke(instance, value);
		} catch (InvocationTargetException | IllegalAccessException | IllegalArgumentException e) {
			throw new InvalidSpecException("Cannot inject value <" + value + "> using method <" + accessible + ">", e);
		}
	}

	@Override
	protected String getPropertyName(final Method accessible) {
		String propertyName = accessible.getName();
		if (propertyName.startsWith("set")) {
			propertyName = propertyName.substring(3);
		}

		return StringUtils.uncapitalize(propertyName);
	}

}
