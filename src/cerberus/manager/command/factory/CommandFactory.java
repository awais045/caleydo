/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 * Creation date: 18-05-2005
 *  
 */
package cerberus.manager.command.factory;

import java.util.LinkedList;
import java.util.StringTokenizer;
import javax.swing.JComponent;

import cerberus.command.CommandType;
import cerberus.command.CommandTypeGroup;
import cerberus.command.ICommand;
import cerberus.command.base.ACommand;

import cerberus.command.data.CmdDataCreateSelection;
import cerberus.command.data.CmdDataCreateSet;
import cerberus.command.data.CmdDataCreateStorage;
import cerberus.command.event.CmdEventRelationCreate;


import cerberus.command.view.swt.CmdViewCreateDataExplorer;
import cerberus.command.view.swt.CmdViewCreateGears;
import cerberus.command.view.swt.CmdViewCreateHeatmap;
import cerberus.command.view.swt.CmdViewCreateMixer;
import cerberus.command.view.swt.CmdViewCreatePathway;
import cerberus.command.view.swt.CmdViewCreateProgressBar;
import cerberus.command.view.swt.CmdViewCreateSlider;
import cerberus.command.view.swt.CmdViewCreateSwtGLCanvas;
import cerberus.command.view.swt.CmdViewCreateTestTriangle;
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
import cerberus.command.window.swt.CmdWindowCreate;
import cerberus.command.window.swt.CmdContainerCreate;

import cerberus.command.queue.CmdSystemRunCmdQueue;
import cerberus.command.queue.CommandQueueVector;

import cerberus.command.system.CmdSystemExit;
import cerberus.command.system.CmdSystemNop;
import cerberus.command.system.CmdSystemNewFrame;
import cerberus.command.system.CmdSystemLoadFileViaImporter;

import cerberus.manager.ICommandManager;
import cerberus.manager.IGeneralManager;


//import cerberus.net.dwt.swing.jogl.WorkspaceSwingFrame;
//import cerberus.net.dwt.swing.mdi.DDesktopPane;

import cerberus.util.exception.CerberusExceptionType;
import cerberus.util.exception.CerberusRuntimeException;

import cerberus.xml.parser.command.CommandQueueSaxType;
import cerberus.xml.parser.parameter.IParameterHandler;

/**
 * Class is responsible for creating the commands.
 * The commands are created according to the command type.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 *
 */
public class CommandFactory 
extends ACommand
	implements ICommand, ICommandFactory {

	/**
	 * Command created by the factory.
	 */
	protected ICommand refCommand;
	
	protected final IGeneralManager refGeneralManager;

	protected final ICommandManager refCommandManager;

	public static final String sDelimiter_CreateStorage_DataItemBlock 	= "@";	
	public static final String sDelimiter_CreateStorage_DataItems 		= " ";
	public static final String sDelimiter_CreateStorage_DataType 		= ";";
	
	public static final String sDelimiter_CreateSelection_DataItems 	= " ";
	public static final String sDelimiter_CreateSelection_DataItemBlock = "@";
	
	public static final String sDelimiter_CreateComposite_Layout 		= " ";
	
	public static final String sDelimiter_CreateView_Size				= " ";
	
	/**
	 * Constructor
	 * 
	 * @param setRefGeneralManager reference to IGeneralManager
	 * @param setCommandType may be null if no command shall be created by the constructor
	 */
	public CommandFactory(  final IGeneralManager setRefGeneralManager,
			final ICommandManager refCommandManager,
			final CommandType setCommandType) {
		
		assert setRefGeneralManager != null:"Can not create CommandFactory from null-pointer to IGeneralManager";
		
		this.refGeneralManager = setRefGeneralManager;		
		this.refCommandManager = refCommandManager;
		
		/* create new command from constructor parameter. */
		if ( setCommandType != null ) {
			refCommand = createCommand( setCommandType, "" );
		}
	}
	
	/*
	 *  (non-Javadoc)
	 * @see cerberus.command.factory.CommandFactoryInterface#createCommand(cerberus.command.CommandType)
	 */
	public ICommand createCommand( 
			final CommandType createCommandByType, 
			final String details ) {
		
		assert createCommandByType != null:"Can not create command from null-pointer.";
		
		switch ( createCommandByType.getGroup()) {
		
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
	
	
	/**
	 * 
	 * List of expected Strings inside LinkedList <String>: <br>
	 * sData_CmdId <br>
	 * sData_TargetId <br>
	 * sData_Cmd_label <br>
	 * sData_Cmd_process <br> 
	 * sData_Cmd_MementoId <br> 
	 * sData_Cmd_detail <br>
	 * sData_Cmd_attribute1 <br>
	 * sData_Cmd_attribute2 <br>
	 * 
	 * @see cerberus.manager.command.factory.ICommandFactory#createCommand(java.lang.String, java.util.LinkedList)
	 */
	public ICommand createCommand(final IParameterHandler phAttributes) {
		
		CommandQueueSaxType cmdType = 
			CommandQueueSaxType.valueOf( phAttributes.getValueString( CommandQueueSaxType.TAG_TYPE.getXmlKey() ) );
			
		ICommand createdCommand = null;
		
		switch ( cmdType ) {
		
		case LOAD_DATA_FILE: 
		{
			createdCommand =
				new CmdSystemLoadFileViaImporter( 
						refGeneralManager,
						phAttributes );
			break;
		}
		

		case CREATE_STORAGE:
		{					
			createdCommand =
				new CmdDataCreateStorage(
						refGeneralManager,
						phAttributes,
						true );
			break;
		}
		
		case CREATE_SET:
		{
			createdCommand =
				new CmdDataCreateSet(
						refGeneralManager,
						phAttributes,
						true );
			break;
		}
		
		case CREATE_SELECTION:
		{
			createdCommand =
				new CmdDataCreateSelection(
						refGeneralManager,
						phAttributes );			
			break;
		}
		
		case CREATE_SWT_WINDOW:
		{
			createdCommand =
				new CmdWindowCreate(
						refGeneralManager,
						phAttributes );			
			break;
		}
		
		case CREATE_SWT_CONTAINER:
		{
			createdCommand =
				new CmdContainerCreate(
						refGeneralManager,
						phAttributes );			
			break;
		}
		
		case CREATE_VIEW_HEATMAP:
		{
			createdCommand =
				new CmdViewCreateHeatmap(
						refGeneralManager,
						phAttributes );			
			break;
		}
		
		case CREATE_VIEW_GEARS:
		{
			createdCommand =
				new CmdViewCreateGears(
						refGeneralManager,
						phAttributes );			
			break;
		}
		
		case CREATE_VIEW_TEST_TRIANGLE:
		{
			createdCommand =
				new CmdViewCreateTestTriangle(
						refGeneralManager,
						phAttributes );			
			break;
		}
		
		case CREATE_VIEW_SWT_GLCANVAS:
		{
			createdCommand =
				new CmdViewCreateSwtGLCanvas(
						refGeneralManager,
						phAttributes );			
			break;
		}
		
		
		
		case CREATE_VIEW_DATA_EXPLORER:
		{
			createdCommand =
				new CmdViewCreateDataExplorer(
						refGeneralManager,
						phAttributes );			
			break;
		}
		
		case CREATE_VIEW_PROGRESSBAR:
		{
			createdCommand =
				new CmdViewCreateProgressBar(
						refGeneralManager,
						phAttributes );			
			break;
		}
		
//		case CREATE_VIEW_PATHWAY:
//		{
//			createdCommand =
//				new CmdViewCreatePathway(
//						refGeneralManager,
//						phAttributes );			
//			break;
//		}

		case CREATE_VIEW_SLIDER:
		{
			createdCommand =
				new CmdViewCreateSlider(
						refGeneralManager,
						phAttributes );			
			break;
		}
		
		case CREATE_VIEW_MIXER:
		{
			createdCommand =
				new CmdViewCreateMixer(
						refGeneralManager,
						phAttributes );			
			break;
		}
		
		case CREATE_EVENT_RELATION:
		{
			createdCommand =
				new CmdEventRelationCreate(
						refGeneralManager,
						phAttributes );			
			break;
		}
		
		default: 
			throw new CerberusRuntimeException("CommandFactory::createCommand() Unsupported CommandQueue key= [" + 
					cmdType + "]",
					CerberusExceptionType.SAXPARSER);
		} // end switch
		
//		/**
//		 * Create a new uniqueId if nessecary
//		 */
//		int iNewUniqueId = iData_CmdId;		
//		if ( iData_CmdId < 0 ) {
//			iNewUniqueId = refCommandManager.createNewId( null );
//		}
//		createdCommand.setId( iNewUniqueId );
//		/**
//		 * End: Create a new uniqueId if nessecary
//		 */			
		
		return createdCommand;
	}
	
	
	public ICommand createCommandQueue( final String sCmdType,
			final String sProcessType,
			final int iCmdId,
			final int iCmdQueueId,
			final int sQueueThread,
			final int sQueueThreadWait ) {
		
		CommandQueueSaxType queueType;
		
		/**
		 * Create a new uniqueId if necessary
		 */
		int iNewUniqueId = iCmdId;		
		if ( iCmdId < 0 ) {
			iNewUniqueId = refCommandManager.createNewId( null );
		}
		/**
		 * End: Create a new uniqueId if necessary
		 */
		
		try 
		{
			queueType =CommandQueueSaxType.valueOf( sCmdType );
		}
		catch ( IllegalArgumentException iae ) 
		{
			throw new CerberusRuntimeException("Undefined CommandQueue key= [" + sCmdType + "]",
					CerberusExceptionType.SAXPARSER);
		}
			
		switch (queueType) 
		{
		case COMMAND_QUEUE_OPEN: {
			ICommand cmdQueue = new CommandQueueVector(iNewUniqueId, iCmdQueueId);				
			return cmdQueue;
		}
			
		case COMMAND_QUEUE_RUN:
			return new CmdSystemRunCmdQueue(iNewUniqueId,
					refGeneralManager,
					iCmdQueueId);
			
			default:
				throw new CerberusRuntimeException("Unsupported CommandQueue key= [" + sCmdType + "]",
						CerberusExceptionType.SAXPARSER);
		}
		
	}
	
	protected ICommand createDatasetCommand( 
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
	
	protected ICommand createSelectionCommand( 
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
	
	protected ICommand createSystemCommand( 
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
	
	protected ICommand createWindowCommand( 
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
	
	
	
	protected ICommand createSelectionValueCommand( 
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
	protected ICommand getLastCreatedCommand() {
		return refCommand;
	}
	
	
	/* (non-Javadoc)
	 * @see cerberus.command.ICommand#doCommand()
	 */
	public void doCommand() throws CerberusRuntimeException {
		try {
			refCommand.doCommand();
		} catch (CerberusRuntimeException pe) {
			throw new CerberusRuntimeException("CommandFactory.doCommand() failed with "+
					pe.toString(),
					CerberusExceptionType.COMMAND );
		} catch (Exception e) {
			throw new CerberusRuntimeException("CommandFactory.doCommand() failed with "+
					e.toString(),
					CerberusExceptionType.COMMAND );
		}
	}


	/* (non-Javadoc)
	 * @see cerberus.command.ICommand#undoCommand()
	 */
	public void undoCommand() throws CerberusRuntimeException {
		try {
			refCommand.undoCommand();
		} catch (CerberusRuntimeException pe) {
			throw new CerberusRuntimeException("CommandFactory.doCommand() failed with "+
					pe.toString(),
					CerberusExceptionType.COMMAND );
		} catch (Exception e) {
			throw new CerberusRuntimeException("CommandFactory.doCommand() failed with "+
					e.toString(),
					CerberusExceptionType.COMMAND );
		}
	}
	
	
	public void setCommandType(CommandType setType) 
		throws CerberusRuntimeException {
		
	}
	
	
	public CommandType getCommandType() 
		throws CerberusRuntimeException {
		
		if ( refCommand == null ) {
			return CommandType.APPLICATION_COMMAND_FACTORY;
		}
		
		try {
			return refCommand.getCommandType();
		} catch (CerberusRuntimeException pe) {
			throw new CerberusRuntimeException("CommandFactroy.getCommandType() failed with "+
					pe.toString(),
					CerberusExceptionType.COMMAND );
		}
	}

}
