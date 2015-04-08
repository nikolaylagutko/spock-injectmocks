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
package org.gerzog.spock.injectmock

import org.apache.commons.lang3.reflect.FieldUtils
import org.gerzog.spock.injectmock.test.TestUtilsTrait
import org.gerzog.spock.injectmock.test.specs.TestSpecs
import org.spockframework.runtime.InvalidSpecException
import org.spockframework.runtime.model.SpecInfo

import spock.lang.Specification

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
class InjectMocksExtensionSpec extends Specification implements TestUtilsTrait {

	def extension = new InjectMocksExtension()

	def "check an error if spec didn't contain subject for @InjectMock fields"() {
		setup:
		def spec = spec(TestSpecs.NO_SUBJECT)

		when:
		applyExtension(spec)

		then:
		thrown(InvalidSpecException)
	}

	def "check an error when @Subject annotation applied to many fields"() {
		setup:
		def spec = spec(TestSpecs.MULTIPLE_SUBJECT)

		when:
		applyExtension(spec)

		then:
		thrown(InvalidSpecException)
	}

	def "check no method interceptor was registered if not @InjectMocks fields present"() {
		setup:
		def spec = spec(TestSpecs.NO_INJECTABLES)

		when:
		applyExtension(spec)

		then:
		findInterceptor(spec).isEmpty()
	}

	def "check method interceptor was registered"() {
		setup:
		def spec = spec(TestSpecs.CORRECT_SPEC)

		when:
		applyExtension(spec)

		then:
		findInterceptor(spec).size() == 1
	}

	def "check interceptor properties"() {
		setup:
		def spec = spec(TestSpecs.CORRECT_SPEC)

		when:
		applyExtension(spec)

		then:
		validateInterceptor(findInterceptor(spec).first())
	}

	private void validateInterceptor(def interceptor) {
		def subject = FieldUtils.readField(interceptor, 'subjectField', true)
		def injectables = FieldUtils.readField(interceptor, 'injectableFields', true)

		validateInterceptor(subject, injectables)
	}

	private void validateInterceptor(def subject, def injectables) {
		assert subject != null
		assert injectables

		validateSubject(subject)
		validateInjectables(injectables)
	}

	private void validateInjectables(injectables) {
		assert injectables.size() == 1
		assert injectables.first().name == 'autowiredField'
	}

	private void validateSubject(def subject) {
		assert subject.name == 'subject'
	}

	private findInterceptor(SpecInfo spec) {
		spec.setupInterceptors.findAll { it instanceof InjectMocksMethodInterceptor }
	}

	private applyExtension(Class specClazz) {
		extension.visitSpec(spec(specClazz))
	}

	private applyExtension(SpecInfo spec) {
		extension.visitSpec(spec)
	}
}
