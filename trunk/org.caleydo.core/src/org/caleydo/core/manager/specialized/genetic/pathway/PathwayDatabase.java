package org.caleydo.core.manager.specialized.genetic.pathway;

import java.io.Serializable;

import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.specialized.genetic.EOrganism;
import org.caleydo.core.manager.specialized.genetic.GeneticUseCase;
import org.caleydo.core.manager.usecase.EDataDomain;

/**
 * Class that holds information about a specific pathway database.
 * 
 * @author Marc Streit
 */
public class PathwayDatabase
	implements Serializable {
	private static final String USER_HOME = "user.home";

	private static final long serialVersionUID = 1L;

	private EPathwayDatabaseType type;

	private String sXMLPath;

	private String sImagePath;

	private String sImageMapPath;

	/**
	 * Constructor.
	 */
	public PathwayDatabase(final EPathwayDatabaseType type, final String sXMLPath, final String sImagePath,
		final String sImageMapPath) {
		this.type = type;
		this.sXMLPath = sXMLPath;
		this.sImagePath = sImagePath;
		this.sImageMapPath = sImageMapPath;

		String sUserHomePath = System.getProperty(USER_HOME);

		if (sXMLPath.startsWith(USER_HOME)) {
			this.sXMLPath = sXMLPath.replace(USER_HOME, sUserHomePath);
		}

		if (sImagePath.startsWith(USER_HOME)) {
			this.sImagePath = sImagePath.replace(USER_HOME, sUserHomePath);
		}

		if (sImageMapPath.startsWith(USER_HOME)) {
			this.sImageMapPath = sImageMapPath.replace(USER_HOME, sUserHomePath);
		}

		if (type == EPathwayDatabaseType.KEGG) {
			EOrganism eOrganism =
				((GeneticUseCase) GeneralManager.get().getUseCase(EDataDomain.GENETIC_DATA)).getOrganism();

			if (eOrganism == EOrganism.HOMO_SAPIENS) {
				this.sImagePath += "hsa/";
				this.sXMLPath += "hsa/";
			}
			else if (eOrganism == EOrganism.MUS_MUSCULUS) {
				this.sImagePath += "mmu/";
				this.sXMLPath += "mmu/";
			}
		}
	}

	public final EPathwayDatabaseType getType() {
		return type;
	}

	public final String getName() {
		return type.getName();
	}

	public final String getURL() {
		return type.getURL();
	}

	public final String getXMLPath() {
		assert sXMLPath.length() != 0 : "Pathway XML path is not set!";

		return sXMLPath;
	}

	public final String getImagePath() {
		assert sImagePath.length() != 0 : "Pathway image path is not set!";

		return sImagePath;
	}

	public final String getImageMapPath() {
		assert sImageMapPath.length() != 0 : "Pathway imagemap path is not set!";

		return sImageMapPath;
	}
}
