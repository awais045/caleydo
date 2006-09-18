/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.command.system;

import java.util.LinkedList;
import java.util.Iterator;

import cerberus.command.ICommand;
import cerberus.command.CommandType;
import cerberus.command.base.ACommand;
//import cerberus.command.window.CmdWindowPopupInfo;
import cerberus.manager.IGeneralManager;
import cerberus.util.exception.CerberusRuntimeException;
import cerberus.util.system.StringConversionTool;
import cerberus.xml.parser.command.CommandQueueSaxType;
import cerberus.xml.parser.parameter.IParameterHandler;

import cerberus.data.loader.MicroArrayLoader;


/**
 * Command, load data from file using a token pattern and a target ISet.
 * Use MicroArrayLoader to laod dataset.
 * 
 * @author Michael Kalkusch
 *
 * @see cerberus.data.collection.ISet
 * @see cerberus.data.loader.MicroArrayLoader
 */
public class CmdSystemLoadFileViaImporter 
extends ACommand
implements ICommand {

	private final IGeneralManager refGeneralManager;
	
	protected String sFileName;
	
	protected String sTokenPattern;
	
	protected int iTargetSetId;
	
	
	/**
	 * 
	 * List of expected Strings inside LinkedList <String>: <br>
	 * sData_CmdId <br>
	 * sData_Cmd_label <br>
	 * sData_Cmd_process <br> 
	 * sData_Cmd_MementoId <br> 
	 * sData_Cmd_detail <br>
	 * sData_Cmd_attribute1 <br>
	 * sData_Cmd_attribute2 <br>
	 * 
	 * @see cerberus.data.loader.MicroArrayLoader
	 */
	public CmdSystemLoadFileViaImporter( IGeneralManager refGeneralManager,
			final LinkedList <String> llAttributes ) {
		
		Iterator <String> iter = llAttributes.iterator();
		
		this.setId( StringConversionTool.convertStringToInt(
				iter.next(), 
				-1 ) );
		
		/**
		 * skip unneeded Strings...
		 */
		iter.next();
		iter.next();
		iter.next();
		iter.next();
		
		this.refGeneralManager = refGeneralManager;		
		this.sFileName = iter.next();		
		this.sTokenPattern = iter.next();
		this.iTargetSetId = StringConversionTool.convertStringToInt(
				iter.next(), 
				-1 );	
	}
	
	public CmdSystemLoadFileViaImporter( IGeneralManager refGeneralManager,
			final IParameterHandler phAttributes ) {
		
		this.refGeneralManager = refGeneralManager;		
		
		this.setId( phAttributes.getValueInt( 
				CommandQueueSaxType.TAG_CMD_ID.getXmlKey()) );
	
		this.sFileName = phAttributes.getValueString( 
				CommandQueueSaxType.TAG_DETAIL.getXmlKey() );
		this.sTokenPattern =  phAttributes.getValueString( 
				CommandQueueSaxType.TAG_ATTRIBUTE1.getXmlKey() );
		this.iTargetSetId = phAttributes.getValueInt(
				CommandQueueSaxType.TAG_TARGET_ID.getXmlKey() );
	}
	
	/**
	 * Use 
	 * 
	 * @see cerberus.data.loader.MicroArrayLoader
	 */
	public CmdSystemLoadFileViaImporter( IGeneralManager refGeneralManager,
			String fileName, 
			String tokenPattern,
			final int iTargetSet ) {
		
		this.refGeneralManager = refGeneralManager;		
		this.sFileName = fileName;		
		this.sTokenPattern =tokenPattern;
		this.iTargetSetId = iTargetSet;
	}

	/**
	 * Load data from file using a token pattern.
	 * 
	 * @see cerberus.data.loader.MicroArrayLoader#loadData()
	 * 
	 * @see cerberus.command.ICommand#doCommand()
	 */
	public void doCommand() throws CerberusRuntimeException {
		System.out.println("load file via importer... ([" +
				sFileName + "] tokens:[" +
				sTokenPattern + "]  targetSet(s)=[" +
				iTargetSetId + "])");	
		
		MicroArrayLoader loader = new MicroArrayLoader( refGeneralManager );
		
		loader.setFileName( sFileName );
		loader.setTokenPattern( sTokenPattern );
		loader.setTargetSet( refGeneralManager.getSingelton().getSetManager().getItemSet( iTargetSetId ));
		
		//boolean bSuccessOnLoad = 
		loader.loadData();
		
//		CmdWindowPopupInfo exitWarning = new CmdWindowPopupInfo("");
//		exitWarning.setText("WARNING","Load data from file..");
//		exitWarning.doCommand();
		
		
	}

	/* (non-Javadoc)
	 * @see cerberus.command.ICommand#undoCommand()
	 */
	public void undoCommand() throws CerberusRuntimeException {
		// no undo of system shutdown!
	}
	

	/* (non-Javadoc)
	 * @see cerberus.command.ICommand#getCommandType()
	 */
	public CommandType getCommandType() throws CerberusRuntimeException {
		return CommandType.DATASET_LOAD; 
	}
	

}
