package org.caleydo.core.view.opengl.canvas.bookmarking;

import java.awt.Font;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;

import javax.media.opengl.GL;

import org.caleydo.core.data.mapping.EIDCategory;
import org.caleydo.core.data.selection.ESelectionType;
import org.caleydo.core.data.selection.EVAOperation;
import org.caleydo.core.data.selection.SelectionCommand;
import org.caleydo.core.data.selection.delta.ISelectionDelta;
import org.caleydo.core.manager.event.data.BookmarkEvent;
import org.caleydo.core.manager.event.data.RemoveBookmarkEvent;
import org.caleydo.core.manager.event.view.SelectionCommandEvent;
import org.caleydo.core.manager.event.view.storagebased.SelectionUpdateEvent;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.util.mapping.color.ColorMapping;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLEventListener;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.core.view.opengl.canvas.listener.ISelectionCommandHandler;
import org.caleydo.core.view.opengl.canvas.listener.ISelectionUpdateHandler;
import org.caleydo.core.view.opengl.canvas.listener.SelectionCommandListener;
import org.caleydo.core.view.opengl.canvas.listener.SelectionUpdateListener;
import org.caleydo.core.view.opengl.canvas.remote.IGLRemoteRenderingView;
import org.caleydo.core.view.opengl.canvas.storagebased.GLHeatMap;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.util.GLHelperFunctions;
import org.caleydo.core.view.opengl.util.overlay.contextmenu.ContextMenu;
import org.caleydo.core.view.opengl.util.overlay.infoarea.GLInfoAreaManager;

import com.sun.opengl.util.j2d.TextRenderer;

/**
 * The list heat map that shows elements on the right of a view that have been selected. It is registered to
 * special listeners that are triggered in such a event. Other than that it is equivalent to the
 * {@link GLHeatMap}
 * 
 * @author Alexander Lex
 */
public class GLBookmarkManager
	extends AGLEventListener
	implements ISelectionUpdateHandler, ISelectionCommandHandler {

	private ColorMapping colorMapper;

	protected BookmarkRenderStyle renderStyle;

	/** A hash map that associated the Category with the container */
	private EnumMap<EIDCategory, ABookmarkContainer> hashCategoryToBookmarkContainer;
	/** A list of bookmark containers, to preserve the ordering */
	private ArrayList<ABookmarkContainer> bookmarkContainers;

	private BookmarkListener bookmarkListener;
	private SelectionUpdateListener selectionUpdateListener;
	private SelectionCommandListener selectionCommandListener;

	private TextRenderer textRenderer;

	private PickingIDManager pickingIDManager;

	private RemoveBookmarkListener removeBookmarkListener;

	class PickingIDManager {
		/**
		 * A hash map that hashes the picking ID of an element to the BookmarkContainer and the id internal to
		 * the bookmark container
		 */
		private HashMap<Integer, Pair<EIDCategory, Integer>> pickingIDToBookmarkContainer;
		private int idCount = 0;

		private PickingIDManager() {
			pickingIDToBookmarkContainer = new HashMap<Integer, Pair<EIDCategory, Integer>>();
		}

		public int getPickingID(ABookmarkContainer container, int privateID) {

			int pickingID = pickingManager.getPickingID(iUniqueID, EPickingType.BOOKMARK_ELEMENT, idCount);
			pickingIDToBookmarkContainer.put(idCount++, new Pair<EIDCategory, Integer>(container
				.getCategory(), privateID));
			return pickingID;
		}

		private Pair<EIDCategory, Integer> getPrivateID(int iExternalID) {
			return pickingIDToBookmarkContainer.get(iExternalID);
		}

		private void reset() {
			idCount = 0;
			pickingIDToBookmarkContainer = new HashMap<Integer, Pair<EIDCategory, Integer>>();
		}
	}

	/**
	 * Constructor.
	 * 
	 * @param glCanvas
	 * @param label
	 * @param viewFrustum
	 */
	public GLBookmarkManager(GLCaleydoCanvas glCanvas, String label, IViewFrustum viewFrustum) {

		super(glCanvas, label, viewFrustum, false);

		renderStyle = new BookmarkRenderStyle(viewFrustum);

		bookmarkContainers = new ArrayList<ABookmarkContainer>();
		hashCategoryToBookmarkContainer = new EnumMap<EIDCategory, ABookmarkContainer>(EIDCategory.class);

		textRenderer = new TextRenderer(new Font("Arial", Font.PLAIN, 24), false);

		pickingIDManager = new PickingIDManager();

		GeneBookmarkContainer geneContainer = new GeneBookmarkContainer(this);
		hashCategoryToBookmarkContainer.put(EIDCategory.GENE, geneContainer);
		bookmarkContainers.add(geneContainer);

		ExperimentBookmarkContainer experimentContainer = new ExperimentBookmarkContainer(this);
		hashCategoryToBookmarkContainer.put(EIDCategory.EXPERIMENT, experimentContainer);
		bookmarkContainers.add(experimentContainer);

	}

	@Override
	public void registerEventListeners() {
		super.registerEventListeners();

		bookmarkListener = new BookmarkListener();
		bookmarkListener.setHandler(this);
		eventPublisher.addListener(BookmarkEvent.class, bookmarkListener);
		
		removeBookmarkListener = new RemoveBookmarkListener();
		removeBookmarkListener.setHandler(this);
		eventPublisher.addListener(RemoveBookmarkEvent.class, removeBookmarkListener);

		selectionUpdateListener = new SelectionUpdateListener();
		selectionUpdateListener.setHandler(this);
		eventPublisher.addListener(SelectionUpdateEvent.class, selectionUpdateListener);

		selectionCommandListener = new SelectionCommandListener();
		selectionCommandListener.setHandler(this);
		eventPublisher.addListener(SelectionCommandEvent.class, selectionCommandListener);

	}

	@Override
	public void unregisterEventListeners() {

		super.unregisterEventListeners();

		if (bookmarkListener != null) {
			eventPublisher.removeListener(bookmarkListener);
			bookmarkListener = null;
		}
		
		if(removeBookmarkListener != null)
		{
			eventPublisher.removeListener(removeBookmarkListener);
			removeBookmarkListener = null;
		}

		if (selectionUpdateListener != null) {
			eventPublisher.removeListener(selectionUpdateListener);
			selectionUpdateListener = null;
		}

		if (selectionCommandListener != null) {
			eventPublisher.removeListener(selectionCommandListener);
			selectionCommandListener = null;
		}
	}

	@Override
	public void display(GL gl) {

		processEvents();

//		GLHelperFunctions.drawViewFrustum(gl, viewFrustum);

		float currentHeight = viewFrustum.getHeight() - BookmarkRenderStyle.TOP_SPACING;
		for (ABookmarkContainer container : bookmarkContainers) {
			container.getDimensions().setOrigins(0.0f, currentHeight);
			currentHeight -= container.getDimensions().getHeight();
			container.render(gl);

		}
	}

	@Override
	protected void displayLocal(GL gl) {
		pickingManager.handlePicking(this, gl);
		display(gl);
		checkForHits(gl);
		pickingIDManager.reset();
	}

	@Override
	public void displayRemote(GL gl) {
		display(gl);
		checkForHits(gl);
		pickingIDManager.reset();
	}

	@Override
	public String getDetailedInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getShortInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void handleEvents(EPickingType ePickingType, EPickingMode ePickingMode, int iExternalID,
		Pick pick) {
		switch (ePickingType) {
			case BOOKMARK_ELEMENT:
				Pair<EIDCategory, Integer> pair = pickingIDManager.getPrivateID(iExternalID);
				hashCategoryToBookmarkContainer.get(pair.getFirst()).handleEvents(ePickingType, ePickingMode,
					pair.getSecond(), pick);
		}
	}

	/**
	 * @param <IDDataType>
	 * @param event
	 */
	public <IDDataType> void handleNewBookmarkEvent(BookmarkEvent<IDDataType> event) {

		ABookmarkContainer container = hashCategoryToBookmarkContainer.get(event.getIDType().getCategory());
		if (container == null)
			throw new IllegalStateException("Can not handle bookmarks of type "
				+ event.getIDType().getCategory());

		container.handleNewBookmarkEvent(event);
	}

	public <IDDataType> void handleRemoveBookmarkEvent(RemoveBookmarkEvent<IDDataType> event) {
		ABookmarkContainer container = hashCategoryToBookmarkContainer.get(event.getIDType().getCategory());
		if (container == null)
			throw new IllegalStateException("Can not handle bookmarks of type "
				+ event.getIDType().getCategory());

		container.handleRemoveBookmarkEvent(event);
	}

	@Override
	public void init(GL gl) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void initLocal(GL gl) {
		init(gl);

	}

	@Override
	public void initRemote(GL gl, AGLEventListener glParentView, GLMouseListener glMouseListener,
		IGLRemoteRenderingView remoteRenderingGLCanvas, GLInfoAreaManager infoAreaManager) {
		this.remoteRenderingGLView = remoteRenderingGLCanvas;
		init(gl);

	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void broadcastElements(EVAOperation type) {
		// TODO Auto-generated method stub

	}

	@Override
	public void clearAllSelections() {
		// TODO Auto-generated method stub

	}

	@Override
	public int getNumberOfSelections(ESelectionType eSelectionType) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void handleSelectionUpdate(ISelectionDelta selectionDelta, boolean scrollToSelection, String info) {
		// EIDCategory category = ;
		ABookmarkContainer container =
			hashCategoryToBookmarkContainer.get(selectionDelta.getIDType().getCategory());
		if (container != null)
			container.handleSelectionUpdate(selectionDelta);
	}

	@Override
	public void handleContentTriggerSelectionCommand(EIDCategory category, SelectionCommand selectionCommand) {
		ABookmarkContainer container = hashCategoryToBookmarkContainer.get(category);
		if (container != null)
			container.handleSelectionCommand(selectionCommand);
	}

	@Override
	public void handleStorageTriggerSelectionCommand(EIDCategory category, SelectionCommand selectionCommand) {
		// separation is not necessary here
		handleContentTriggerSelectionCommand(category, selectionCommand);
	}

	ContextMenu getContextMenu() {
		return contextMenu;
	}

	TextRenderer getTextRenderer() {
		return textRenderer;
	}

	PickingIDManager getPickingIDManager() {
		return pickingIDManager;
	}
}
