/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.command.window;

//import javax.swing.JComponent;
//import javax.swing.JOptionPane;

import cerberus.manager.GeneralManager;
import cerberus.manager.ViewCanvasManager;
import cerberus.command.CommandInterface;
import cerberus.command.CommandType;
import cerberus.command.base.CommandAbstractBase;

//import cerberus.net.dwt.swing.jogl.WorkspaceSwingFrame;
//import cerberus.net.dwt.swing.mdi.DInternalFrame;

import cerberus.util.exception.PrometheusCommandException;

/**
 * Creates a popup window dispaying info.
 * 
 * @author Michael Kalkusch
 *
 */
public class CmdWindowNewInternalFrame 
extends CommandAbstractBase
implements CommandInterface {

//	private DInternalFrame refNewDInternalFrame = null;
	
	private int iCallingFrameId;
	
	protected final GeneralManager refGeneralManage;
	
	protected final ViewCanvasManager refViewCanvasManager;
	
	protected String sHeaderText;
	
	/**
	 * Does not set the reference to the parent JComponent.
	 */
	public CmdWindowNewInternalFrame( final GeneralManager refGeneralManage,
			final int iCallingFrameId,
			final String sHeaderText ) {
		
		this.refGeneralManage = refGeneralManage;
		this.iCallingFrameId = iCallingFrameId;		
		this.refViewCanvasManager = 
			refGeneralManage.getSingelton().getViewCanvasManager();
		this.sHeaderText = sHeaderText;
	}


//	/**
//	 * Get the reference to the new created internal frame after calling doCommand() internal.
//	 * 
//	 * @see cerberus.command.window.CmdWindowNewInternalFrame#getCurrentViewCanvas()
//	 * @see cerberus.command.window.CmdWindowNewInternalFrame#setTargetFrameId(String)
//	 * 
//	 * @return new created ViewCanvas object 
//	 */
//	public DInternalFrame doCommand_getDInternalFrame() {
//		this.doCommand();
//		return refNewDInternalFrame;
//	}
//	
//	/**
//	 * Get the last ViewCanvas created with doCommand().
//	 * 
//	 * @see cerberus.command.window.CmdWindowNewInternalFrame#doCommand_getViewCanvas()
//	 * @see cerberus.command.window.CmdWindowNewInternalFrame#setTargetFrameId(String)
//	 * 
//	 * @return last ViewCanvas created by doCommand() 
//	 */
//	public DInternalFrame getCurrentDInternalFrame() {
//		return refNewDInternalFrame;
//	}
	
	public void setHeaderText( final String sSetHeaderText ) {
		this.sHeaderText = sSetHeaderText;
	}
	/**
	 * Reset the TargetFrameId for the next doCommand().
	 * 
	 * @see cerberus.command.window.CmdWindowNewInternalFrame#getCurrentViewCanvas()
	 * @see cerberus.command.window.CmdWindowNewInternalFrame#doCommand_getViewCanvas()
	 * 
	 * @param sTargetFrameId TargetFramId of the parent frame of the internal frame to be created
	 */
	public void setTargetFrameId( final int iCallingFrameId ) {
		this.iCallingFrameId = iCallingFrameId;
	}
	
	/* (non-Javadoc)
	 * @see cerberus.command.CommandInterface#doCommand()
	 */
	public void doCommand() throws PrometheusCommandException {
		
//		WorkspaceSwingFrame refCallingFrame =
//			this.refViewCanvasManager.getItemWorkspace( iCallingFrameId );
//		
//		refNewDInternalFrame = 
//			refGeneralManage.getSingelton().getViewCanvasManager().createNewInternalFrame(
//					refCallingFrame.getTargetFrameId() , sHeaderText );
	}

	/* (non-Javadoc)
	 * @see cerberus.command.CommandInterface#undoCommand()
	 */
	public void undoCommand() throws PrometheusCommandException {
		
	}

	/* (non-Javadoc)
	 * @see cerberus.command.CommandInterface#getCommandType()
	 */
	public CommandType getCommandType() throws PrometheusCommandException {
		return CommandType.WINDOW_IFRAME_NEW_INTERNAL_FRAME;
	}

}
