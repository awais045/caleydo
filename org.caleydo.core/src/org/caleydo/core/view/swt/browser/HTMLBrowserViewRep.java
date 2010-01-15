package org.caleydo.core.view.swt.browser;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.event.view.browser.ChangeURLEvent;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.serialize.SerializedDummyView;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.core.view.swt.ASWTView;
import org.caleydo.core.view.swt.ISWTView;
import org.caleydo.data.loader.ResourceLoader;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

/**
 * Simple HTML browser.
 * 
 * @author Marc Streit
 */
public class HTMLBrowserViewRep
	extends ASWTView
	implements ISWTView {

	public final static String CALEYDO_HOME = "http://www.caleydo.org";

	protected Browser browser;

	/**
	 * Subclasses can add widgets to this composite which appear before the browser icons.
	 */
	protected Composite subContributionComposite;

	protected String url = CALEYDO_HOME + "/help/user_interface.html";

	protected Text textURL;

	// protected IDExtractionLocationListener idExtractionLocationListener;

	private ToolItem goButton;
	private ToolItem homeButton;
	private ToolItem backButton;
	private ToolItem stopButton;

	private ChangeURLListener changeURLListener;

	/**
	 * Constructor.
	 */
	public HTMLBrowserViewRep(final int iParentContainerId, final String sLabel) {
		super(iParentContainerId, sLabel, GeneralManager.get().getIDManager().createID(
			EManagedObjectType.VIEW_SWT_BROWSER_GENERAL));
		init();
	}

	/**
	 * Constructor.
	 */
	public HTMLBrowserViewRep(final int iParentContainerId, final String sLabel, int iViewID) {
		super(iParentContainerId, sLabel, iViewID);
		init();
	}

	/**
	 * Basic initialization, used only within constructors.
	 * 
	 * @param parentComposite
	 */
	private void init() {
		generalManager = GeneralManager.get();
		eventPublisher = generalManager.getEventPublisher();
	}

	@Override
	public void initViewSWTComposite(Composite parentComposite) {
		Composite composite = new Composite(parentComposite, SWT.NONE);
		GridLayout layout = new GridLayout(1, false);
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

		Composite browserBarComposite = new Composite(composite, SWT.NONE);
		browserBarComposite.setLayout(new GridLayout(3, false));

		subContributionComposite = new Composite(browserBarComposite, SWT.NONE);
		subContributionComposite.setLayout(new FillLayout());

		ToolBar toolbar = new ToolBar(browserBarComposite, SWT.NONE);
		GridData data = new GridData(GridData.FILL_VERTICAL);
		// toolbar.setLayoutData(data);

		ResourceLoader resourceLoader = GeneralManager.get().getResourceLoader();

		goButton = new ToolItem(toolbar, SWT.PUSH);
		goButton.setImage(resourceLoader.getImage(parentComposite.getDisplay(),
			EIconTextures.BROWSER_REFRESH_IMAGE.getFileName()));

		backButton = new ToolItem(toolbar, SWT.PUSH);
		backButton.setImage(resourceLoader.getImage(parentComposite.getDisplay(),
			EIconTextures.BROWSER_BACK_IMAGE.getFileName()));

		stopButton = new ToolItem(toolbar, SWT.PUSH);
		stopButton.setImage(resourceLoader.getImage(parentComposite.getDisplay(),
			EIconTextures.BROWSER_STOP_IMAGE.getFileName()));

		homeButton = new ToolItem(toolbar, SWT.PUSH);
		homeButton.setImage(resourceLoader.getImage(parentComposite.getDisplay(),
			EIconTextures.BROWSER_HOME_IMAGE.getFileName()));

		textURL = new Text(browserBarComposite, SWT.BORDER);

		if (checkInternetConnection()) {
			textURL.setText(url);
		}

		data = new GridData(GridData.FILL_HORIZONTAL);
		data.heightHint = 15;
		textURL.setLayoutData(data);

		data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		data.grabExcessHorizontalSpace = true;
		data.heightHint = 45;
		browserBarComposite.setLayoutData(data);

		Listener listener = new Listener() {
			public void handleEvent(Event event) {
				if (!checkInternetConnection())
					return;

				ToolItem item = (ToolItem) event.widget;
				if (item.equals(backButton)) {
					browser.back();
				}
				else if (item.equals(stopButton)) {
					browser.stop();
				}
				else if (item.equals(goButton)) {
					url = textURL.getText();
				}
				else if (item.equals(homeButton)) {
					url = "www.caleydo.org";
					textURL.setText(CALEYDO_HOME);
					browser.setUrl(url);
				}
			}
		};

		goButton.addListener(SWT.Selection, listener);
		backButton.addListener(SWT.Selection, listener);
		stopButton.addListener(SWT.Selection, listener);
		homeButton.addListener(SWT.Selection, listener);

		textURL.addListener(SWT.DefaultSelection, new Listener() {
			public void handleEvent(Event e) {
				url = textURL.getText();
				drawView();
			}
		});

		parentComposite.getDisplay().addFilter(SWT.FocusIn, new Listener() {

			public void handleEvent(Event event) {

				if (!event.widget.getClass().equals(this.getClass()))
					return;
			}
		});

		browser = new Browser(composite, SWT.BORDER);

		// Mechanism prevents the browser to steal the focus from other views
		browser.addProgressListener(new ProgressListener() {

			@Override
			public void completed(ProgressEvent event) {
				// Give the focus back to active view
				if (GeneralManager.get().getViewGLCanvasManager().getActiveSWTView() != null) {
					GeneralManager.get().getViewGLCanvasManager().getActiveSWTView().setFocus();
				}
			}

			@Override
			public void changed(ProgressEvent event) {
				// Give the focus back to active view
				if (GeneralManager.get().getViewGLCanvasManager().getActiveSWTView() != null) {
					GeneralManager.get().getViewGLCanvasManager().getActiveSWTView().setFocus();
				}
			}
		});

		// idExtractionLocationListener = new IDExtractionLocationListener(browser, iUniqueID, -1);
		// browser.addLocationListener(idExtractionLocationListener);

		data = new GridData();
		browser.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
	}

	@Override
	public void drawView() {
		generalManager.getLogger().log(new Status(IStatus.INFO, IGeneralManager.PLUGIN_ID, "Load " + url));

		try {
			parentComposite.getDisplay().asyncExec(new Runnable() {

				public void run() {
					if (!checkInternetConnection())
						return;

					textURL.setText(url);
					browser.setUrl(url);
					// browser.refresh();
				}
			});
		}
		catch (SWTException swte) {
			generalManager.getLogger().log(
				new Status(IStatus.INFO, IGeneralManager.PLUGIN_ID, "Error while loading " + url, swte));
		}
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String sUrl) {
		this.url = sUrl;

		// idExtractionLocationListener.updateSkipNextChangeEvent(true);
		drawView();
	}

	protected boolean checkInternetConnection() {
		// Check internet connection
		try {
			InetAddress.getByName("www.google.at");

		}
		catch (UnknownHostException e) {
			textURL.setText("No internet connection available!");
			return false;
		}

		return true;
	}

	@Override
	public void registerEventListeners() {
		super.registerEventListeners();
		changeURLListener = new ChangeURLListener();
		changeURLListener.setHandler(this);
		eventPublisher.addListener(ChangeURLEvent.class, changeURLListener);

	}

	/**
	 * Registers the listeners for this view to the event system. To release the allocated resources
	 * unregisterEventListeners() has to be called.
	 */
	@Override
	public void unregisterEventListeners() {
		super.unregisterEventListeners();
		if (changeURLListener != null) {
			eventPublisher.removeListener(ChangeURLEvent.class, changeURLListener);
			changeURLListener = null;
		}
	}

	/**
	 * Unregisters the listeners for this view from the event system. To release the allocated resources
	 * unregisterEventListenrs() has to be called.
	 */
	@Override
	public ASerializedView getSerializableRepresentation() {
		SerializedDummyView serializedForm = new SerializedDummyView(dataDomain);
		serializedForm.setViewID(this.getID());
		return serializedForm;
	}

	@Override
	public void initFromSerializableRepresentation(ASerializedView ser) {
		// this implementation does not initialize anything yet
	}
}
