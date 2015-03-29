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
package org.gerzog.spock.injectmock.internal.injectors;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.gerzog.spock.injectmock.injections.IAccessor;
import org.gerzog.spock.injectmock.injections.IInjectable;
import org.gerzog.spock.injectmock.injections.IInjector;
import org.gerzog.spock.injectmock.internal.accessors.ConstructorAccessor;
import org.gerzog.spock.injectmock.internal.accessors.FieldAccessor;
import org.gerzog.spock.injectmock.internal.accessors.MethodAccessor;
import org.gerzog.spock.injectmock.utils.InjectMocksUtils;
import org.paukov.combinatorics.Factory;
import org.paukov.combinatorics.Generator;
import org.paukov.combinatorics.ICombinatoricsVector;
import org.spockframework.runtime.InvalidSpecException;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
public class Injector implements IInjector {

	private static final String DEFAULT_CONSTRUCTOR_METHOD = "init";

	private static final IAccessor[] ACCESSORS = { new MethodAccessor(), new FieldAccessor() };

	private static final IAccessor CONSTRUCTOR_ACCESSOR = new ConstructorAccessor();

	private final Class<?> clazz;

	private final Object instance;

	public Injector(final Class<?> clazz, final Object instance) {
		this.clazz = clazz;
		this.instance = instance;
	}

	@Override
	public Object inject(final Object specInstance, final List<IInjectable> injectables) {
		final Object result = createInstance(specInstance, injectables);

		// TODO: need to be refactored for Stream API
		final Iterator<IInjectable> injectableIterator = injectables.iterator();
		while (injectableIterator.hasNext()) {
			final IInjectable currentInjectable = injectableIterator.next();

			final Optional<IAccessor> actualAccessor = Stream.of(ACCESSORS).filter(accessor -> accessor.exists(clazz, currentInjectable.getName(), currentInjectable.getType())).findFirst();

			actualAccessor.ifPresent(accessor -> {
				accessor.apply(result, currentInjectable.getName(), currentInjectable.instantiate(specInstance));
				injectableIterator.remove();
			});
		}

		return result;
	}

	private Object createInstance(final Object specInstance, final List<IInjectable> injectables) {
		Object result = instance;

		if (result == null) {
			result = defineConstructorArguments(injectables).map(args -> CONSTRUCTOR_ACCESSOR.apply(clazz, DEFAULT_CONSTRUCTOR_METHOD, InjectMocksUtils.toObjectArray(args, injector -> injector.instantiate(specInstance)))).orElseThrow(
					() -> new InvalidSpecException("There is no constructor for <" + clazz + "> instance with args <" + injectables + ">"));
		}
		return result;
	}

	private Optional<List<IInjectable>> defineConstructorArguments(final List<IInjectable> injectables) {
		final ICombinatoricsVector<IInjectable> originalVector = Factory.createVector(injectables);

		final Generator<IInjectable> combinationGenerator = Factory.createSubSetGenerator(originalVector);

		return combinationGenerator.generateAllObjects().stream().sorted((vector, anotherVector) -> anotherVector.getSize() - vector.getSize()).map(vector -> vector.getVector()).filter(this::isConstructorExists).findFirst();
	}

	private boolean isConstructorExists(final List<IInjectable> injectables) {
		final Class<?>[] parameters = InjectMocksUtils.toClassArray(injectables, IInjectable::getType);

		return CONSTRUCTOR_ACCESSOR.exists(clazz, DEFAULT_CONSTRUCTOR_METHOD, parameters);
	}
}
