package org.caleydo.core.view.opengl.canvas.listener;

import org.caleydo.core.data.selection.delta.ISelectionDelta;
import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.IEventListener;
import org.caleydo.core.manager.event.view.storagebased.SelectionUpdateEvent;

/**
 * Listener for selection update events.
 * This listener gets the payload from a SelectionUpdateEvent and calls 
 * a related {@link ISelectionUpdateHandler}. 
 * @author Werner Puff
 */
public class SelectionUpdateListener 
	implements IEventListener {

	/** {@link ISelectionUpdateHandler} this listener is related to */
	protected ISelectionUpdateHandler handler = null;

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof SelectionUpdateEvent) {
			SelectionUpdateEvent selectioUpdateEvent = (SelectionUpdateEvent) event; 
			ISelectionDelta delta = selectioUpdateEvent.getSelectionDelta();
			boolean scrollToSelection = selectioUpdateEvent.isScrollToSelection();
			handler.handleSelectionUpdate(delta, scrollToSelection, null);
		}
	}

	public ISelectionUpdateHandler getHandler() {
		return handler;
	}

	public void setHandler(ISelectionUpdateHandler view) {
		this.handler = view;
	}
	
}
