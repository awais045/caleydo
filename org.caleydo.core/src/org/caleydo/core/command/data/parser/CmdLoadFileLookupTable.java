package org.caleydo.core.command.data.parser;

import java.util.Map;
import java.util.StringTokenizer;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.base.ACommand;
import org.caleydo.core.data.mapping.EMappingDataType;
import org.caleydo.core.data.mapping.EMappingType;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.IIDMappingManager;
import org.caleydo.core.parser.ascii.lookuptable.LookupTableLoader;
import org.caleydo.core.parser.parameter.IParameterHandler;
import org.caleydo.core.util.system.StringConversionTool;

/**
 * Command loads lookup table from file using one delimiter and a target Collection.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public class CmdLoadFileLookupTable
	extends ACommand {
	protected String sFileName;

	private String sLookupTableInfo;

	protected String sLookupTableType;

	/**
	 * Special cases for creating reverse map and using internal LUTs. Valid values are: LUT|LUT_2 REVERSE
	 */
	protected String sLookupTableOptions;

	/**
	 * Define type of lookup table to be created.
	 * 
	 * @see org.caleydo.core.data.mapping.EIDType
	 */
	protected String sLookupTableDelimiter;

	protected int iStartPareseFileAtLine = 0;

	/**
	 * Default is -1 indicating read till end of file.
	 * 
	 * @see org.caleydo.core.parser.ascii.microarray.MicroArrayLoader1Storage#iStopParsingAtLine
	 * @see org.caleydo.core.parser.ascii.microarray.MicroArrayLoader1Storage#getStopParsingAtLine()
	 * @see org.caleydo.core.parser.ascii.microarray.MicroArrayLoader1Storage#setStartParsingStopParsingAtLine(int,
	 *      int)
	 */
	protected int iStopParseFileAtLine = -1;

	protected boolean bCreateReverseMap = false;

	/**
	 * Boolean indicates if one column of the mapping needs to be resolved. Resolving means replacing codes by
	 * internal IDs.
	 */
	protected boolean bResolveCodeMappingUsingCodeToId_LUT = false;

	/**
	 * Variable contains the lookup table types that are needed to resolve mapping tables that contain codes
	 * instead of internal IDs.
	 */
	protected String sCodeResolvingLUTTypes;

	protected String sCodeResolvingLUTMappingType;

	/**
	 * Constructor.
	 * 
	 * @param cmdType
	 */
	public CmdLoadFileLookupTable(final ECommandType cmdType) {
		super(cmdType);
	}

	@Override
	public void setParameterHandler(final IParameterHandler parameterHandler) {
		sFileName = parameterHandler.getValueString(ECommandType.TAG_DETAIL.getXmlKey());

		sLookupTableInfo = parameterHandler.getValueString(ECommandType.TAG_ATTRIBUTE1.getXmlKey());

		sLookupTableDelimiter = parameterHandler.getValueString(ECommandType.TAG_ATTRIBUTE2.getXmlKey());

		int[] iArrayStartStop =
			StringConversionTool.convertStringToIntArray(parameterHandler
				.getValueString(ECommandType.TAG_ATTRIBUTE3.getXmlKey()), " ");

		if (iArrayStartStop.length == 2) {
			iStartPareseFileAtLine = iArrayStartStop[0];
			iStopParseFileAtLine = iArrayStartStop[1];
		}

		sCodeResolvingLUTTypes = parameterHandler.getValueString(ECommandType.TAG_ATTRIBUTE4.getXmlKey());

		extractParameters();
	}

	public void setAttributes(final String sFileName, final int iStartParseFileAtLine,
		final int iStopParseFileAtLine, final String sLookupTableInfo, final String sLookupTableDelimiter,
		final String sCodeResolvingLUTTypes) {

		this.sFileName = sFileName;
		this.iStartPareseFileAtLine = iStartParseFileAtLine;
		this.iStopParseFileAtLine = iStopParseFileAtLine;
		this.sLookupTableInfo = sLookupTableInfo;
		this.sLookupTableDelimiter = sLookupTableDelimiter;
		this.sCodeResolvingLUTTypes = sCodeResolvingLUTTypes;

		extractParameters();
	}

	private void extractParameters() {
		StringTokenizer tokenizer =
			new StringTokenizer(sLookupTableInfo, IGeneralManager.sDelimiter_Parser_DataItems);

		sLookupTableType = tokenizer.nextToken();

		while (tokenizer.hasMoreTokens()) {
			sLookupTableOptions = tokenizer.nextToken();

			if (sLookupTableOptions.equals("REVERSE")) {
				bCreateReverseMap = true;
			}
			else if (sLookupTableOptions.equals("LUT")) {
				tokenizer =
					new StringTokenizer(sCodeResolvingLUTTypes, IGeneralManager.sDelimiter_Parser_DataItems);

				sCodeResolvingLUTMappingType = tokenizer.nextToken();

				bResolveCodeMappingUsingCodeToId_LUT = true;
			}
		}
	}

	/**
	 * Load data from file using a token pattern.
	 * 
	 * @see org.caleydo.core.parser.ascii.microarray.MicroArrayLoader1Storage#loadData()
	 * @see org.caleydo.core.command.ICommand#doCommand()
	 */
	public void doCommand() {
		LookupTableLoader loader = null;

		IIDMappingManager genomeIdManager = generalManager.getIDMappingManager();

		// Remove old lookuptable if it already exists
		// genomeIdManager.removeMapByType(EMappingType.valueOf(sLookupTableType));

		EMappingType mappingType = EMappingType.valueOf(sLookupTableType);
		EMappingDataType dataType;
		dataType = mappingType.getDataMapppingType();

		int iIndex = 0;
		if (sFileName.equals("generate")) {
			genomeIdManager
				.createMap(EMappingType.REFSEQ_MRNA_2_REFSEQ_MRNA_INT, EMappingDataType.INT2STRING);
			Map hashTmp = genomeIdManager.getMapping(EMappingType.REFSEQ_MRNA_2_REFSEQ_MRNA_INT);
			for (Object sRefSeqID : genomeIdManager.getMapping(EMappingType.DAVID_2_REFSEQ_MRNA).values()) {
				hashTmp.put(sRefSeqID, iIndex++);
			}
		}
		else if (!sFileName.equals("already_loaded")) {
			loader = new LookupTableLoader(sFileName, mappingType, dataType);
			loader.setTokenSeperator(sLookupTableDelimiter);
			loader.setStartParsingStopParsingAtLine(iStartPareseFileAtLine, iStopParseFileAtLine);
			loader.loadData();
		}

		if (bResolveCodeMappingUsingCodeToId_LUT) {
			genomeIdManager.createCodeResolvedMap(mappingType, EMappingType
				.valueOf(sCodeResolvingLUTMappingType));
		}

		/* --- create reverse Map ... --- */
		if (bCreateReverseMap) {
			if (sCodeResolvingLUTMappingType != null) {
				mappingType = EMappingType.valueOf(sCodeResolvingLUTMappingType);
			}

			// Concatenate genome id type target and origin type in swapped
			// order to determine reverse genome mapping type.
			EMappingType reverseMappingType =
				EMappingType.valueOf(mappingType.getTypeTarget().toString() + "_2_"
					+ mappingType.getTypeOrigin().toString());

			genomeIdManager.createReverseMap(mappingType, reverseMappingType);
		}

		commandManager.runDoCommand(this);
	}

	@Override
	public void undoCommand() {
		commandManager.runUndoCommand(this);
	}
}
