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

import org.gerzog.spock.injectmock.api.InjectMock
import org.gerzog.spock.injectmock.test.TestUtilsTrait
import org.gerzog.spock.injectmock.test.data.Bean
import org.gerzog.spock.injectmock.test.specs.CorrectSpec
import org.gerzog.spock.injectmock.test.specs.CustomInjection
import org.gerzog.spock.injectmock.test.specs.FieldInjectionSpec
import org.gerzog.spock.injectmock.test.specs.MethodInjectionSpec
import org.gerzog.spock.injectmock.test.specs.MockInjection
import org.gerzog.spock.injectmock.test.specs.SpyInjection
import org.gerzog.spock.injectmock.test.specs.SubjectNotInitialized
import org.gerzog.spock.injectmock.test.specs.UnmappedInjectables
import org.spockframework.mock.ISpockMockObject
import org.spockframework.runtime.InvalidSpecException
import org.spockframework.runtime.extension.IMethodInvocation
import org.spockframework.util.ReflectionUtil

import spock.lang.Specification
import spock.lang.Subject

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
class InjectMocksMethodInterceptorSpec extends Specification implements TestUtilsTrait {

	@Subject
	def interceptor

	def invocation = Mock(IMethodInvocation)

	def target

	def spec

	def "check an error occured when @Subject field not initialized"() {
		setup:
		initialize(SubjectNotInitialized)

		when:
		applyInterceptor()

		then:
		thrown(InvalidSpecException)
	}

	def "check original method was called"() {
		setup:
		initialize(CorrectSpec)

		when:
		applyInterceptor()

		then:
		1 * invocation.proceed()
	}

	def "check a mock was created for injectable field"() {
		setup:
		initialize(MockInjection)

		when:
		applyInterceptor()

		then:
		def result = fieldValue('autowiredField')
		result != null
		result instanceof ISpockMockObject
		result instanceof Bean
	}

	def "check a spy was created for injectable field"() {
		setup:
		initialize(SpyInjection)

		when:
		applyInterceptor()

		then:
		def result = fieldValue('autowiredField')
		result != null
		result instanceof ISpockMockObject
		result instanceof Bean
	}

	def "check field value is raw on CUSTOM instantiation type"() {
		setup:
		initialize(CustomInjection)

		when:
		applyInterceptor()

		then:
		def result = fieldValue('autowiredField')
		result != null
		!(result instanceof ISpockMockObject)
		result instanceof Bean
	}

	def "check unmapped injectabled found"() {
		setup:
		initialize(UnmappedInjectables)

		when:
		applyInterceptor()

		then:
		thrown(InvalidSpecException)
	}

	def "check fields after field injection"() {
		setup:
		initialize(FieldInjectionSpec)

		when:
		applyInterceptor()

		then:
		target.subject.autowiredField != null
		target.subject.injectField != null
		target.subject.resourceField != null
	}

	def "check fields after method injection"() {
		setup:
		initialize(MethodInjectionSpec)

		when:
		applyInterceptor()

		then:
		target.subject.autowired != null
		target.subject.inject != null
		target.subject.resource != null
		target.subject.required != null
	}

	private initialize(clazz) {
		spec = spec(clazz)

		initializeInterceptor()

		initializeInvocationTarget()
	}

	private initializeInvocationTarget() {
		target = spec.reflection.newInstance()

		if (spec.initializerMethod?.reflection != null) {
			ReflectionUtil.invokeMethod(target, spec.initializerMethod.reflection)
		}

		invocation.target >> target
	}

	private applyInterceptor() {
		interceptor.intercept(invocation)
	}

	private initializeInterceptor() {
		interceptor = new InjectMocksMethodInterceptor(supportedAnnotations(), fields(Subject).first(), fields(InjectMock))
	}

	private fieldValue(name) {
		spec.allFields.findResult { it.name == name ? it.readValue(target) : null}
	}

	private fields(annotation) {
		spec.allFields.findAll { it.isAnnotationPresent(annotation) }
	}
}
