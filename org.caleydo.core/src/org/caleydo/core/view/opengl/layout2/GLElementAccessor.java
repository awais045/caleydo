/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.core.view.opengl.layout2;

import org.caleydo.core.view.opengl.layout2.layout.IGLLayoutElement;

/**
 * accessor helper for element container outside of this package, use with caution
 * 
 * @author Samuel Gratzl
 * 
 */
public class GLElementAccessor {
	/**
	 * just the container should access the layout data and here just for layouting purpose
	 *
	 * @param elem
	 * @return
	 */
	public static IGLLayoutElement asLayoutElement(GLElement elem) {
		return elem.layoutElement;
	}

	public static void setParent(GLElement elem, IGLElementParent parent) {
		elem.setParent(parent);
	}

	public static void init(GLElement elem, IGLElementContext context) {
		elem.init(context);
	}

	public static void takeDown(GLElement elem) {
		elem.takeDown();
	}
}
