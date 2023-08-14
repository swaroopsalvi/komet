/*
 * Copyright © 2015 Integrated Knowledge Management (support@ikm.dev)
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
package org.semanticweb.elk.reasoner.indexing.model;

import org.semanticweb.elk.owl.interfaces.ElkIndividual;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;

/**
 * An {@link IndexedClassExpression} that corresponds to an
 * {@link ElkIndividual}, which can be obtained by {@link #getElkEntity()}.
 * 
 * Notation:
 * 
 * <pre>
 * A
 * </pre>
 * 
 * @author Frantisek Simancik
 * @author "Yevgeny Kazakov"
 * 
 */
public interface IndexedIndividual extends IndexedClassEntity {

	/**
	 * @return The {@link ElkNamedIndividual} represented by this
	 *         {@link IndexedIndividual}.
	 */
	@Override
	ElkNamedIndividual getElkEntity();

	/**
	 * The visitor pattern for instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 * @param <O>
	 *            the type of the output
	 */
	interface Visitor<O> {

		O visit(IndexedIndividual element);

	}

}
