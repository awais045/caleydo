package org.caleydo.core.view.opengl.canvas.hierarchy;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

public abstract class DrawingStateAnimation
	extends DrawingState {

	private double fPreviousTimeStamp;
	protected boolean bAnimationStarted;
	protected float fAnimationDuration;
	protected ArrayList<MovementValue> alMovementValues;

	public DrawingStateAnimation(DrawingController drawingController, GLRadialHierarchy radialHierarchy) {
		super(drawingController, radialHierarchy);
		fPreviousTimeStamp = 0;
		bAnimationStarted = false;
		alMovementValues = new ArrayList<MovementValue>();
	}

	public final void draw(float fXCenter, float fYCenter, GL gl, GLU glu) {

		double fCurrentTimeStamp = GregorianCalendar.getInstance().getTimeInMillis();

		if (!bAnimationStarted)
			draw(fXCenter, fYCenter, gl, glu, 0);
		else {
			double fTimePassed = (fCurrentTimeStamp - fPreviousTimeStamp) / 1000;
			draw(fXCenter, fYCenter, gl, glu, fTimePassed);
		}
		fPreviousTimeStamp = fCurrentTimeStamp;

	}

	@Override
	public final void handleClick(PartialDisc pdClicked) {
		// do nothing
	}

	@Override
	public final void handleMouseOver(PartialDisc pdMouseOver) {
		// do nothing
	}

	public abstract void draw(float fXCenter, float fYCenter, GL gl, GLU glu, double fTimePassed);

	protected boolean areStoppingCreteriaFulfilled() {

		int iNumTargetsReached = 0;

		for (MovementValue movementValue : alMovementValues) {
			if (movementValue.isTargetValueReached()) {
				iNumTargetsReached++;
				movementValue.setCriterionToTargetValue();
			}
		}

		return (iNumTargetsReached == alMovementValues.size());
	}

	protected MovementValue createNewMovementValue(float fStartValue, float fTargetValue, float fMovementDuration) {

		float fSpeed = (fTargetValue - fStartValue) / fMovementDuration;
		MovementValue movementValue;
		if (fSpeed > 0)
			movementValue =
				new MovementValue(fStartValue, fTargetValue, fSpeed, MovementValue.CRITERION_GREATER_OR_EQUAL);
		else
			movementValue =
				new MovementValue(fStartValue, fTargetValue, fSpeed, MovementValue.CRITERION_SMALLER_OR_EQUAL);

		alMovementValues.add(movementValue);
		
		return movementValue;
	}
	
	protected void moveValues(double dTimePassed) {
		
		for (MovementValue movementValue : alMovementValues) {
			movementValue.move(dTimePassed);
		}
	}

	public float getAnimationDuration() {
		return fAnimationDuration;
	}

	public void setAnimationDuration(float fAnimationDuration) {
		this.fAnimationDuration = fAnimationDuration;
	}

}
