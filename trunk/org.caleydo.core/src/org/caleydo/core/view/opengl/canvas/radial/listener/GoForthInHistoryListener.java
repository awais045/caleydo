package org.caleydo.core.view.opengl.canvas.radial.listener;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.view.radial.GoForthInHistoryEvent;
import org.caleydo.core.view.opengl.canvas.radial.GLRadialHierarchy;

/**
 * Listener that reacts on go forth in history events for RadialHierarchy.
 * 
 * @author Christian Partl
 */
public class GoForthInHistoryListener
	extends AEventListener<GLRadialHierarchy> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof GoForthInHistoryEvent) {
			handler.goForthInHistory();
		}
	}

}
