package org.caleydo.view.grouper;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.datadomain.IDataDomainBasedView;
import org.caleydo.core.serialize.ASerializedTopLevelDataView;
import org.caleydo.core.view.ARcpGLViewPart;
import org.eclipse.swt.widgets.Composite;

public class RcpGLGrouperView extends ARcpGLViewPart {

	/**
	 * Constructor.
	 */
	public RcpGLGrouperView() {
		super();

		try {
			viewContext = JAXBContext.newInstance(SerializedGrouperView.class);
		} catch (JAXBException ex) {
			throw new RuntimeException("Could not create JAXBContext", ex);
		}

		isSupportView = true;
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);

		createGLCanvas();

		view = new GLGrouper(glCanvas, parentComposite, serializedView.getViewFrustum());
		initializeViewWithData();
		view.initFromSerializableRepresentation(serializedView);
		view.initialize();
		createPartControlGL();
	}

	@Override
	public void createDefaultSerializedView() {
		serializedView = new SerializedGrouperView();
		determineDataConfiguration(serializedView);
	}

	@Override
	public String getViewGUIID() {
		return GLGrouper.VIEW_TYPE;
	}
}