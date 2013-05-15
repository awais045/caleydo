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
package org.caleydo.view.tourguide.internal.stratomex;

import java.awt.Color;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.media.opengl.GL2;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.event.EventListenerManager.DeepScan;
import org.caleydo.core.io.gui.dataimport.widget.ICallback;
import org.caleydo.core.util.base.ILabeled;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.layout.ALayoutRenderer;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLContextLocal;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.util.text.TextUtils;
import org.caleydo.view.stratomex.tourguide.IAddWizardElementFactory;
import org.caleydo.view.tourguide.api.state.IDefaultTransition;
import org.caleydo.view.tourguide.api.state.IState;
import org.caleydo.view.tourguide.api.state.ITransition;
import org.caleydo.view.tourguide.api.state.IUserTransition;
import org.caleydo.view.tourguide.internal.Activator;
import org.caleydo.view.tourguide.internal.score.ScoreFactories;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * @author Samuel Gratzl
 *
 */
public class AddWizardElement extends ALayoutRenderer implements ICallback<IState> {
	private final Map<IState, Integer> stateMap = new HashMap<>();
	@DeepScan
	private StateMachineImpl stateMachine;

	private final AGLView view;
	private GLContextLocal contextLocal;
	private int hovered = -1;

	public AddWizardElement(AGLView view, Object receiver, List<TablePerspective> existing) {
		contextLocal = new GLContextLocal(view.getTextRenderer(), view.getTextureManager(),
				Activator.getResourceLocator());
		this.view = view;
		this.stateMachine = createStateMachine(receiver, existing);
		this.stateMachine.getCurrent().onEnter();

		stateMap.put(this.stateMachine.getCurrent(), 0);

		// this.add(convert(this.stateMachine.getCurrent()));
	}

	private StateMachineImpl createStateMachine(Object receiver, List<TablePerspective> existing) {
		StateMachineImpl state = new StateMachineImpl();
		ScoreFactories.fillStateMachine(state, receiver, existing);
		return state;
	}

	/**
	 * @param pick
	 */
	public void onPick(Pick pick) {
		switch (pick.getPickingMode()) {
		case CLICKED:
			IState current = stateMachine.getCurrent();
			List<IUserTransition> transitions = Lists.newArrayList(Iterables.filter(
					stateMachine.getTransitions(current), IUserTransition.class));
			transitions.get(pick.getObjectID()).apply(this);
			repaint();
			break;
		case MOUSE_OVER:
			hovered = pick.getObjectID();
			repaint();
			break;
		case MOUSE_OUT:
			hovered = -1;
			repaint();
			break;
		default:
			break;
		}

	}

	private void repaint() {
		setDisplayListDirty(true);
		layoutManager.setRenderingDirty();
	}

	@Override
	public void on(IState target) {
		stateMachine.move(target);
		Collection<ITransition> transitions = stateMachine.getTransitions(target);
		for (ITransition t : transitions) {
			t.onSourceEnter(this);
		}
		// automatically switch default single transitions
		if (transitions.size() == 1 && transitions.iterator().next() instanceof IDefaultTransition) {
			((IDefaultTransition) transitions.iterator().next()).apply(this);
			return;
		}

		// if (!stateMap.containsKey(target)) {
		// this.add(convert(target));
		// stateMap.put(target, size() - 1);
		// } else {
		// setDisplayListDirty(true);
		// layoutManager.setRenderingDirty();
		// }
	}

	@Override
	protected void renderContent(GL2 gl) {
		final GLGraphics g = new GLGraphics(gl, contextLocal, false, 0);
		final float w = x;
		final float h = y;

		g.color(0.95f).fillRect(0, 0, w, h);
		g.color(Color.DARK_GRAY).drawRect(0, 0, w, h);

		final PixelGLConverter converter = view.getPixelGLConverter();

		final float h_header = converter.getGLHeightForPixelHeight(100);
		final float gap = h_header * 0.1f;

		IState current = stateMachine.getCurrent();
		Collection<IUserTransition> transitions = Lists.newArrayList(Iterables.filter(stateMachine.getTransitions(current), IUserTransition.class));

		if (transitions.isEmpty()) {
			drawMultiLineText(g, current, 0, 0, w, h);
		} else {
			drawMultiLineText(g, current, 0, h - h_header, w, h_header);
			float hi = (h - h_header - transitions.size() * gap) / (transitions.size());
			float y = h_header+gap;
			int i = 0;
			for (IUserTransition t : transitions) {
				g.pushName(getPickingID(i));
				if (hovered == i)
					g.color(0.85f);
				else
					g.color(0.90f);
				g.fillRect(gap, h - y - hi, w - 2 * gap, hi);
				g.popName();
				drawMultiLineText(g, t, gap, h - y - hi, w - 2 * gap, hi);
				y += hi + gap;
				i++;
			}
		}
	}

	private int getPickingID(int i) {
		return view.getPickingManager().getPickingID(view.getID(), IAddWizardElementFactory.PICKING_TYPE, i);
	}

	private void drawMultiLineText(GLGraphics g, ILabeled item, float x, float y, float w, float h) {
		if (item.getLabel().isEmpty())
			return;
		final float lineHeight = view.getPixelGLConverter().getGLHeightForPixelHeight(14);

		List<String> lines = TextUtils.wrap(g.text, item.getLabel(), w, lineHeight);

		g.drawText(lines, x, y + (h - lineHeight * lines.size()) * 0.5f, w, lineHeight * lines.size(), 0, VAlign.CENTER);
	}

	@Override
	protected boolean permitsWrappingDisplayLists() {
		return true;
	}
}



