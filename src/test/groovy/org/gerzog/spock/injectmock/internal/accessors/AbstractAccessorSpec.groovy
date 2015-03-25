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

import spock.lang.Specification
import spock.lang.Unroll

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
abstract class AbstractAccessorSpec extends Specification {

	final static ACCESS_NAMES = [
		'privateValue',
		'protectedValue',
		'publicValue'
	]

	final static VALUE = 'value'

	private class TestClass {

		private CharSequence privateValue

		protected CharSequence protectedValue

		/*public*/ CharSequence publicValue

		private setPrivateValue(CharSequence value) {
			privateValue = value
		}

		protected setProtectedValue(CharSequence value) {
			protectedValue = value
		}

		/*public*/void setPublicValue(CharSequence value) {
			publicValue = value
		}
	}

	def accessor = initializeAccessor()

	def instance = new TestClass()

	abstract initializeAccessor()

	@Unroll('check existing #name is found')
	def "check field exists"(def name) {
		expect:
		initializeAccessor.exists(TestClass, CharSequence, name)

		where:
		name << ACCESS_NAMES
	}

	def "check not existing field"() {
		expect:
		!initializeAccessor.exists(TestClass, CharSequence, 'unknown')
	}

	def "check type is not compatible"() {
		expect:
		!initializeAccessor.exists(TestClass, Number, ACCESS_NAMES[0])
	}

	def "check supertype is OK"() {
		expect:
		initializeAccessor.exists(TestClass, String, ACCESS_NAMES[0])
	}

	@Unroll('check writing to #name')
	def "check set field"(def name) {
		when:
		initializeAccessor.set(instance, name, VALUE)

		then:
		validateWriting(instance, name, VALUE)

		where:
		name << ACCESS_NAMES
	}

	void validateWriting(def instance, def name, def result) {
		assert result == instance."$name"
	}
}
