/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander Lex, Christian Partl, Johannes Kepler
 * University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.view.heatmap.v2;

import static org.caleydo.view.heatmap.heatmap.GLHeatMap.SELECTION_HIDDEN;
import gleem.linalg.Vec2f;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.TablePerspectiveSelectionMixin;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.event.EventListenerManager.DeepScan;
import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.id.IDType;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.color.mapping.UpdateColorMappingEvent;
import org.caleydo.core.view.contextmenu.AContextMenuItem;
import org.caleydo.core.view.contextmenu.item.BookmarkMenuItem;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.core.view.opengl.layout2.PickableGLElement;
import org.caleydo.core.view.opengl.picking.IPickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.datadomain.genetic.GeneticDataDomain;
import org.caleydo.datadomain.pathway.contextmenu.container.GeneMenuItemContainer;
import org.caleydo.view.heatmap.heatmap.GLHeatMap;
import org.caleydo.view.heatmap.v2.spacing.IRecordSpacingLayout;
import org.caleydo.view.heatmap.v2.spacing.IRecordSpacingStrategy;
import org.caleydo.view.heatmap.v2.spacing.UniformRecordSpacingCalculator;

import com.google.common.base.Preconditions;
import com.jogamp.common.util.IntIntHashMap;

public class HeatMapElement extends PickableGLElement implements
		TablePerspectiveSelectionMixin.ITablePerspectiveMixinCallback {
	/**
	 * maximum pixel size of a text
	 */
	private static final int MAX_TEXT_HEIGHT = 12;

	private final static int TEXT_WIDTH = 80; // [px]

	/** hide elements with the state {@link #SELECTION_HIDDEN} if this is true */
	private boolean hideElements = true;

	@DeepScan
	private final TablePerspectiveSelectionMixin mixin;

	private final IntIntHashMap recordPickingIds = new IntIntHashMap();
	private final IPickingListener recordPickingListener = new IPickingListener() {
		@Override
		public void pick(Pick pick) {
			onRecordPick(pick.getObjectID(), pick);
		}
	};
	private final SelectionRenderer recordSelectionRenderer;

	private final IntIntHashMap dimensionPickingIds = new IntIntHashMap();
	private final IPickingListener dimensionPickingListener = new IPickingListener() {
		@Override
		public void pick(Pick pick) {
			onDimensionPick(pick.getObjectID(), pick);
		}
	};
	private final SelectionRenderer dimensionSelectionRenderer;

	// helper as we have record and dimension need a central point when both is done
	private List<AContextMenuItem> toShow = new ArrayList<>(2);

	// TODO parameterize
	private final IRecordSpacingStrategy recordSpacingStrategy = new UniformRecordSpacingCalculator();
	private IRecordSpacingLayout recordSpacing;

	/**
	 * strategy to render a single field in the heat map
	 */
	private final IBlockColorer blockColorer;

	private final HeatMapTextureRenderer textureRenderer;

	/**
	 * whether the labels of the
	 */
	private boolean showDimensionLabels = false;
	private boolean showRecordLabels = false;

	public HeatMapElement(TablePerspective tablePerspective) {
		this(tablePerspective, BasicBlockColorer.INSTANCE, EDetailLevel.HIGH);
	}

	public HeatMapElement(TablePerspective tablePerspective, IBlockColorer blockColorer, EDetailLevel detailLevel) {
		this.mixin = new TablePerspectiveSelectionMixin(tablePerspective, this);
		Preconditions.checkNotNull(blockColorer, "need a valid renderer");

		this.blockColorer = blockColorer;

		this.dimensionSelectionRenderer = new SelectionRenderer(tablePerspective, mixin.getDimensionSelectionManager(),
				true,
				dimensionPickingIds);
		this.recordSelectionRenderer = new SelectionRenderer(tablePerspective, mixin.getRecordSelectionManager(),
				false,
				recordPickingIds);

		setPicker(null); // no overall picking

		switch (detailLevel) {
		case HIGH:
			setVisibility(EVisibility.PICKABLE); //pickable + no textures
			textureRenderer = null;
			break;
		default:
			setVisibility(EVisibility.VISIBLE); // not pickable + textures
			textureRenderer = new HeatMapTextureRenderer(tablePerspective, blockColorer);
			break;
		}
	}

	@Override
	protected void init(IGLElementContext context) {
		super.init(context);
		onVAUpdate(mixin.getTablePerspective());
	}

	@Override
	public <T> T getLayoutDataAs(Class<T> clazz, T default_) {
		if (clazz.isAssignableFrom(Vec2f.class)) {
			return clazz.cast(getMinSize());
		}
		return super.getLayoutDataAs(clazz, default_);
	}

	/**
	 * @return the recommended min size of this heatmap
	 */
	public Vec2f getMinSize() {
		TablePerspective tablePerspective = mixin.getTablePerspective();
		float w = tablePerspective.getNrDimensions() * (showDimensionLabels ? 16 : 1);
		float h = tablePerspective.getNrRecords() * (showRecordLabels ? 16 : 1);
		if (showRecordLabels)
			w += TEXT_WIDTH;
		if (showDimensionLabels)
			h += TEXT_WIDTH;
		return new Vec2f(w,h);
	}

	private void ensureEnoughPickingIds() {
		if (getVisibility() == EVisibility.PICKABLE && context != null) {
			// we are pickable
			TablePerspective tablePerspective = mixin.getTablePerspective();
			ensureEnoughPickingIds(tablePerspective.getRecordPerspective(), recordPickingIds, recordPickingListener);
			ensureEnoughPickingIds(tablePerspective.getDimensionPerspective(), dimensionPickingIds,
				dimensionPickingListener);
		}
	}

	/**
	 * ensoures that we have registered picking is for our records and dimensions
	 *
	 * @param perspective
	 * @param map
	 * @param listener
	 */
	private void ensureEnoughPickingIds(Perspective perspective, IntIntHashMap map, IPickingListener listener) {
		if (map.size() == 0) { // just add
			for (Integer recordID : perspective.getVirtualArray()) {
				map.put(recordID, context.registerPickingListener(listener, recordID));
			}
		} else {// track changed and update picking ids
			IntIntHashMap bak = new IntIntHashMap();
			bak.putAll(map);
			for (Integer recordID : perspective.getVirtualArray()) {
				bak.remove(recordID); // to track which one were removed
				if (map.containsKey(recordID))
					continue;
				map.put(recordID, context.registerPickingListener(listener, recordID));
			}
			for (IntIntHashMap.Entry entry : bak) {
				context.unregisterPickingListener(entry.value);
				map.remove(entry.key); // update map
			}
		}
	}

	@Override
	protected void onVisibilityChanged(EVisibility old, EVisibility new_) {
		ensureEnoughPickingIds();
	}

	/**
	 * @param showDimensionLabels
	 *            setter, see {@link showDimensionLabels}
	 */
	public void setShowDimensionLabels(boolean showDimensionLabels) {
		if (this.showDimensionLabels == showDimensionLabels)
			return;
		this.showDimensionLabels = showDimensionLabels;
		relayout();
	}

	/**
	 * @param showRecordLabels
	 *            setter, see {@link showRecordLabels}
	 */
	public void setShowRecordLabels(boolean showRecordLabels) {
		if (this.showRecordLabels == showRecordLabels)
			return;
		this.showRecordLabels = showRecordLabels;
		relayout();
	}

	/**
	 * @return the showDimensionLabels, see {@link #showDimensionLabels}
	 */
	public boolean isShowDimensionLabels() {
		return showDimensionLabels;
	}

	/**
	 * @return the showRecordLabels, see {@link #showRecordLabels}
	 */
	public boolean isShowRecordLabels() {
		return showRecordLabels;
	}

	@Override
	public void onVAUpdate(TablePerspective tablePerspective) {
		ensureEnoughPickingIds();
		if (textureRenderer != null) {
			textureRenderer.init(context);
		}
		repaintAll();
	}

	@Override
	public void onSelectionUpdate(SelectionManager manager) {
		repaintAll();
	}


	@Override
	protected void takeDown() {
		// free ids again
		for (IntIntHashMap.Entry entry : recordPickingIds) {
			context.unregisterPickingListener(entry.value);
		}
		for (IntIntHashMap.Entry entry : dimensionPickingIds) {
			context.unregisterPickingListener(entry.value);
		}
		if (textureRenderer != null)
			textureRenderer.takeDown();
		super.takeDown();
	}

	@Override
	protected void layoutImpl() {
		Vec2f size = getSize().copy();
		if (showRecordLabels) {
			size.setX(size.x() - TEXT_WIDTH);
		}
		if (showDimensionLabels) {
			size.setY(size.y() - TEXT_WIDTH);
		}
		// compute the layout
		this.recordSpacing = recordSpacingStrategy.apply(mixin.getTablePerspective(),
				mixin.getRecordSelectionManager(), isHideElements(),
				size.x(), size.y(), 0 /* FIXME */);
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		g.save();
		if (showRecordLabels) {
			w -= TEXT_WIDTH;
			g.move(TEXT_WIDTH, 0);
		}
		if (showDimensionLabels) {
			h -= TEXT_WIDTH;
			g.move(0, TEXT_WIDTH);
		}

		if (showRecordLabels) {
			final TablePerspective tablePerspective = mixin.getTablePerspective();
			final VirtualArray recordVA = tablePerspective.getRecordPerspective().getVirtualArray();
			final ATableBasedDataDomain dataDomain = tablePerspective.getDataDomain();

			for (int i = 0; i < recordVA.size(); ++i) {
				Integer recordID = recordVA.get(i);
				if (isHidden(recordID)) {
					continue;
				}
				float y = recordSpacing.getYPosition(i);
				float fieldHeight = recordSpacing.getFieldHeight(recordID);
				float textHeight = Math.min(fieldHeight, MAX_TEXT_HEIGHT);

				g.drawText(dataDomain.getRecordLabel(recordID), 2 - TEXT_WIDTH, y + (fieldHeight - textHeight) * 0.5f,
						TEXT_WIDTH - 2, textHeight, VAlign.RIGHT);
			}
		}

		if (showDimensionLabels) {
			final TablePerspective tablePerspective = mixin.getTablePerspective();
			final VirtualArray dimensionVA = tablePerspective.getDimensionPerspective().getVirtualArray();
			final ATableBasedDataDomain dataDomain = tablePerspective.getDataDomain();
			final float fieldWidth = recordSpacing.getFieldWidth();
			final float textWidth = Math.min(fieldWidth, MAX_TEXT_HEIGHT);

			g.save();
			g.gl.glRotatef(-90, 0, 0, 1);
			float x = 0;
			for (int i = 0; i < dimensionVA.size(); ++i) {
				Integer dimensionID = dimensionVA.get(i);

				g.drawText(dataDomain.getDimensionLabel(dimensionID), 2, x + (fieldWidth - textWidth) * 0.5f,
						TEXT_WIDTH - 2, textWidth, VAlign.LEFT);
				x += fieldWidth;
			}
			g.restore();
		}

		if (textureRenderer != null)
			textureRenderer.render(g, w, h);
		else
			render(g, w, h, false);
		g.restore();
	}


	private void render(GLGraphics g, float w, float h, boolean doPicking) {
		final TablePerspective tablePerspective = mixin.getTablePerspective();
		final VirtualArray recordVA = tablePerspective.getRecordPerspective().getVirtualArray();
		final VirtualArray dimensionVA = tablePerspective.getDimensionPerspective().getVirtualArray();
		final ATableBasedDataDomain dataDomain = tablePerspective.getDataDomain();

		final float fieldWidth = recordSpacing.getFieldWidth();

		for (int i = 0; i < recordVA.size(); ++i) {
			Integer recordID = recordVA.get(i);
			if (isHidden(recordID)) {
				continue;
			}
			float y = recordSpacing.getYPosition(i);
			float fieldHeight = recordSpacing.getFieldHeight(recordID);

			float x = 0;
			if (doPicking)
				g.pushName(recordPickingIds.get(recordID));

			for (Integer dimensionID : dimensionVA) {
				if (doPicking) {
					g.pushName(dimensionPickingIds.get(dimensionID));
					g.fillRect(x, y, fieldWidth, fieldHeight);
					g.popName();
				} else {
					boolean deSelected = isDeselected(recordID);
					Color color = blockColorer.apply(recordID, dimensionID, dataDomain, deSelected);
					g.color(color).fillRect(x, y, fieldWidth, fieldHeight);
				}
				x += fieldWidth;
			}
			if (doPicking)
				g.popName();
		}

		g.incZ();
		recordSelectionRenderer.render(g, w, h, recordSpacing, doPicking);
		dimensionSelectionRenderer.render(g, w, h, recordSpacing, doPicking);
		g.decZ();
	}

	@Override
	protected void renderPickImpl(GLGraphics g, float w, float h) {
		// ensureEnoughPickingIds();
		super.renderPickImpl(g, w, h);
		g.save();
		if (showRecordLabels) {
			w -= TEXT_WIDTH;
			g.move(TEXT_WIDTH, 0);
		}
		if (showDimensionLabels) {
			h -= TEXT_WIDTH;
			g.move(0, TEXT_WIDTH);
		}

		g.incZ();
		render(g, w, h, true);
		g.decZ();

		g.restore();
	}

	@Override
	protected void onMouseOut(Pick pick) {
		// clear all hovered elements
		createSelection(mixin.getDimensionSelectionManager(), SelectionType.MOUSE_OVER, -1);
		createSelection(mixin.getRecordSelectionManager(), SelectionType.MOUSE_OVER, -1);
	}

	private boolean isHidden(Integer recordID) {
		return isHideElements() && mixin.getRecordSelectionManager().checkStatus(GLHeatMap.SELECTION_HIDDEN, recordID);
	}

	private boolean isDeselected(int recordID) {
		return mixin.getRecordSelectionManager().checkStatus(SelectionType.DESELECTED, recordID);
	}

	@Override
	public void layout(int deltaTimeMs) {
		super.layout(deltaTimeMs);

		if (!toShow.isEmpty()) { // show the context menu
			context.getSWTLayer().showContextMenu(toShow);
			toShow.clear();
		}
	}



	protected void onDimensionPick(int dimensionID, Pick pick) {
		SelectionManager dimensionSelectionManager = mixin.getDimensionSelectionManager();
		switch (pick.getPickingMode()) {
		case CLICKED:
			createSelection(dimensionSelectionManager, SelectionType.SELECTION, dimensionID);
			break;
		case MOUSE_OVER:
			createSelection(dimensionSelectionManager, SelectionType.MOUSE_OVER, dimensionID);
			break;
		case RIGHT_CLICKED:
			createSelection(dimensionSelectionManager, SelectionType.SELECTION, dimensionID);

			ATableBasedDataDomain dataDomain = mixin.getDataDomain();
			if (dataDomain instanceof GeneticDataDomain && !dataDomain.isColumnDimension()) {
				GeneMenuItemContainer contexMenuItemContainer = new GeneMenuItemContainer();
				contexMenuItemContainer.setDataDomain(dataDomain);
				contexMenuItemContainer.setData(dataDomain.getDimensionIDType(), dimensionID);
				contexMenuItemContainer.addSeparator();
				toShow.addAll(contexMenuItemContainer.getContextMenuItems());
			} else {
				IDType dimensionIDType = dataDomain.getDimensionIDType();
				IDType recordIDType = dataDomain.getRecordIDType();
				AContextMenuItem menuItem = new BookmarkMenuItem("Bookmark "
						+ recordIDType.getIDCategory().getHumanReadableIDType() + ": "
						+ dataDomain.getDimensionLabel(dimensionIDType, dimensionID), dimensionIDType, dimensionID);
				toShow.add(menuItem);
			}
			break;
		default:
			break;
		}
	}

	protected void onRecordPick(int recordID, Pick pick) {
		SelectionManager recordSelectionManager = mixin.getRecordSelectionManager();
		switch (pick.getPickingMode()) {
		case CLICKED:
			createSelection(recordSelectionManager, SelectionType.SELECTION, recordID);
			break;
		case MOUSE_OVER:
			createSelection(recordSelectionManager, SelectionType.MOUSE_OVER, recordID);
			break;
		case RIGHT_CLICKED:
			createSelection(recordSelectionManager, SelectionType.SELECTION, recordID);

			ATableBasedDataDomain dataDomain = mixin.getDataDomain();
			if (dataDomain instanceof GeneticDataDomain && dataDomain.isColumnDimension()) {
				GeneMenuItemContainer contexMenuItemContainer = new GeneMenuItemContainer();
				contexMenuItemContainer.setDataDomain(dataDomain);
				contexMenuItemContainer.setData(dataDomain.getRecordIDType(), recordID);
				contexMenuItemContainer.addSeparator();
				toShow.addAll(contexMenuItemContainer.getContextMenuItems());
			} else {
				IDType recordIDType = dataDomain.getRecordIDType();
				AContextMenuItem menuItem = new BookmarkMenuItem("Bookmark "
						+ recordIDType.getIDCategory().getHumanReadableIDType() + ": "
						+ dataDomain.getRecordLabel(recordIDType, recordID), recordIDType, recordID);
				toShow.add(menuItem);
			}
			break;
		default:
			break;
		}
	}

	private void createSelection(SelectionManager manager, SelectionType selectionType, int recordID) {
		if (manager.checkStatus(selectionType, recordID))
			return;

		// check if the mouse-overed element is already selected, and if it is,
		// whether mouse over is clear.
		// If that all is true we don't need to do anything
		if (selectionType == SelectionType.MOUSE_OVER && manager.checkStatus(SelectionType.SELECTION, recordID)
				&& manager.getElements(SelectionType.MOUSE_OVER).isEmpty())
			return;

		manager.clearSelection(selectionType);

		// TODO: Integrate multi spotting support again

		if (recordID >= 0)
			manager.addToType(selectionType, recordID);

		mixin.fireSelectionDelta(manager.getIDType());
		relayout();
	}



	public void upDownSelect(boolean isUp) {
		TablePerspective tablePerspective = mixin.getTablePerspective();
		SelectionManager recordSelectionManager = mixin.getRecordSelectionManager();
		VirtualArray virtualArray = tablePerspective.getRecordPerspective().getVirtualArray();
		if (virtualArray == null)
			throw new IllegalStateException("Virtual Array is required for selectNext Operation");
		int selectedElement = cursorSelect(virtualArray, recordSelectionManager, isUp);
		if (selectedElement < 0)
			return;
		createSelection(recordSelectionManager, SelectionType.MOUSE_OVER, selectedElement);
	}

	public void leftRightSelect(boolean isLeft) {
		TablePerspective tablePerspective = mixin.getTablePerspective();
		SelectionManager dimensionSelectionManager = mixin.getDimensionSelectionManager();
		VirtualArray virtualArray = tablePerspective.getDimensionPerspective().getVirtualArray();
		if (virtualArray == null)
			throw new IllegalStateException("Virtual Array is required for selectNext Operation");

		int selectedElement = cursorSelect(virtualArray, dimensionSelectionManager, isLeft);
		if (selectedElement < 0)
			return;
		createSelection(dimensionSelectionManager, SelectionType.MOUSE_OVER, selectedElement);
	}

	public void enterPressedSelect() {
		TablePerspective tablePerspective = mixin.getTablePerspective();
		SelectionManager dimensionSelectionManager = mixin.getDimensionSelectionManager();
		VirtualArray virtualArray = tablePerspective.getDimensionPerspective().getVirtualArray();
		if (virtualArray == null)
			throw new IllegalStateException("Virtual Array is required for enterPressed Operation");

		Set<Integer> elements = dimensionSelectionManager.getElements(SelectionType.MOUSE_OVER);
		Integer selectedElement = -1;
		if (elements.size() == 1) {
			selectedElement = elements.iterator().next();
			createSelection(dimensionSelectionManager, SelectionType.SELECTION, selectedElement);
		}

		SelectionManager recordSelectionManager = mixin.getRecordSelectionManager();
		VirtualArray recordVirtualArray = tablePerspective.getRecordPerspective().getVirtualArray();
		if (recordVirtualArray == null)
			throw new IllegalStateException("Virtual Array is required for enterPressed Operation");
		elements = recordSelectionManager.getElements(SelectionType.MOUSE_OVER);
		selectedElement = -1;
		if (elements.size() == 1) {
			selectedElement = elements.iterator().next();
			createSelection(recordSelectionManager, SelectionType.SELECTION, selectedElement);
		}
	}

	private int cursorSelect(VirtualArray virtualArray, SelectionManager selectionManager, boolean isUp) {
		Set<Integer> elements = selectionManager.getElements(SelectionType.MOUSE_OVER);
		if (elements.isEmpty()) {
			elements = selectionManager.getElements(SelectionType.SELECTION);
			if (elements.isEmpty())
				return -1;
		}

		if (elements.size() == 1) {
			Integer element = elements.iterator().next();
			int index = virtualArray.indexOf(element);
			int newIndex;
			if (isUp) {
				newIndex = index - 1;
				if (newIndex < 0)
					return -1;
			} else {
				newIndex = index + 1;
				if (newIndex == virtualArray.size())
					return -1;

			}
			return virtualArray.get(newIndex);

		}
		return -1;
	}


	/**
	 * Check whether we should hide elements
	 *
	 * @return
	 */
	public boolean isHideElements() {
		return hideElements;
	}

	/**
	 * @param hideElements
	 *            setter, see {@link hideElements}
	 */
	public void setHideElements(boolean hideElements) {
		if (this.hideElements == hideElements)
			return;
		this.hideElements = hideElements;
		relayout();
	}

	/**
	 * returns the number of elements currently visible in the heat map
	 *
	 * @return
	 */
	public int getNumberOfVisibleRecords() {
		TablePerspective tablePerspective = mixin.getTablePerspective();
		SelectionManager recordSelectionManager = mixin.getRecordSelectionManager();
		int size = tablePerspective.getRecordPerspective().getVirtualArray().size();
		if (isHideElements())
			return size - recordSelectionManager.getNumberOfElements(SELECTION_HIDDEN);
		else
			return size;
	}

	public Set<Integer> getZoomedElements() {
		TablePerspective tablePerspective = mixin.getTablePerspective();
		SelectionManager recordSelectionManager = mixin.getRecordSelectionManager();
		Set<Integer> zoomedElements = new HashSet<Integer>(
				recordSelectionManager.getElements(SelectionType.SELECTION));

		if (zoomedElements.size() > 5)
			return new HashSet<Integer>(1);
		Iterator<Integer> elementIterator = zoomedElements.iterator();
		while (elementIterator.hasNext()) {
			int recordID = elementIterator.next();
			if (!tablePerspective.getRecordPerspective().getVirtualArray().contains(recordID))
				elementIterator.remove();
			else if (recordSelectionManager.checkStatus(SELECTION_HIDDEN, recordID))
				elementIterator.remove();
		}
		return zoomedElements;
	}

	@ListenTo
	private void onColorMappingUpdate(UpdateColorMappingEvent event) {
		repaint();
		if (textureRenderer != null && context != null)
			textureRenderer.init(context);
	}

	@Override
	public String toString() {
		return "Heat map for " + mixin;
	}

}