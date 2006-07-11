/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.data;

import cerberus.manager.GeneralManager;
//import cerberus.manager.type.BaseManagerType;
import cerberus.data.UniqueManagedInterface;

/**
 * Abstract class providing methodes defiend in UniqueManagedInterface.
 * Stores reference to creator of item in private variable.
 * 
 * @author Michael Kalkusch
 *
 * @see prometheus.data.xml.MementiItemXML
 */
public abstract class UniqueManagedItem 
implements UniqueManagedInterface {

	/**
	 * Unique Id
	 */
	private int iCollectionId;
	
	
	/**
	 * Reference to manager, who created this object.
	 */
	private GeneralManager refGeneralManager = null;
	
	/**
	 * 
	 */
	protected UniqueManagedItem( int iSetCollectionId, GeneralManager setGeneralManager ) {
		
		assert setGeneralManager != null: "SetFlatSimple() with null pointer";
		
		refGeneralManager = setGeneralManager;
		iCollectionId = iSetCollectionId;
	}

	/* (non-Javadoc)
	 * @see prometheus.data.collection.UniqueManagedInterface#getManager()
	 */
	public final GeneralManager getManager() {
		return this.refGeneralManager;
	}

	/**
	 * Reset the GeneralManager.
	 * 
	 * @see prometheus.data.collection.UniqueManagedInterface#getGeneralManager()
	 */
	final protected void setManager( final GeneralManager setGeneralManager) {
		this.refGeneralManager = setGeneralManager;
	}
	
	/**
	 * Get Id by calling prometheus.data.collection.BaseManagerItem#getCollecionId().
	 * Part of prometheus.data.xml.MementiItemXML iterface.
	 * 
	 * @see prometheus.data.collection.BaseManagerItem#getCollecionId()
	 * @see prometheus.data.xml.MementiItemXML
	 * 
	 * @return
	 */
	public final int getId() {
		return this.iCollectionId;
	}
	
	/**
	 * Sets Id by calling prometheus.data.collection.BaseManagerItem#setCollecionId(GeneralManager, int)
	 * Part of prometheus.data.xml.MementiItemXML iterface.
	 * @param creator
	 * @param iSetDNetEventId
	 * 
	 * @see prometheus.data.collection.BaseManagerItem#setCollecionId(GeneralManager, int)
	 * @see prometheus.data.xml.MementiItemXML
	 */
	public final void setId( final int iSetDNetEventId ) {		
		this.iCollectionId = iSetDNetEventId;		
	}
	
	//public abstract BaseManagerType getBaseType();
	
}
