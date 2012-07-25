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
package org.caleydo.view.heatmap.heatmap.template;

import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.view.heatmap.heatmap.GLHeatMap;

/**
 * Render LayoutTemplate for the detail view of the Matchmaker.
 * 
 * @author Alexander Lex
 * 
 */
public class MatchmakerDetailTemplate extends AHeatMapLayoutConfiguration {

	private boolean isLeft = true;

	public MatchmakerDetailTemplate(GLHeatMap heatMap, boolean isLeft) {
		super(heatMap);
		this.isLeft = isLeft;

	}

	@Override
	public void setStaticLayouts() {
		Column mainColumn = new Column();
		baseElementLayout = mainColumn;
		mainColumn.setRatioSizeX(1);
		mainColumn.setRatioSizeY(1);

		Row hmRow = new Row();
		// hmRow.grabY = true;
		// heat map
		heatMapLayout = new ElementLayout();
		heatMapLayout.setGrabX(true);
		heatMapLayout.setRatioSizeY(1f);
		heatMapLayout.setRenderer(heatMapRenderer);
		heatMapLayout.addForeGroundRenderer(recordSelectionRenderer);
		heatMapLayout.addForeGroundRenderer(dimensionSelectionRenderer);

		boolean renderCaptions = false;
		if (heatMap.isShowCaptions() || heatMap.isActive())
			renderCaptions = true;
		ElementLayout caption = null;
		ElementLayout spacing = null;
		if (renderCaptions) {
			// content cage

			spacing = new ElementLayout();
			spacing.setAbsoluteSizeX(0.01f);

			// content captions
			caption = new ElementLayout();
			caption.setRatioSizeX(0.29f);
			caption.setRatioSizeY(1);

			caption.setRenderer(recordCaptionRenderer);
			caption.addBackgroundRenderer(captionCageRenderer);

			// rendererParameters.add(caption);
		}

		if (isLeft) {
			if (renderCaptions) {

				hmRow.append(spacing);
				hmRow.append(caption);
			}

			hmRow.append(heatMapLayout);

		} else {

			hmRow.append(heatMapLayout);

			if (renderCaptions) {

				hmRow.append(spacing);
				hmRow.append(caption);
			}
		}

		mainColumn.append(hmRow);
		// if (isActive) {
		// ElementLayout toolBar;
		//
		// toolBar = new ElementLayout();
		// toolBar.setRatioSizeX(1);
		// toolBar.setAbsoluteSizeY(0.1f);
		//
		// toolBar.setRenderer(new DetailToolBar(heatMap));
		//
		// mainColumn.append(toolBar);
		// }

	}

}