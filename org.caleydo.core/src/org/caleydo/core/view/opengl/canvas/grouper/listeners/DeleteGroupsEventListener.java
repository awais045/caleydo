package org.caleydo.core.view.opengl.canvas.grouper.listeners;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.view.grouper.DeleteGroupsEvent;
import org.caleydo.core.view.opengl.canvas.grouper.GLGrouper;

public class DeleteGroupsEventListener
	extends AEventListener<GLGrouper> {

	@Override
	public void handleEvent(AEvent event) {
		DeleteGroupsEvent deleteGroupsEvent = null;
		if (event instanceof DeleteGroupsEvent) {
			deleteGroupsEvent = (DeleteGroupsEvent) event;
			handler.deleteGroups(deleteGroupsEvent.getGroupsToDelete());
		}
	}

}
