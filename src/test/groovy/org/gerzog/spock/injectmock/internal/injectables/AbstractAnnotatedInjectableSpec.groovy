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
package org.gerzog.spock.injectmock.internal.injectables

import org.spockframework.runtime.InvalidSpecException
import org.spockframework.runtime.model.FieldInfo

import spock.lang.Specification

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
class AbstractAnnotatedInjectableSpec extends Specification {

	final static INSTANTIATION_METHOD_NAME = 'method'

	final static FIELD_CLASS = String

	final static FIELD_VALUE = 'value'

	final static FIELD_NAME = 'field'

	def field = Mock(FieldInfo)

	def target = GroovyMock(Object)

	def injectable

	def setup() {
		field.name >> FIELD_NAME
		field.type >> FIELD_CLASS

		injectable = Spy(AbstractAnnotatedInjectable, constructorArgs: [
			field,
			INSTANTIATION_METHOD_NAME
		])
	}

	def "check an error if field already have value"() {
		setup:
		field.readValue(target) >> new Object()

		when:
		injectable.instantiate(target)

		then:
		thrown(InvalidSpecException)
	}

	def "check method was called on target object to instantiate injectable"() {
		when:
		injectable.instantiate(target)

		then:
		1 * target."$INSTANTIATION_METHOD_NAME"(FIELD_NAME, FIELD_CLASS)
	}

	def "check returned value"() {
		setup:
		target."$INSTANTIATION_METHOD_NAME"(FIELD_NAME, FIELD_CLASS) >> FIELD_VALUE

		when:
		def result = injectable.instantiate(target)

		then:
		result == FIELD_VALUE
	}
}
