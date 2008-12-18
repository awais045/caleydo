package org.caleydo.core.command.view.swt;

import java.util.ArrayList;
import java.util.StringTokenizer;
import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.base.ACmdExternalAttributes;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.parser.parameter.IParameterHandler;

/**
 * Class implements the command for creating a pathway view.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public class CmdViewCreatePathway
	extends ACmdExternalAttributes
{
	private ArrayList<Integer> iArSetIDs;

	/**
	 * Constructor
	 */
	public CmdViewCreatePathway(final ECommandType cmdType)
	{
		super(cmdType);

		iArSetIDs = new ArrayList<Integer>();
	}

	@Override
	public void doCommand()
	{

		// IViewManager viewManager = ((IViewManager) generalManager
		// .getManagerByObjectType(ManagerObjectType.VIEW));
		//		
		// Pathway2DViewRep pathwayView = (Pathway2DViewRep)viewManager
		// .createView(ManagerObjectType.VIEW_SWT_PATHWAY,
		// iUniqueID,
		// iParentContainerId,
		// sLabel);
		//		
		// viewManager.registerItem(
		// pathwayView,
		// iUniqueID);
		//
		// int[] iArTmp = new int[iArSetIDs.size()];
		// for(int index = 0; index < iArSetIDs.size(); index++)
		// iArTmp[index] = iArSetIDs.get(index);
		//		
		// pathwayView.setAttributes(iHTMLBrowserId);
		// pathwayView.addSetId(iArTmp);
		// pathwayView.initView();
		// pathwayView.drawView();
		//		
		// commandManager.runDoCommand(this);
	}

	@Override
	public void setParameterHandler(final IParameterHandler parameterHandler)
	{
		super.setParameterHandler(parameterHandler);

		// TODO: load browser ID dynamically
		// parameterHandler.setValueAndTypeAndDefault("iHTMLBrowserId",
		// parameterHandler.getValueString(
		// CommandType.TAG_DETAIL.getXmlKey() ),
		// IParameterHandler.ParameterHandlerType.INT,
		// "-1");
		//		
		// iHTMLBrowserId = parameterHandler.getValueInt("iHTMLBrowserId");

		// Read SET IDs (Data and Selection)
		String sPathwaySets = "";
		parameterHandler.setValueAndTypeAndDefault("sPathwaySets", parameterHandler
				.getValueString(ECommandType.TAG_DETAIL.getXmlKey()),
				IParameterHandler.ParameterHandlerType.STRING, "-1");

		sPathwaySets = parameterHandler.getValueString("sPathwaySets");

		StringTokenizer setToken = new StringTokenizer(sPathwaySets,
				IGeneralManager.sDelimiter_Parser_DataItems);

		while (setToken.hasMoreTokens())
		{
			iArSetIDs.add(Integer.valueOf(setToken.nextToken()).intValue());
		}
	}

	@Override
	public void undoCommand()
	{
		commandManager.runUndoCommand(this);
	}
}
