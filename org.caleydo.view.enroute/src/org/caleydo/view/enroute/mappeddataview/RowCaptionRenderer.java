/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander Lex, Christian Partl, Johannes Kepler
 * University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.view.enroute.mappeddataview;

import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.id.IDType;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.layout.ALayoutRenderer;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
import org.caleydo.view.enroute.SelectionColorCalculator;

/**
 * Renders a row caption based on david IDs
 *
 * @author Alexander Lex
 *
 */
public class RowCaptionRenderer extends ALayoutRenderer {

	private CaleydoTextRenderer textRenderer;
	private PixelGLConverter pixelGLConverter;

	protected MappedDataRenderer parent;

	private IDType rowIDType;
	private Integer rowID;

	private AGLView parentView;
	private SelectionColorCalculator colorCalculator;

	/**
	 * Constructor
	 *
	 * @param textRenderer
	 *            the <code>CaleydoTextRenderer</code> of the parent GL view
	 * @param pixelGLConverter
	 *            the <code>PixelGLConverter</code> of the parent GL view
	 * @param rowID
	 *            the id used for the resolution of the human readable id type that is rendered
	 * @param backgroundColor
	 *            RGBA value of the background color.
	 */
	public RowCaptionRenderer(IDType rowIDType, Integer rowID, AGLView parentView, MappedDataRenderer parent,
			float[] backgroundColor) {

		this.parentView = parentView;
		colorCalculator = new SelectionColorCalculator(new Color(backgroundColor));
		this.rowID = rowID;
		this.rowIDType = rowIDType;
		this.parent = parent;

		textRenderer = parentView.getTextRenderer();
		pixelGLConverter = parentView.getPixelGLConverter();

	}

	@Override
	public void renderContent(GL2 gl) {
		List<SelectionType> selectionTypes = parent.getSelectionManager(rowIDType).getSelectionTypes(rowID);

		colorCalculator.calculateColors(selectionTypes);
		float[] topBarColor = colorCalculator.getPrimaryColor().getRGBA();
		float[] bottomBarColor = colorCalculator.getSecondaryColor().getRGBA();
		float backgroundZ = 0;
		float frameZ = 0.3f;

		gl.glPushName(parentView.getPickingID(rowIDType.getTypeName(), rowID));

		gl.glBegin(GL2.GL_QUADS);

		gl.glColor3f(bottomBarColor[0], bottomBarColor[1], bottomBarColor[2]);

		gl.glVertex3f(0, 0, backgroundZ);
		gl.glColor3f(bottomBarColor[0] * 0.9f, bottomBarColor[1] * 0.9f, bottomBarColor[2] * 0.9f);
		gl.glVertex3f(x, 0, backgroundZ);
		gl.glColor3f(topBarColor[0] * 0.9f, topBarColor[1] * 0.9f, topBarColor[2] * 0.9f);

		gl.glVertex3f(x, y, backgroundZ);
		gl.glColor4fv(topBarColor, 0);

		gl.glVertex3f(0, y, backgroundZ);

		gl.glEnd();

		gl.glLineWidth(1);
		gl.glColor4fv(MappedDataRenderer.FRAME_COLOR, 0);
		gl.glBegin(GL.GL_LINE_LOOP);
		gl.glVertex3f(0, 0, frameZ);
		gl.glVertex3f(0, y, frameZ);
		gl.glVertex3f(x, y, frameZ);
		gl.glVertex3f(x, 0, frameZ);
		gl.glEnd();

		float sideSpacing = pixelGLConverter.getGLWidthForPixelWidth(8);
		float height = pixelGLConverter.getGLHeightForPixelHeight(15);
		IDMappingManager rowIDMappingManager = IDMappingManagerRegistry.get().getIDMappingManager(rowIDType);
		String rowName = rowIDMappingManager
				.getID(rowIDType, rowIDType.getIDCategory().getHumanReadableIDType(), rowID);
		if (rowName != null)
			textRenderer.renderTextInBounds(gl, rowName, sideSpacing, (y - height) / 2, 0.1f, x, height);

		gl.glPopName();
	}

	@Override
	protected boolean permitsWrappingDisplayLists() {
		return false;
	}

}
