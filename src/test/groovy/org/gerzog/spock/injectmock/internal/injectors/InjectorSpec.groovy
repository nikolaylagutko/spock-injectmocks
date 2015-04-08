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
package org.gerzog.spock.injectmock.internal.injectors

import org.gerzog.spock.injectmock.injections.IInjectable
import org.spockframework.runtime.InvalidSpecException
import org.spockframework.runtime.model.FieldInfo

import spock.lang.Specification

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
class InjectorSpec extends Specification {

	static final long LONG_VALUE = 3L

	static final String STRING_VALUE = 'value'

	static final String THIRD_PROPERTY = 'property3'

	static final String FIRST_PROPERTY = 'property1'

	static final int INTEGER_VALUE = 2

	static final String SECOND_PROPERTY = 'property2'

	static final String FOURTH_PROPERTY = 'property4'

	static final float FLOAT_VALUE = 4f

	static final String UNKNOWN_PROPERTY = 'property5'

	static final int UNKNOWN_VALUE = 5

	static class NoDefaultConstructorBean {
		NoDefaultConstructorBean(Number param) {
		}
	}

	static class TestBean {
		private final String property1

		Integer property2

		Long property3

		Float property4

		TestBean() {
			property1 = null
			property2 = null
			property3 = null
			property4 = null
		}

		TestBean(String property1) {
			this.property1 = property1
		}

		TestBean(String property1, Integer property2) {
			this(property1)
			this.property2 = property2
		}

		def setProperty3(Long value) {
			this.property3 = value
		}
	}

	def injector

	def instance = Spy(TestBean)

	def spec = Mock(Object)

	def "check method injection working"() {
		setup:
		def injectables = injectables([(THIRD_PROPERTY):LONG_VALUE])
		initialize(TestBean, instance)

		when:
		injector.inject(spec, injectables)

		then:
		1 * instance.setProperty3(LONG_VALUE)
	}

	def "check field injection working"() {
		setup:
		def injectables = injectables([(FIRST_PROPERTY):STRING_VALUE])
		initialize(TestBean, instance)

		when:
		injector.inject(spec, injectables)

		then:
		instance.property1 == STRING_VALUE
	}

	def "check constructor injection"() {
		setup:
		def injectables = injectables([(FIRST_PROPERTY):STRING_VALUE, (SECOND_PROPERTY):INTEGER_VALUE])
		initialize(TestBean)

		when:
		def result = injector.inject(spec, injectables)

		then:
		result != null
		result.property1 == STRING_VALUE
		result.property2 == INTEGER_VALUE
	}

	def "check complext injection"() {
		setup:
		def injectables = injectables([(FIRST_PROPERTY):STRING_VALUE, (THIRD_PROPERTY):LONG_VALUE, (FOURTH_PROPERTY):FLOAT_VALUE])
		initialize(TestBean)

		when:
		def result = injector.inject(spec, injectables)

		then:
		result != null
		result.property1 == STRING_VALUE
		result.property3 == LONG_VALUE
		result.property4 == FLOAT_VALUE
	}

	def "check no constructor found"() {
		setup:
		def injectables = injectables([(UNKNOWN_PROPERTY):UNKNOWN_VALUE])
		initialize(NoDefaultConstructorBean)

		when:
		injector.inject(spec, injectables)

		then:
		thrown(InvalidSpecException)
	}

	def injectables(def valueMap) {
		valueMap.collect { key, value -> injectable(key, value) }
	}

	def injectable(def name, def value) {
		def injectable = Mock(IInjectable)

		injectable.type >> value.getClass()
		injectable.name >> name
		injectable.instantiate(_) >> value

		injectable
	}

	def initialize(def clazz, def instance = null) {
		def field = Mock(FieldInfo)
		field.type >> clazz
		field.readValue(_) >> instance
		injector = new Injector(field)
	}
}
