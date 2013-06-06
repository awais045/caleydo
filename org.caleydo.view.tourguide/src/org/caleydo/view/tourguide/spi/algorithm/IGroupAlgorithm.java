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
package org.caleydo.view.tourguide.spi.algorithm;

import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * a group algorithm is an abstract definition of an algorithm that computes of two Sets a score
 * 
 * @author Samuel Gratzl
 * 
 */
public interface IGroupAlgorithm extends IAlgorithm {
	/**
	 * computes the score between the two sets identified by a set of integer noted in the same id type
	 *
	 * @param a
	 * @param b
	 * @return
	 */
	float compute(Set<Integer> a, Set<Integer> b, IProgressMonitor monitor);
}
