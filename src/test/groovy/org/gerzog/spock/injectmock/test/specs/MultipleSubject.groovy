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
package org.gerzog.spock.injectmock.test.specs

import org.gerzog.spock.injectmock.api.InjectMock
import org.gerzog.spock.injectmock.test.data.Bean
import org.gerzog.spock.injectmock.test.data.FieldInjection

import spock.lang.Specification
import spock.lang.Subject

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
class MultipleSubject extends Specification {

	@InjectMock
	Bean autowiredField

	@Subject
	FieldInjection subject1

	@Subject
	FieldInjection subject2
}
