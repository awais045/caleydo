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
package org.caleydo.view.tourguide.api.score;

import org.caleydo.core.util.base.ILabeled;
import org.caleydo.vis.rank.model.mapping.JavaScriptFunctions;

import com.google.common.base.Function;

/**
 * @author Samuel Gratzl
 *
 */
public enum ECombinedOperator implements Function<float[], Float>, ILabeled {
	MAX, MIN, MEAN, MEDIAN, GEOMETRIC_MEAN;

	@Override
	public Float apply(float[] data) {
		return combine(data);
	}

	public String getAbbreviation() {
		switch (this) {
		case GEOMETRIC_MEAN:
			return "GEO";
		case MAX:
			return "MAX";
		case MEAN:
			return "AVG";
		case MEDIAN:
			return "MED";
		case MIN:
			return "MIN";
		}
		throw new IllegalStateException("unknown operator: " + this);
	}

	@Override
	public String getLabel() {
		switch (this) {
		case GEOMETRIC_MEAN:
			return "Geometric Mean";
		case MAX:
			return "Maximum";
		case MEAN:
			return "Average";
		case MEDIAN:
			return "Median";
		case MIN:
			return "Minium";
		}
		throw new IllegalStateException("unknown operator: " + this);
	}

	public float combine(float[] data) {
		switch (this) {
		case MAX:
			return JavaScriptFunctions.max(data);
		case MIN:
			return JavaScriptFunctions.min(data);
		case MEAN:
			return JavaScriptFunctions.mean(data);
		case GEOMETRIC_MEAN:
			return JavaScriptFunctions.geometricMean(data);
		case MEDIAN:
			return JavaScriptFunctions.median(data);
		}
		throw new IllegalStateException("unknown operator: " + this);
	}
}
