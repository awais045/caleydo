package org.caleydo.view.datawindows;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.rcp.view.rcp.ARcpGLViewPart;
import org.caleydo.view.heatmap.hierarchical.SerializedHierarchicalHeatMapView;
import org.eclipse.swt.widgets.Composite;

public class RcpGLDataWindowsView extends ARcpGLViewPart {

	/**
	 * Constructor.
	 */
	public RcpGLDataWindowsView() {
		super();
		
		try {
			viewContext = JAXBContext
					.newInstance(SerializedDataWindowsView.class);
		} catch (JAXBException ex) {
			throw new RuntimeException("Could not create JAXBContext", ex);
		}
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);

		createGLCanvas();
		createGLView(initSerializedView, glCanvas.getID());
	}

	@Override
	public ASerializedView createDefaultSerializedView() {

		SerializedDataWindowsView serializedView = new SerializedDataWindowsView();

		dataDomainType = determineDataDomain(serializedView);
		serializedView.setDataDomainType(dataDomainType);
		return serializedView;
	}

	@Override
	public String getViewGUIID() {
		return GLDataWindows.VIEW_ID;
	}

}