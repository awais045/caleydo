/*
 * Project: GenView
 * 
 * Author: Marc Streit
 * 
 * Creation date: 26-07-2006
 *  
 */

package cerberus.view.gui.swt.widget;

import cerberus.view.gui.swt.widget.ASWTWidget;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

/**
 * Class takes a composite in the constructor and
 * embedds an AWT Frame in it.
 * The Frame can be retrieved over the getEmbeddedFrame()
 * method.
 * 
 * @author Marc Streit
 */
public class SWTEmbeddedGraphWidget extends ASWTWidget 
{
	/**
	 * Embedded AWT Frame.
	 */
	protected final java.awt.Frame refEmbeddedFrame;
	
	/**
	 * Constructor that takes the composite in which it should 
	 * place the content and creates an embedded AWT frame.
	 * 
	 * @param Composite Reference to the composite 
	 * that is supposed to be filled.
	 */
	public SWTEmbeddedGraphWidget(Composite refParentComposite)
	{
		super(refParentComposite);
		
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		
		// FIXME how to make the widget full screen?
		gridData.heightHint = 2000;
		gridData.widthHint = 2000;
		
		Composite composite = new Composite(refParentComposite, SWT.EMBEDDED);
		
		composite.setLayoutData(gridData);
		
		refEmbeddedFrame = SWT_AWT.new_Frame(composite);
	}

	/**
	 * Get the embedded frame.
	 * 
	 * @return The embedded AWT Frame.
	 */
	public final java.awt.Frame getEmbeddedFrame()
	{
		return refEmbeddedFrame;
	}
}
