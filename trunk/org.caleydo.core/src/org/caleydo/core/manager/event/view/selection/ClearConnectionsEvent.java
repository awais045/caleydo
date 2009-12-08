package org.caleydo.core.manager.event.view.selection;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.manager.event.AEvent;

/**
 * Event to signal that all selections for a specific {@link EIDType} should be deleted.
 * 
 * @author Werner Puff
 */
@XmlRootElement
@XmlType
public class ClearConnectionsEvent
	extends AEvent {

	/** selection type to be deleted */
	private EIDType idType;

	@Override
	public boolean checkIntegrity() {
		if (idType == null) {
			return false;
		}
		return true;
	}

	public EIDType getIdType() {
		return idType;
	}

	public void setIdType(EIDType idType) {
		this.idType = idType;
	}

}
