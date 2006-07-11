/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.manager.storage;

import java.util.Vector;
import java.util.Iterator;

import cerberus.manager.GeneralManager;
import cerberus.manager.StorageManager;
import cerberus.manager.collection.CollectionManager;
import cerberus.manager.type.BaseManagerGroupType;
import cerberus.manager.type.BaseManagerType;
import cerberus.manager.singelton.SingeltonManager;
//import java.util.Hashtable;

import cerberus.data.collection.Storage;
//import cerberus.data.collection.StorageType;
import cerberus.data.collection.storage.FlatThreadStorageSimple;
import cerberus.util.exception.PrometheusRuntimeException;

/**
 * @author Michael Kalkusch
 *
 */
public class StorageManagerSimple 
extends CollectionManager
implements StorageManager {
	
	/**
	 * Vector holds a list of all Storage's
	 */
	protected Vector<Storage> vecStorage;
	
	/**
	 * 
	 */
	public StorageManagerSimple( GeneralManager setGeneralManager,
			final int iInitSizeContainer ) {
		
		super( setGeneralManager, 
				iUniqueId_TypeOffset_Storage );
		
		assert setGeneralManager != null : "Constructor with null-pointer to singelton";
		assert iInitSizeContainer > 0 : "Constructor with iInitSizeContainer < 1";
			
		vecStorage = new Vector< Storage > ( iInitSizeContainer );
		
		refGeneralManager.getSingelton().setStorageManager( this );
			
	}

	/* (non-Javadoc)
	 * @see cerberus.data.manager.StorageManager#createStorage()
	 */
	public Storage createStorage( final BaseManagerType useStorageType ) {
		
		if ( useStorageType.getGroupType() != BaseManagerGroupType.STORAGE ) {
			throw new PrometheusRuntimeException("try to create object with wrong type " + useStorageType.name() );
		}

		
		final int iNewId = this.createNewId( useStorageType );
		
		switch ( useStorageType ) {
			case STORAGE:
			case STORAGE_FLAT:
				return new FlatThreadStorageSimple( iNewId, refGeneralManager, null );				
				
			default:
				throw new PrometheusRuntimeException("StorageManagerSimple.createStorage() failed due to unhandled type [" +
						useStorageType.toString() + "]");
		}
		
		//registerItem( newStorage, iNewId, useStorageType );
		
		//return null;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.manager.StorageManager#deleteStorage(cerberus.data.collection.Storage)
	 */
	public boolean deleteStorage(Storage deleteStorage ) {
		return vecStorage.remove( deleteStorage );
	}
	
	/* (non-Javadoc)
	 * @see cerberus.data.manager.StorageManager#deleteStorage(cerberus.data.collection.Storage)
	 */
	public boolean deleteStorage( final int iItemId ) {
		try {
			unregisterItem( iItemId, null );
			
			vecStorage.remove( iItemId );
			return true;
		}
		catch (ArrayIndexOutOfBoundsException ae) {
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see cerberus.data.manager.StorageManager#getItemStorage(int)
	 */
	public Storage getItemStorage( final int iItemId) {
		
		try {
			return vecStorage.get( getLookupValueById(iItemId) );
		} 
		catch (NullPointerException npe) {
			assert false:"uniqueId was not found";
			return null;
		}
		catch (ArrayIndexOutOfBoundsException ae) {
			assert false: "StorageManagerSimple.getItemStorage() ArrayIndexOutOfBoundsException ";
			return null;
		}
	}

	/**
	 *  
	 * @see cerberus.manager.GeneralManager#getItem(int)
	 * 
	 * @throws ArrayIndexOutOfBoundsException
	 */
	public final Object getItem( final int iItemId) {
		return getItemStorage(iItemId);
	}
	
	/* (non-Javadoc)
	 * @see cerberus.data.manager.StorageManager#getAllStorageItems()
	 */
	public Storage[] getAllStorageItems() {
		
		Storage[] resultArray = new Storage[ vecStorage.size() ];
		
		Iterator<Storage> iter = vecStorage.iterator();
		for ( int i=0 ; iter.hasNext() ; i++ ) {
			resultArray[i] = iter.next();
		}
		
		return resultArray;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see cerberus.data.manager.StorageManager#getAllStorageItemsVector()
	 */
	public Vector<Storage> getAllStorageItemsVector() {
		return vecStorage;
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
		return vecStorage.size();
	}

	/* (non-Javadoc)
	 * @see cerberus.data.manager.GeneralManagerInterface#getManagerType()
	 */
	public final BaseManagerType getManagerType() {		
		return BaseManagerType.STORAGE_FLAT;
	}
	
	public boolean unregisterItem( final int iItemId,
			final BaseManagerType type  ) {
		
		if ( this.hasLookupValueById( iItemId )) {
			vecStorage.remove( 
					(int) getLookupValueById( iItemId ));
			unregisterItemCollection( iItemId );
			return true;
		}
		return false;
	}

	public boolean registerItem( final Object registerItem, 
			final int iItemId , 
			final BaseManagerType type ) {
		
		
		try {
			
			Storage addItem = (Storage) registerItem;
			
			if ( hasLookupValueById( iItemId ) ) {
				vecStorage.set( getLookupValueById( iItemId ), addItem );
				return true;
			}
			
			registerItemCollection( iItemId, vecStorage.size() );
			vecStorage.addElement( addItem );
				
			return true;
		}
		catch ( NullPointerException npe) {
			assert false:"cast of object ot storage falied";
			return false;
		}
		
				
	}

}
