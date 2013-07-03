/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.util.function;

import java.util.Iterator;

/**
 * @author Samuel Gratzl
 *
 */
public interface IFloatIterator extends Iterator<Float> {
	float nextPrimitive();
}

