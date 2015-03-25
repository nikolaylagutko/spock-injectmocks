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

import org.spockframework.runtime.model.FieldInfo

import spock.lang.Specification

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
class AbstractInjectableSpec extends Specification {

	final static FIELD_VALUE = 'value'

	final static FIELD_NAME = 'field'

	final static FIELD_CLASS = String

	def field = Mock(FieldInfo)

	def target = Mock(Object)

	def injectable

	def setup() {
		field.name >> FIELD_NAME
		field.type >> FIELD_CLASS

		injectable = Spy(AbstractInjectable, constructorArgs: [field])
	}

	def "verify injectable's name"() {
		expect:
		injectable.name == FIELD_NAME
	}

	def "verify injectable's type"() {
		expect:
		injectable.type == FIELD_CLASS
	}

	def "check original field's value instantiated"() {
		setup:
		field.readValue(target) >> FIELD_VALUE

		when:
		def result = injectable.instantiate(target)

		then:
		result == FIELD_VALUE
	}
}
