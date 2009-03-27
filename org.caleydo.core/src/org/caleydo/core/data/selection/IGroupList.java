package org.caleydo.core.data.selection;

/**
 * A group list provides handling of groups (generated by a cluster algorithm or by user interaction). A group
 * is a bulk of genes or patients.
 * 
 * @author Bernhard Schlegl
 */

public interface IGroupList
	extends Iterable<Group> {

	/**
	 * Returns an Iterator<Group> of type GroupIterator, which allows to iterate over the group list
	 */
	public GroupIterator iterator();

	/**
	 * Merges two elements in the list. This means the second group will be deleted and the number of elements
	 * will be added to the first group. The selection type, the index of the example, and the status of
	 * collapse will not be changed in the first group. Returns true if operation executed correctly and false
	 * otherwise
	 * 
	 * @param virtualArray
	 *            the virtual array for updating indexes
	 * @param iIndex1
	 *            the index of the first element
	 * @param iIndex2
	 *            the index of the second element
	 * @return true if operation executed correctly otherwise false
	 */
	public boolean merge(IVirtualArray virtualArray, int iIndex1, int iIndex2);

	/**
	 * Interchanges two elements in the list. Returns true if operation executed correctly and false otherwise
	 * 
	 * @param virtualArray
	 *            the virtual array for updating indexes
	 * @param iIndex1
	 *            the index of the first element
	 * @param iIndex2
	 *            the index of the second element
	 * @return true if operation executed correctly otherwise false
	 */
	public boolean interchange(IVirtualArray virtualArray, int iIndex1, int iIndex2);

	/**
	 * Splits the element at the specified index into two or three new groups. The number of generated
	 * elements depends on the position of the new group inside the old one. Returns true if operation
	 * executed correctly and false otherwise. An error occurs when the indexes in the VA exceed the indexes
	 * determined by the group
	 * 
	 * @param iIndex
	 *            the index of the element to split
	 * @param iVAIdx1
	 *            the first index of the new group in the VA
	 * @param iVAIdx2
	 *            the last index of the new group in the VA
	 * @return true if operation executed correctly otherwise false
	 */
	public boolean split(int iIndex, int iVAIdx1, int iVAIdx2);

	/**
	 * Returns the element at the specified index in the group list
	 * 
	 * @param iIndex
	 *            the index
	 * @return the element at the index
	 */
	public Group get(int iIndex);

	/**
	 * Adds an element to the end of the list.
	 * 
	 * @param iNewElement
	 *            the index to the collection
	 */
	public void append(Group newElement);

	/**
	 * Adds an element to the end of the list, if the element is not already contained.
	 * 
	 * @param newElement
	 *            the index to the collection
	 * @exception IllegalArgumentException
	 *                if the value of the new element is larger than allowed. The maximum allowed value is the
	 *                length of the collection which is managed - 1
	 * @return true if the array was modified, else false
	 */
	public boolean appendUnique(Group newElement);

	/**
	 * Inserts the specified element at the specified position in this list. Shifts the element currently at
	 * that position (if any) and any subsequent elements to the right (adds one to their indices).
	 * 
	 * @param iIndex
	 *            the position on which to insert the new element
	 * @param newElement
	 *            the index to the collection
	 */
	public void add(int iIndex, Group newElement);

	/**
	 * Replaces the element at the specified position in this list with the specified element.
	 * 
	 * @param iIndex
	 * @param newElement
	 */
	public void set(int iIndex, Group newElement);

	/**
	 * Copies the element at index iIndex to the next index. Shifts the element currently at that position (if
	 * any) and any subsequent elements to the right (adds one to their indices).
	 * 
	 * @param iIndex
	 *            the index of the element to be copied
	 */
	public void copy(int iIndex);

	// /**
	// * Moves the element at iIndex to the left
	// *
	// * @param iIndex
	// * the index of the element to be moved
	// */
	// public void moveLeft(int iIndex);
	//
	// /**
	// * Moves the element at the specified src index to the target index. The element formerly at iSrcIndex
	// is
	// * at iTargetIndex after this operation. The rest of the elements can change the index.
	// *
	// * @param iSrcIndex
	// * the src index of the element
	// * @param iTargetIndex
	// * the target index of the element
	// */
	// public void move(int iSrcIndex, int iTargetIndex);
	//
	// /**
	// * Moves the element at iIndex to the right
	// *
	// * @param iIndex
	// * the index of the element to be moved
	// */
	// public void moveRight(int iIndex);

	/**
	 * Removes the element at the specified index. Shifts any subsequent elements to the left (subtracts one
	 * from their indices).
	 * 
	 * @param iIndex
	 *            the index of the element to be removed
	 * @return the Element that was removed from the list
	 */
	public Group remove(int iIndex);

	/**
	 * <p>
	 * Remove an element from the list. Shifts any subsequent elements to the left (subtracts one from their
	 * indices).
	 * </p>
	 * <p>
	 * Notice that this has a complexity of O(n) TODO: probably add a higher performance version, with an
	 * additional hash map
	 * </p>
	 * 
	 * @param element
	 *            the element to be removed
	 */
	public void removeByElement(Group element);

	/**
	 * Returns the size of the group list
	 * 
	 * @return the size
	 */
	public Integer size();

	/**
	 * Reset the group list to the indices in the managed data entity
	 */
	public void reset();

	/**
	 * Reset the group list to contain no elements
	 */
	public void clear();

	/**
	 * Returns the index of the specified element in this list, or -1 if this list does not contain the
	 * element.
	 * 
	 * @param element
	 *            element to search for
	 * @return the index of the the specified element in this list, or -1 if this list does not contain the
	 *         element
	 */
	public int indexOf(Group element);

	/**
	 * Applies the operations specified in the delta to the group list
	 * 
	 * @param delta
	 */
	public void setDelta(IVirtualArrayDelta delta);

	/**
	 * Checks whether an element is contained in the group list.
	 * 
	 * @param element
	 *            the element to be checked
	 * @return true or false
	 */
	public boolean containsElement(Group element);

}