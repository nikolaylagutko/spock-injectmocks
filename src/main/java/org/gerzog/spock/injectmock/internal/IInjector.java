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

import java.util.List;

import org.spockframework.runtime.model.FieldInfo;

/**
 * Injector of field from Spec's field value to @Subjec's field
 *
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
public interface IInjector {

	/**
	 * Inject's corresponding field from candidates to subject retrieving
	 * field's value from spec
	 *
	 * @param spec
	 *            Spec's instance
	 * @param subject
	 *            instance of field marked with @Subject
	 * @param fields
	 *            list of fields marked with @InjectMock
	 */
	void inject(final Object spec, Object subject, final List<FieldInfo> fields);

	/**
	 * Name of injectable property
	 *
	 * @return name of property associated with current element
	 */
	String getPropertyName();

}
