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
package org.gerzog.spock.injectmock;

import java.lang.annotation.Annotation;
import java.util.List;

import org.spockframework.runtime.extension.AbstractMethodInterceptor;
import org.spockframework.runtime.model.FieldInfo;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
public class InjectMocksMethodInterceptor extends AbstractMethodInterceptor {

	private final List<Class<? extends Annotation>> supportedAnnotations;

	private final FieldInfo subjectField;

	private final List<FieldInfo> injectableFields;

	public InjectMocksMethodInterceptor(final List<Class<? extends Annotation>> supportedAnnotations, final FieldInfo subjectField, final List<FieldInfo> injectableFields) {
		this.supportedAnnotations = supportedAnnotations;
		this.subjectField = subjectField;
		this.injectableFields = injectableFields;
	}

}
