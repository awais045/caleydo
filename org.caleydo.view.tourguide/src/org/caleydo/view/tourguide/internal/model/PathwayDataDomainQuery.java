/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.internal.model;

import java.util.List;

import org.caleydo.datadomain.pathway.PathwayDataDomain;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.manager.EPathwayDatabaseType;
import org.caleydo.datadomain.pathway.manager.PathwayManager;
import org.caleydo.vis.rank.model.RankTableModel;

import com.google.common.collect.Lists;

/**
 * @author Samuel Gratzl
 *
 */
public class PathwayDataDomainQuery extends ADataDomainQuery {
	private final EPathwayDatabaseType type;

	public PathwayDataDomainQuery(PathwayDataDomain dataDomain, EPathwayDatabaseType type) {
		super(dataDomain);
		this.type = type;
	}

	/**
	 * @return the type, see {@link #type}
	 */
	public EPathwayDatabaseType getType() {
		return type;
	}
	@Override
	public PathwayDataDomain getDataDomain() {
		return (PathwayDataDomain) super.getDataDomain();
	}

	@Override
	public boolean apply(AScoreRow row) {
		return true;
	}

	@Override
	protected List<AScoreRow> getAll() {
		List<AScoreRow> r = Lists.newArrayList();
		for (PathwayGraph per : PathwayManager.get().getAllItems()) {
			if (per.getType() != type)
				continue;
			r.add(new PathwayPerspectiveRow(per));
		}
		return r;
	}

	@Override
	public List<AScoreRow> onDataDomainUpdated() {
		// up to now not way to create new pathways
		return null;
	}

	@Override
	public boolean hasFilter() {
		return false;
	}

	@Override
	public boolean isFilteringPossible() {
		return false;
	}

	@Override
	public void createSpecificColumns(RankTableModel table) {

	}

	@Override
	public void removeSpecificColumns(RankTableModel table) {

	}
}
