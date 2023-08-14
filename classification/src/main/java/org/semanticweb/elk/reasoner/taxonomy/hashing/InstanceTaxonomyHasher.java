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
package org.semanticweb.elk.reasoner.taxonomy.hashing;

import org.semanticweb.elk.owl.interfaces.ElkEntity;
import org.semanticweb.elk.reasoner.taxonomy.model.InstanceTaxonomy;
import org.semanticweb.elk.util.hashing.HashGenerator;

/**
 * A class for computing the structural hash of a
 * Taxonomy. This is mainly useful during testing to check if the reasoner
 * produces the same taxonomy.
 * 
 * 
 * @author Frantisek Simancik
 */
public class InstanceTaxonomyHasher {

	/**
	 * Compute the hash code of a taxonomy.
	 * 
	 * @param taxonomy
	 * @return hash
	 */
	public static int hash(InstanceTaxonomy<? extends ElkEntity, ? extends ElkEntity> taxonomy) {
		int typeHash = HashGenerator.combineMultisetHash(true, taxonomy.getNodes(), TypeNodeHasher.INSTANCE);
		int instanceHash = HashGenerator.combineMultisetHash(true, taxonomy.getInstanceNodes(), InstanceNodeHasher.INSTANCE);		
		return HashGenerator.combineListHash(typeHash, instanceHash);
	}

}
