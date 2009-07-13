package org.caleydo.core.view.opengl.canvas.storagebased.listener;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.view.storagebased.BookmarkEvent;
import org.caleydo.core.view.opengl.canvas.storagebased.GLParallelCoordinates;

/**
 * Listener that reacts on angular brushing events for PCs.
 * 
 * @author Alexander Lex
 */
public class BookmarkListener
	extends AEventListener<GLParallelCoordinates> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof BookmarkEvent) {
			handler.bookmarkElements();
		}
	}

}
