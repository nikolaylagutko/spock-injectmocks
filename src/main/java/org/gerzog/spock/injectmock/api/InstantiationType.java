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
package org.gerzog.spock.injectmock.api;

/**
 * Type of instantiation
 *
 * Can be:
 * <ul>
 * <li>MOCK - in this case field will be initialized as Spock's Mock</li>
 * <li>SPY - in this case field will be initialized as Spock's Spy
 * <li>CUSTOM - in this case field will not be initialized automatically but
 * will use it's original value (even it is null)
 * </ul>
 *
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 *
 */
public enum InstantiationType {
	CUSTOM, MOCK, SPY;
}
