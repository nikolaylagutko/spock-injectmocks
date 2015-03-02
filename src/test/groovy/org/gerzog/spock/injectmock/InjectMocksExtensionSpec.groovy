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

import org.gerzog.spock.injectmock.test.TestUtilsTrait
import org.gerzog.spock.injectmock.test.specs.CorrectSpec
import org.gerzog.spock.injectmock.test.specs.MultipleSubject
import org.gerzog.spock.injectmock.test.specs.NoInjectables
import org.gerzog.spock.injectmock.test.specs.NoSubject
import org.spockframework.runtime.InvalidSpecException
import org.spockframework.runtime.model.SpecInfo
import org.springframework.test.util.ReflectionTestUtils

import spock.lang.Specification

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
class InjectMocksExtensionSpec extends Specification implements TestUtilsTrait {

	def extension = new InjectMocksExtension()

	def "check an error if spec didn't contain subject for @InjectMock fields"() {
		when:
		applyExtension(NoSubject)

		then:
		thrown(InvalidSpecException)
	}

	def "check an error when @Subject annotation applied to many fields"() {
		when:
		applyExtension(MultipleSubject)

		then:
		thrown(InvalidSpecException)
	}

	def "check no method interceptor was registered if not @InjectMocks fields present"() {
		setup:
		def spec = spec(NoInjectables)

		when:
		applyExtension(spec)

		then:
		findInterceptor(spec).isEmpty()
	}

	def "check method interceptor was registered"() {
		setup:
		def spec = spec(CorrectSpec)

		when:
		applyExtension(spec)

		then:
		findInterceptor(spec).size() == 1
	}

	def "check interceptor properties"() {
		setup:
		def spec = spec(CorrectSpec)

		when:
		applyExtension(spec)

		then:
		validateInterceptor(findInterceptor(spec).first())
	}

	private void validateInterceptor(def interceptor) {
		def annotations = ReflectionTestUtils.getField(interceptor, 'supportedAnnotations')
		def subject = ReflectionTestUtils.getField(interceptor, 'subjectField')
		def injectables = ReflectionTestUtils.getField(interceptor, 'injectableFields')

		validateInterceptor(annotations, subject, injectables)
	}

	private void validateInterceptor(def annotations, def subject, def injectables) {
		assert annotations != null
		assert subject != null
		assert injectables

		validateAnnotations(annotations)
		validateSubject(subject)
		validateInjectables(injectables)
	}

	private void validateInjectables(injectables){
		assert injectables.size() == 1
		assert injectables.first().name == 'autowiredField'
	}

	private void validateSubject(def subject) {
		assert subject.name == 'subject'
	}

	private void validateAnnotations(def annotations) {
		assert annotations == supportedAnnotations()
	}

	private findInterceptor(spec) {
		spec.setupMethod.interceptors.findAll{it instanceof InjectMocksMethodInterceptor}
	}

	private applyExtension(Class specClazz) {
		extension.visitSpec(spec(specClazz))
	}

	private applyExtension(SpecInfo spec) {
		extension.visitSpec(spec)
	}
}
