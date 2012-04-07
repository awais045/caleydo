package org.caleydo.core.event.view.tablebased;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.event.AEvent;

/**
 * Event that signals that the spacing between the axis should be redataTable.
 * 
 * @author Alexander Lex
 */
@XmlRootElement
@XmlType
public class ResetAxisSpacingEvent
	extends AEvent {

	@Override
	public boolean checkIntegrity() {
		return true;
	}

}
