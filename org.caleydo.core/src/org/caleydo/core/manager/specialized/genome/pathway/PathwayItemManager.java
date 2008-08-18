package org.caleydo.core.manager.specialized.genome.pathway;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.caleydo.core.data.graph.ICaleydoGraphItem;
import org.caleydo.core.data.graph.pathway.item.edge.PathwayReactionEdgeGraphItem;
import org.caleydo.core.data.graph.pathway.item.edge.PathwayReactionEdgeGraphItemRep;
import org.caleydo.core.data.graph.pathway.item.edge.PathwayRelationEdgeGraphItem;
import org.caleydo.core.data.graph.pathway.item.edge.PathwayRelationEdgeGraphItemRep;
import org.caleydo.core.data.graph.pathway.item.vertex.PathwayVertexGraphItem;
import org.caleydo.core.data.graph.pathway.item.vertex.PathwayVertexGraphItemRep;
import org.caleydo.core.manager.AManager;
import org.caleydo.core.manager.specialized.genome.IPathwayItemManager;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.util.graph.EGraphItemHierarchy;
import org.caleydo.util.graph.EGraphItemProperty;
import org.caleydo.util.graph.IGraph;
import org.caleydo.util.graph.IGraphItem;

/**
 * The element manager is in charge for handling the items. Items are vertices
 * and edges. The class is implemented as a Singleton.
 * 
 * @author Marc Streit
 */
public class PathwayItemManager
	extends AManager<ICaleydoGraphItem>
	implements IPathwayItemManager, Serializable
{

	private static final long serialVersionUID = 1L;

	private HashMap<String, ICaleydoGraphItem> hashVertexNameToGraphItem;
	
	// TODO: replace these hash maps by GenomeIDManager
	private HashMap<Integer, Integer> hashDavidIdToPathwayVertexGraphItemId;
	private HashMap<Integer, Integer> hashPathwayVertexGraphItemIdToDavidId;
	
	private HashMap<Integer, PathwayVertexGraphItemRep> hashIDToPathwayVertexGraphItemRep;

	private boolean bHashDavidIdToPathwayVertexGraphItemIdInvalid = true;

	/**
	 * Constructor.
	 */
	public PathwayItemManager()
	{
		hashVertexNameToGraphItem = new HashMap<String, ICaleydoGraphItem>();
		hashDavidIdToPathwayVertexGraphItemId = new HashMap<Integer, Integer>();
		hashPathwayVertexGraphItemIdToDavidId = new HashMap<Integer, Integer>();
		hashIDToPathwayVertexGraphItemRep = new HashMap<Integer, PathwayVertexGraphItemRep>();
	}

	@Override
	public IGraphItem createVertex(final String sName, final String sType,
			final String sExternalLink, final String sReactionId)
	{

		// Check if same vertex is already contained
		if (hashVertexNameToGraphItem.containsKey(sName))
		{
			// Return existing vertex
			return hashVertexNameToGraphItem.get(sName);
		}

		ICaleydoGraphItem pathwayVertex = new PathwayVertexGraphItem(sName, sType,
				sExternalLink, sReactionId);

		hashItems.put(pathwayVertex.getID(), pathwayVertex);

		((PathwayManager)generalManager.getPathwayManager())
			.getRootPathway().addItem(pathwayVertex);

		hashVertexNameToGraphItem.put(sName, pathwayVertex);

		return pathwayVertex;
	}

	public IGraphItem createVertexGene(final String sName, final String sType,
			final String sExternalLink, final String sReactionId, final int iDavidId)
	{

		IGraphItem tmpGraphItem = createVertex(sName, sType, sExternalLink, sReactionId);

		// Extract David gene ID and add element
		hashDavidIdToPathwayVertexGraphItemId.put(iDavidId, tmpGraphItem.getId());
		hashPathwayVertexGraphItemIdToDavidId.put(tmpGraphItem.getId(), iDavidId);

		return tmpGraphItem;
	}

	public IGraphItem createVertexRep(final IGraph parentPathway,
			final ArrayList<IGraphItem> alVertexGraphItem, final String sName,
			final String sShapeType, final short shHeight, final short shWidth,
			final short shXPosition, final short shYPosition)
	{
		ICaleydoGraphItem pathwayVertexRep = new PathwayVertexGraphItemRep(sName,
				sShapeType, shHeight, shWidth, shXPosition, shYPosition);

		registerItem(pathwayVertexRep);

		parentPathway.addItem(pathwayVertexRep);

		pathwayVertexRep.addGraph(parentPathway, EGraphItemHierarchy.GRAPH_PARENT);

		Iterator<IGraphItem> iterVertexGraphItem = alVertexGraphItem.iterator();
		IGraphItem pathwayVertex;
		while (iterVertexGraphItem.hasNext())
		{
			pathwayVertex = iterVertexGraphItem.next();

			pathwayVertexRep.addItem(pathwayVertex, EGraphItemProperty.ALIAS_PARENT);

			pathwayVertex.addItem(pathwayVertexRep, EGraphItemProperty.ALIAS_CHILD);
		}
		return pathwayVertexRep;
	}

	public IGraphItem createVertexRep(final IGraph parentPathway,
			final IGraphItem parentVertex, final String sName, final String sShapeType,
			final String sCoords)
	{
		ICaleydoGraphItem pathwayVertexRep = new PathwayVertexGraphItemRep(sName,
				sShapeType, sCoords);

		registerItem(pathwayVertexRep);

		parentPathway.addItem(pathwayVertexRep);

		pathwayVertexRep.addGraph(parentPathway, EGraphItemHierarchy.GRAPH_PARENT);

		pathwayVertexRep.addItem(parentVertex, EGraphItemProperty.ALIAS_PARENT);

		parentVertex.addItem(pathwayVertexRep, EGraphItemProperty.ALIAS_CHILD);

		return pathwayVertexRep;
	}

	public IGraphItem createRelationEdge(final List<IGraphItem> alGraphItemIn,
			final List<IGraphItem> alGraphItemOut, final String sType)
	{
		IGraphItem pathwayRelationEdge = new PathwayRelationEdgeGraphItem(sType);

		IGraph rootPathway = ((PathwayManager)generalManager.getPathwayManager())
			.getRootPathway();

		// Add edge to root pathway
		rootPathway.addItem(pathwayRelationEdge);

		// Add root pathway to edge
		pathwayRelationEdge.addGraph(rootPathway, EGraphItemHierarchy.GRAPH_PARENT);

		// Add connection to incoming and outgoing items
		for (IGraphItem graphItemIn : alGraphItemIn)
		{
			pathwayRelationEdge.addItemDoubleLinked(graphItemIn, EGraphItemProperty.INCOMING);
		}
		
		for (IGraphItem graphItemOut : alGraphItemOut)
		{
			pathwayRelationEdge.addItemDoubleLinked(graphItemOut, EGraphItemProperty.OUTGOING);
		}

		return pathwayRelationEdge;
	}

	public void createRelationEdgeRep(final IGraph parentPathway,
			final IGraphItem pathwayRelationEdge, final IGraphItem graphItemIn,
			final IGraphItem graphItemOut)
	{
		IGraphItem pathwayRelationEdgeRep = new PathwayRelationEdgeGraphItemRep();

		// Add edge to pathway representation
		parentPathway.addItem(pathwayRelationEdgeRep);

		// Add pathway representation to created edge
		pathwayRelationEdgeRep.addGraph(parentPathway, EGraphItemHierarchy.GRAPH_PARENT);

		// Add edge data to representation as ALIAS_PARENT
		pathwayRelationEdgeRep.addItem(pathwayRelationEdge, EGraphItemProperty.ALIAS_PARENT);

		// Add edge representation to data as ALIAS_CHILD
		pathwayRelationEdge.addItem(pathwayRelationEdgeRep, EGraphItemProperty.ALIAS_CHILD);

		// Add connection to incoming and outgoing items
		pathwayRelationEdgeRep.addItemDoubleLinked(graphItemIn, EGraphItemProperty.INCOMING);
		pathwayRelationEdgeRep.addItemDoubleLinked(graphItemOut, EGraphItemProperty.OUTGOING);
	}

	public IGraphItem createReactionEdge(final IGraph parentPathway,
			final String sReactionName, final String sReactionType)
	{

		// Create edge (data)
		IGraphItem pathwayReactionEdge = new PathwayReactionEdgeGraphItem(sReactionName, sReactionType);

		// Create edge representation
		IGraphItem pathwayReactionEdgeRep = new PathwayReactionEdgeGraphItemRep();

		IGraph rootPathway = ((PathwayManager)generalManager.getPathwayManager()).getRootPathway();

		// Add edge to root pathway
		rootPathway.addItem(pathwayReactionEdge);

		// Add root pathway to edge
		pathwayReactionEdge.addGraph(rootPathway, EGraphItemHierarchy.GRAPH_PARENT);

		// Add edge to pathway representation
		parentPathway.addItem(pathwayReactionEdgeRep);

		// Add pathway representation to created edge
		pathwayReactionEdgeRep.addGraph(parentPathway, EGraphItemHierarchy.GRAPH_PARENT);

		// Add edge data to representation as ALIAS_PARENT
		pathwayReactionEdgeRep.addItem(pathwayReactionEdge, EGraphItemProperty.ALIAS_PARENT);

		// Add edge representation to data as ALIAS_CHILD
		pathwayReactionEdge.addItem(pathwayReactionEdgeRep, EGraphItemProperty.ALIAS_CHILD);

		return pathwayReactionEdgeRep;
	}


	@Override
	// TODO: throw exception
	public final int getPathwayVertexGraphItemIdByDavidId(final int iDavidId)
	{
		generalManager.getPathwayManager().waitUntilPathwayLoadingIsFinished();
		
		if (hashDavidIdToPathwayVertexGraphItemId.containsKey(iDavidId))
			return hashDavidIdToPathwayVertexGraphItemId.get(iDavidId);

		return -1;
	}

	@Override
	public int getDavidIdByPathwayVertexGraphItemId(final int iPathwayVertexGraphItemId)
	{
		generalManager.getPathwayManager().waitUntilPathwayLoadingIsFinished();
		
		if (hashPathwayVertexGraphItemIdToDavidId.containsKey(iPathwayVertexGraphItemId))
			return hashPathwayVertexGraphItemIdToDavidId.get(iPathwayVertexGraphItemId);

		return -1;
	}
	
	public PathwayVertexGraphItemRep getPathwayVertexRep(int iID)
	{
		generalManager.getPathwayManager().waitUntilPathwayLoadingIsFinished();
		
		if (!hashIDToPathwayVertexGraphItemRep.containsKey(iID))
		{
			throw new CaleydoRuntimeException("Requested pathway vertex representation ID " 
					+iID + " does not exist!");
		}
		
		return hashIDToPathwayVertexGraphItemRep.get(iID);
	}
}
