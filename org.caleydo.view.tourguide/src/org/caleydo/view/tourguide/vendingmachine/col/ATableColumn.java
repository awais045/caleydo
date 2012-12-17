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
package org.caleydo.view.tourguide.vendingmachine.col;

import static org.caleydo.core.view.opengl.layout.ElementLayouts.createXSpacer;
import static org.caleydo.core.view.opengl.layout.ElementLayouts.createYSpacer;
import static org.caleydo.core.view.opengl.layout.ElementLayouts.wrap;
import static org.caleydo.view.tourguide.renderstyle.TourGuideRenderStyle.COL_SPACING;
import static org.caleydo.view.tourguide.renderstyle.TourGuideRenderStyle.LABEL_PADDING;
import static org.caleydo.view.tourguide.renderstyle.TourGuideRenderStyle.ROW_HEIGHT;
import static org.caleydo.view.tourguide.renderstyle.TourGuideRenderStyle.ROW_SPACING;

import java.util.List;

import org.caleydo.core.util.base.ConstantLabelProvider;
import org.caleydo.core.util.base.ILabelProvider;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.util.PickingRenderer;
import org.caleydo.core.view.opengl.layout.util.Renderers;
import org.caleydo.view.tourguide.data.ScoreQuery;
import org.caleydo.view.tourguide.data.ScoringElement;
import org.caleydo.view.tourguide.vendingmachine.ScoreQueryUI;

/**
 * @author Samuel Gratzl
 *
 */
public abstract class ATableColumn extends Column {
	protected final AGLView view;

	protected ElementLayout th;
	private final ElementLayout rowSpacing = createYSpacer(ROW_SPACING);
	protected ElementLayout colSpacing = createXSpacer(COL_SPACING);

	public ATableColumn(AGLView view) {
		this.view = view;
	}

	protected abstract ElementLayout createHeader();

	protected void init() {
		this.th = createHeader();
		this.th.setPixelSizeY(ROW_HEIGHT);
		this.setBottomUp(false);
		this.clearBody();
	}

	protected void clearBody() {
		this.clear();
		this.add(th);
		this.add(createYSpacer(5));
		this.setPixelSizeY(th.getPixelSizeX() + 5);
	}

	protected final void addTd(ElementLayout td, int i) {
		td.setGrabY(false);
		td.setPixelSizeY(ROW_HEIGHT);
		if (i >= 0)
			td.addBackgroundRenderer(new PickingRenderer(ScoreQueryUI.SELECT_ROW, i, this.view));
		this.add(td).add(rowSpacing);
		this.setPixelSizeY(this.getPixelSizeY() + ROW_HEIGHT + ROW_SPACING);
	}


	public final ElementLayout getTd(int i) {
		List<ElementLayout> elem = this.getElements();
		int pos = 2 + i * 2;
		if (elem.size() <= pos)
			return null;
		return elem.get(pos);
	}

	protected final ElementLayout createLabel(String label, int width) {
		return createLabel(new ConstantLabelProvider(label), width);
	}

	protected final ElementLayout createLabel(ILabelProvider label, int width) {
		return wrap(Renderers.createLabel(label, view.getTextRenderer()).padding(LABEL_PADDING).build(), width);
	}

	protected final int getTextWidth(String text) {
		float height = ROW_HEIGHT - LABEL_PADDING.get(1) - LABEL_PADDING.get(3);
		int width = Math.round(this.view.getTextRenderer().getRequiredTextWidth(text,
				height));
		width += LABEL_PADDING.get(0) + LABEL_PADDING.get(2) + 3;
		return width;
	}

	protected final int getTextWidth(ILabelProvider text) {
		return getTextWidth(text.getLabel());
	}

	protected final ElementLayout createRightLabel(ILabelProvider label, int width) {
		return wrap(Renderers.createLabel(label, view.getTextRenderer()).padding(LABEL_PADDING).alignRight().build(),
				width);
	}

	/**
	 * @param data
	 * @param query
	 */
	public abstract void setData(List<ScoringElement> data, ScoreQuery query);
}
