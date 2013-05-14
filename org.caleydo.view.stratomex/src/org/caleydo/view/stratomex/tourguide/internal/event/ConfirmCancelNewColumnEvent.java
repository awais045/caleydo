/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.view.stratomex.tourguide.internal.event;

import org.caleydo.core.event.ADirectedEvent;

/**
 * @author Samuel Gratzl
 *
 */
public class ConfirmCancelNewColumnEvent extends ADirectedEvent {

	private final boolean isConfirm;
	private final int objectID;

	/**
	 * @param b
	 * @param objectID
	 */
	public ConfirmCancelNewColumnEvent(boolean confirm, int objectID) {
		this.isConfirm = confirm;
		this.objectID = objectID;
	}

	/**
	 * @return the isConfirm, see {@link #isConfirm}
	 */
	public boolean isConfirm() {
		return isConfirm;
	}

	/**
	 * @return the objectID, see {@link #objectID}
	 */
	public int getObjectID() {
		return objectID;
	}

	@Override
	public boolean checkIntegrity() {
		return true;
	}

}

