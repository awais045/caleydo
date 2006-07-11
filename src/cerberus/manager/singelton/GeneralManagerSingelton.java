package cerberus.manager.singelton;

import cerberus.manager.CommandManager;
import cerberus.manager.GeneralManager;
//import cerberus.manager.ViewCanvasManager;
import cerberus.manager.type.BaseManagerType;
import cerberus.data.xml.MementoCallbackXML;

public interface GeneralManagerSingelton 
extends GeneralManager, MementoCallbackXML {

	public static final String sXMLDelimiter = ";";
	
	/**
	 * Get the current type used to create new Id with createNewId().
	 * 
	 * @see cerberus.manager.singelton.OneForAllManager#createNewId()
	 * @see cerberus.manager.singelton.OneForAllManager#setCurrentType(BaseManagerType)
	 * 
	 * @return current type used to create new Id with createNewId()
	 */
	public abstract BaseManagerType getCurrentType();

	/**
	 * Creates a new unique Id with the type, that was set previouse.
	 * This methode returns and creates a unique Id.
	 * 
	 * @param define type of object ot be created.
	 * 
	 * @return int new unique Id
	 * 
	 * @see cerberus.manager.singelton.OneForAllManager#setNewType(BaseManagerType)
	 * @see cerberus.manager.singelton.OneForAllManager#createNewId(BaseManagerType)
	 */
	public abstract int createNewId(BaseManagerType setNewBaseType);

	/**
	 * Create a new item.
	 * 
	 * @param createNewType type of item. only used locally
	 * @param sNewTypeDetails optional details used to create new object
	 * @return new object
	 */
	public abstract Object createNewItem(final BaseManagerType createNewType,
			final String sNewTypeDetails);

//	/**
//	 * Get the reference to the mangerer handling View's and Canvas.
//	 * 
//	 * @return manger for ViewCanvas objects
//	 */
//	public ViewCanvasManager getViewCanvasManager();
	
	/**
	 * Get the current CommandManager.
	 * 
	 * @return current CommandManager
	 */
	public CommandManager getCommandManager();
	
	/**
	 * Get the reference to the managers using the BaseManagerType.
	 * Note: Instead of writing one get-methode for all Managers this methode
	 * handles all differnt types of managers.
	 * 
	 * @param managerType define type of manger that is requested
	 * @return manager for a certain type.
	 */
	public GeneralManager getManagerByBaseType(BaseManagerType managerType);
	
	/**
	 * Define an error message presented in the GUI.
	 * Must be connected to an error log and/or a GUI status bar.
	 * 
	 * @param sErrorMsg error message
	 */
	public void setErrorMessage( final String sErrorMsg );
	
}