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
package org.caleydo.vis.rank.config;

import org.caleydo.core.view.opengl.picking.AdvancedPick;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.vis.rank.model.ACompositeRankColumnModel;
import org.caleydo.vis.rank.model.ARankColumnModel;
import org.caleydo.vis.rank.model.IRankColumnParent;
import org.caleydo.vis.rank.model.MaxCompositeRankColumnModel;
import org.caleydo.vis.rank.model.StackedRankColumnModel;
import org.caleydo.vis.rank.model.mixin.IFloatRankableColumnMixin;

/**
 * basic implementation of {@link IRankTableConfig}
 *
 * with all features enabled and combine by default to a {@link MaxCompositeRankColumnModel} and with ALT down to a
 * {@link StackedRankColumnModel}
 *
 * @author Samuel Gratzl
 *
 */
public class RankTableConfigBase implements IRankTableConfig {
	private static final int MAX_MODE = 0;
	private static final int SUM_MODE = 1;

	@Override
	public boolean isMoveAble(ARankColumnModel model, boolean clone) {
		return true;
	}

	@Override
	public int getCombineMode(ARankColumnModel model, Pick pick) {
		int default_ = model instanceof StackedRankColumnModel ? SUM_MODE : MAX_MODE;
		if (!(pick instanceof AdvancedPick))
			return default_;
		AdvancedPick apick = (AdvancedPick) pick;
		if (apick.isAltDown())
			return 1 - default_; // opposite one
		return default_;
	}

	@Override
	public ACompositeRankColumnModel createNewCombined(int combineMode) {
		switch (combineMode) {
		case SUM_MODE:
			return new StackedRankColumnModel();
		default:
			return new MaxCompositeRankColumnModel();
		}
	}

	@Override
	public boolean canBeReusedForCombining(ACompositeRankColumnModel t, int combineMode) {
		switch (combineMode) {
		case SUM_MODE:
			return t instanceof StackedRankColumnModel;
		default:
			return t instanceof MaxCompositeRankColumnModel;
		}
	}

	@Override
	public boolean isCombineAble(ARankColumnModel model, ARankColumnModel with, boolean clone, int combineMode) {
		if (model instanceof ACompositeRankColumnModel && ((ACompositeRankColumnModel)model).canAdd(with))
			return true;
		if (!(model instanceof IFloatRankableColumnMixin) || !(with instanceof IFloatRankableColumnMixin))
			return false;
		IRankColumnParent parent = model.getParent();
		switch (combineMode) {
		case SUM_MODE:
			return parent.getClass() != StackedRankColumnModel.class;
		default:
			return parent.getClass() != MaxCompositeRankColumnModel.class;
		}
	}

	@Override
	public String getCombineStringHint(ARankColumnModel model, ARankColumnModel with, int combineMode) {
		switch (combineMode) {
		case SUM_MODE:
			return "SUM";
		default:
			return "MAX";
		}
	}

	@Override
	public boolean isDefaultCollapseAble() {
		return true;
	}

	@Override
	public boolean isDefaultHideAble() {
		return true;
	}

	@Override
	public boolean isDestroyOnHide() {
		return false;
	}

}
