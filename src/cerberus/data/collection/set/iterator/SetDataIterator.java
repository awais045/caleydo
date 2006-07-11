package cerberus.data.collection.set.iterator;

import java.util.Iterator;

public interface SetDataIterator<E extends Number> {

	public void begin();
	
	public boolean hasNext();
	
	public boolean hasNextInSelection();
	
	public int nextIndex();
	
	public int nextSelection();
	
}
