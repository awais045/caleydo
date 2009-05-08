package org.caleydo.core.view.opengl.canvas.remote.bucket;
/**
 *  This class implements the renderConnectionLines method which is the same for all connection
 *  graph types. renderLinBundling is forwarded to the specific graph drawing classes 
 */


import gleem.linalg.Mat4f;
import gleem.linalg.Rotf;
import gleem.linalg.Vec3f;

import java.util.ArrayList;

import javax.media.opengl.GL;

import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.selection.SelectedElementRep;
import org.caleydo.core.manager.IViewManager;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.view.opengl.canvas.remote.AGLConnectionLineRenderer;
import org.caleydo.core.view.opengl.util.hierarchy.RemoteLevel;
import org.caleydo.core.view.opengl.util.hierarchy.RemoteLevelElement;

public abstract class BucketGraphDrawingAdapter
	extends AGLConnectionLineRenderer {

	public BucketGraphDrawingAdapter(final RemoteLevel focusLevel, final RemoteLevel stackLevel,
		final RemoteLevel poolLevel) {
		super(focusLevel, stackLevel, poolLevel);
	}
	
	@Override
	protected void renderConnectionLines(final GL gl) {
		Vec3f vecTranslation;
		Vec3f vecScale;

		Rotf rotation;
		Mat4f matSrc = new Mat4f();
		Mat4f matDest = new Mat4f();
		matSrc.makeIdent();
		matDest.makeIdent();

		// int iViewID = 0;
		RemoteLevel activeLevel = null;
		RemoteLevelElement remoteLevelElement = null;

		IViewManager viewGLCanvasManager = GeneralManager.get().getViewGLCanvasManager();

		for (EIDType idType : connectedElementRepManager.getOccuringIDTypes()) {
			ArrayList<ArrayList<Vec3f>> alPointLists = null;

			for (int iSelectedElementID : connectedElementRepManager.getIDList(idType)) {
				for (SelectedElementRep selectedElementRep : connectedElementRepManager
					.getSelectedElementRepsByElementID(idType, iSelectedElementID)) {
					remoteLevelElement =
						viewGLCanvasManager.getGLEventListener(selectedElementRep.getContainingViewID())
							.getRemoteLevelElement();
					// views that are not rendered remote
					if (remoteLevelElement == null) {
						continue;
					}

					activeLevel = remoteLevelElement.getRemoteLevel();

					if (activeLevel == stackLevel || activeLevel == focusLevel) {
						vecTranslation = remoteLevelElement.getTransform().getTranslation();
						vecScale = remoteLevelElement.getTransform().getScale();
						rotation = remoteLevelElement.getTransform().getRotation();

						ArrayList<Vec3f> alPoints = selectedElementRep.getPoints();
						ArrayList<Vec3f> alPointsTransformed = new ArrayList<Vec3f>();

						for (Vec3f vecCurrentPoint : alPoints) {
							alPointsTransformed.add(transform(vecCurrentPoint, vecTranslation, vecScale,
								rotation, remoteLevelElement));
						}
						int iKey = selectedElementRep.getContainingViewID();

						alPointLists = hashViewToPointLists.get(iKey);
						if (alPointLists == null) {
							alPointLists = new ArrayList<ArrayList<Vec3f>>();
							hashViewToPointLists.put(iKey, alPointLists);
						}

						alPointLists.add(alPointsTransformed);
					}
				}

				if (hashViewToPointLists.size() > 1) {
					renderLineBundling(gl, new float[] { 0, 0, 0 });
					hashViewToPointLists.clear();
				}
			}
		}
	}

	@Override
	protected abstract void renderLineBundling(GL gl, float[] arColor);

}
