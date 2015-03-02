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
import org.gerzog.spock.injectmock.test.specs.SubjectNotInitialized
import org.spockframework.runtime.InvalidSpecException
import org.spockframework.runtime.extension.IMethodInvocation
import org.spockframework.runtime.model.SpecInfo

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

	def "check an error occured when @Subject field not initialized"() {
		setup:
		initializeInterceptor(SubjectNotInitialized)
		initializeInvocationTarget(SubjectNotInitialized)

		when:
		applyInterceptor(SubjectNotInitialized)

		then:
		thrown(InvalidSpecException)
	}

	private initializeInvocationTarget(clazz) {
		target = clazz.newInstance()

		invocation.target >> target

		target
	}

	private applyInterceptor(Class clazz) {
		applyInterceptor(spec(clazz))
	}

	private applyInterceptor(SpecInfo spec) {
		interceptor.interceptSetupMethod(invocation)
	}

	private initializeInterceptor(Class clazz) {
		initializeInterceptor(spec(clazz))
	}

	private initializeInterceptor(SpecInfo spec) {
		interceptor = new InjectMocksMethodInterceptor(supportedAnnotations(), fields(spec, Subject).first(), fields(spec, InjectMock))
	}

	private fields(spec, annotation) {
		spec.allFields.findAll { it.isAnnotationPresent(annotation) }
	}
}
