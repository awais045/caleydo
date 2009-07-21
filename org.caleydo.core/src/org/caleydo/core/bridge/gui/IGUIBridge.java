package org.caleydo.core.bridge.gui;

import org.caleydo.core.view.serialize.ASerializedView;
import org.eclipse.swt.widgets.Display;

public interface IGUIBridge {
	public void closeApplication();

	// public void setActiveGLView();

	// public void setActiveGLSubView(AGLEventListener parentGLEventListener,
	// AGLEventListener subGLEventListener);

	public void setShortInfo(String sMessage);

	public void setFileNameCurrentDataSet(String sFileName);

	public String getFileNameCurrentDataSet();

	public Display getDisplay();
	
	/**
	 * Creates a GUI-based view from its serialized form
	 * @param serializedView serialized form of the view to create 
	 */
	public void createView(ASerializedView serializedView);
}
