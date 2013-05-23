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
package org.caleydo.view.tourguide.api.state;

import java.util.Collection;
import java.util.Set;

import org.caleydo.view.tourguide.api.query.EDataDomainQueryMode;

/**
 * @author Samuel Gratzl
 *
 */
public interface IStateMachine {
	String ADD_PATHWAY = EDataDomainQueryMode.PATHWAYS.name();
	String ADD_OTHER = EDataDomainQueryMode.OTHER.name();
	String ADD_STRATIFICATIONS = EDataDomainQueryMode.STRATIFICATIONS.name();
	String BROWSE_PATHWAY = EDataDomainQueryMode.PATHWAYS.name() + "_browse";
	String BROWSE_OTHER = EDataDomainQueryMode.OTHER.name() + "_browse";
	String BROWSE_STRATIFICATIONS = EDataDomainQueryMode.STRATIFICATIONS.name() + "_browse";

	IState addState(String id, IState state);

	void addTransition(IState source, ITransition transition);

	IState get(String id);

	Set<String> getStates();

	IState getCurrent();

	Collection<ITransition> getTransitions(IState state);
}
