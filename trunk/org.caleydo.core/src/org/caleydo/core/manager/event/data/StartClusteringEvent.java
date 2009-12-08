package org.caleydo.core.manager.event.data;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.util.clusterer.ClusterState;

/**
 * Event that signals the start of a clustering algorithm. The parameters are specified in the ClusterState
 * parameter
 * 
 * @author Alexander Lex
 */
@XmlRootElement
@XmlType
public class StartClusteringEvent
	extends AEvent {

	private ClusterState ClusterState;

	/**
	 * default no-arg constructor
	 */
	public StartClusteringEvent() {
		// nothing to initialize here
	}

	public StartClusteringEvent(ClusterState state) {
		this.ClusterState = state;
	}

	public ClusterState getClusterState() {
		return ClusterState;
	}

	@Override
	public boolean checkIntegrity() {
		if (ClusterState == null)
			return false;
		return true;
	}

}
