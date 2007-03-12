package cerberus.command.view.swt;

import cerberus.command.CommandQueueSaxType;
import cerberus.command.ICommand;
import cerberus.command.base.ACmdCreate_IdTargetLabelParentAttr;
import cerberus.manager.ICommandManager;
import cerberus.manager.IGeneralManager;
import cerberus.manager.IViewManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.util.exception.CerberusRuntimeException;
import cerberus.view.gui.swt.heatmap.jogl.Heatmap2DViewRep;
import cerberus.xml.parser.parameter.IParameterHandler;

/**
 * Class implementes the command for creating a heatmap view.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 *
 */
public class CmdViewCreateHeatmap 
extends ACmdCreate_IdTargetLabelParentAttr 
implements ICommand {
	
	protected int iGLCanvasId = 0;
	
	/**
	 * Constructor.
	 * 
	 */
	public CmdViewCreateHeatmap(
			final IGeneralManager refGeneralManager,
			final ICommandManager refCommandManager,
			final CommandQueueSaxType refCommandQueueSaxType) {
		
		super(refGeneralManager, 
				refCommandManager,
				refCommandQueueSaxType);
	}

	/**
	 * Method creates a gears view, sets the attributes 
	 * and calls the init and draw method.
	 */
	public void doCommand() throws CerberusRuntimeException {
		
		IViewManager viewManager = ((IViewManager) refGeneralManager
				.getManagerByBaseType(ManagerObjectType.VIEW));
		
		Heatmap2DViewRep heatmapView = (Heatmap2DViewRep)viewManager
				.createView(ManagerObjectType.VIEW_SWT_HEATMAP2D,
						iUniqueTargetId, 
						iParentContainerId, 
						sLabel);
		
		viewManager.registerItem(
				heatmapView, 
				iUniqueTargetId, 
				ManagerObjectType.VIEW);
		
		heatmapView.initView();
		heatmapView.drawView();
		
		refCommandManager.runDoCommand(this);
	}

	
	public void setParameterHandler( final IParameterHandler refParameterHandler ) {

		assert refParameterHandler != null: "ParameterHandler object is null!";	

		super.setParameterHandler(refParameterHandler);
	}
	
	public void undoCommand() throws CerberusRuntimeException {
		
		refCommandManager.runUndoCommand(this);
	}
}
