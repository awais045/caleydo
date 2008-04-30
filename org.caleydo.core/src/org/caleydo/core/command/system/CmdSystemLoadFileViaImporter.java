package org.caleydo.core.command.system;

import org.caleydo.core.command.CommandQueueSaxType;
import org.caleydo.core.command.base.ACommand;
import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.manager.ICommandManager;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.ILoggerManager.LoggerType;
import org.caleydo.core.parser.ascii.microarray.MicroArrayLoader1Storage;
import org.caleydo.core.parser.parameter.IParameterHandler;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.util.exception.CaleydoRuntimeExceptionType;
import org.caleydo.core.util.system.StringConversionTool;


/**
 * Command, load data from file using a token pattern and a target ISet.
 * Use MicroArrayLoader1Storage to load data set.
 * 
 * @author Michael Kalkusch
 *
 * @see org.caleydo.core.data.collection.ISet
 * @see org.caleydo.core.parser.ascii.microarray.MicroArrayLoader1Storage
 */
public class CmdSystemLoadFileViaImporter 
extends ACommand {
	
	protected String sFileName;
	
	protected String sTokenPattern;
	
	/**
	 * Default is 32, because gpr files have a header of that size!
	 * 
	 * @see org.caleydo.core.parser.ascii.microarray.MicroArrayLoader1Storage#iStartParsingAtLine
	 * @see org.caleydo.core.parser.ascii.microarray.MicroArrayLoader1Storage#getStartParsingAtLine()
	 * @see org.caleydo.core.parser.ascii.microarray.MicroArrayLoader1Storage#setStartParsingStopParsingAtLine(int, int)
	 */
	protected int iStartPareseFileAtLine = 32;
	
	/**
	 * Default is -1 indicateing read till end of file.
	 * 
	 * @see org.caleydo.core.parser.ascii.microarray.MicroArrayLoader1Storage#iStopParsingAtLine
	 * @see org.caleydo.core.parser.ascii.microarray.MicroArrayLoader1Storage#getStopParsingAtLine()
	 * @see org.caleydo.core.parser.ascii.microarray.MicroArrayLoader1Storage#setStartParsingStopParsingAtLine(int, int)
	 */
	protected int iStopPareseFileAtLine = -1;
	
	protected int iTargetSetId;
	
	
	/**
	 * Constructor.
	 * 
	 * @param refGeneralManager
	 * @param refCommandManager
	 * @param refCommandQueueSaxType
	 */
	public CmdSystemLoadFileViaImporter( 
			final IGeneralManager refGeneralManager,
			final ICommandManager refCommandManager,
			final CommandQueueSaxType refCommandQueueSaxType) {
		
		super(-1,
				refGeneralManager,
				refCommandManager,
				refCommandQueueSaxType);
		
		setCommandQueueSaxType(CommandQueueSaxType.LOAD_DATA_FILE_BY_IMPORTER);
	}
	
	public void setParameterHandler( IParameterHandler refParameterHandler) {
		super.setParameterHandler(refParameterHandler);
		
		this.setId( refParameterHandler.getValueInt( 
				CommandQueueSaxType.TAG_CMD_ID.getXmlKey()) );
	
		this.sFileName = refParameterHandler.getValueString( 
				CommandQueueSaxType.TAG_DETAIL.getXmlKey() );
		this.sTokenPattern =  refParameterHandler.getValueString( 
				CommandQueueSaxType.TAG_ATTRIBUTE1.getXmlKey() );
		this.iTargetSetId =	StringConversionTool.convertStringToInt(
				refParameterHandler.getValueString( 
						CommandQueueSaxType.TAG_ATTRIBUTE2.getXmlKey()),
				-1 );
		
		int[] iArrayStartStop = StringConversionTool.convertStringToIntArrayVariableLength(
				refParameterHandler.getValueString( 
						CommandQueueSaxType.TAG_ATTRIBUTE3.getXmlKey() ),
				" " );
		
		if ( iArrayStartStop.length > 0 ) 
		{
			iStartPareseFileAtLine = iArrayStartStop[0];
			
			if ( iArrayStartStop.length > 1 ) 
			{
				if (( iArrayStartStop[0] > iArrayStartStop[1] )&&
				   ( iArrayStartStop[1] != -1 )){
//					generalManager.logMsg(
//							"CmdSystemLoadFileViaImporter ignore stop index=(" + 
//							iArrayStartStop[1]  + 
//							"), because it is smaller than start index (" + 
//							iArrayStartStop[0] + ") !",
//							LoggerType.STATUS );
					return;
				}
				iStopPareseFileAtLine = iArrayStartStop[1];
			} // if ( iArrayStartStop.length > 0 ) 
		} // if ( iArrayStartStop.length > 0 ) 
	}
	
//	/**
//	 * Use 
//	 * 
//	 * @see org.caleydo.core.parser.handler.importer.ascii.MicroArrayLoader1Storage
//	 */
//	public CmdSystemLoadFileViaImporter( IGeneralManager refGeneralManager,
//			String fileName, 
//			String tokenPattern,
//			final int iTargetSet ) {
//		
//		this.refGeneralManager = refGeneralManager;		
//		this.sFileName = fileName;		
//		this.sTokenPattern =tokenPattern;
//		this.iTargetSetId = iTargetSet;
//	}

	public void setAttributes(String fileName,
			String tokenPattern,
			int startPareseFileAtLine,
			int stopPareseFileAtLine,
			int targetSetId) {
		
		sFileName = fileName;		
		sTokenPattern = tokenPattern;		
		iStartPareseFileAtLine = startPareseFileAtLine;		
		iStopPareseFileAtLine = stopPareseFileAtLine;		
		iTargetSetId = targetSetId;
	}
	
	/**
	 * Load data from file using a token pattern.
	 * 
	 * @see org.caleydo.core.parser.ascii.microarray.MicroArrayLoader1Storage#loadData()
	 * 
	 * @see org.caleydo.core.command.ICommand#doCommand()
	 */
	public void doCommand() throws CaleydoRuntimeException {
		
//		generalManager.logMsg(
//	    		"load file via importer... ([" +
//				sFileName + "] tokens:[" +
//				sTokenPattern + "]  targetSet(s)=[" +
//				iTargetSetId + "])",
//				LoggerType.STATUS );
		
		ISet useSet = generalManager.getSetManager(
				).getItemSet( iTargetSetId );
		
		if ( useSet == null ) {
			String errorMsg = "Could not load data via MicroArrayLoader1Storage, target Set is not valid! file=["+
			sFileName + "] tokens:[" +
			sTokenPattern + "]  targetSet(s)=[" +
			iTargetSetId + "]) CmdSystemLoadfileViaImporter";
			
//			generalManager.logMsg(
//					errorMsg,
//					LoggerType.ERROR );
			
			throw new CaleydoRuntimeException("Set is not valid!",
					CaleydoRuntimeExceptionType.SET);
			
//			CmdWindowPopupInfo exitWarning = new CmdWindowPopupInfo(refGeneralManager,"");
//			exitWarning.setText("ERROR",errorMsg);
//			exitWarning.doCommand();
//			return;
		}
		
		MicroArrayLoader1Storage loader = null;
		
		try 
		{
			loader = new MicroArrayLoader1Storage( generalManager, 
					sFileName,
					IGeneralManager.bEnableMultipelThreads );
			
			//loader.setFileName( sFileName );
			loader.setTokenPattern( sTokenPattern );
			loader.setTargetSet( useSet );
			loader.setStartParsingStopParsingAtLine( iStartPareseFileAtLine,
					iStopPareseFileAtLine );
			
			loader.loadData();
			
			
		} //try
		catch ( Exception e ) 
		{
			String errorMsg = "Could not load data via MicroArrayLoader1Storage, error during loading! file=["+
				sFileName + "] tokens:[" +
				sTokenPattern + "]  targetSet(s)=[" +
				iTargetSetId + "])";
			
//			generalManager.logMsg(
//					errorMsg,
//					LoggerType.ERROR );
		} // catch
		finally 
		{
			if ( loader != null ) 
			{
				loader.destroy();
				loader = null;
			}
		} // finally
		
		refCommandManager.runDoCommand(this);
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.command.ICommand#undoCommand()
	 */
	public void undoCommand() throws CaleydoRuntimeException {
		refCommandManager.runUndoCommand(this);
	}
}
