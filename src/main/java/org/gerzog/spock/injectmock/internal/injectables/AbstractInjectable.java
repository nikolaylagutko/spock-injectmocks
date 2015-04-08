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

import java.util.function.Function;

import org.gerzog.spock.injectmock.injections.IInjectable;
import org.spockframework.runtime.model.FieldInfo;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
abstract class AbstractInjectable implements IInjectable {

	private final String name;

	private final Class<?> type;

	private final FieldInfo field;

	protected AbstractInjectable(final FieldInfo fieldInfo) {
		this.field = fieldInfo;
		this.name = fieldInfo.getName();
		this.type = fieldInfo.getType();
	}

	@Override
	public Class<?> getType() {
		return type;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Object instantiate(final Object target) {
		final Object original = field.readValue(target);

		final Object actual = getInstatiationProcessor(target).apply(original);

		field.writeValue(target, actual);

		return actual;
	}

	protected abstract Function<Object, Object> getInstatiationProcessor(Object target);

}
