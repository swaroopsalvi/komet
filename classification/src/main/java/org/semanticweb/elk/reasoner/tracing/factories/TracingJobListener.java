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
package org.semanticweb.elk.reasoner.tracing.factories;

import org.semanticweb.elk.reasoner.indexing.model.IndexedContextRoot;
import org.semanticweb.elk.reasoner.saturation.inferences.ClassInference;
import org.semanticweb.elk.reasoner.tracing.ModifiableTracingProof;

public interface TracingJobListener {

	/**
	 * called when tracing of the {@link IndexedContextRoot} finished producing
	 * the inferences in the {@link ModifiableTracingProof}.
	 * 
	 * @param context
	 * @param proof
	 */
	public void notifyJobFinished(final IndexedContextRoot context,
			ModifiableTracingProof<ClassInference> proof);

}
