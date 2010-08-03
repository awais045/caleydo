package org.caleydo.core.manager;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.ICommand;
import org.caleydo.core.command.ICommandActionListener;
import org.caleydo.core.command.ICommandListener;
import org.caleydo.core.parser.parameter.IParameterHandler;

/**
 * One Manager handle all ICommandListener. This is a singleton for all Commands and ICommandListener objects.
 * "ISingelton" Design Pattern.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public interface ICommandManager
	extends ICommandActionListener, IManager<ICommand> {

	/**
	 * create a new command. Calls createCommandByType(CommandType) internal.
	 * 
	 * @see org.caleydo.core.manager.ICommandManager#createCommandByType(ECommandType)
	 * @param phAttributes
	 *            Define several attributes and assign them in new Command
	 * @return new Command with attributes defined in phAttributes
	 */
	public ICommand createCommand(final IParameterHandler phAttributes);

	/**
	 * Create a new command using the CommandType.
	 * 
	 * @param cmdType
	 * @return
	 */
	public ICommand createCommandByType(final ECommandType cmdType);

	/**
	 * Add reference to one ICommandListener object.
	 * 
	 * @param addCommandListener
	 *            adds reference to ICommandListener object.
	 */
	public void addCommandListener(ICommandListener addCommandListener);

	/**
	 * Remove reference to one ICommandListener object.
	 * 
	 * @param removeCommandListener
	 *            removes references to ICommandListener object.
	 * @return TRUE if the reference was removed, false if the reference was not found.
	 */
	public boolean removeCommandListener(ICommandListener removeCommandListener);

	/**
	 * Tests if the reference to one ICommandListener object exists.
	 * 
	 * @param hasCommandListener
	 *            reference to be tested
	 * @return true if the reference is bound to this ICommandManager
	 */
	public boolean hasCommandListener(ICommandListener hasCommandListener);

	/**
	 * Register a org.caleydo.core.command.ICommand after its doCommand() method was called. Used for
	 * redo-undo.
	 * 
	 * @see org.caleydo.core.command.ICommand#doCommand()
	 * @param runCmd
	 */
	public void runDoCommand(ICommand runCmd);

	/**
	 * Register a org.caleydo.core.command.ICommand after its undoCommand() method was called. Used for
	 * redo-undo.
	 * 
	 * @see org.caleydo.core.command.ICommand#undoCommand()
	 * @param runCmd
	 */
	public void runUndoCommand(ICommand runCmd);

	/**
	 * Trigger serialization to file
	 */
	public void writeSerializedObjects(final String sFileName);

	/**
	 * Read serialized commands from file
	 */
	public void readSerializedObjects(final String sFileName);
}
