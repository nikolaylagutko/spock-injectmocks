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

import org.gerzog.spock.injectmock.api.Injectable
import org.spockframework.runtime.model.FieldInfo

import spock.lang.Specification
import spock.lang.Unroll

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
class InjectablesSpec extends Specification {

	def field = Mock(FieldInfo)

	@Unroll('check #expectedClass instance was creted by #annotations annotations')
	def "check injectable instance"(def annotations, def expectedClass) {
		setup:
		annotations.forEach {
			field.isAnnotationPresent(it) >> true
		}

		when:
		def result = Injectables.forField(field).orElse(null)

		then:
		expectedClass == null ? result == null : expectedClass.isInstance(result)

		where:
		annotations | expectedClass
		[Injectable] | CustomInjectable
		[
			Injectable,
			org.gerzog.spock.injectmock.mocking.api.Mock
		] | MockInjectable
		[
			Injectable,
			org.gerzog.spock.injectmock.mocking.api.Spy
		] | SpyInjectable
		[
			Injectable,
			org.gerzog.spock.injectmock.mocking.api.Stub
		] | StubInjectable
		[] | null
		[
			org.gerzog.spock.injectmock.mocking.api.Mock
		] | null
		[
			org.gerzog.spock.injectmock.mocking.api.Spy
		] | null
		[
			org.gerzog.spock.injectmock.mocking.api.Stub
		] | null
	}
}
