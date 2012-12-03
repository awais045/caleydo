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
package org.caleydo.view.tourguide.vendingmachine;


import static org.caleydo.core.view.opengl.layout.ElementLayouts.createColor;
import static org.caleydo.core.view.opengl.layout.ElementLayouts.createXSpacer;
import static org.caleydo.view.tourguide.renderstyle.TourGuideRenderStyle.COLX_SCORE_WIDTH;
import static org.caleydo.view.tourguide.renderstyle.TourGuideRenderStyle.COL_SPACING;
import static org.caleydo.view.tourguide.renderstyle.TourGuideRenderStyle.DATADOMAIN_TYPE_WIDTH;
import static org.caleydo.view.tourguide.renderstyle.TourGuideRenderStyle.GROUP_WIDTH;
import static org.caleydo.view.tourguide.renderstyle.TourGuideRenderStyle.STRATIFACTION_WIDTH;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.view.tourguide.data.ESorting;
import org.caleydo.view.tourguide.data.ScoringElement;
import org.caleydo.view.tourguide.data.score.CollapseScore;
import org.caleydo.view.tourguide.data.score.IScore;

/**
 * factory class for different score column renderers
 * 
 * @author Samuel Gratzl
 * 
 */
public class ScoreColumns {

	public static AScoreColumn create(IScore score, int i, ESorting sorting, AGLView view) {
		switch (score.getScoreType()) {
		case STANDALONE_METRIC:
			return new MetricScoreColumn(score, i, sorting, view);
		case STANDALONE_SCORE:
			return new ScoreScoreColumn(score, i, sorting, view);
		case GROUP_SCORE:
			return new GroupScoreColumn(score, i, sorting, view);
		case STRATIFICATION_SCORE:
			return new StratificationScoreColumn(score, i, sorting, view);
		}
		throw new IllegalStateException("not implemented");
	}


	private static class StratificationScoreColumn extends AScoreColumn {
		protected StratificationScoreColumn(IScore scoreID, int i, ESorting sorting, AGLView view) {
			super(scoreID, i, sorting, view);
			setPixelSizeX(COLX_SCORE_WIDTH + COL_SPACING + DATADOMAIN_TYPE_WIDTH + COL_SPACING + STRATIFACTION_WIDTH);
		}

		@Override
		protected void addScoreSpecific(Row row, ScoringElement elem) {
			IScore underlyingScore = this.score;
			if (underlyingScore instanceof CollapseScore)
				underlyingScore = elem.getSelected((CollapseScore) underlyingScore);
			TablePerspective strat = resolveStratification(underlyingScore);

			addScoreValue(row, elem, strat);
			row.add(colSpacing);
			// create label
			if (strat != null) {
				row.add(createColor(strat.getDataDomain().getColor(), DATADOMAIN_TYPE_WIDTH));
				row.add(colSpacing);
				row.add(createLabel(strat.getRecordPerspective(), STRATIFACTION_WIDTH));
			} else {
				row.add(createXSpacer(DATADOMAIN_TYPE_WIDTH + COL_SPACING + STRATIFACTION_WIDTH));
			}
		}
	}

	private static class GroupScoreColumn extends AScoreColumn {
		protected GroupScoreColumn(IScore scoreID, int i, ESorting sorting, AGLView view) {
			super(scoreID, i, sorting, view);
			setPixelSizeX(COLX_SCORE_WIDTH + COL_SPACING + DATADOMAIN_TYPE_WIDTH + COL_SPACING + GROUP_WIDTH);
		}

		@Override
		protected void addScoreSpecific(Row row, ScoringElement elem) {
			IScore underlyingScore = this.score;
			if (underlyingScore instanceof CollapseScore)
				underlyingScore = elem.getSelected((CollapseScore) underlyingScore);
			TablePerspective strat = resolveStratification(underlyingScore);
			Group group = resolveGroup(underlyingScore);

			addScoreValue(row, elem, strat);

			row.add(colSpacing);
			// create label
			if (strat != null) {
				row.add(createColor(strat.getDataDomain().getColor(), DATADOMAIN_TYPE_WIDTH));
			} else {
				row.add(createXSpacer(DATADOMAIN_TYPE_WIDTH));
			}
			if (group != null) {
				row.add(colSpacing);
				row.add(createLabel(group, GROUP_WIDTH));
			} else {
				row.add(createXSpacer(COL_SPACING + GROUP_WIDTH));
			}
		}
	}

	private static class ScoreScoreColumn extends AScoreColumn {
		protected ScoreScoreColumn(IScore scoreID, int i, ESorting sorting, AGLView view) {
			super(scoreID, i, sorting, view);
			setPixelSizeX(COLX_SCORE_WIDTH);
		}

		@Override
		protected void addScoreSpecific(Row row, ScoringElement elem) {
			addScoreValue(row, elem, null);
		}
	}

	private static class MetricScoreColumn extends AScoreColumn {
		protected MetricScoreColumn(IScore scoreID, int i, ESorting sorting, AGLView view) {
			super(scoreID, i, sorting, view);
			setPixelSizeX(COLX_SCORE_WIDTH);
		}

		@Override
		protected void addScoreSpecific(Row row, ScoringElement elem) {
			addMetricValue(row, elem);
		}
	}
}
