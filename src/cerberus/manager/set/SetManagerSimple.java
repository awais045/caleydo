/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.manager.set;

import java.util.Vector;
import java.util.Iterator;

import cerberus.manager.GeneralManager;
import cerberus.manager.SetManager;
import cerberus.manager.collection.CollectionManager;
import cerberus.manager.type.BaseManagerType;

//import cerberus.data.collection.Selection;
import cerberus.data.collection.Set;
//import cerberus.data.collection.SetType;
import cerberus.data.collection.set.SetFlatSimple;
import cerberus.data.collection.set.SetPlanarSimple;


/**
 * @author Michael Kalkusch
 *
 */
public class SetManagerSimple 
extends CollectionManager
implements SetManager {

	/**
	 * Reference to the singelton manager.
	 * 
	 * Note: See "Design Pattern" Singelton
	 */
	//protected GeneralManager refGeneralManager = null;
	
	/**
	 * Vector holds a list of all Set's
	 */
	protected Vector<Set> vecSets;
	
	
	
	/**
	 * 
	 */
	public SetManagerSimple( GeneralManager setSingelton,
			final int iInitSizeContainer ) {
		super( setSingelton , iUniqueId_TypeOffset_Set );

		assert setSingelton != null : "Constructor with null-pointer to singelton";
		assert iInitSizeContainer > 0 : "Constructor with iInitSizeContainer < 1";
		
		vecSets = new Vector< Set > ( iInitSizeContainer );
		
		refGeneralManager.getSingelton().setSetManager( this );			
	}
	

	/* (non-Javadoc)
	 * @see cerberus.data.manager.SetManager#createSet()
	 */
	public Set createSet( final BaseManagerType useStorageType ) {
			
		switch ( useStorageType ) {
			case SET_LINEAR:
				return new SetFlatSimple(4,getGeneralManager());
				
			case SET_PLANAR:
				return new SetPlanarSimple(4,getGeneralManager());
			
			case SET_CUBIC:
				break;
				
			case SET_MULTI_DIM:
				
			default:
				throw new RuntimeException("SetManagerSimple.createSet() failed due to unhandled type [" +
						useStorageType.toString() + "]");
		}
		
		return null;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.manager.SetManager#deleteSet(cerberus.data.collection.Set)
	 */
	public boolean deleteSet(Set deleteSet ) {
		return vecSets.remove( deleteSet );
	}
	
	/* (non-Javadoc)
	 * @see cerberus.data.manager.SetManager#deleteSet(cerberus.data.collection.Set)
	 */
	public boolean deleteSet( final int iItemId ) {
		try {
			vecSets.remove( iItemId );
			return true;
		}
		catch (ArrayIndexOutOfBoundsException ae) {
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see cerberus.data.manager.SetManager#getItemSet(int)
	 */
	public Set getItemSet( final int iItemId) {
		
		try {
			return vecSets.get( getLookupValueById( iItemId ) );
		} 
		catch (ArrayIndexOutOfBoundsException ae) {
			assert false: "SetManagerSimple.getItemSet() ArrayIndexOutOfBoundsException ";
			return null;
		}
	}
	
	/*
	 *  (non-Javadoc)
	 * @see cerberus.data.manager.GeneralManager#getItem(int)
	 */
	public final Object getItem( final int iItemId) {
		return getItemSet(iItemId);
	}

	/* (non-Javadoc)
	 * @see cerberus.data.manager.SetManager#getAllSetItems()
	 */
	public Set[] getAllSetItems() {
		
		Set[] resultArray = new Set[ vecSets.size() ];
		
		Iterator<Set> iter = vecSets.iterator();
		for ( int i=0 ; iter.hasNext() ; i++ ) {
			resultArray[i] = iter.next();
		}
		
		return resultArray;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.manager.GeneralManagerInterface#hasItem(int)
	 */
	public final boolean hasItem(int iItemId) {
		return hasLookupValueById( iItemId );
	}

	/* (non-Javadoc)
	 * @see cerberus.data.manager.GeneralManagerInterface#size()
	 */
	public final int size() {		
		return vecSets.size();
	}

	/* (non-Javadoc)
	 * @see cerberus.data.manager.GeneralManagerInterface#getManagerType()
	 */
	public final BaseManagerType getManagerType() {		
		return BaseManagerType.SET;
	}
	
	public boolean unregisterItem( final int iItemId,
			final BaseManagerType type  ) {
		
		if ( this.hasLookupValueById( iItemId )) {
			unregisterItemCollection( iItemId );
			return true;
		}
		return false;
	}

	public boolean registerItem( final Object registerItem, 
			final int iItemId , 
			final BaseManagerType type ) {
		
		
		try {
			Set addItem = (Set) registerItem;
			
			if ( hasLookupValueById( iItemId ) ) {
				vecSets.set( getLookupValueById( iItemId ), addItem );
				return true;
			}
			
			registerItemCollection( iItemId, vecSets.size() );
			vecSets.addElement( addItem );
				
			return true;
		}
		catch ( NullPointerException npe) {
			assert false:"cast of object ot storage falied";
			return false;
		}
	
	}
}
