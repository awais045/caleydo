package cerberus.command.window.swt;



//import org.eclipse.swt.widgets.Composite;

import cerberus.command.ICommand;
import cerberus.command.base.ACmdCreate_IdTargetLabelParent;
import cerberus.manager.IGeneralManager;
import cerberus.util.exception.CerberusRuntimeException;
import cerberus.xml.parser.command.CommandQueueSaxType;
import cerberus.xml.parser.parameter.IParameterHandler;

public class CmdContainerCreate
extends ACmdCreate_IdTargetLabelParent
implements ICommand 
{
	protected String sLayoutAttributes;
	
	public CmdContainerCreate( final IGeneralManager refGeneralManager,
			final IParameterHandler refParameterHandler ) 
	{
		super( refGeneralManager, refParameterHandler );	
		setAttributes( refParameterHandler );
	}

	public void doCommand() throws CerberusRuntimeException
	{
		refGeneralManager.getSingelton().
			getSWTGUIManager().createComposite(
					iUniqueTargetId, 
					iParentContainerId, 
					sLayoutAttributes);	
	}

	public void undoCommand() throws CerberusRuntimeException
	{
		// TODO Auto-generated method stub
		
	}

	protected boolean setAttributes( final IParameterHandler refParameterHandler ) 
	{
		super.setAttributesBaseParent( refParameterHandler );
		
		sLayoutAttributes = refParameterHandler.getValueString( 
				CommandQueueSaxType.TAG_ATTRIBUTE1.getXmlKey() );
		
		return true;
		
	}
}