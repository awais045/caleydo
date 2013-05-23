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

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.view.stratomex.tourguide.AAddWizardElement;
import org.caleydo.view.stratomex.tourguide.IAddWizardElementFactory;
import org.caleydo.view.stratomex.tourguide.IStratomexAdapter;
import org.caleydo.view.tourguide.api.state.EWizardMode;

/**
 * @author Samuel Gratzl
 *
 */
public class AddWizardElementFactory implements IAddWizardElementFactory {

	@Override
	public AAddWizardElement create(IStratomexAdapter adapter, AGLView view) {
		return new AddWizardElement(view, adapter, EWizardMode.GLOBAL, null);
	}

	@Override
	public AAddWizardElement createDependent(IStratomexAdapter adapter, AGLView view, TablePerspective tablePerspective) {
		return new AddWizardElement(view, adapter, EWizardMode.DEPENDENT, tablePerspective);
	}

	@Override
	public AAddWizardElement createIndepenent(IStratomexAdapter adapter, AGLView view, TablePerspective tablePerspective) {
		return new AddWizardElement(view, adapter, EWizardMode.INDEPENDENT, tablePerspective);
	}

}
