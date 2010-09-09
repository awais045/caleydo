package org.caleydo.core.data.selection;

import org.caleydo.core.data.mapping.IDType;
import org.caleydo.core.data.virtualarray.StorageVAType;
import org.caleydo.core.data.virtualarray.StorageVirtualArray;
import org.caleydo.core.data.virtualarray.delta.StorageVADelta;

public class StorageSelectionManager
	extends
	VABasedSelectionManager<StorageSelectionManager, StorageVirtualArray, StorageVAType, StorageVADelta> {

	public StorageSelectionManager(IDType idType) {
		super(idType);
	}
}
