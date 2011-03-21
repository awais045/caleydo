package org.caleydo.view.visbricks.dimensiongroup;

import java.util.HashMap;

import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.view.visbricks.brick.GLBrick;


public class SubGroupMatch {
	
	private GLBrick glBrick;
	
	private float leftAnchorYStart;

	private float leftAnchorYEnd;
	
	private float rightAnchorYStart;
	
	private float rightAnchorYEnd;

	private HashMap<SelectionType, Float> hashSelectionTypeToRatio = new HashMap<SelectionType, Float>();
	
	public SubGroupMatch(GLBrick glBrick) {
		this.glBrick = glBrick;
	}
	
	public void setLeftAnchorYStart(float leftAnchorYStart) {
		this.leftAnchorYStart = leftAnchorYStart;
	}
	
	public void setLeftAnchorYEnd(float leftAnchorYEnd) {
		this.leftAnchorYEnd = leftAnchorYEnd;
	}

	public void setRightAnchorYStart(float rightAnchorYStart) {
		this.rightAnchorYStart = rightAnchorYStart;
	}
	
	public void setRightAnchorYEnd(float rightAnchorYEnd) {
		this.rightAnchorYEnd = rightAnchorYEnd;
	}
	
	public float getLeftAnchorYTop() {
		return leftAnchorYStart;
	}
	
	public float getLeftAnchorYBottom() {
		return leftAnchorYEnd;
	}
	
	public float getRightAnchorYTop() {
		return rightAnchorYStart;
	}
	
	public float getRightAnchorYBottom() {
		return rightAnchorYEnd;
	}
	
	public GLBrick getBrick() {
		return glBrick;
	}
	
	public void addSelectionTypeRatio(float ratio, SelectionType selectionType) {
		hashSelectionTypeToRatio.put(selectionType, ratio);
	}
	
	public HashMap<SelectionType, Float> getHashRatioToSelectionType() {
		return hashSelectionTypeToRatio;
	}
}
