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
package org.caleydo.core.io.gui.dataimport;

import java.util.Collection;
import java.util.List;

import org.caleydo.core.io.MatrixDefinition;
import org.caleydo.core.io.gui.dataimport.widget.BooleanCallback;
import org.caleydo.core.io.gui.dataimport.widget.DelimiterWidget;
import org.caleydo.core.io.gui.dataimport.widget.ICallback;
import org.caleydo.core.io.gui.dataimport.widget.PreviewTableWidget;
import org.caleydo.core.io.gui.dataimport.widget.SelectAllNoneWidget;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Samuel Gratzl
 *
 */
public class PreviewTable {
	private final MatrixDefinition spec;

	/**
	 * Matrix that stores the data for {@link #MAX_PREVIEW_TABLE_ROWS} rows and all columns of the data file.
	 */
	private List<? extends List<String>> dataMatrix;

	/**
	 * The total number of columns of the input file.
	 */
	private int totalNumberOfColumns;

	/**
	 * Parser used to parse data files.
	 */
	private FilePreviewParser parser = new FilePreviewParser();

	private DelimiterWidget delimeter;

	private SelectAllNoneWidget selectAllNone;

	private PreviewTableWidget previewTable;

	private final IPreviewCallback previewCallback;


	public PreviewTable(Composite parent, MatrixDefinition spec, IPreviewCallback previewCallback) {
		this.spec = spec;
		this.previewCallback = previewCallback;

		delimeter = new DelimiterWidget(parent, new ICallback<String>() {
			@Override
			public void on(String delimiter) {
				onDelimiterChanged(delimiter);
			}
		});
		delimeter.setDelimeter(spec.getDelimiter());

		selectAllNone = new SelectAllNoneWidget(parent, new BooleanCallback() {
			@Override
			public void on(boolean selectAll) {
				onSelectAllNone(selectAll);
			}
		});
		this.selectAllNone.setEnabled(false);

		previewTable = new PreviewTableWidget(parent, new BooleanCallback() {
			@Override
			public void on(boolean data) {
				onShowAllColumns(data);
			}
		});
	}

	/**
	 * Creates the preview table from the file specified by {@link #groupingParseSpecification}. Widgets of the
	 * {@link #dialog} are updated accordingly.
	 *
	 * @param showOnlyPreviewColumns
	 *            Determines whether {@link #MAX_PREVIEW_TABLE_COLUMNS}, or all columns of the file are shown.
	 */
	public void createDataPreviewTableFromFile(boolean showOnlyPreviewColumns) {
		parser.parse(spec.getDataSourcePath(), spec.getDelimiter(), false, PreviewTableWidget.MAX_PREVIEW_TABLE_ROWS);
		dataMatrix = parser.getDataMatrix();
		totalNumberOfColumns = parser.getTotalNumberOfColumns();
		this.previewTable.createDataPreviewTableFromDataMatrix(dataMatrix,
				showOnlyPreviewColumns ? PreviewTableWidget.MAX_PREVIEW_TABLE_COLUMNS : totalNumberOfColumns);
		previewCallback.on(totalNumberOfColumns,  parser.getTotalNumberOfRows(), dataMatrix);
		previewTable.updateVisibleColumns(totalNumberOfColumns);
		this.previewTable.updateTableColors(spec.getNumberOfHeaderLines(), -1, spec.getColumnOfRowIds());
	}

	public Collection<Integer> getSelectedColumns() {
		return previewTable.getSelectedColumns();
	}

	public String getValue(int rowIndex, int columnIndex) {
		if (previewTable == null)
			return null;
		return previewTable.getValue(rowIndex, columnIndex);
	}


	/**
	 * @param selectedColumns
	 *
	 */
	public void generatePreview(List<Integer> selectedColumns) {
		this.delimeter.setEnabled(true);
		this.selectAllNone.setEnabled(true);

		int maxColumnIndex = 0;
		for (Integer columnIndex : selectedColumns) {
			if (columnIndex > maxColumnIndex)
				maxColumnIndex = columnIndex;
		}
		if (maxColumnIndex + 1 > PreviewTableWidget.MAX_PREVIEW_TABLE_COLUMNS) {
			createDataPreviewTableFromFile(false);
		} else {
			createDataPreviewTableFromFile(true);
		}
		previewTable.setSelectedColumns(selectedColumns);
	}

	public void generatePreview() {
		this.delimeter.setEnabled(true);
		this.selectAllNone.setEnabled(true);
		createDataPreviewTableFromFile(true);
	}

	public void onSelectAllNone(boolean selectAll) {
		this.previewTable.selectColumns(selectAll, spec.getColumnOfRowIds());
	}

	/**
	 * Updates the preview table according to the number of the spinner.
	 */
	public void onNumHeaderRowsChanged(int numHeaderRows) {
		try {
			spec.setNumberOfHeaderLines(numHeaderRows);
			previewTable.updateTableColors(spec.getNumberOfHeaderLines(), -1, spec.getColumnOfRowIds());
		} catch (NumberFormatException exc) {

		}

	}

	/**
	 * Updates the preview table according to the number of the spinner.
	 */
	public void onColumnOfRowIDChanged(int column) {
		spec.setColumnOfRowIds(column - 1);
		previewTable.updateTableColors(spec.getNumberOfHeaderLines(), -1, spec.getColumnOfRowIds());
	}

	public void onDelimiterChanged(String delimiter) {
		spec.setDelimiter(delimiter);
		createDataPreviewTableFromFile(true);
	}

	/**
	 * Loads all columns or the {@link #MAX_PREVIEW_TABLE_COLUMNS} into the preview table, depending on the state of
	 * showAllColumnsButton of the {@link #dialog}.
	 */
	public void onShowAllColumns(boolean showAllColumns) {
		this.previewTable.createDataPreviewTableFromDataMatrix(dataMatrix, showAllColumns ? totalNumberOfColumns
				: PreviewTableWidget.MAX_PREVIEW_TABLE_COLUMNS);
		// determineRowIDType();
		this.previewTable.updateTableColors(spec.getNumberOfHeaderLines(), -1, spec.getColumnOfRowIds());
		this.previewTable.updateVisibleColumns(totalNumberOfColumns);
	}

	public interface IPreviewCallback {
		public void on(int numColumn, int numRow, List<? extends List<String>> dataMatrix);
	}
}
