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

import org.semanticweb.elk.owl.visitors.ElkClassExpressionVisitor;

/**
 * Corresponds to a
 * <a href= "http://www.w3.org/TR/owl2-syntax/#Class_Expressions">Class
 * Expression<a> in the OWL 2 specification.
 * 
 * @author Markus Kroetzsch
 * 
 */
public interface ElkClassExpression extends ElkObject {

	/**
	 * Accept an {@link ElkClassExpressionVisitor}.
	 * 
	 * @param visitor
	 *            the visitor that can work with this object type
	 * @return the output of the visitor
	 */
	public <O> O accept(ElkClassExpressionVisitor<O> visitor);

	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	interface Factory extends ElkClass.Factory,
			ElkDataPropertyListRestrictionQualified.Factory,
			ElkObjectComplementOf.Factory, ElkObjectIntersectionOf.Factory,
			ElkObjectOneOf.Factory, ElkObjectUnionOf.Factory,
			ElkPropertyRestriction.Factory {

		// combined interface

	}

}
