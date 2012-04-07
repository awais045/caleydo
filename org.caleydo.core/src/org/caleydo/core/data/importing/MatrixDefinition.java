package org.caleydo.core.data.importing;

import java.io.BufferedReader;

import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Definitions for a Matrix-File stored in a delimited text file.
 * </p>
 * <p>
 * The general contract of this class is the following:
 * </p>
 * <ul>
 * <li>The data is stored in a delimited text file. Common delimiters are tabs
 * or semicolons, but any ascii string is possible. Lines in the matrix are
 * separated with an ascii linebreak according to
 * {@link BufferedReader#readLine()} contract, i.e. either a line feed, a
 * carriage return, or both.</li>
 * <li>The file may have a header of arbitrary length (an arbitrary number of
 * lines)</li>
 * <li>The data matrix follows the header. The data matrix has a constant number
 * of columns and every line until the end of the file contains nothing but
 * data.</li>
 * <li>Generally, there should be a row containing identifiers for the columns.
 * The location of this row is assumed to be in the header directly preceding
 * the data (i.e. in the line before the data starts). If this row is at some
 * other position in the header this can be specified using
 * {@link #rowOfColumnIDs}. If no column IDs are specified, they are
 * automatically generated</li>
 * <li>There must be a column containing identifiers for the rows. The column
 * can be specified in {@link #columnOfRowIds}. If this is not specified it is
 * assumed to be the first column.</li>
 * </ul>
 * 
 * @author Alexander Lex
 * 
 */
@XmlType
public class MatrixDefinition {

	/** The path to the source table csv file. Mandatory */
	protected String dataSourcePath;

	/**
	 * The number of lines which should be ignored for parsing, i.e. that don't
	 * contain data. They may contain the line containing the IDs. This defaults
	 * to 1 line for the IDs.
	 */
	protected Integer numberOfHeaderLines = 1;

	/**
	 * The text delimiter used. Default is tab ("\t");
	 */
	protected String delimiter = "\t";

	/** Flag telling whether columnIDs are available. Defaults to true. */
	protected boolean containsColumnIDs = true;

	/**
	 * Defines in which row the IDs for the columns are found. By default, this
	 * is assumed to be {@link #numberOfHeaderLines}-1, i.e., if the row of
	 * column IDs is directly followed by the data it is not necessary to
	 * specify this. Defaults to null, which means that
	 * {@link #numberOfHeaderLines}-1 should be used.
	 */
	protected Integer rowOfColumnIDs = null;

	/**
	 * Defines in which columns the IDs for the rows are found. By default it is
	 * assumed that this is in row 0 (the first row).
	 */
	protected Integer columnOfRowIds = 0;

	/**
	 * @param dataSourcePath
	 *            setter, see {@link #dataSourcePath}
	 */
	public void setDataSourcePath(String dataSourcePath) {
		this.dataSourcePath = dataSourcePath;
	}

	/**
	 * @return the dataSourcePath, see {@link #dataSourcePath}
	 */
	public String getDataSourcePath() {
		return dataSourcePath;
	}

	/**
	 * @param numberOfHeaderLines
	 *            setter, see {@link #numberOfHeaderLines}
	 */
	public void setNumberOfHeaderLines(Integer numberOfHeaderLines) {
		this.numberOfHeaderLines = numberOfHeaderLines;
	}

	/**
	 * @return the numberOfHeaderLines, see {@link #numberOfHeaderLines}
	 */
	public Integer getNumberOfHeaderLines() {
		return numberOfHeaderLines;
	}

	/**
	 * @param delimiter
	 *            setter, see {@link #delimiter}
	 */
	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	/**
	 * @return the delimiter, see {@link #delimiter}
	 */
	public String getDelimiter() {
		return delimiter;
	}

	/**
	 * @param containsColumnIDs
	 *            setter, see {@link #containsColumnIDs}
	 */
	public void setContainsColumnIDs(boolean containsColumnIDs) {
		this.containsColumnIDs = containsColumnIDs;
	}

	/**
	 * @return the containsColumnIDs, see {@link #containsColumnIDs}
	 */
	public boolean isContainsColumnIDs() {
		return containsColumnIDs;
	}

	/**
	 * @param rowOfColumnIDs
	 *            setter, see {@link #rowOfColumnIDs}
	 */
	public void setRowOfColumnIDs(Integer rowOfColumnIDs) {
		this.rowOfColumnIDs = rowOfColumnIDs;
	}

	/**
	 * @return the rowOfColumnIDs, see {@link #rowOfColumnIDs}
	 */
	public Integer getRowOfColumnIDs() {
		return rowOfColumnIDs;
	}

	/**
	 * @param columnOfRowIds
	 *            setter, see {@link #columnOfRowIds}
	 */
	public void setColumnOfRowIds(Integer columnOfRowIds) {
		this.columnOfRowIds = columnOfRowIds;
	}

	/**
	 * @return the columnOfRowIds, see {@link #columnOfRowIds}
	 */
	public Integer getColumnOfRowIds() {
		return columnOfRowIds;
	}

}
