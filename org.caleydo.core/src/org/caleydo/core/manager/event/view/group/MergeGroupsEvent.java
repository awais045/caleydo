package org.caleydo.core.manager.event.view.group;

import org.caleydo.core.manager.event.AEvent;

/**
 * Event that signals the merging of two groups
 * 
 * TODO document
 * 
 * @author Bernhard Schlegl
 * @author Alexander Lex
 */
public class MergeGroupsEvent
	extends AEvent {

	private boolean bGeneGroup = false;

	public void setGeneExperimentFlag(boolean bGeneGroup) {
		this.bGeneGroup = bGeneGroup;
	}

	public boolean isGeneGroup() {
		return bGeneGroup;
	}
	
	@Override
	public boolean checkIntegrity() {
		// nothing to check
		return true;
	}

}
