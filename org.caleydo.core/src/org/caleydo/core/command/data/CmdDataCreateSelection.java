package org.caleydo.core.command.data;

import org.caleydo.core.command.CommandType;
import org.caleydo.core.command.base.ACmdCreate_IdTargetLabelAttrDetail;
import org.caleydo.core.data.selection.ISelection;
import org.caleydo.core.util.exception.CaleydoRuntimeException;

/**
 * Class creates a selection.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public class CmdDataCreateSelection
	extends ACmdCreate_IdTargetLabelAttrDetail
{
	ISelection selection = null;
	
	/**
	 * Constructor.
	 */
	public CmdDataCreateSelection(final CommandType cmdType)
	{
		super(cmdType);
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.command.ICommand#doCommand()
	 */
	public void doCommand() throws CaleydoRuntimeException
	{
		selection = generalManager.getSelectionManager().createSelection();

		if (iExternalID != -1)
		{
			generalManager.getIDManager().mapInternalToExternalID(selection.getID(), iExternalID);
		}
		
		commandManager.runDoCommand(this);
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.command.ICommand#undoCommand()
	 */
	public void undoCommand() throws CaleydoRuntimeException
	{
		commandManager.runUndoCommand(this);
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.command.base.ACommand#getInfoText()
	 */
	public String getInfoText()
	{
		return super.getInfoText() + " -> " + this.iExternalID + ": " + this.sLabel;
	}
	
	public int getSelectionID() 
	{
		return selection.getID();
	}
}
