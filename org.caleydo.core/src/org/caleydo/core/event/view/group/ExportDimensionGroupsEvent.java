package org.caleydo.core.event.view.group;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.event.AEvent;

/**
 * Event that signals the exporting of groups. Depending on a boolean gene or experiment groupInfo has to be
 * used.
 * 
 * @author Bernhard Schlegl
 */
@XmlRootElement
@XmlType
public class ExportDimensionGroupsEvent
	extends AEvent {

	@Override
	public boolean checkIntegrity() {
		// nothing to check
		return true;
	}

}
