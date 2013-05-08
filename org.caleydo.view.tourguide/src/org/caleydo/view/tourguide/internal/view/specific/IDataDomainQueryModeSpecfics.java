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
package org.caleydo.view.tourguide.internal.view.specific;

import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.view.tourguide.internal.model.ADataDomainQuery;
import org.caleydo.view.tourguide.internal.view.col.IAddToStratomex;
import org.caleydo.vis.rank.model.RankTableModel;

/**
 * @author Samuel Gratzl
 *
 */
public interface IDataDomainQueryModeSpecfics {

	/**
	 * @return
	 */
	Iterable<? extends ADataDomainQuery> createDataDomainQueries();

	/**
	 * @param table
	 * @param glTourGuideView
	 */
	void addDefaultColumns(RankTableModel table, IAddToStratomex add2Stratomex);

	/**
	 * @param dd
	 * @return
	 */
	Iterable<? extends ADataDomainQuery> createDataDomainQuery(IDataDomain dd);

	/**
	 * @return
	 */
	int getNumCategories();

	/**
	 * @param dataDomain
	 * @return
	 */
	int getCategory(IDataDomain dataDomain);

}

