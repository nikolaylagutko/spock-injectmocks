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

import org.gerzog.spock.injectmock.test.specs.MultipleSubject
import org.gerzog.spock.injectmock.test.specs.NoSubject
import org.spockframework.runtime.InvalidSpecException
import org.spockframework.runtime.SpecInfoBuilder
import org.spockframework.runtime.model.SpecInfo

import spock.lang.Specification

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
class InjectMocksExtensionSpec extends Specification {

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

	private spec(clazz) {
		new SpecInfoBuilder(clazz).build()
	}

	private applyExtension(Class specClazz) {
		extension.visitSpec(spec(specClazz))
	}

	private applyExtension(SpecInfo spec) {
		extension.visitSpec(spec)
	}
}
