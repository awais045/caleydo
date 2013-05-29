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
package org.caleydo.view.info.selection.external;

import org.caleydo.core.id.IDCategory;
import org.caleydo.core.id.IDType;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.datadomain.genetic.EGeneIDTypes;
import org.caleydo.view.info.selection.Activator;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * @author Samuel Gratzl
 *
 */
public class MyPreferences extends AbstractPreferenceInitializer {

	private static IPreferenceStore prefs() {
		return Activator.getDefault().getPreferenceStore();
	}

	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = prefs();
		store.setDefault("external.idcategory.GENE.pattern",
				"http://www.genecards.org/index.php?path=/Search/keyword/{0}");
		store.setDefault("external.idcategory.GENE.idType", EGeneIDTypes.GENE_SYMBOL.name());
	}

	static Pair<String, IDType> getExternalIDCategory(IDCategory category) {
		return getExternalIDCategory(prefs(), category, false);
	}

	static Pair<String, IDType> getExternalIDCategory(IPreferenceStore prefs, IDCategory category, boolean defaultValue) {

		final String prefix = "external.idcategory." + category + ".";

		String pattern = defaultValue ? prefs.getDefaultString(prefix + "pattern") : prefs
				.getString(prefix + "pattern");
		IDType type = IDType.getIDType(defaultValue ? prefs.getDefaultString(prefix + "idType") : prefs
				.getString(prefix + "idType"));

		if (type == null || pattern == null || pattern.isEmpty())
			return null;
		return Pair.make(pattern, type);
	}
}
