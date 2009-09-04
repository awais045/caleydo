package org.caleydo.core.util.collection;

/**
 * A pair of values, inspired by STL Caution: when using the compare function only the first element of the
 * pair is used
 * 
 * @author Alexander Lex
 * @param <T>
 *            first type
 * @param <E>
 *            second type
 */
public class Pair<T, E>
	implements Comparable<Pair<T, E>> {
	private T first;
	private E second;

	public Pair() {

	}

	/**
	 * Constructor
	 * 
	 * @param first
	 *            the first value
	 * @param second
	 *            the second value
	 */
	public Pair(T first, E second) {
		this.first = first;
		this.second = second;
	}

	public T getFirst() {
		return first;
	}

	public E getSecond() {
		return second;
	}

	public void set(T first, E second) {
		this.first = first;
		this.second = second;
	}

	public void setFirst(T first) {
		this.first = first;
	}

	public void setSecond(E second) {
		this.second = second;
	}

	// @Override
	// public int compareTo(Pair<T, E> checkedPair) {
	// int compareResultFirst = first.compareTo(checkedPair.getFirst());
	// int compareResultSecond = second.compareTo(checkedPair.getSecond());
	//			
	// if(compareResultFirst > 0 && compareResultSecond > 0)
	// return 1;
	// if(compareResultFirst == 0 && compareResultSecond == 0)
	// return 0;
	//			
	// return -1;
	// }

	@Override
	public String toString() {
		return "<" + first.toString() + ", " + second.toString() + ">";
	}

	@Override
	@SuppressWarnings("unchecked")
	public int compareTo(Pair<T, E> o) {
		if (o.getFirst() instanceof Comparable<?>) {
			return ((Comparable<T>) first).compareTo(o.getFirst());
		}
		else {
			throw new IllegalStateException("Tried to compare non-comparable values");
		}
	}

}
