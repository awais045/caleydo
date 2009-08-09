package org.caleydo.rcp.view.opengl;

import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.canvas.storagebased.SerializedParallelCoordinatesView;
import org.eclipse.swt.widgets.Composite;

public class ClinicalGLParCoordsView
	extends AGLViewPart {

	public static final String ID = SerializedParallelCoordinatesView.GUI_ID;

	/**
	 * Constructor.
	 */
	public ClinicalGLParCoordsView() {
		super();
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);

		createGLCanvas();
		createGLEventListener(initSerializedView, glCanvas.getID());
	}

	@Override
	public ASerializedView createDefaultSerializedView() {
		SerializedParallelCoordinatesView serializedView = new SerializedParallelCoordinatesView();
		return serializedView;
	}

	@Override
	public String getViewGUIID() {
		return ID;
	}

}