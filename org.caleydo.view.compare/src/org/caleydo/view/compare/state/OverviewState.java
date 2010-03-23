package org.caleydo.view.compare.state;

import gleem.linalg.Vec3f;

import java.awt.Point;
import java.util.ArrayList;

import javax.media.opengl.GL;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.mapping.EIDCategory;
import org.caleydo.core.data.selection.ContentVirtualArray;
import org.caleydo.core.data.selection.SelectionCommand;
import org.caleydo.core.data.selection.StorageVAType;
import org.caleydo.core.data.selection.delta.ISelectionDelta;
import org.caleydo.core.manager.IUseCase;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.manager.picking.PickingManager;
import org.caleydo.core.manager.usecase.EDataDomain;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.util.GLCoordinateUtils;
import org.caleydo.core.view.opengl.util.draganddrop.DragAndDropController;
import org.caleydo.core.view.opengl.util.texture.TextureManager;
import org.caleydo.core.view.opengl.util.vislink.NURBSCurve;
import org.caleydo.view.compare.GLCompare;
import org.caleydo.view.compare.HeatMapWrapper;
import org.caleydo.view.compare.SetBar;
import org.caleydo.view.compare.layout.AHeatMapLayout;
import org.caleydo.view.compare.layout.HeatMapLayoutOverviewLeft;
import org.caleydo.view.compare.layout.HeatMapLayoutOverviewMid;
import org.caleydo.view.compare.layout.HeatMapLayoutOverviewRight;
import org.caleydo.view.compare.rendercommand.RenderCommandFactory;
import org.caleydo.view.compare.renderer.CompareConnectionBandRenderer;
import org.caleydo.view.compare.renderer.ICompareConnectionRenderer;

import com.sun.opengl.util.j2d.TextRenderer;

public class OverviewState extends ACompareViewStateStatic {

	private static final float HEATMAP_WRAPPER_OVERVIEW_GAP_PORTION = 0.8f;
	private static final float HEATMAP_WRAPPER_SPACE_PORTION = 0.7f;

	private ICompareConnectionRenderer compareConnectionRenderer;

	public OverviewState(GLCompare view, int viewID, TextRenderer textRenderer,
			TextureManager textureManager, PickingManager pickingManager,
			GLMouseListener glMouseListener, SetBar setBar,
			RenderCommandFactory renderCommandFactory, EDataDomain dataDomain,
			IUseCase useCase, DragAndDropController dragAndDropController,
			CompareViewStateController compareViewStateController) {

		super(view, viewID, textRenderer, textureManager, pickingManager,
				glMouseListener, setBar, renderCommandFactory, dataDomain, useCase,
				dragAndDropController, compareViewStateController);
		this.setBar.setPosition(new Vec3f(0.0f, 0.0f, 0.5f));
		compareConnectionRenderer = new CompareConnectionBandRenderer();
		numSetsInFocus = 4;
	}

	@Override
	public void drawActiveElements(GL gl) {

		for (HeatMapWrapper heatMapWrapper : heatMapWrappers) {
			heatMapWrapper.drawRemoteItems(gl, glMouseListener, pickingManager);
		}
	}

	@Override
	public void buildDisplayList(GL gl) {

		gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);

		for (HeatMapWrapper heatMapWrapper : heatMapWrappers) {
			heatMapWrapper.drawLocalItems(gl, textureManager, pickingManager,
					glMouseListener, viewID);
		}

		IViewFrustum viewFrustum = view.getViewFrustum();

		setBar.setWidth(viewFrustum.getWidth());
		setBar.render(gl);

		for (int i = 0; i < heatMapWrappers.size() - 1; i++) {
			// renderTree(gl, heatMapWrappers.get(i), heatMapWrappers.get(i +
			// 1));
			// renderOverviewRelations(gl, heatMapWrappers.get(i),
			// heatMapWrappers
			// .get(i + 1));

			renderGroupBand(gl, heatMapWrappers.get(i), heatMapWrappers.get(i + 1));
		}
	}

	@Override
	public void init(GL gl) {

		compareConnectionRenderer.init(gl);
		setsChanged = false;

	}

	public void renderGroupBand(GL gl, HeatMapWrapper leftHeatMapWrapper,
			HeatMapWrapper rightHeatMapWrapper) {

		ArrayList<Vec3f> points = new ArrayList<Vec3f>();
		
		ContentVirtualArray overview = leftHeatMapWrapper.getContentVA().clone();
		ContentVirtualArray overviewRight = rightHeatMapWrapper.getContentVA().clone();

		float overviewDistance = rightHeatMapWrapper
				.getLeftOverviewLinkPositionFromIndex(0).x()
				- leftHeatMapWrapper.getLeftOverviewLinkPositionFromIndex(0).x();
		float firstLevelOffset = overviewDistance / 5;

		float sampleHeight = leftHeatMapWrapper.getLayout().getOverviewHeight()
				/ overview.size();
		float top = leftHeatMapWrapper.getLayout().getOverviewPosition().y()
				+ leftHeatMapWrapper.getLayout().getOverviewHeight();

		leftHeatMapWrapper.sort(rightHeatMapWrapper.getContentVAsOfHeatMaps(false),
				false, false);
		rightHeatMapWrapper.sort(leftHeatMapWrapper.getContentVAsOfHeatMaps(false),
				false, false);

		for (ContentVirtualArray groupVA : leftHeatMapWrapper
				.getContentVAsOfHeatMaps(false)) {

			// Is there a better way to find the y pos of the cluster beginning
			float groupTopY = 0;
			for (Integer overviewContentID : overview) {
				if (groupVA.containsElement(overviewContentID) == 0)
					continue;

				groupTopY = overview.indexOf(overviewContentID) * sampleHeight;
				break;
			}

			for (Integer contentID : groupVA) {

				points.clear();
				
				float overviewX = leftHeatMapWrapper
						.getRightOverviewLinkPositionFromContentID(contentID).x();
				float overviewY = leftHeatMapWrapper
						.getRightOverviewLinkPositionFromContentID(contentID).y();

				float sortedY = top - groupTopY - groupVA.indexOf(contentID)
						* sampleHeight;

				setRelationColor(gl, leftHeatMapWrapper, contentID);

				points.add(new Vec3f(overviewX, overviewY, 0));
				points.add(new Vec3f(overviewX + firstLevelOffset, sortedY, 0));				

//				gl.glBegin(GL.GL_LINES);
//				gl.glVertex3f(overviewX, overviewY, 0);
//				gl.glVertex3f(overviewX + firstLevelOffset, sortedY, 0);
//				gl.glEnd();

				ArrayList<ContentVirtualArray> rightContentVAs = rightHeatMapWrapper
						.getContentVAsOfHeatMaps(false);

				for (int rightClusterIndex = 0; rightClusterIndex < rightContentVAs
						.size(); rightClusterIndex++) {

					if (rightContentVAs.get(rightClusterIndex).containsElement(contentID) == 0)
						continue;

					// Is there a better way to find the y pos of the cluster
					// beginning
					float rightGroupTopY = 0;
					for (Integer overviewRightContentID : overviewRight) {
						if (rightContentVAs.get(rightClusterIndex).containsElement(
								overviewRightContentID) == 0)
							continue;

						rightGroupTopY = overviewRight.indexOf(overviewRightContentID)
								* sampleHeight;
						break;
					}

					float overviewRightX = rightHeatMapWrapper
							.getLeftOverviewLinkPositionFromContentID(contentID).x();
					float overviewRightY = rightHeatMapWrapper
							.getLeftOverviewLinkPositionFromContentID(contentID).y();

					float sortedRightY = top - rightGroupTopY
							- rightContentVAs.get(rightClusterIndex).indexOf(contentID)
							* sampleHeight;

					points.add(new Vec3f(overviewRightX - firstLevelOffset, sortedRightY, 0));
					points.add(new Vec3f(overviewRightX, overviewRightY, 0));
										
//					gl.glPushName(pickingManager.getPickingID(viewID,
//							EPickingType.POLYLINE_SELECTION, contentID));
//
//					gl.glBegin(GL.GL_LINES);
//					gl.glVertex3f(overviewX + firstLevelOffset, sortedY, 0);
//					gl.glVertex3f(overviewRightX - firstLevelOffset, sortedRightY, 0);
//					gl.glEnd();
//
//					gl.glBegin(GL.GL_LINES);
//					gl.glVertex3f(overviewRightX - firstLevelOffset, sortedRightY, 0);
//					gl.glVertex3f(overviewRightX, overviewRightY, 0);
//					gl.glEnd();
//					
//					gl.glPopMatrix();
				}
			
				NURBSCurve curve = new NURBSCurve(points, NUMBER_OF_SPLINE_POINTS);
				points = curve.getCurvePoints();

				gl.glPushName(pickingManager.getPickingID(viewID,
						EPickingType.POLYLINE_SELECTION, contentID));

				gl.glBegin(GL.GL_LINE_STRIP);
				for (int i = 0; i < points.size(); i++)
					gl.glVertex3f(points.get(i).x(), points.get(i).y(), 0);
				gl.glEnd();

				gl.glPopName();
			}
		}		
	}

	@Override
	public ECompareViewStateType getStateType() {
		return ECompareViewStateType.OVERVIEW;
	}

	@Override
	public void duplicateSetBarItem(int itemID) {
		setBar.handleDuplicateSetBarItem(itemID);

	}

	@Override
	public void handleSelectionUpdate(ISelectionDelta selectionDelta,
			boolean scrollToSelection, String info) {
		for (HeatMapWrapper heatMapWrapper : heatMapWrappers) {
			heatMapWrapper.handleSelectionUpdate(selectionDelta, scrollToSelection, info);
			heatMapWrapper.getOverview().updateHeatMapTextures(
					heatMapWrapper.getContentSelectionManager());
		}
	}

	@Override
	public void adjustPValue() {
		// TODO Auto-generated method stub

	}

	@Override
	public int getMaxSetsInFocus() {
		return 6;
	}

	@Override
	public int getMinSetsInFocus() {
		return 2;
	}

	@Override
	public void handleStateSpecificPickingEvents(EPickingType ePickingType,
			EPickingMode pickingMode, int iExternalID, Pick pick, boolean isControlPressed) {

	}

	@Override
	public void handleSelectionCommand(EIDCategory category,
			SelectionCommand selectionCommand) {

		for (HeatMapWrapper heatMapWrapper : heatMapWrappers) {
			if (category == heatMapWrapper.getContentSelectionManager().getIDType()
					.getCategory())
				heatMapWrapper.getContentSelectionManager().executeSelectionCommand(
						selectionCommand);
			else
				return;
		}
	}

	@Override
	public void setSetsInFocus(ArrayList<ISet> setsInFocus) {
		// FIXME: Maybe we can put this in the base class.

		if (setsInFocus.size() >= getMinSetsInFocus()
				&& setsInFocus.size() <= getMaxSetsInFocus()) {

			this.setsInFocus = setsInFocus;

			if (layouts.isEmpty() || setsInFocus.size() != layouts.size()) {
				layouts.clear();
				heatMapWrappers.clear();

				int heatMapWrapperID = 0;
				for (ISet set : setsInFocus) {
					AHeatMapLayout layout = null;
					if (heatMapWrapperID == 0) {
						layout = new HeatMapLayoutOverviewLeft(renderCommandFactory);
					} else if (heatMapWrapperID == setsInFocus.size() - 1) {
						layout = new HeatMapLayoutOverviewRight(renderCommandFactory);
					} else {
						layout = new HeatMapLayoutOverviewMid(renderCommandFactory);
					}

					layouts.add(layout);

					HeatMapWrapper heatMapWrapper = new HeatMapWrapper(heatMapWrapperID,
							layout, view, null, useCase, view, dataDomain);
					heatMapWrappers.add(heatMapWrapper);
					heatMapWrapperID++;
				}
			}

			// FIXME: Use array of relations?
			// ISet setLeft = setsInFocus.get(0);
			// ISet setRight = setsInFocus.get(1);
			// relations = SetComparer.compareSets(setLeft, setRight);

			for (int i = 0; i < heatMapWrappers.size(); i++) {
				HeatMapWrapper heatMapWrapper = heatMapWrappers.get(i);
				heatMapWrapper.setSet(setsInFocus.get(i));
			}
			setsChanged = true;
			numSetsInFocus = setsInFocus.size();

			view.setDisplayListDirty();
		}
	}

	@Override
	public void handleMouseWheel(GL gl, int amount, Point wheelPoint) {
		if (amount < 0) {

			OverviewToDetailTransition transition = (OverviewToDetailTransition) compareViewStateController
					.getState(ECompareViewStateType.OVERVIEW_TO_DETAIL_TRANSITION);

			float[] wheelPointWorldCoordinates = GLCoordinateUtils
					.convertWindowCoordinatesToWorldCoordinates(gl, wheelPoint.x,
							wheelPoint.y);

			int itemOffset = 0;
			for (int i = 0; i < layouts.size() - 1; i++) {

				if ((i == layouts.size() - 2)
						&& (wheelPointWorldCoordinates[0] >= layouts.get(i).getPosition()
								.x())) {
					itemOffset = i;
					break;
				}

				if ((wheelPointWorldCoordinates[0] >= layouts.get(i).getPosition().x())
						&& (wheelPointWorldCoordinates[0] <= layouts.get(i + 1)
								.getPosition().x()
								+ (layouts.get(i + 1).getWidth() / 2.0f))) {
					itemOffset = i;
					break;
				}
			}
			compareViewStateController
					.setCurrentState(ECompareViewStateType.OVERVIEW_TO_DETAIL_TRANSITION);

			transition.initTransition(gl, itemOffset);
			view.setDisplayListDirty();
		}

	}

	@Override
	protected void setupLayouts() {

		IViewFrustum viewFrustum = view.getViewFrustum();
		float setBarHeight = setBar.getHeight();
		float heatMapWrapperPosY = setBar.getPosition().y() + setBarHeight;

		float heatMapWrapperPosX = 0.0f;

		float spaceForHeatMapWrapperOverviews = (1.0f - HEATMAP_WRAPPER_OVERVIEW_GAP_PORTION)
				* viewFrustum.getWidth();
		float heatMapWrapperWidth = HEATMAP_WRAPPER_SPACE_PORTION
				* viewFrustum.getWidth() / (float) heatMapWrappers.size();
		int numTotalExperiments = 0;
		for (HeatMapWrapper heatMapWrapper : heatMapWrappers) {
			numTotalExperiments += heatMapWrapper.getSet().getStorageVA(
					StorageVAType.STORAGE).size();
		}
		float heatMapWrapperGapWidth = (1 - HEATMAP_WRAPPER_SPACE_PORTION)
				* viewFrustum.getWidth() / (float) (heatMapWrappers.size() - 1);

		for (int i = 0; i < heatMapWrappers.size(); i++) {
			HeatMapWrapper heatMapWrapper = heatMapWrappers.get(i);
			AHeatMapLayout layout = layouts.get(i);
			int numExperiments = heatMapWrapper.getSet().getStorageVA(
					StorageVAType.STORAGE).size();
			// TODO: Maybe get info in layout from heatmapwrapper
			layout.setTotalSpaceForAllHeatMapWrappers(spaceForHeatMapWrapperOverviews);
			layout.setNumExperiments(numExperiments);
			layout.setNumTotalExperiments(numTotalExperiments);

			layout.setLayoutParameters(heatMapWrapperPosX, heatMapWrapperPosY,
					viewFrustum.getHeight() - setBarHeight, heatMapWrapperWidth);
			layout.setHeatMapWrapper(heatMapWrapper);

			heatMapWrapperPosX += heatMapWrapperWidth + heatMapWrapperGapWidth;
		}

	}
}
