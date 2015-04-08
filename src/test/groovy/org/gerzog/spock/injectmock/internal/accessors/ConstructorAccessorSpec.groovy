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
package org.gerzog.spock.injectmock.internal.accessors

import org.spockframework.runtime.InvalidSpecException

import spock.lang.Specification
import spock.lang.Unroll

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
class ConstructorAccessorSpec extends Specification {

	private static final String CONSTRUCTOR_NAME = 'any'

	private static final String FIRST_PARAMETER = 'param1'

	private static final String SECOND_PARAMETER = 'param2'

	private static final String STRING_VALUE = 'string'

	private static final int INTEGER_VALUE = 1

	static class TestClass {

		def param1

		def param2

		TestClass(String param1, Integer param2) {
			this.param1 = param1
			this.param2 = param2
		}

		TestClass(Integer param2) {
			this.param2 = param2
		}

		TestClass() {
		}
	}

	static class AnotherTestClass {

		AnotherTestClass(Number param) {
		}
	}

	def accessor = new ConstructorAccessor()

	@Unroll('check costructor was found for #list parameters')
	def "check constructor was found by parameter list"(def list) {
		expect:
		accessor.exists(TestClass, CONSTRUCTOR_NAME, list as Class[])

		where:
		list << [
			[String, Integer],
			[Integer],
			[]
		]
	}

	@Unroll('check no constructor of #clazz found for #list parameters')
	def "check no constructor found by parameter"(def clazz, def list) {
		expect:
		!accessor.exists(clazz, CONSTRUCTOR_NAME, list as Class[])

		where:
		clazz | list
		TestClass | [String, Long]
		AnotherTestClass | []
	}

	@Unroll('check constructor applied with #args generated object #values')
	def "check constructor appliance"(def args, def values) {
		when:
		def result = accessor.apply(TestClass, CONSTRUCTOR_NAME, args as Object[])

		then:
		result != null
		values.each { key, value ->
			assert result."$key" == value
		}

		where:
		args | values
		[STRING_VALUE, INTEGER_VALUE]| [(FIRST_PARAMETER):STRING_VALUE, (SECOND_PARAMETER):INTEGER_VALUE]
		[INTEGER_VALUE]| [(FIRST_PARAMETER):null, (SECOND_PARAMETER):INTEGER_VALUE]
		[]| [(FIRST_PARAMETER):null, (SECOND_PARAMETER):null]
	}

	def "check constructor cannot be applied"() {
		when:
		accessor.apply(AnotherTestClass, CONSTRUCTOR_NAME, [STRING_VALUE] as Object[])

		then:
		thrown(InvalidSpecException)
	}
}
