package org.caleydo.datadomain.genetic.contextmenu.item;

import java.util.Arrays;
import java.util.Set;

import org.caleydo.core.data.id.IDType;
import org.caleydo.core.manager.datadomain.ATableBasedDataDomain;
import org.caleydo.core.manager.event.view.remote.LoadPathwaysByGeneEvent;
import org.caleydo.core.view.contextmenu.AContextMenuItem;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.manager.GeneticIDMappingHelper;

/**
 * <p>
 * Item for showing all pathways that contain a specific gene in a sub menu,
 * where these pathways can be loaded individually. The sub-pathways can either
 * be specified manually or the convenience method
 * {@link ShowPathwaysByGeneItem#setRefSeqInt(int)} can be used, which creates
 * the sub-menus automatically.
 * </p>
 * <p>
 * Text and icon have default values but can be overriden.
 * </p>
 * 
 * @author Alexander Lex
 * @author Marc Streit
 */
public class ShowPathwaysByGeneItem extends AContextMenuItem {

	/**
	 * Constructor which sets the default values for icon and text
	 */
	public ShowPathwaysByGeneItem() {
		super();
		//setIconTexture(EIconTextures.CM_DEPENDING_PATHWAYS);
		setLabel("Pathways");
	}

	/**
	 * Convenience method that automatically creates a
	 * {@link LoadPathwaysByGeneEvent} based on a david ID
	 * 
	 */
	public void setDavidID(IDType idType, int david, String dataDomainID) {

		Set<PathwayGraph> pathwayGraphs = GeneticIDMappingHelper.get()
				.getPathwayGraphsByGeneID(idType, david);

		int iPathwayCount = 0;

		if (pathwayGraphs != null) {

			iPathwayCount = pathwayGraphs.size();

			PathwayGraph[] pathways = new PathwayGraph[pathwayGraphs.size()];
			pathwayGraphs.toArray(pathways);
			Arrays.sort(pathways);

			for (PathwayGraph pathwayGraph : pathways) {
				addSubItem(new LoadPathwaysByPathwayItem(pathwayGraph, dataDomainID));
			}
		}

		setLabel("Pathways (" + iPathwayCount + ")");
	}
}
