package org.caleydo.core.view.opengl.canvas.hyperbolic.treelayouters.projections;

import gleem.linalg.Vec3f;

import javax.media.opengl.GL;

public interface ITreeProjection
	extends Comparable<ITreeProjection> {

	public Vec3f projectCoordinates(Vec3f fvCoords);

	public int getID();

	public void drawCanvas(GL gl);
	
	public float getProjectedLineFromCenterToBorder();

	void updateFrustumInfos(float fHeight, float fWidth, float fDepth, float[] fViewSpaceX,
		float fViewSpaceXAbs, float[] fViewSpaceY, float fViewSpaceYAbs);

	float[][] getEuclidianCanvas();

	Vec3f getNearestPointOnEuclidianBorder(Vec3f point);
	
	public float getLineFromCenterToBorderOfViewSpace();
	
	public float getLineFromPointToCenter(float fPointX, float fPointY);

	public Vec3f getRootPoint();

	public Vec3f getCenterPoint();

	int numOfAnimationStepsToGo(Vec3f vPoint);
}
