package cerberus.command.view.swt;

import java.util.LinkedList;

import cerberus.command.ICommand;
import cerberus.command.base.AcmdCreate_IdTargetLabelParentXY;
import cerberus.manager.IGeneralManager;
import cerberus.manager.IViewManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.util.exception.CerberusRuntimeException;
import cerberus.view.gui.swt.data.explorer.DataExplorerViewRep;
import cerberus.xml.parser.parameter.IParameterHandler;

/**
 * Class implementes the command for creating a data explorer view.
 * 
 * @author Marc Streit
 *
 */
public class CmdViewCreateDataExplorer 
extends AcmdCreate_IdTargetLabelParentXY 
implements ICommand
{
	/**
	 * Constructor
	 * 
	 * @param refGeneralManager
	 * @param listAttributes List of attributes
	 */
	public CmdViewCreateDataExplorer(
			final IGeneralManager refGeneralManager,
			final IParameterHandler refParameterHandler )
	{
		super(refGeneralManager, refParameterHandler);
	}

	/**
	 * Method creates a data explorer view, sets the attributes 
	 * and calls the init and draw method.
	 */
	public void doCommand() throws CerberusRuntimeException
	{
		IViewManager viewManager = ((IViewManager) refGeneralManager
				.getManagerByBaseType(ManagerObjectType.VIEW));
		
		DataExplorerViewRep dataExplorerView = (DataExplorerViewRep)viewManager
				.createView(ManagerObjectType.VIEW_SWT_DATA_EXPLORER,
							iUniqueTargetId, 
							iParentContainerId, 
							sLabel);
		
		viewManager.registerItem(
				dataExplorerView, 
				iUniqueTargetId, 
				ManagerObjectType.VIEW);
		
		dataExplorerView.setAttributes(refParameterHandler);
		
		dataExplorerView.retrieveGUIContainer();
		dataExplorerView.initView();
		dataExplorerView.drawView();
	}

	public void undoCommand() throws CerberusRuntimeException
	{
		// TODO Auto-generated method stub
		
	}
}
