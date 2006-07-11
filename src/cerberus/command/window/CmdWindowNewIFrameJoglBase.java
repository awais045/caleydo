/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.command.window;

import cerberus.manager.GeneralManager;
import cerberus.command.CommandInterface;
import cerberus.command.base.CommandAbstractBase;
//import cerberus.net.dwt.swing.jogl.DSwingJoglCanvas;
//import cerberus.net.dwt.swing.jogl.listener.GLEventForwardListener;
import cerberus.util.exception.PrometheusCommandException;

/**
 * Base class for Jogl classes provinding a GL rendering contexst inside an internal frame.
 * 
 * @author Michael Kalkusch
 *
 */
public abstract class CmdWindowNewIFrameJoglBase 
extends CommandAbstractBase
implements CommandInterface {

	/**
	 * Reference to the sub command to create a new Jogl canvas.
	 * Is null if an existing frame is used instead of creating a new one
	 */
	private final CmdWindowNewIFrameJoglCanvas refCmdWindowNewIFrameJoglCanvas;
		
	/**
	 * Define if a new frame shall be created each time the command is executed or
	 * if an existing frame shall be used!
	 */
	private final boolean bEnableCreationOfNewIFrame;
	
	/**
	 * Reference to singelton.
	 */
	protected final GeneralManager refGeneralManager;

	/**
	 * Current Jogl canvas.
	 * If initDSwingJoglCanvas == null this varaibel holds the reference 
	 * to the current GL canvas after calling
	 * methode prometheus.command.window.CmdWindowNewIFrameJoglBase#doCommand_IFrame() 
	 * which has to be done befor accessing the variable inside a doCommand() statement.
	 * 
	 * See prometheus.command.window.CmdWindowNewIFrameJoglHistogram#doCommand() as
	 * example for proper use of this abstract class.
	 * 
	 * Do not change this variable, it is suppose do be read only.
	 * 
	 * @see prometheus.command.window.CmdWindowNewIFrameJoglBase#doCommand_IFrame()
	 * @see prometheus.command.window.CmdWindowNewIFrameJoglHistogram#doCommand()
	 */
//	protected DSwingJoglCanvas initDSwingJoglCanvas;

	/**
	 * Set the reference to the parent JComponent.
	 * 
	 * @param refGeneralManager reference to singelton
	 * @param iCallingFrameId Id of the calling frame
	 * @param refGLEventListener listener for GLEvents or null if listener shall be created
	 * @param initDSwingJoglCanvas reference to existing GLcanvas or null if canvas shall be created
	 */
	protected CmdWindowNewIFrameJoglBase( final GeneralManager refGeneralManager,
			final int iCallingFrameId,
//			final GLEventForwardListener refGLEventListener,
//			final DSwingJoglCanvas initDSwingJoglCanvas,
			final String sHeaderText ) {

		//super(iSetCmdCollectionId);
		
		assert refGeneralManager != null :"Can not handle null-pointer to DSwingJoglCanvas";
		
		this.refGeneralManager = refGeneralManager;
		
		bEnableCreationOfNewIFrame = true;
				
		refCmdWindowNewIFrameJoglCanvas = null;
		
//		this.initDSwingJoglCanvas = initDSwingJoglCanvas;
//		
//		if ( initDSwingJoglCanvas == null ) {
//			bEnableCreationOfNewIFrame = true;
//			refCmdWindowNewIFrameJoglCanvas = 
//				new CmdWindowNewIFrameJoglCanvas(refGeneralManager, 
//						refGLEventListener, 
//						iCallingFrameId,
//						sHeaderText );
//		}
//		else {
//			bEnableCreationOfNewIFrame = false;
//			
//			/// need to fit final statement!
//			refCmdWindowNewIFrameJoglCanvas = null;
//		}
	}

	/* (non-Javadoc)
	 * @see prometheus.command.CommandInterface#doCommand()
	 */
	protected void doCommand_IFrame() throws PrometheusCommandException {
		
//		if ( bEnableCreationOfNewIFrame ) {
//			refCmdWindowNewIFrameJoglCanvas.doCommand();
//			initDSwingJoglCanvas = refCmdWindowNewIFrameJoglCanvas.getGLCanvas();
//		}
	}

}
