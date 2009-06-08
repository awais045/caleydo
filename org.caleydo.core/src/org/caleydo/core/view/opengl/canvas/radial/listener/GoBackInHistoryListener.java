package org.caleydo.core.view.opengl.canvas.radial.listener;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.view.radial.GoBackInHistoryEvent;
import org.caleydo.core.view.opengl.canvas.radial.GLRadialHierarchy;

public class GoBackInHistoryListener
	extends AEventListener<GLRadialHierarchy> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof GoBackInHistoryEvent) {
			handler.goBackInHistory();
		}

	}

}
