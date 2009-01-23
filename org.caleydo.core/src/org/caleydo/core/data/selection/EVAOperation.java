package org.caleydo.core.data.selection;

/**
 * List of operations allowed in virtual array deltas ({@link VADeltaItem}
 * 
 * @author Alexander Lex
 * 
 */
public enum EVAOperation
{
	/**
	 * Append an element at the end of a virtual array
	 */
	APPEND,
	/**
	 * Append an element at the end of a virtual array, if the element is not
	 * yet contained in the list
	 */
	APPEND_UNIQUE,
	/**
	 * Add an element at a specified index
	 */
	ADD,
	/**
	 * Remove an element at a specified index
	 */
	REMOVE,
	/**
	 * Remove all occurrences of a specific element
	 */
	REMOVE_ELEMENT,
	/**
	 * Move an element from a specific index to another index
	 */
	MOVE,
	/**
	 * Move an element at a specific index one to the right
	 */
	MOVE_RIGHT,
	/**
	 * Move an element at a specific index one to the left
	 */
	MOVE_LEFT,
	/**
	 * Copy an element at a specific index. The result will be added at index +
	 * 1, the rest will be moved one to the right
	 */
	COPY
}
