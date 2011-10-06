package org.caleydo.core.event.view.grouper;

import java.util.ArrayList;

import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.event.AEvent;

public class CompareGroupsEvent
	extends AEvent {

	private ArrayList<DataTable> setsToCompare;

	public CompareGroupsEvent(ArrayList<DataTable> setsToCompare) {

		this.setsToCompare = setsToCompare;
	}

	@Override
	public boolean checkIntegrity() {
		return setsToCompare != null;
	}

	public ArrayList<DataTable> getTables() {
		return setsToCompare;
	}

	public void setTables(ArrayList<DataTable> setsToCompare) {
		this.setsToCompare = setsToCompare;
	}
}