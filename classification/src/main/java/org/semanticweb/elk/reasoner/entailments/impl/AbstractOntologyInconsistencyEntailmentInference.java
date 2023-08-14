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
package org.semanticweb.elk.reasoner.entailments.impl;

import java.util.Collections;
import java.util.List;

import org.semanticweb.elk.reasoner.entailments.model.Entailment;
import org.semanticweb.elk.reasoner.entailments.model.HasReason;
import org.semanticweb.elk.reasoner.entailments.model.OntologyInconsistency;
import org.semanticweb.elk.reasoner.entailments.model.OntologyInconsistencyEntailmentInference;

abstract class AbstractOntologyInconsistencyEntailmentInference<C>
		extends AbstractEntailmentInference
		implements OntologyInconsistencyEntailmentInference, HasReason<C> {

	private final C reason_;

	public AbstractOntologyInconsistencyEntailmentInference(final C reason) {
		this.reason_ = reason;
	}

	@Override
	public OntologyInconsistency getConclusion() {
		return OntologyInconsistencyImpl.INSTANCE;
	}

	@Override
	public List<? extends Entailment> getPremises() {
		return Collections.emptyList();
	}

	@Override
	public C getReason() {
		return reason_;
	}

}
