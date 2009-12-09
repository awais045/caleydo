package org.caleydo.core.view.opengl.canvas.storagebased.scatterplot.listener;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.view.storagebased.YAxisSelectorEvent;
import org.caleydo.core.view.opengl.canvas.storagebased.scatterplot.GLScatterplot;

/**
 * Listener that reacts events for setting the max. displayed hierarchy depth in RadialHierarchy.
 * 
 * @author Christian
 */

public class YAxisSelectorListener 
	extends AEventListener<GLScatterplot> {

		@Override
		public void handleEvent(AEvent event) {
			if (event instanceof YAxisSelectorEvent) {
				handler.setYAxis(((YAxisSelectorEvent) event)
					.getSelectedAxis());

			
			}
		}
}



