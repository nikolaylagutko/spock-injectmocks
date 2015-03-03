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

import java.lang.reflect.AccessibleObject;
import java.util.List;
import java.util.Optional;

import org.spockframework.runtime.model.FieldInfo;

/**
 * General implementation of Injector
 *
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
public abstract class AbstractInjector<T extends AccessibleObject> implements IInjector {

	private final T accessible;

	private final String propertyName;

	public AbstractInjector(final T accessible) {
		this.accessible = accessible;

		this.propertyName = getPropertyName(accessible);
	}

	@Override
	public void inject(final Object spec, final Object subject, final List<FieldInfo> fields) {
		Optional<Object> value = fields.stream().filter(field -> field.getName().equals(propertyName)).map(field -> field.readValue(spec)).findFirst();

		inject(accessible, subject, value.get());
	}

	/**
	 * Asks corresponding Java's element to set value for instance
	 *
	 * @param accessible
	 *            java 's element for injections
	 * @param instance
	 *            subject object for injections
	 * @param value
	 *            injectable value
	 */
	protected abstract void inject(T accessible, Object instance, Object value);

	protected abstract String getPropertyName(T accessible);

	@Override
	public String getPropertyName() {
		return propertyName;
	}

}
