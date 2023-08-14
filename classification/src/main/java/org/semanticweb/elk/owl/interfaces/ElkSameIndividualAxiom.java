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
/**
 * @author Markus Kroetzsch, Aug 8, 2011
 */
package org.semanticweb.elk.owl.interfaces;

import java.util.List;

import org.semanticweb.elk.owl.visitors.ElkSameIndividualAxiomVisitor;

/**
 * Corresponds to an
 * <a href= "http://www.w3.org/TR/owl2-syntax/#Individual_Equality">individual
 * equality axiom<a> in the OWL 2 specification.
 * 
 * @author Markus Kroetzsch
 */
public interface ElkSameIndividualAxiom extends ElkAssertionAxiom {

	/**
	 * Get the list of individuals that this axiom refers to. The order of
	 * individuals does not affect the semantics but it is relevant to the
	 * syntax of OWL.
	 * 
	 * @return list of individuals
	 */
	public List<? extends ElkIndividual> getIndividuals();

	/**
	 * Accept an {@link ElkSameIndividualAxiomVisitor}.
	 * 
	 * @param visitor
	 *            the visitor that can work with this axiom type
	 * @return the output of the visitor
	 */
	public <O> O accept(ElkSameIndividualAxiomVisitor<O> visitor);

	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	interface Factory {

		/**
		 * Create an {@link ElkSameIndividualAxiom}.
		 * 
		 * @param first
		 *            the first equivalent {@link ElkIndividual} for which the
		 *            axiom should be created
		 * @param second
		 *            the second equivalent {@link ElkIndividual} for which the
		 *            axiom should be created
		 * @param other
		 *            other equivalent {@link ElkIndividual} for which the axiom
		 *            should be created
		 * @return an {@link ElkSameIndividualAxiom} corresponding to the input
		 */
		public ElkSameIndividualAxiom getSameIndividualAxiom(
				ElkIndividual first, ElkIndividual second,
				ElkIndividual... other);

		/**
		 * Create an {@link ElkSameIndividualAxiom}.
		 * 
		 * @param individuals
		 *            the equivalent {@link ElkIndividual} for which the axiom
		 *            should be created
		 * @return an {@link ElkSameIndividualAxiom} corresponding to the input
		 */
		public ElkSameIndividualAxiom getSameIndividualAxiom(
				List<? extends ElkIndividual> individuals);
	}

}
