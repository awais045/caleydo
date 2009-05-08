package org.caleydo.core.view.opengl.canvas.remote.listener;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.view.group.MergeGroupsEvent;
import org.caleydo.core.view.opengl.canvas.remote.receiver.IGroupsMergingActionReceiver;

public class GroupMergingActionListener
	extends AEventListener<IGroupsMergingActionReceiver> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof MergeGroupsEvent) {
			MergeGroupsEvent mergeGroupsEvent = (MergeGroupsEvent) event;
			handler.handleMergeGroups(mergeGroupsEvent.isGeneGroup());
		}
	}
}
