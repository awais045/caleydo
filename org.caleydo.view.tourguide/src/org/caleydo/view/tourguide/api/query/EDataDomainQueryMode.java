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
package org.caleydo.view.tourguide.api.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.util.base.DefaultLabelProvider;
import org.caleydo.datadomain.pathway.PathwayDataDomain;

/**
 * the mode in which the data domain query is see {@link DataDomainQuery}
 *
 * @author Samuel Gratzl
 *
 */
public enum EDataDomainQueryMode {
	STRATIFICATIONS, PATHWAYS, OTHER;

	/**
	 * @return
	 */
	public String getLabel() {
		switch (this) {
		case STRATIFICATIONS:
			return "Stratifications";
		case PATHWAYS:
			return "Pathways";
		case OTHER:
			return "Other";
		}
		throw new IllegalArgumentException("unknown me");
	}

	public boolean isDependent() {
		return this != STRATIFICATIONS;
	}

	public boolean isCompatible(IDataDomain dataDomain) {
		switch(this) {
		case PATHWAYS:
			return dataDomain instanceof PathwayDataDomain;
		case STRATIFICATIONS:
			return (dataDomain instanceof ATableBasedDataDomain && ((ATableBasedDataDomain) dataDomain).getTable()
					.isDataHomogeneous());
		case OTHER:
			return (dataDomain instanceof ATableBasedDataDomain && !((ATableBasedDataDomain) dataDomain).getTable()
					.isDataHomogeneous());
		}
		throw new IllegalArgumentException("unknown me");
	}

	public Collection<? extends IDataDomain> getAllDataDomains() {
		switch(this) {
		case STRATIFICATIONS:
			List<ATableBasedDataDomain> dataDomains = new ArrayList<>(DataDomainManager.get().getDataDomainsByType(ATableBasedDataDomain.class));

			for (Iterator<ATableBasedDataDomain> it = dataDomains.iterator(); it.hasNext();)
				if (!it.next().getTable().isDataHomogeneous()) // remove inhomogenous
					it.remove();

			// Sort data domains alphabetically
			Collections.sort(dataDomains, DefaultLabelProvider.BY_LABEL);
			return dataDomains;
		case PATHWAYS:
			return DataDomainManager.get().getDataDomainsByType(PathwayDataDomain.class);
		case OTHER:
			List<ATableBasedDataDomain> catDataDomains = new ArrayList<>(DataDomainManager.get().getDataDomainsByType(
					ATableBasedDataDomain.class));

			for (Iterator<ATableBasedDataDomain> it = catDataDomains.iterator(); it.hasNext();)
				if (it.next().getTable().isDataHomogeneous()) // remove inhomogenous
					it.remove();

			// Sort data domains alphabetically
			Collections.sort(catDataDomains, DefaultLabelProvider.BY_LABEL);
			return catDataDomains;
		}
		throw new IllegalArgumentException("unknown me");
	}
}
