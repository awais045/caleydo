/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.enroute.mappeddataview;

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.table.Average;
import org.caleydo.core.data.perspective.table.TablePerspectiveStatistics;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.id.IDType;
import org.caleydo.core.util.base.ILabelProvider;
import org.caleydo.core.util.collection.Algorithms;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.picking.APickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.view.enroute.EPickingType;

/**
 * @author Alexander Lex
 *
 */
public class ContinuousContentRenderer extends ContentRenderer {

	private static Integer rendererIDCounter = 0;

	Average average;
	boolean useShading = true;
	private int rendererID;

	public ContinuousContentRenderer(IDType rowIDType, Integer rowID, IDType resolvedRowIDType, Integer resolvedRowID,
			ATableBasedDataDomain dataDomain, Perspective columnPerspective, AGLView parentView,
			MappedDataRenderer parent, Group group, boolean isHighlightMode) {
		super(rowIDType, rowID, resolvedRowIDType, resolvedRowID, dataDomain, columnPerspective, parentView, parent,
				group, isHighlightMode);

		synchronized (rendererIDCounter) {
			rendererID = rendererIDCounter++;
		}

		init();
	}

	@Override
	public void init() {
		if (rowID == null)
			return;
		average = TablePerspectiveStatistics.calculateAverage(columnPerspective.getVirtualArray(), dataDomain,
				resolvedRowIDType, resolvedRowID);

		registerPickingListener();
	}

	@Override
	public void renderContent(GL2 gl) {
		if (x / columnPerspective.getVirtualArray().size() < parentView.getPixelGLConverter()
				.getGLWidthForPixelWidth(3)) {
			useShading = false;
		}

		if (rowID == null)
			return;
		List<SelectionType> rowSelectionTypes;

		rowSelectionTypes = parent.getSelectionManager(rowIDType).getSelectionTypes(rowIDType, rowID);

		List<SelectionType> selectionTypes = parent.sampleGroupSelectionManager.getSelectionTypes(group.getID());
		if (selectionTypes.size() > 0 && selectionTypes.contains(MappedDataRenderer.abstractGroupType)) {

			renderAverageBar(gl, rowSelectionTypes);
		} else {
			renderAllBars(gl, rowSelectionTypes);
		}

	}

	@SuppressWarnings("unchecked")
	private void renderAllBars(GL2 gl, List<SelectionType> geneSelectionTypes) {
		float xIncrement = x / columnPerspective.getVirtualArray().size();
		int experimentCount = 0;

		for (Integer columnID : columnPerspective.getVirtualArray()) {

			float value;
			if (resolvedRowID != null) {

				value = dataDomain.getNormalizedValue(resolvedRowIDType, resolvedRowID, resolvedColumnIDType, columnID);

				List<SelectionType> experimentSelectionTypes = parent.sampleSelectionManager.getSelectionTypes(
						columnIDType, columnID);

				float[] topBarColor = MappedDataRenderer.BAR_COLOR.getRGBA();
				float[] bottomBarColor = MappedDataRenderer.BAR_COLOR.getRGBA();
				// FIXME - bad hack
				if (!rowIDType.getIDCategory().getCategoryName().equals("GENE")) {

					topBarColor = MappedDataRenderer.CONTEXT_BAR_COLOR.getRGBA();
					bottomBarColor = MappedDataRenderer.CONTEXT_BAR_COLOR.getRGBA();
				}

				List<SelectionType> selectionTypes = Algorithms.mergeListsToUniqueList(experimentSelectionTypes,
						geneSelectionTypes);

				if (isHighlightMode
						&& !(selectionTypes.contains(SelectionType.MOUSE_OVER) || selectionTypes
								.contains(SelectionType.SELECTION))) {
					experimentCount++;
					continue;
				}

				if (isHighlightMode) {
					colorCalculator.setBaseColor(MappedDataRenderer.BAR_COLOR);

					colorCalculator.calculateColors(selectionTypes);

					topBarColor = colorCalculator.getPrimaryColor().getRGBA();
					bottomBarColor = colorCalculator.getSecondaryColor().getRGBA();
				}

				float leftEdge = xIncrement * experimentCount;
				float upperEdge = value * y;

				// gl.glPushName(parentView.getPickingManager().getPickingID(
				// parentView.getID(), PickingType.ROW_PRIMARY.name(), rowID));

				Integer resolvedSampleID = columnIDMappingManager.getID(dataDomain.getPrimaryIDType(columnIDType),
						parent.sampleIDType, columnID);
				if (resolvedSampleID != null) {
					gl.glPushName(parentView.getPickingManager().getPickingID(parentView.getID(),
							EPickingType.SAMPLE.name(), resolvedSampleID));
				}
				gl.glPushName(parentView.getPickingManager().getPickingID(parentView.getID(),
						EPickingType.SAMPLE.name() + hashCode(), columnID));

				gl.glBegin(GL2.GL_QUADS);

				gl.glColor4fv(bottomBarColor, 0);
				gl.glVertex3f(leftEdge, 0, z);
				if (useShading) {
					gl.glColor3f(bottomBarColor[0] * 0.9f, bottomBarColor[1] * 0.9f, bottomBarColor[2] * 0.9f);

				}
				gl.glVertex3f(leftEdge + xIncrement, 0, z);
				if (useShading) {
					gl.glColor3f(topBarColor[0] * 0.9f, topBarColor[1] * 0.9f, topBarColor[2] * 0.9f);
				} else {
					gl.glColor4fv(topBarColor, 0);
				}

				gl.glVertex3f(leftEdge + xIncrement, upperEdge, z);
				gl.glColor4fv(topBarColor, 0);

				gl.glVertex3f(leftEdge, upperEdge, z);

				gl.glEnd();
				if (resolvedSampleID != null)
					gl.glPopName();
				gl.glPopName();

				// gl.glPopName();
				experimentCount++;
			}

		}
	}

	public void renderAverageBar(GL2 gl, List<SelectionType> geneSelectionTypes) {
		// topBarColor = MappedDataRenderer.SUMMARY_BAR_COLOR;
		// bottomBarColor = topBarColor;
		if (average == null)
			return;

		colorCalculator.setBaseColor(MappedDataRenderer.SUMMARY_BAR_COLOR);

		List<List<SelectionType>> selectionLists = new ArrayList<List<SelectionType>>();
		selectionLists.add(geneSelectionTypes);

		// for (Integer sampleID : columnPerspective.getVirtualArray()) {
		// // Integer resolvedSampleID = columnIDMappingManager.getID(
		// // dataDomain.getSampleIDType(), parent.sampleIDType,
		// // experimentID);
		//
		// selectionLists.add(parent.sampleSelectionManager.getSelectionTypes(
		// columnIDType, sampleID));
		// }

		colorCalculator.calculateColors(Algorithms.mergeListsToUniqueList(selectionLists));
		float[] topBarColor = colorCalculator.getPrimaryColor().getRGBA();
		float[] bottomBarColor = colorCalculator.getSecondaryColor().getRGBA();

		gl.glPushName(parentView.getPickingManager().getPickingID(parentView.getID(),
				EPickingType.SAMPLE_GROUP_RENDERER.name(), rendererID));
		gl.glBegin(GL2.GL_QUADS);
		gl.glColor4fv(bottomBarColor, 0);
		gl.glVertex3f(0, y / 3, z);
		gl.glColor3f(bottomBarColor[0] * 0.9f, bottomBarColor[1] * 0.9f, bottomBarColor[2] * 0.9f);
		gl.glVertex3d(average.getArithmeticMean() * x, y / 3, z);
		gl.glColor3f(topBarColor[0] * 0.9f, topBarColor[1] * 0.9f, topBarColor[2] * 0.9f);
		gl.glVertex3d(average.getArithmeticMean() * x, y / 3 * 2, z);
		gl.glColor4fv(topBarColor, 0);
		gl.glVertex3f(0, y / 3 * 2, z);
		gl.glEnd();

		gl.glColor3f(0, 0, 0);
		gl.glLineWidth(0.5f);
		gl.glBegin(GL.GL_LINE_STRIP);
		gl.glVertex3f(0, y / 3, z);
		gl.glVertex3d(average.getArithmeticMean() * x, y / 3, z);
		gl.glVertex3d(average.getArithmeticMean() * x, y / 3 * 2, z);
		gl.glVertex3f(0, y / 3 * 2, z);
		gl.glEnd();

		float lineZ = z + 0.01f;

		gl.glColor3f(0, 0, 0);
		// gl.glColor3f(1 , 1, 1);

		gl.glLineWidth(0.8f);

		float xMinusDeviation = (float) (average.getArithmeticMean() - average.getStandardDeviation()) * x;
		float xPlusDeviation = (float) (average.getArithmeticMean() + average.getStandardDeviation()) * x;

		float lineTailHeight = parentView.getPixelGLConverter().getGLHeightForPixelHeight(3);

		gl.glBegin(GL.GL_LINES);
		gl.glVertex3f(xMinusDeviation, y / 2, lineZ);
		gl.glVertex3f(xPlusDeviation, y / 2, lineZ);

		gl.glLineWidth(0.6f);

		gl.glVertex3f(xPlusDeviation, y / 2 - lineTailHeight, lineZ);
		gl.glVertex3f(xPlusDeviation, y / 2 + lineTailHeight, lineZ);

		gl.glVertex3f(xMinusDeviation, y / 2 - lineTailHeight, lineZ);
		gl.glVertex3f(xMinusDeviation, y / 2 + lineTailHeight, lineZ);

		gl.glEnd();
		gl.glPopName();

	}

	private void registerPickingListener() {
		pickingListener = new APickingListener() {

			@Override
			public void clicked(Pick pick) {

				parent.sampleSelectionManager.clearSelection(SelectionType.SELECTION);

				parent.sampleSelectionManager.addToType(SelectionType.SELECTION, columnIDType, columnPerspective
						.getVirtualArray().getIDs());
				parent.sampleSelectionManager.triggerSelectionUpdateEvent();

				parent.sampleGroupSelectionManager.clearSelection(SelectionType.SELECTION);

				parent.sampleGroupSelectionManager.addToType(SelectionType.SELECTION, group.getID());
				parent.sampleGroupSelectionManager.triggerSelectionUpdateEvent();
				parentView.setDisplayListDirty();
			}

		};

		parentView.addIDPickingListener(pickingListener, EPickingType.SAMPLE_GROUP_RENDERER.name(), rendererID);

		for (final Integer sampleID : columnPerspective.getVirtualArray()) {
			// FIXME: add one type listener
			parentView.addIDPickingTooltipListener(new ILabelProvider() {

				@Override
				public String getProviderName() {
					return "";
				}

				@Override
				public String getLabel() {
					return ""
							+ dataDomain.getRawAsString(resolvedRowIDType, resolvedRowID, resolvedColumnIDType,
									sampleID);
				}
			}, EPickingType.SAMPLE.name() + hashCode(), sampleID);

		}

	}

	@Override
	protected boolean permitsWrappingDisplayLists() {
		return false;
	}

}
