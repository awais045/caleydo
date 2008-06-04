package org.caleydo.core.view.opengl.util.infoarea;

import java.util.ArrayList;

import org.caleydo.core.data.graph.pathway.core.PathwayGraph;
import org.caleydo.core.data.mapping.EGenomeMappingType;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.data.IGenomeIdManager;
import org.caleydo.core.util.mapping.GeneAnnotationMapper;
import org.caleydo.core.view.opengl.canvas.parcoords.EInputDataType;

/**
 * 
 * Creates the content for eg the InfoArea
 * Just pass it an ID and an Inputdatatype,
 * it returns an AL of Relevant data
 * 
 * @author Alexander Lex
 * @author Marc Streit
 *
 */

public class InformationContentCreator 
{
	
	private ArrayList<String> sContent;
	
	private IGenomeIdManager IDManager;
	private GeneAnnotationMapper mapper;
	private IGeneralManager generalManager;
	
	/**
	 * Constructor
	 * @param generalManager
	 */
	public InformationContentCreator(final IGeneralManager generalManager) 
	{
		this.generalManager = generalManager;
		sContent = new ArrayList<String>();
		IDManager = this.generalManager.getGenomeIdManager();
		// TODO Auto-generated constructor stub
		mapper = new GeneAnnotationMapper(generalManager);
	}
	
	/**
	 * Returns an AL of Strings when you pass it an ID and a data type
	 * The list is in such order that the first element is suitable for a title
	 * 
	 * @param iUniqueID
	 * @param eInputDataTypes
	 * @return
	 */
	ArrayList<String> getStringContentForID(final int iUniqueID, 
			final EInputDataType eInputDataTypes)
	{
		sContent.clear();
		switch (eInputDataTypes)
		{
		case GENE:
			
			String sRefSeq;
			String sGeneName;
			
			if (iUniqueID == -1)
			{
				sRefSeq = "unknown";
				sGeneName = "unknown";
			}
			else
			{
				sRefSeq = generalManager.getGenomeIdManager().getIdStringFromIntByMapping(
						iUniqueID, EGenomeMappingType.DAVID_2_REFSEQ_MRNA);							
				sGeneName = "TODO";//mapper.getGeneShortNameByAccession(iUniqueID);
			}
			
			sContent.add("Type: Gene");
			sContent.add("Name: " + sGeneName);			
			sContent.add("RefSeq: " + sRefSeq);
		
			break;
			
		case PATHWAY:
			
			PathwayGraph pathway = ((PathwayGraph)generalManager.getPathwayManager()
					.getItem(iUniqueID));
			
			if (pathway == null)
			{
				break;
			}
			
			String sPathwayTitle = pathway.getTitle();
			
			sContent.add("Type: " +pathway.getType().getName() +"Pathway");
			sContent.add("PW: " +sPathwayTitle);
			break;
			
		case EXPERIMENT:
			
			sContent.add("Type: Experiment");
			break;
			
		default:
			sContent.add("No Data");
		}
		
		return sContent;
	}
}
