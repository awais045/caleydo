package org.caleydo.core.manager.specialized;

import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.mapping.EIDCategory;
import org.caleydo.core.data.selection.EVAType;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.manager.usecase.AUseCase;
import org.caleydo.core.manager.usecase.EDataDomain;

/**
 * TODO The use case for pathway input data.
 * 
 * @author Marc Streit
 */
@XmlType
@XmlRootElement
public class PathwayUseCase
	extends AUseCase {

	/**
	 * Constructor.
	 */
	public PathwayUseCase() {
		useCaseMode = EDataDomain.PATHWAY_DATA;
		possibleViews = new ArrayList<EManagedObjectType>();
		possibleViews.add(EManagedObjectType.GL_PATHWAY);

		possibleIDCategories = new HashMap<EIDCategory, String>();
		// possibleIDCategories.put(EIDCategory.GENE, null);
		possibleIDCategories.put(EIDCategory.PATHWAY, null);
	}

	@Override
	public void setSet(ISet set) {

		super.setSet(set);
	}
}
