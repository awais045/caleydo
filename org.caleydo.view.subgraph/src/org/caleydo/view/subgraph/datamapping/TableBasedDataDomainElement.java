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
package org.caleydo.view.subgraph.datamapping;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.basic.GLButton;

/**
 * Element representing a table based data domain element in the data mapping view.
 *
 * @author Marc streit
 *
 */
public class TableBasedDataDomainElement extends GLButton implements GLButton.ISelectionCallback {

	protected final ATableBasedDataDomain dd;

	protected final String pathEventSpace;

	public TableBasedDataDomainElement(ATableBasedDataDomain dd, String pathEventSpace) {

		setLayoutData(dd);
		this.dd = dd;
		this.pathEventSpace = pathEventSpace;

		setMode(EButtonMode.CHECKBOX);
		setSize(150, 18);
		setCallback(this);
	}

	@Override
	public void onSelectionChanged(GLButton button, boolean selected) {

		if (selected) {

			// AddTablePerspectivesEvent event = new AddTablePerspectivesEvent(tablePerspective);
			// event.setReceiver(view);
			// event.setSender(this);
			// event.setEventSpace(pathEventSpace);
			// EventPublisher.publishEvent(event);
		}
		// else

	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		super.renderImpl(g, w, h);
		if (isSelected()) {
			g.fillImage("resources/icons/dataassignment/accept.png", 3, 2, 14, 14);
		} else {
			g.fillImage("resources/icons/dataassignment/accept_disabled.png", 3, 2, 14, 14);
		}

		g.color(dd.getColor()).fillRect(18, 2, 14, 14);
		g.drawText(dd, 18 + 18, 1, w - 18, 14);
	}
}