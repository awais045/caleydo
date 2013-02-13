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
package org.caleydo.view.tourguide.v2.r.model;

import java.util.Iterator;

import org.caleydo.core.data.collection.Histogram;
import org.caleydo.core.data.collection.column.container.FloatContainer;

/**
 * @author Samuel Gratzl
 *
 */
public class DataUtils {
	public static Histogram getHist(int bins, Iterator<IValue> it) {
		Histogram hist = new Histogram(bins);
		while (it.hasNext()) {
			float value = it.next().asFloat();
			if (Float.isNaN(value))
				hist.addNAN(0);
			else
				hist.add(Math.round(value * (bins - 1)), 0);
		}
		return hist;
	}

	public static Histogram getHist(int bins, FloatContainer c) {
		Histogram hist = new Histogram(bins);
		for (int i = 0; i < c.size(); ++i) {
			float value = c.getPrimitive(i);
			if (Float.isNaN(value))
				hist.addNAN(0);
			else
				hist.add(Math.round(value * (bins - 1)), 0);
		}
		return hist;
	}

	public static int getHistBin(int bins, float normalized) {
		float value = normalized;
		return Math.round(value * (bins - 1));
	}
}
