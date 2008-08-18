package org.caleydo.core.manager.specialized.genome;

import org.caleydo.core.data.graph.pathway.core.PathwayGraph;
import org.caleydo.core.data.view.rep.jgraph.PathwayImageMap;
import org.caleydo.core.manager.IManager;
import org.caleydo.core.manager.specialized.genome.pathway.EPathwayDatabaseType;
import org.caleydo.core.manager.specialized.genome.pathway.PathwayDatabase;
import org.caleydo.util.graph.core.Graph;

/**
 * Interface for creating and accessing pathways.
 * 
 * @author Marc Streit
 */
public interface IPathwayManager
	extends IManager<PathwayGraph>
{
	public PathwayGraph createPathway(final EPathwayDatabaseType type, final String sName,
			final String sTitle, final String sImageLink, final String sExternalLink);

	public void createPathwayDatabase(final EPathwayDatabaseType type, final String sXMLPath,
			final String sImagePath, final String sImageMapPath);

	public void triggerParsingPathwayDatabases();

	public void createPathwayImageMap(final String sImageLink);
	
	public PathwayImageMap getCurrentPathwayImageMap();

	public int searchPathwayIdByName(final String sPathwayName);

	public PathwayDatabase getPathwayDatabaseByType(EPathwayDatabaseType type);

	public void setPathwayVisibilityStateByID(final int iPathwayID,
			final boolean bVisibilityState);

	public boolean isPathwayVisible(final int iPathwayID);
	
	public void waitUntilPathwayLoadingIsFinished();
}
