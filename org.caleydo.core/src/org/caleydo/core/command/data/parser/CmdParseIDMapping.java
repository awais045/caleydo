package org.caleydo.core.command.data.parser;

import java.util.StringTokenizer;

import org.caleydo.core.command.CommandType;
import org.caleydo.core.command.base.ACmdExternalAttributes;
import org.caleydo.core.data.id.IDCategory;
import org.caleydo.core.data.id.IDType;
import org.caleydo.core.data.mapping.IDMappingManager;
import org.caleydo.core.data.mapping.IDMappingManagerRegistry;
import org.caleydo.core.data.mapping.MappingType;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.parser.ascii.AStringConverter;
import org.caleydo.core.parser.ascii.ATextParser;
import org.caleydo.core.parser.ascii.IDMappingParser;
import org.caleydo.core.parser.parameter.ParameterHandler;
import org.caleydo.core.specialized.Organism;
import org.caleydo.core.util.conversion.ConversionTools;

/**
 * Command loads lookup table from file using one delimiter and a target Collection.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public class CmdParseIDMapping
	extends ACmdExternalAttributes {

	protected String fileName;

	private String lookupTableInfo;

	private IDCategory idCategory;

	private IDType fromIDType;

	private IDType toIDType;

	/**
	 * Special cases for creating reverse map and using internal LUTs. Valid values are: LUT|LUT_2 REVERSE
	 */
	private String lookupTableOptions;

	/**
	 * Define type of lookup table to be created.
	 * 
	 * @see org.caleydo.core.data.mapping.EIDType
	 */
	private String delimiter;

	private int startParsingAtLine = 0;

	/**
	 * Default is -1 indicating read till end of file.
	 */
	private int stopParsingAtLine = -1;

	private boolean createReverseMap = false;

	/**
	 * Boolean indicates if one column of the mapping needs to be resolved. Resolving means replacing codes by
	 * internal IDs.
	 */
	private boolean resolveCodeMappingUsingCodeToId_LUT = false;

	/**
	 * Variable contains the lookup table types that are needed to resolve mapping tables that contain codes
	 * instead of internal IDs.
	 */
	private String codeResolvingLUTTypes;

	private String codeResolvingLUTMappingType;

	private boolean isMultiMap;

	private AStringConverter stringConverter;

	public CmdParseIDMapping() {
		super(CommandType.PARSE_ID_MAPPING);
	}

	@Override
	public void setParameterHandler(final ParameterHandler parameterHandler) {

		super.setParameterHandler(parameterHandler);

		fileName = detail;
		lookupTableInfo = attrib1;
		delimiter = attrib2;

		if (attrib3 != null) {
			int[] iArrayStartStop = ConversionTools.convertStringToIntArray(attrib3, " ");

			if (iArrayStartStop.length == 2) {
				startParsingAtLine = iArrayStartStop[0];
				stopParsingAtLine = iArrayStartStop[1];
			}
		}

		codeResolvingLUTTypes = attrib4;

		isMultiMap = Boolean.parseBoolean(attrib5);

		idCategory = IDCategory.getIDCategory(attrib6);

		extractParameters();
	}

	public void setAttributes(final String fileName, final int startParsingInLine,
		final int stopParsingInLine, final String sLookupTableInfo, final String delimiter,
		final String sCodeResolvingLUTTypes, final IDCategory idCategory) {

		this.startParsingAtLine = startParsingInLine;
		this.stopParsingAtLine = stopParsingInLine;
		this.lookupTableInfo = sLookupTableInfo;
		this.delimiter = delimiter;
		this.codeResolvingLUTTypes = sCodeResolvingLUTTypes;
		this.fileName = fileName;
		this.idCategory = idCategory;
		extractParameters();
	}

	/**
	 * @param stringConverter
	 *            setter, see {@link #stringConverter}
	 */
	public void setStringConverter(AStringConverter stringConverter) {
		this.stringConverter = stringConverter;
	}

	private void extractParameters() {
		StringTokenizer tokenizer = new StringTokenizer(lookupTableInfo, ATextParser.SPACE);

		String mappingTypeString = tokenizer.nextToken();
		fromIDType = IDType.getIDType(mappingTypeString.substring(0, mappingTypeString.indexOf("_2_")));
		toIDType =
			IDType.getIDType(mappingTypeString.substring(mappingTypeString.indexOf("_2_") + 3,
				mappingTypeString.length()));

		while (tokenizer.hasMoreTokens()) {
			lookupTableOptions = tokenizer.nextToken();

			if (lookupTableOptions.equals("REVERSE")) {
				createReverseMap = true;
			}
			else if (lookupTableOptions.equals("LUT")) {
				tokenizer = new StringTokenizer(codeResolvingLUTTypes, ATextParser.SPACE);

				codeResolvingLUTMappingType = tokenizer.nextToken();

				resolveCodeMappingUsingCodeToId_LUT = true;
			}
		}
	}

	/**
	 * Load data from file using a token pattern.
	 * 
	 * @see org.caleydo.core.command.ICommand#doCommand()
	 */
	@Override
	public void doCommand() {
		IDMappingParser idMappingParser = null;

		if (fileName.contains("ORGANISM")) {
			Organism eOrganism = GeneralManager.get().getBasicInfo().getOrganism();
			this.fileName = fileName.replace("ORGANISM", eOrganism.toString());
		}

		// FIXME: Currently we do not have the ensembl mapping table for home sapiens
		if (fileName.contains("HOMO_SAPIENS") && fileName.contains("ENSEMBL"))
			return;

		if (idCategory == null)
			throw new IllegalStateException("ID Category was null");
		IDMappingManager idMappingManager = IDMappingManagerRegistry.get().getIDMappingManager(idCategory);
		MappingType mappingType = idMappingManager.createMap(fromIDType, toIDType, isMultiMap);

		if (resolveCodeMappingUsingCodeToId_LUT) {

			IDType codeResolvedFromIDType =
				IDType.getIDType(codeResolvingLUTMappingType.substring(0,
					codeResolvingLUTMappingType.indexOf("_2_")));
			IDType codeResolvedToIDType =
				IDType.getIDType(codeResolvingLUTMappingType.substring(
					codeResolvingLUTMappingType.indexOf("_2_") + 3, codeResolvingLUTMappingType.length()));

			idMappingManager.createCodeResolvedMap(mappingType, codeResolvedFromIDType, codeResolvedToIDType);
		}

		if (!fileName.equals("already_loaded")) {
			idMappingParser = new IDMappingParser(idCategory, fileName, mappingType);
			idMappingParser.setStringConverter(stringConverter);
			idMappingParser.setTokenSeperator(delimiter);
			idMappingParser.setStartParsingStopParsingAtLine(startParsingAtLine, stopParsingAtLine);
			idMappingParser.loadData();
		}

		if (createReverseMap) {
			idMappingManager.createReverseMap(mappingType);
		}
	}

	@Override
	public void undoCommand() {
	}
}
