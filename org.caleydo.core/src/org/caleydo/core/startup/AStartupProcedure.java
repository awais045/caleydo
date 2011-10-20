package org.caleydo.core.startup;

import org.caleydo.core.data.datadomain.ADataDomain;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.RCPViewManager;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.IFolderLayout;
import org.osgi.framework.BundleException;

/**
 * Abstract startup procedure. Handling of view initialization and application init data.
 * 
 * @author Marc Streit
 */
public abstract class AStartupProcedure {

	protected ApplicationInitData appInitData;

	protected ADataDomain dataDomain;

	public void init(ApplicationInitData appInitData) {
		this.appInitData = appInitData;
		initializeStartViews();
	}

	/**
	 * Initialization stuff that has to be done before the workbench opens (e.g., copying the workbench data
	 * from a serialized project).
	 */
	public void initPreWorkbenchOpen() {

	}

	public void execute() {
		loadPathways();

		// Create RCP view manager
		RCPViewManager.get();
	}

	public abstract void addDefaultStartViews();

	/**
	 * Parses through the list of start-views to initialize them by creating default serialized
	 * representations of them.
	 */
	public void initializeStartViews() {
		// Create view list dynamically when not specified via the command line

		addDefaultStartViews();

		for (Pair<String, String> viewWithDataDomain : appInitData.getAppArgumentStartViewWithDataDomain()) {

			// ASerializedView view =
			// GeneralManager.get().getViewGLCanvasManager().getViewCreator(viewWithDataDomain.getFirst())
			// .createSerializedView();
			//
			// view.setDataDomainType(viewWithDataDomain.getSecond());
			appInitData.getInitializedStartViews().add(viewWithDataDomain.getFirst());
		}
	}

	public void openRCPViews(IFolderLayout layout) {

		for (String startViewID : appInitData.getInitializedStartViews()) {
			layout.addView(startViewID);
		}
	}

	private void loadPathways() {
		if (!appInitData.isLoadPathways())
			return;

		try {
			Platform.getBundle("org.caleydo.datadomain.pathway").start();
		}
		catch (BundleException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
