
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

import spock.lang.Specification

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
class InjectorSpec extends Specification {

	static class NoDefaultConstructorBean {
		NoDefaultConstructorBean(Number param) {
		}
	}

	static class TestBean {
		private String property1

		Integer property2

		Long property3

		Float property4

		TestBean() {
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

	def "check method injection working"(){
		setup:
		def injectables = injectables(['property3': 3l])
		initialize(TestBean, instance)

		when:
		injector.inject(spec, injectables)

		then:
		1 * instance.setProperty3(3l)
	}

	def "check field injection working"() {
		setup:
		def injectables = injectables(['property1': 'value'])
		initialize(TestBean, instance)

		when:
		injector.inject(spec, injectables)

		then:
		instance.property1 == 'value'
	}

	def "check constructor injection"() {
		setup:
		def injectables = injectables(['property1': 'value', 'property2': 2])
		initialize(TestBean)

		when:
		def result = injector.inject(spec, injectables)

		then:
		result != null
		result.property1 == 'value'
		result.property2 == 2
	}

	def "check complext injection"() {
		setup:
		def injectables = injectables(['property1': 'value', 'property3': 3l, 'property4': 4f])
		initialize(TestBean)

		when:
		def result = injector.inject(spec, injectables)

		then:
		result != null
		result.property1 == 'value'
		result.property3 == 3l
		result.property4 == 4f
	}

	def "check no constructor found"() {
		setup:
		def injectables = injectables(['property5': 5])
		initialize(NoDefaultConstructorBean)

		when:
		def result = injector.inject(spec, injectables)

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
		injector = new Injector(clazz, instance)
	}
}
