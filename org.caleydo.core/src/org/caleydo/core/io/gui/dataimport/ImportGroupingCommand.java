/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.io.gui.dataimport;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.gui.util.WithinSWTThread;
import org.caleydo.core.id.IDCategory;
import org.caleydo.core.io.DataLoader;
import org.caleydo.core.io.DataSetDescription;
import org.caleydo.core.io.GroupingParseSpecification;
import org.eclipse.swt.widgets.Shell;

/**
 * command for importing a group
 *
 * @author Samuel Gratzl
 *
 */
public class ImportGroupingCommand implements Runnable {
	private final IDCategory idCategory;
	private final ATableBasedDataDomain dataDomain;

	public ImportGroupingCommand(IDCategory idCategory, ATableBasedDataDomain dataDomain) {
		this.idCategory = idCategory;
		this.dataDomain = dataDomain;
	}

	@WithinSWTThread
	@Override
	public void run() {
		ImportGroupingDialog d = new ImportGroupingDialog(new Shell(), idCategory);
		GroupingParseSpecification spec = d.call();
		if (spec == null)
			return;
		DataSetDescription dataSetDescription = dataDomain.getDataSetDescription();
		if (dataDomain.getRecordIDCategory() == idCategory) {
			if (dataDomain.isColumnDimension()) {
				dataSetDescription.addRowGroupingSpecification(spec);
			} else {
				dataSetDescription.addColumnGroupingSpecification(spec);
			}
		} else {
			if (dataDomain.isColumnDimension()) {
				dataSetDescription.addColumnGroupingSpecification(spec);
			} else {
				dataSetDescription.addRowGroupingSpecification(spec);
			}
		}
		DataLoader.loadGrouping(dataDomain, spec);
	}
}
