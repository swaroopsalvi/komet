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
package org.semanticweb.elk.reasoner.query;

import java.util.Collection;
import java.util.Collections;

import org.semanticweb.elk.reasoner.entailments.impl.EntailedDisjointClassesEntailsDifferentIndividualsAxiomImpl;
import org.semanticweb.elk.reasoner.entailments.model.DifferentIndividualsAxiomEntailment;
import org.semanticweb.elk.reasoner.entailments.model.EntailmentInference;

/**
 * Query whether an
 * {@link org.semanticweb.elk.owl.interfaces.ElkDifferentIndividualsAxiom
 * ElkDifferentIndividualsAxiom} is entailed.
 * <p>
 * Premises contain one {@link DisjointClassesEntailmentQuery} querying whether
 * nominals of individuals from the queried axiom are disjoint.
 * 
 * @author Peter Skocovsky
 */
public class DifferentIndividualsEntailmentQuery extends
		AbstractEntailmentQueryWithPremises<DifferentIndividualsAxiomEntailment, DisjointClassesEntailmentQuery> {

	/**
	 * @param query
	 *            What entailment is queried.
	 * @param disjointness
	 *            {@link DisjointClassesEntailmentQuery} querying whether
	 *            nominals of individuals from the queried axiom are disjoint.
	 */
	public DifferentIndividualsEntailmentQuery(
			final DifferentIndividualsAxiomEntailment query,
			final DisjointClassesEntailmentQuery disjointness) {
		super(query, Collections.singletonList(disjointness));
	}

	@Override
	public Collection<? extends EntailmentInference> getEntailmentInference() {

		return Collections.singleton(
				new EntailedDisjointClassesEntailsDifferentIndividualsAxiomImpl(
						getQuery(), getPremises().get(0).getQuery()));
	}

}
