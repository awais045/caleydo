/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.command.factory;


import javax.swing.JComponent;

import cerberus.manager.GeneralManager;


import cerberus.command.CommandInterface;
import cerberus.command.CommandType;
import cerberus.command.CommandTypeGroup;
import cerberus.command.base.CommandAbstractBase;

import cerberus.command.factory.CommandFactoryInterface;

import cerberus.command.window.CmdWindowNewIFrameHeatmap2D;
import cerberus.command.window.CmdWindowNewIFrameHistogram2D;
import cerberus.command.window.CmdWindowNewIFrameScatterplot2D;
import cerberus.command.window.CmdWindowPopupCredits;
import cerberus.command.window.CmdWindowPopupInfo;
import cerberus.command.window.CmdWindowNewInternalFrame;
import cerberus.command.window.CmdWindowNewIFrameSelection;
import cerberus.command.window.CmdWindowNewIFrameStorage;
import cerberus.command.window.CmdWindowNewIFrameJoglCanvas;
import cerberus.command.window.CmdWindowNewIFrameJoglHistogram;
import cerberus.command.window.CmdWindowNewIFrameJoglHeatmap;
import cerberus.command.window.CmdWindowNewIFrameJoglScatterplot;
import cerberus.command.window.CmdWindowSetActiveFrame;

import cerberus.command.system.CmdSystemExit;
import cerberus.command.system.CmdSystemNop;
import cerberus.command.system.CmdSystemNewFrame;

//import cerberus.net.dwt.swing.jogl.WorkspaceSwingFrame;
//import cerberus.net.dwt.swing.mdi.DDesktopPane;

import cerberus.util.exception.PrometheusCommandException;

/**
 * @author Michael Kalkusch
 *
 */
public class CommandFactory 
extends CommandAbstractBase
	implements CommandInterface, CommandFactoryInterface {

	/**
	 * Command created by the factory.
	 */
	protected CommandInterface refCommand;
	
	protected final GeneralManager refGeneralManager;


	/**
	 * Constructor
	 * 
	 * @param setRefGeneralManager reference to GeneralManager
	 * @param setCommandType may be null if no command shall be created by the constructor
	 */
	public CommandFactory(  final GeneralManager setRefGeneralManager,
			final CommandType setCommandType) {
		
		assert setRefGeneralManager != null:"Can not create CommandFactory from null-pointer to GeneralManager";
		
		refGeneralManager = setRefGeneralManager;
		
		/* create new command from constructor parameter. */
		if ( setCommandType != null ) {
			refCommand = createCommand( setCommandType, null );
		}
	}
	
	/*
	 *  (non-Javadoc)
	 * @see cerberus.command.factory.CommandFactoryInterface#createCommand(cerberus.command.CommandType)
	 */
	public CommandInterface createCommand( 
			final CommandType createCommandByType, 
			final String details ) {
		
		assert createCommandByType != null:"Can not create command from null-pointer.";
		
		switch ( (CommandTypeGroup)createCommandByType.getGroup()) {
		
		case APPLICATION:
			break;
			
		case DATASET:
			return createDatasetCommand(createCommandByType,details);
			
		case DATA_COLLECTION:
			return createDatasetCommand(createCommandByType,details);
		
		case HOST:
			break;
			
		case SELECT:
			return createSelectionCommand(createCommandByType,details);
			
		case SELECT_VALUE:
			return createSelectionValueCommand(createCommandByType,details);
			
		case SERVER:
			break;
			
		case SYSTEM:
			return createSystemCommand(createCommandByType,details);	
			
		case WINDOW:
			return createWindowCommand(createCommandByType,details);
			
		default:
			System.err.println("CommandFactory(CommandType) failed, because CommandTypeGroup ["+
					createCommandByType.getGroup() +"] is not known by factory.");
			refCommand = null;
			return null;			
		}
		
		return null;
		
	}
	
	protected CommandInterface createDatasetCommand( 
			final CommandType setCommandType,
			final String details ) {
		switch (setCommandType) {
			case DATA_COLLECTION_LOAD :
				break;
			case DATA_COLLECTION_SAVE :
				break;	
			case DATASET_RELOAD :
				break;
			case DATASET_LOAD :
				break;
			case DATASET_SAVE :
				break;
				
			default:
				System.err.println("CommandFactory(CommandType) failed, because CommandType ["+
						setCommandType +"] is not known by factory.");
				refCommand = null;
			return null;
		}
		
		return null;
	}
	
	protected CommandInterface createSelectionCommand( 
			final CommandType setCommandType,
			final String details ) {
		switch (setCommandType) {
			case SELECT_NEW :
				break;
			case SELECT_DEL :
				break;	
			case SELECT_ADD :
				break;
			case SELECT_LOAD :
				break;
			case SELECT_SAVE :
				break;
				
			default:
				System.err.println("CommandFactory(CommandType) failed, because CommandType ["+
						setCommandType +"] is not known by factory.");
				refCommand = null;
			return null;
		}
		
		return null;
	}
	
	protected CommandInterface createSystemCommand( 
			final CommandType setCommandType,
			final String details ) {
		switch (setCommandType) {
			case SYSTEM_EXIT :
				refCommand = new CmdSystemExit();
				return refCommand;
			case SYSTEM_NOP:
				return new CmdSystemNop();
				
			case SYSTEM_NEW_FRAME:
				return new CmdSystemNewFrame();
				
			default:
				System.err.println("CommandFactory(CommandType) failed, because CommandType ["+
						setCommandType +"] is not known by factory.");
				refCommand = null;
		}
		
		return null;
	}
	
	protected CommandInterface createWindowCommand( 
			final CommandType setCommandType,
			final String details ) {
		
//		int iWorkspaceTargetId = -1;
//		//JComponent refJComponent = null;
//		DDesktopPane refDDesktopPane = null;
//		
//		if ( details != null ) {
//			try {
//				iWorkspaceTargetId = Integer.valueOf( details );
//				WorkspaceSwingFrame targetFrame = 
//					this.refGeneralManager.getSingelton().getViewCanvasManager().getItemWorkspace( iWorkspaceTargetId );
//				refDDesktopPane = targetFrame.getDesktopPane();
//				
//			} catch (NumberFormatException nfe ) {
//				assert false:"Can not handle detail [" +
//				details +
//				"] in createWindowCommand()";
//			}
//		}
//		
//		switch (setCommandType) {
//			case WINDOW_POPUP_CREDITS :
//				refCommand = new CmdWindowPopupCredits();
//				return refCommand;
//				
//			case WINDOW_POPUP_INFO :
//				refCommand = new CmdWindowPopupInfo(details);
//				return refCommand;
//				
//			case WINDOW_NEW_INTERNAL_FRAME:
//				return  new CmdWindowNewInternalFrame(refGeneralManager,
//						iWorkspaceTargetId, 
//						details );
//				
//			case WINDOW_IFRAME_OPEN_HEATMAP2D:
//				return new CmdWindowNewIFrameHeatmap2D(refGeneralManager, iWorkspaceTargetId);
//				
//			case WINDOW_IFRAME_OPEN_HISTOGRAM2D:
//				return new CmdWindowNewIFrameHistogram2D(refGeneralManager,iWorkspaceTargetId);
//				
//			case WINDOW_IFRAME_OPEN_SCATTERPLOT2D:
//				return new CmdWindowNewIFrameScatterplot2D();
//				
//			case WINDOW_IFRAME_OPEN_SELECTION:
//				return new CmdWindowNewIFrameSelection(refGeneralManager,iWorkspaceTargetId);
//			
//			case WINDOW_IFRAME_OPEN_STORAGE:
//				return new CmdWindowNewIFrameStorage(refGeneralManager,iWorkspaceTargetId);
//				
//			case WINDOW_IFRAME_OPEN_JOGL_CANVAS:
//				
//				return new CmdWindowNewIFrameJoglCanvas(refGeneralManager, 
//						null, 
//						iWorkspaceTargetId,
//						details );
//				
//			case WINDOW_IFRAME_OPEN_JOGL_HISTOGRAM:
//				return new CmdWindowNewIFrameJoglHistogram( refGeneralManager, 
//						iWorkspaceTargetId,
//						null, 
//						null );
//				
//			case WINDOW_IFRAME_OPEN_JOGL_HEATMAP:
//				return new CmdWindowNewIFrameJoglHeatmap( refGeneralManager, 
//						iWorkspaceTargetId, 
//						null, 						
//						null );
//			
//			case WINDOW_IFRAME_OPEN_JOGL_SCATTERPLOT:
//				return new CmdWindowNewIFrameJoglScatterplot( refGeneralManager, 
//						iWorkspaceTargetId, 
//						null, 						
//						null );
//				
//			case WINDOW_SET_ACTIVE_FRAME:
//				return new CmdWindowSetActiveFrame( refGeneralManager, details );
//				
//			case WINDOW_IFRAME_OPEN_SET:
//				//return new CmdWindowNewIFrameSet(refGeneralManager);
//				
//			
//			default:
//				System.err.println("CommandFactory(CommandType) failed, because CommandType ["+
//						setCommandType +"] is not known by factory.");
//				refCommand = null;
//		}
		
		return null;
	}
	
	
	
	protected CommandInterface createSelectionValueCommand( 
			final CommandType setCommandType,
			final String details ) {
		switch (setCommandType) {
			case SELECT_SHOW:
				break;
			case SELECT_HIDE:
				break;
			case SELECT_LOCK:
				break;
			case SELECT_UNLOCK:
				break;
				
			default:
				System.err.println("CommandFactory(CommandType) failed, because CommandType ["+
						setCommandType +"] is not known by factory.");
				refCommand = null;
			return null;
		}
		
		return null;
	}
	
	/**
	 * Since the last created command is stored its reference is returned.
	 * Note: be carefull with this methode, becaus maybe the commadn was already executed or distryed, or a new command was created meanwhile
	 * @return reference to last created command
	 */
	protected CommandInterface getLastCreatedCommand() {
		return refCommand;
	}
	
	
	/* (non-Javadoc)
	 * @see cerberus.command.CommandInterface#doCommand()
	 */
	public void doCommand() throws PrometheusCommandException {
		try {
			refCommand.doCommand();
		} catch (PrometheusCommandException pe) {
			throw new PrometheusCommandException("CommandFactory.doCommand() failed with "+
					pe.toString() );
		} catch (Exception e) {
			throw new PrometheusCommandException("CommandFactory.doCommand() failed with "+
					e.toString() );
		}
	}


	/* (non-Javadoc)
	 * @see cerberus.command.CommandInterface#undoCommand()
	 */
	public void undoCommand() throws PrometheusCommandException {
		try {
			refCommand.undoCommand();
		} catch (PrometheusCommandException pe) {
			throw new PrometheusCommandException("CommandFactory.doCommand() failed with "+
					pe.toString() );
		} catch (Exception e) {
			throw new PrometheusCommandException("CommandFactory.doCommand() failed with "+
					e.toString() );
		}
	}
	
	
	public void setCommandType(CommandType setType) 
		throws PrometheusCommandException {
		
	}
	
	
	public CommandType getCommandType() 
		throws PrometheusCommandException {
		
		if ( refCommand == null ) {
			return CommandType.APPLICATION_COMMAND_FACTORY;
		}
		
		try {
			return refCommand.getCommandType();
		} catch (PrometheusCommandException pe) {
			throw new PrometheusCommandException("CommandFactroy.getCommandType() failed with "+
					pe.toString() );
		}
	}

}
