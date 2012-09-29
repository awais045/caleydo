/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 * 
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.view.stratomex.vendingmachine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.awt.GLCanvas;
import org.caleydo.core.data.datadomain.ADataDomain;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.perspective.table.CategoricalTablePerspectiveCreator;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.canvas.remote.IGLRemoteRenderingView;
import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.ILayoutedElement;
import org.caleydo.core.view.opengl.layout.LayoutManager;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.core.view.opengl.layout.util.ColorRenderer;
import org.caleydo.core.view.opengl.layout.util.LabelRenderer;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.picking.APickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.util.button.Button;
import org.caleydo.core.view.opengl.util.button.ButtonRenderer;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.view.stratomex.GLStratomex;
import org.caleydo.view.stratomex.column.BrickColumn;
import org.caleydo.view.stratomex.column.BrickColumnGlowRenderer;
import org.caleydo.view.stratomex.column.BrickColumnManager;
import org.caleydo.view.stratomex.event.ScoreTablePerspectiveEvent;
import org.caleydo.view.stratomex.listener.ScoreTablePerspectiveListener;
import org.caleydo.view.stratomex.listener.VendingMachineKeyListener;
import org.eclipse.swt.widgets.Composite;

/**
 * <p>
 * The vending machine for stratification and cluster comparisons using scoring
 * approach.
 * </p>
 * 
 * @author Marc Streit
 */

public class VendingMachine
	extends AGLView
	implements IGLRemoteRenderingView, ILayoutedElement {

	public static String VIEW_TYPE = "org.caleydo.view.vendingmachine";

	public static String VIEW_NAME = "Vending Machine";

	// private final static String DATASET_BUTTON_PICKING_TYPE =
	// "org.caleydo.view.stratomex.vendingmachine.testbutton";
	private final static int DATASET_BUTTON_PICKING_ID = 0;

	public static int VENDING_MACHINE_PIXEL_WIDTH = 500;

	private static int MAX_RANKED_ELEMENTS = 35;
	private static float SCORE_CUTOFF = 0.2f;

	private static float[] RANK_SELECTION_COLOR = new float[] { 1, 1, 0, 1 };
	private static float[] REFERENCE_SELECTION_COLOR = new float[] { 1, 0, 0, 1 };

	private LayoutManager layoutManager;

	private Column mainColumn;
	private Column dataSetButtonListColumn;
	private Column rankColumn;

	private boolean isRankedListDirty = false;

	private List<TablePerspective> referenceTablePerspectives;

	private List<TablePerspective> scoringTablePerspectives = new ArrayList<TablePerspective>();

	private int selectedTablePerspectiveIndex = 0;

	private ColorRenderer highlightRankBackgroundRenderer;

	private List<RankedElement> rankedElements;

	private GLStratomex stratomex;

	private boolean isActive = true;

	private BrickColumnManager brickColumnManager;

	private BrickColumn referenceBrickColumn;

	private ScoreTablePerspectiveListener scoreGroupListener;

	private ArrayList<Button> dataDomainButtons = new ArrayList<Button>();

	private CategoricalTablePerspectiveCreator categoricalTablePerspectiveCreator = new CategoricalTablePerspectiveCreator();

	private EScoreReferenceMode scoreReferenceMode;

	/**
	 * Constructor.
	 * 
	 * @param glCanvas
	 * @param viewLabel
	 * @param viewFrustum
	 */
	public VendingMachine(GLCanvas glCanvas, Composite parentComposite, ViewFrustum viewFrustum) {

		super(glCanvas, parentComposite, viewFrustum, VIEW_TYPE, VIEW_NAME);

		glKeyListener = new VendingMachineKeyListener(this);

		parentGLCanvas.removeMouseWheelListener(glMouseListener);
		parentGLCanvas.addMouseWheelListener(glMouseWheelListener);
	}

	@Override
	public void init(GL2 gl) {
		displayListIndex = gl.glGenLists(1);

		super.renderStyle = renderStyle;
		detailLevel = EDetailLevel.HIGH;

		textRenderer = new CaleydoTextRenderer(10);
		highlightRankBackgroundRenderer = new ColorRenderer(RANK_SELECTION_COLOR);

		layoutManager = new LayoutManager(viewFrustum, pixelGLConverter);

		initLayouts();
	}

	@Override
	public void initLocal(GL2 gl) {
		init(gl);
	}

	@Override
	public void initRemote(final GL2 gl, final AGLView glParentView,
			final GLMouseListener glMouseListener) {

		// Register keyboard listener to GL2 canvas
		glParentView.getParentComposite().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				glParentView.getParentComposite().addKeyListener(glKeyListener);
			}
		});

		this.glMouseListener = glMouseListener;

		init(gl);
	}

	@Override
	public void displayLocal(GL2 gl) {

		// TODO: iterate over visbricks views
		// visBricks.processEvents();

		pickingManager.handlePicking(this, gl);

		display(gl);
		if (busyState != EBusyState.OFF) {
			renderBusyMode(gl);
		}
	}

	public void initLayouts() {

		mainColumn = new Column("mainColumn");
		layoutManager.setBaseElementLayout(mainColumn);
		layoutManager.setUseDisplayLists(true);

		rankColumn = new Column("rankColum");
		rankColumn.setBottomUp(false);
		mainColumn.append(rankColumn);

		dataSetButtonListColumn = new Column("dataSetButtonColum");
		dataSetButtonListColumn.setBottomUp(false);
		dataSetButtonListColumn.setPixelSizeY(200);
		mainColumn.append(dataSetButtonListColumn);

		addDataSetButtons();

		layoutManager.updateLayout();
	}

	private void updateRankedList() {

		selectedTablePerspectiveIndex = 0;
		rankColumn.clear();

		// Trigger ranking of data containers
		rankedElements = new ArrayList<RankedElement>();
		for (TablePerspective scoredTablePerspective : scoringTablePerspectives) {

			if (referenceTablePerspectives == null) {
				RankedElement rankedElement = new RankedElement(0, scoredTablePerspective,
						null, null);
				rankedElements.add(rankedElement);
			}
			else {

				switch (scoreReferenceMode) {
					case COLUMN:

						TablePerspective columnTablePerspective = referenceTablePerspectives
								.get(0);
						float score = scoredTablePerspective.getContainerStatistics()
								.getAdjustedRandIndex().getScore(columnTablePerspective, true);

						RankedElement rankedElement = new RankedElement(score,
								scoredTablePerspective, null, columnTablePerspective);
						rankedElements.add(rankedElement);

						break;

					case SINGLE_GROUP:

						TablePerspective singleReferenceTablePerspective = referenceTablePerspectives
								.get(0);

						createSingleJaccardRankedElement(singleReferenceTablePerspective,
								scoredTablePerspective);

						break;

					case ALL_GROUPS_IN_COLUMN:

						for (TablePerspective referenceTablePerspective : referenceTablePerspectives) {
							createSingleJaccardRankedElement(referenceTablePerspective,
									scoredTablePerspective);
						}

						break;
				}
			}
		}

		Collections.sort(rankedElements);

		int rank = 0;

		for (RankedElement rankedElement : rankedElements) {

			rankedElement.setRank(++rank);
			rankedElement.setPixelSizeX(VENDING_MACHINE_PIXEL_WIDTH);
			rankedElement.setPixelSizeY(18);
			rankedElement.createLayout(this);

			if (rank >= MAX_RANKED_ELEMENTS)
				break;

			// if (rankedElement.getScore() < SCORE_CUTOFF)
			// break;

			rankColumn.append(rankedElement);

			ElementLayout spacerLayout = new ElementLayout("spacerLayout");
			spacerLayout.setPixelSizeY(3);
			rankColumn.append(spacerLayout);
		}

		if (rankedElements.size() > 0) {

			// Add first ranked table perspective as the currently selected one
			// to
			// stratomex
			TablePerspective tablePerspective = rankedElements.get(
					selectedTablePerspectiveIndex).getColumnTablePerspective();
			addTablePerspectiveToStratomex(tablePerspective);

			// Move newly added table perspective to be right of the reference
			// table
			// perspective
			brickColumnManager.moveBrickColumn(
					brickColumnManager.getBrickColumn(tablePerspective),
					brickColumnManager.indexOfBrickColumn(referenceBrickColumn) + 1);

			rankedElements.get(0).addBackgroundRenderer(highlightRankBackgroundRenderer);
		}

		layoutManager.updateLayout();
	}

	private void createSingleJaccardRankedElement(TablePerspective referenceTablePerspective,
			TablePerspective scoredTablePerspective) {

		HashMap<TablePerspective, Float> subTablePerspectiveToScore = referenceTablePerspective
				.getContainerStatistics().getJaccardIndex()
				.getScore(scoredTablePerspective, true);

		for (TablePerspective subTablePerspective : subTablePerspectiveToScore.keySet()) {

			RankedElement tmpRankedElement = new RankedElement(
					subTablePerspectiveToScore.get(subTablePerspective),
					scoredTablePerspective, subTablePerspective, referenceTablePerspective);
			rankedElements.add(tmpRankedElement);
		}
	}

	@Override
	public void displayRemote(GL2 gl) {
		display(gl);
	}

	@Override
	public void display(GL2 gl) {
		checkForHits(gl);
		processEvents();

		if (isRankedListDirty) {
			updateRankedList();
			isRankedListDirty = false;
		}

		layoutManager.render(gl);
	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		return null;
	}

	@Override
	public String toString() {
		return "TODO: ADD INFO THAT APPEARS IN THE LOG";
	}

	@Override
	public void registerEventListeners() {
		super.registerEventListeners();

		scoreGroupListener = new ScoreTablePerspectiveListener();
		scoreGroupListener.setHandler(this);
		eventPublisher.addListener(ScoreTablePerspectiveEvent.class, scoreGroupListener);
	}

	@Override
	public void unregisterEventListeners() {
		super.unregisterEventListeners();

		if (scoreGroupListener != null) {
			eventPublisher.removeListener(scoreGroupListener);
			scoreGroupListener = null;
		}
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		super.reshape(drawable, x, y, width, height);
	}

	@Override
	public List<AGLView> getRemoteRenderedViews() {

		return null;
	}

	public void setScoringReference(EScoreReferenceMode scoreReferenceMode,
			List<TablePerspective> referenceTablePerspectives, BrickColumn referenceBrickColumn) {

		this.referenceBrickColumn = referenceBrickColumn;
		this.referenceTablePerspectives = referenceTablePerspectives;
		this.scoreReferenceMode = scoreReferenceMode;

		isActive = true;

		// Highlight reference table
		referenceBrickColumn.getLayout().addBackgroundRenderer(
				new BrickColumnGlowRenderer(REFERENCE_SELECTION_COLOR, referenceBrickColumn,
						false));

		updateScoredTablePerspectives();
		isRankedListDirty = true;
	}

	private void updateScoredTablePerspectives() {

		scoringTablePerspectives.clear();
		for (Button dataDomainButton : dataDomainButtons) {

			if (!dataDomainButton.isSelected())
				continue;

			ATableBasedDataDomain dataDomain = (ATableBasedDataDomain) DataDomainManager.get()
					.getDataDomainByID(dataDomainButton.getPickingType());

			if (dataDomain.getLabel().toLowerCase().contains("mutation")
					|| dataDomain.getLabel().toLowerCase().contains("copy")) {

				scoringTablePerspectives.addAll(dataDomain.getAllTablePerspectives());
			}
			else {
				// We take the first dimension perspective we find - it does not
				// matter
				String dimensionPerspectiveID = (String) dataDomain
						.getDimensionPerspectiveIDs().toArray()[0];

				Set<String> rowPerspectiveIDs = dataDomain.getRecordPerspectiveIDs();

				// we ignore stratifications with only one group, which is the
				// ungrouped default
				if (rowPerspectiveIDs.size() == 1)
					continue;

				for (String rowPerspectiveID : rowPerspectiveIDs) {

					if (referenceBrickColumn != null
							&& rowPerspectiveID.equals(referenceBrickColumn
									.getTablePerspective().getRecordPerspective()
									.getPerspectiveID()))
						continue;

					boolean existsAlready = false;
					if (dataDomain.hasTablePerspective(rowPerspectiveID,
							dimensionPerspectiveID))
						existsAlready = true;

					TablePerspective newTablePerspective = dataDomain.getTablePerspective(
							rowPerspectiveID, dimensionPerspectiveID);

					// We do not want to overwrite the state of already existing
					// public table perspectives.
					if (!existsAlready)
						newTablePerspective.setPrivate(true);

					scoringTablePerspectives.add(newTablePerspective);
				}
			}
		}
	}

	private void addDataSetButtons() {

		dataDomainButtons.clear();

		List<ATableBasedDataDomain> dataDomains = DataDomainManager.get()
				.getDataDomainsByType(ATableBasedDataDomain.class);

		// Sort data domains alphabetically
		Collections.sort(dataDomains, new Comparator<ADataDomain>() {
			public int compare(ADataDomain dd1, ADataDomain dd2) {
				return dd1.toString().compareTo(dd2.toString());
			}
		});

		ElementLayout horizontalSpacerLayout = new ElementLayout("spacerLayout");
		horizontalSpacerLayout.setPixelSizeY(3);

		ElementLayout verticalSpacerLayout = new ElementLayout("spacerLayout");
		verticalSpacerLayout.setPixelSizeX(7);

		ElementLayout topSpacerLayout = new ElementLayout("spacerLayout");
		topSpacerLayout.setPixelSizeY(20);
		dataSetButtonListColumn.append(topSpacerLayout);

		for (ATableBasedDataDomain dataDomain : dataDomains) {

			Row singleDataSetRow = new Row("singleDataSetRow");
			singleDataSetRow.setGrabX(true);
			singleDataSetRow.setPixelSizeY(18);

			if (dataDomain.getLabel().toLowerCase().equals("clinical"))
				continue;

			ElementLayout dataSetButtonLayout = new ElementLayout("dataSetButtonLayout");
			dataSetButtonLayout.setPixelSizeX(20);

			final Button dataDomainButton = new Button(dataDomain.getDataDomainID(),
					DATASET_BUTTON_PICKING_ID,
					EIconTextures.CM_SELECTION_RIGHT_EXTENSIBLE_BLACK);

			// if ((dataDomain.getLabel().toLowerCase().contains("mutation") ||
			// dataDomain
			// .getLabel().toLowerCase().contains("copy")))
			dataDomainButton.setSelected(false);
			// else
			// dataDomainButton.setSelected(true);

			dataDomainButtons.add(dataDomainButton);

			ButtonRenderer dataDomainButtonRenderer = new ButtonRenderer(dataDomainButton,
					this);
			dataDomainButtonRenderer.setZCoordinate(1);
			dataSetButtonLayout.setRenderer(dataDomainButtonRenderer);

			addTypePickingListener(new APickingListener() {
				private boolean isCNVInitialized;
				private boolean isMutationInitialized;

				@Override
				public void clicked(Pick pick) {

					dataDomainButton.setSelected(!dataDomainButton.isSelected());

					// Remove current score column before adding the new one
					if (rankedElements != null
							&& rankedElements.size() > 0
							&& rankedElements.get(selectedTablePerspectiveIndex) != null
							&& rankedElements.get(selectedTablePerspectiveIndex)
									.getColumnTablePerspective() != null)
						stratomex.removeTablePerspective(rankedElements
								.get(selectedTablePerspectiveIndex)
								.getColumnTablePerspective().getID());

					if (referenceTablePerspectives != null) {

						if (dataDomainButton.isSelected()) {

							// FIND A BETTER PLACE FOR CREATING CAT DATA
							ATableBasedDataDomain dataDomain = (ATableBasedDataDomain) DataDomainManager
									.get()
									.getDataDomainByID(dataDomainButton.getPickingType());
							if (dataDomain.getLabel().toLowerCase().contains("mutation")
									&& !isMutationInitialized) {
								categoricalTablePerspectiveCreator.createAllTablePerspectives(dataDomain);
								isMutationInitialized = true;
							}
							else if (dataDomain.getLabel().toLowerCase().contains("copy")
									&& !isCNVInitialized) {
								categoricalTablePerspectiveCreator
										.createAllTablePerspectives(dataDomain);
								isCNVInitialized = true;
							}
						}
					}

					updateScoredTablePerspectives();
					isRankedListDirty = true;
				}

			}, dataDomain.getDataDomainID());

			singleDataSetRow.append(dataSetButtonLayout);
			singleDataSetRow.append(verticalSpacerLayout);

			ElementLayout dataSetIndicatorLayout = new ElementLayout("dataSetIndicatorLayout");
			dataSetIndicatorLayout.addBackgroundRenderer(new ColorRenderer(dataDomain
					.getColor().getRGBA()));
			dataSetIndicatorLayout.setPixelSizeX(RankedElement.DATASET_COLOR_INDICATOR_WIDTH);
			singleDataSetRow.append(dataSetIndicatorLayout);
			singleDataSetRow.append(verticalSpacerLayout);

			ElementLayout labelLayout = new ElementLayout("labelLayout");
			labelLayout.setRenderer(new LabelRenderer(this, dataDomain.getLabel()));
			labelLayout.setPixelSizeX(VENDING_MACHINE_PIXEL_WIDTH - 30);
			singleDataSetRow.append(labelLayout);

			dataSetButtonListColumn.append(singleDataSetRow);
			dataSetButtonListColumn.append(horizontalSpacerLayout);
		}
	}

	public List<TablePerspective> getTablePerspectives() {
		return scoringTablePerspectives;
	}

	public void highlightRankedElement(boolean next) {

		RankedElement prevSelectedRankedElement = rankedElements
				.get(selectedTablePerspectiveIndex);

		if (next && selectedTablePerspectiveIndex < MAX_RANKED_ELEMENTS - 1)
			selectedTablePerspectiveIndex++;
		else if (!next && selectedTablePerspectiveIndex > 0)
			selectedTablePerspectiveIndex--;

		RankedElement newlySelectedRankedElement = rankedElements
				.get(selectedTablePerspectiveIndex);

		if (prevSelectedRankedElement != newlySelectedRankedElement) {

			int replaceIndex = brickColumnManager.indexOfBrickColumn(brickColumnManager
					.getBrickColumn(prevSelectedRankedElement.getColumnTablePerspective()));

			stratomex.removeTablePerspective(prevSelectedRankedElement
					.getColumnTablePerspective().getID());

			prevSelectedRankedElement.clearBackgroundRenderers();

			newlySelectedRankedElement.addBackgroundRenderer(highlightRankBackgroundRenderer);

			addTablePerspectiveToStratomex(newlySelectedRankedElement
					.getColumnTablePerspective());
			brickColumnManager.moveBrickColumn(brickColumnManager
					.getBrickColumn(newlySelectedRankedElement.getColumnTablePerspective()),
					replaceIndex);

			rankColumn.getLayoutManager().updateLayout();
		}
	}

	private void addTablePerspectiveToStratomex(TablePerspective newlySelectedTablePerspective) {

		// AddTablePerspectivesEvent event = new
		// AddTablePerspectivesEvent(newlySelectedTablePerspective);
		// event.setReceiver(stratomex);
		// event.setSender(this);
		// eventPublisher.triggerEvent(event);

		stratomex.addTablePerspective(newlySelectedTablePerspective);

		BrickColumn brickColumn = brickColumnManager
				.getBrickColumn(newlySelectedTablePerspective);
		brickColumn.getLayout().addBackgroundRenderer(
				new BrickColumnGlowRenderer(RANK_SELECTION_COLOR, brickColumn, false));
		brickColumn.getLayout().updateSubLayout();
	}

	public void selectChoice() {

		rankedElements.get(selectedTablePerspectiveIndex).getColumnTablePerspective()
				.setPrivate(false);
		// FIXME: we need to think about the workflow
		isActive = true;

		brickColumnManager
				.getBrickColumn(
						rankedElements.get(selectedTablePerspectiveIndex)
								.getColumnTablePerspective()).getLayout()
				.clearBackgroundRenderers();

		if (referenceBrickColumn != null)
			referenceBrickColumn.getLayout().clearBackgroundRenderers();

		scoringTablePerspectives.clear();
		referenceTablePerspectives = null;
		referenceBrickColumn = null;
		isRankedListDirty = true;
	}

	@Override
	protected void destroyViewSpecificContent(GL2 gl) {

		// TODO: remove button picking listeners
		// this.removeAllIDPickingListeners(DATASET_BUTTON_PICKING_TYPE,
		// DATASET_BUTTON_PICKING_ID);
	}

	@Override
	public ElementLayout getLayout() {
		return mainColumn;
	}

	/**
	 * @param glStratomex
	 */
	public void setStratomex(GLStratomex stratomex) {
		this.stratomex = stratomex;
		brickColumnManager = stratomex.getBrickColumnManager();
	}

	/**
	 * @return the isActive, see {@link #isActive}
	 */
	public boolean isActive() {
		return isActive;
	}

	public void updatLayout() {
		// if (isActive)
		// mainColumn.setPixelSizeX(VENDING_MACHINE_PIXEL_WIDTH);
		// else
		// mainColumn.setAbsoluteSizeX(0);
	}
}
