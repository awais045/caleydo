/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.rank.data;

import com.google.common.base.Function;

/**
 * float special version of a {@link Function} to avoid boxing primitives
 * 
 * @author Samuel Gratzl
 * 
 */
public interface IFloatFunction<F> extends Function<F, Float> {
	float applyPrimitive(F in);
}

