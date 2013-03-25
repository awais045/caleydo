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
package org.caleydo.vis.rank.ui;

import gleem.linalg.Vec2f;

import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.IMouseLayer;
import org.caleydo.core.view.opengl.layout2.PickableGLElement;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.vis.rank.model.ARankColumnModel;

/**
 * a visual glyph for a separator, i.e. a place where to drop a column
 *
 * @author Samuel Gratzl
 *
 */
public class SeparatorUI extends PickableGLElement {
	protected int index;
	protected boolean armed = false;
	private final IMoveHereChecker model;

	public SeparatorUI(IMoveHereChecker model) {
		setzDelta(0.2f);
		this.model = model;
	}

	public SeparatorUI(IMoveHereChecker model, int index) {
		this(model);
		this.index = index;
	}

	/**
	 * @param index
	 *            setter, see {@link index}
	 */
	public void setIndex(int index) {
		this.index = index;
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		g.color(1, 1, 1, armed ? 1 : 0.5f);
		float v = RenderStyle.HEADER_ROUNDED_RADIUS;
		g.fillPolygon(new Vec2f(-v, 0), new Vec2f(+v + w, 0), new Vec2f(w, v), new Vec2f(0, v));
		// if (armed) {
		// renderHint(g, w, h);
		// }
	}

	protected void renderHint(GLGraphics g, float w, float h) {
		renderTriangle(g, w);
		g.fillRect(0, 0, w, h);
	}

	protected void renderTriangle(GLGraphics g, float w) {
		g.color(RenderStyle.COLOR_ALIGN_MARKER);
		g.drawPath(true, new Vec2f(0, 3), new Vec2f(-5, -10), new Vec2f(w + 5, -10), new Vec2f(w, 3));
		g.fillPolygon(new Vec2f(0, 3), new Vec2f(-5, -10), new Vec2f(w + 5, -10),
				new Vec2f(w, 3));
	}

	@Override
	protected void renderPickImpl(GLGraphics g, float w, float h) {
		super.renderPickImpl(g, w, h);
		if (getVisibility() == EVisibility.PICKABLE)
			renderTriangle(g, w);
	}

	@Override
	protected void onMouseOver(Pick pick) {
		if (!pick.isAnyDragging())
			return;
		IMouseLayer m = context.getMouseLayer();
		if (!m.hasDraggable(ARankColumnModel.class))
			return;
		Pair<GLElement, ARankColumnModel> info = m.getFirstDraggable(ARankColumnModel.class);
		if (!model.canMoveHere(index, info.getSecond(), RenderStyle.isCloneDragging(pick)))
			return;
		m.setDropable(ARankColumnModel.class, true);
		armed = true;
		repaint();
	}

	@Override
	protected void onMouseOut(Pick pick) {
		if (!armed)
			return;
		IMouseLayer m = context.getMouseLayer();
		m.setDropable(ARankColumnModel.class, false);
		armed = false;
		repaint();
	}

	@Override
	protected void onMouseReleased(Pick pick) {
		if (!armed)
			return;
		IMouseLayer m = context.getMouseLayer();
		m.setDropable(ARankColumnModel.class, false);
		Pair<GLElement, ARankColumnModel> info = m.getFirstDraggable(ARankColumnModel.class);
		m.removeDraggable(info.getFirst());
		m.setDropable(ARankColumnModel.class, false);
		context.setCursor(-1);
		armed = false;
		model.moveHere(index, info.getSecond(), RenderStyle.isCloneDragging(pick));
	}



	public interface IMoveHereChecker {
		boolean canMoveHere(int index, ARankColumnModel model, boolean clone);

		void moveHere(int index, ARankColumnModel model, boolean clone);
	}
}
