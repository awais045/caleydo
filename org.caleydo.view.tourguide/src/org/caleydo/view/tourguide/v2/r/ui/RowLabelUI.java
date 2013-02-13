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
package org.caleydo.view.tourguide.v2.r.ui;

import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.view.tourguide.v2.r.model.ScoreTable;

/**
 * @author Samuel Gratzl
 *
 */
public class RowLabelUI extends GLElement {
	private final ScoreTable table;
	private int row;

	public RowLabelUI(ScoreTable table, int row) {
		this.table = table;
		this.row = row;
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		super.renderImpl(g, w, h);
		if (h > ScoreItemUI.RENDER_TEXT)
			g.drawText(table.getLabel(row), 0, 0, w, h);
		else if (h >= 3)
			g.color(0.5f, 0.5f, 0.5f).fillRect(1, 1, w - 2, h - 2);
	}
}
