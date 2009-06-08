package org.caleydo.core.view.opengl.canvas.storagebased.listener;

import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.selection.SelectionCommand;
import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.view.TriggerPropagationCommandEvent;
import org.caleydo.core.view.opengl.canvas.storagebased.GLHeatMap;

/**
 * Listener for {@link TriggerPropagationCommandEvent}s related to {@link GLHeatMap} views
 * @author Werner Puff
 */
public class TriggerPropagationCommandListener
	extends AEventListener<GLHeatMap> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof TriggerPropagationCommandEvent) {
			TriggerPropagationCommandEvent triggerSelectionCommandEvent = (TriggerPropagationCommandEvent) event; 
			EIDType type = triggerSelectionCommandEvent.getType();
			SelectionCommand selectionCommand = triggerSelectionCommandEvent.getSelectionCommand();
			switch (type) {
				case DAVID:
				case REFSEQ_MRNA_INT:
				case EXPRESSION_INDEX:
					handler.handleContentTriggerSelectionCommand(type, selectionCommand);
					break;
				case EXPERIMENT_INDEX:
					handler.handleStorageTriggerSelectionCommand(type, selectionCommand);
					break;
			}
		}
	}
	
}
