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
package org.caleydo.core.view.opengl.layout2.view;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.media.opengl.GLAutoDrawable;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.event.view.TablePerspectivesChangedEvent;
import org.caleydo.core.view.ISingleTablePerspectiveBasedView;
import org.caleydo.core.view.listener.AddTablePerspectivesEvent;
import org.caleydo.core.view.listener.RemoveTablePerspectiveEvent;
import org.caleydo.core.view.opengl.canvas.IGLCanvas;
import org.caleydo.core.view.opengl.layout2.AGLElementDecorator;
import org.caleydo.core.view.opengl.layout2.AGLElementView;
import org.caleydo.core.view.opengl.layout2.GLElement;

/**
 *
 * @author Samuel Gratzl
 * 
 */
public abstract class ASingleTablePerspectiveElementView extends AGLElementView implements
		ISingleTablePerspectiveBasedView {
	protected TablePerspective tablePerspective;

	public ASingleTablePerspectiveElementView(IGLCanvas glCanvas, String viewType, String viewName) {
		super(glCanvas, viewType, viewName);
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		super.init(drawable);

		applyTablePerspective(getRootDecorator(), tablePerspective);
	}

	protected abstract void applyTablePerspective(AGLElementDecorator root, TablePerspective tablePerspective);

	@Override
	protected final AGLElementDecorator createRoot() {
		return new WrapperRoot();
	}

	protected final AGLElementDecorator getRootDecorator() {
		return (AGLElementDecorator) getRoot();
	}

	protected GLElement getContent() {
		AGLElementDecorator rootDecorator = getRootDecorator();
		if (rootDecorator == null)
			return null;
		return rootDecorator.getContent();
	}

	@Override
	public boolean isDataView() {
		return true;
	}

	@Override
	public final void setDataDomain(ATableBasedDataDomain dataDomain) {
		// unused
	}

	@Override
	public final ATableBasedDataDomain getDataDomain() {
		if (tablePerspective != null)
			return tablePerspective.getDataDomain();
		return null;
	}

	@Override
	public Set<IDataDomain> getDataDomains() {
		if (tablePerspective != null)
			return Collections.singleton((IDataDomain) tablePerspective.getDataDomain());
		return Collections.emptySet();
	}

	@Override
	public final void setTablePerspective(TablePerspective tablePerspective) {
		this.tablePerspective = tablePerspective;
		fireTablePerspectiveChanged();
		AGLElementDecorator root = getRootDecorator();
		if (root != null) {
			applyTablePerspective(root, tablePerspective);
		}
	}

	@Override
	public final TablePerspective getTablePerspective() {
		return tablePerspective;
	}

	@Override
	public final List<TablePerspective> getTablePerspectives() {
		return Collections.singletonList(getTablePerspective());
	}

	private void fireTablePerspectiveChanged() {
		EventPublisher.trigger(new TablePerspectivesChangedEvent(this).from(this));
	}

	@ListenTo
	private void onAddTablePerspective(AddTablePerspectivesEvent event) {
		Collection<TablePerspective> validTablePerspectives = getDataSupportDefinition().filter(
				event.getTablePerspectives());
		if (validTablePerspectives.isEmpty() || validTablePerspectives.size() > 1) {
			// Make clear for (e.g. for DVI) that no perspectives have been added.
			fireTablePerspectiveChanged();
		} else {
			setTablePerspective(validTablePerspectives.iterator().next());
		}
	}

	@ListenTo(sendToMe = true)
	private void onRemoveTablePerspective(RemoveTablePerspectiveEvent event) {
		if (tablePerspective == event.getTablePerspective())
			setTablePerspective(null);
	}
}
