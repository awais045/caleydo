package org.caleydo.view.visbricks.brick.layout;

import java.util.ArrayList;

import org.caleydo.core.data.container.DataContainer;
import org.caleydo.core.data.perspective.RecordPerspective;
import org.caleydo.core.event.data.StartClusteringEvent;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.util.clusterer.gui.StartClusteringDialog;
import org.caleydo.core.util.clusterer.initialization.ClusterConfiguration;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.util.LabelRenderer;
import org.caleydo.core.view.opengl.picking.APickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.util.button.Button;
import org.caleydo.core.view.opengl.util.button.ButtonRenderer;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.view.visbricks.PickingType;
import org.caleydo.view.visbricks.brick.GLBrick;
import org.caleydo.view.visbricks.brick.ui.BrickViewSwitchingButton;
import org.caleydo.view.visbricks.brick.ui.DimensionBarRenderer;
import org.caleydo.view.visbricks.brick.ui.FuelBarRenderer;
import org.caleydo.view.visbricks.dimensiongroup.DimensionGroup;
import org.eclipse.swt.widgets.Shell;

/**
 * Abstract base class for brick configurers for table based data that provides
 * mainly helper functions.
 * 
 * @author Partl
 * 
 */
public abstract class ATableBasedDataConfigurer implements IBrickConfigurer {

    protected static final int FUELBAR_HEIGHT_PIXELS = 4;
    protected static final int CAPTION_HEIGHT_PIXELS = 16;
    protected static final int DIMENSION_BAR_HEIGHT_PIXELS = 12;
    protected static final int LINE_SEPARATOR_HEIGHT_PIXELS = 3;
    protected static final int BUTTON_HEIGHT_PIXELS = 16;
    protected static final int BUTTON_WIDTH_PIXELS = 16;
    protected static final int HANDLE_SIZE_PIXELS = 8;
    protected static final int SPACING_PIXELS = 4;

    protected static final int CLUSTER_BUTTON_ID = 1;

    protected DataContainer dataContainer;

    public ATableBasedDataConfigurer(DataContainer dimensionGroupData) {
	this.dataContainer = dimensionGroupData;
    }

    protected ArrayList<ElementLayout> createHeaderBarElements(
	    HeaderBrickLayoutTemplate layoutTemplate) {

	final GLBrick brick = layoutTemplate.getBrick();

	ArrayList<ElementLayout> headerBarElements = new ArrayList<ElementLayout>();

	ElementLayout spacingLayoutX = new ElementLayout("spacingLayoutX");
	spacingLayoutX.setPixelSizeX(SPACING_PIXELS);
	spacingLayoutX.setRatioSizeY(0);

	ElementLayout captionLayout = new ElementLayout("caption1");

	captionLayout.setPixelSizeY(CAPTION_HEIGHT_PIXELS);
	captionLayout.setFrameColor(0, 0, 1, 1);

	LabelRenderer captionRenderer = new LabelRenderer(layoutTemplate
		.getDimensionGroup().getVisBricksView(),
		layoutTemplate.getBrick(), PickingType.DIMENSION_GROUP.name(),
		layoutTemplate.getDimensionGroup().getID());
	captionLayout.setRenderer(captionRenderer);

	headerBarElements.add(captionLayout);
	headerBarElements.add(spacingLayoutX);

	Button clusterButton = new Button(
		PickingType.DIMENSION_GROUP_CLUSTER_BUTTON.name(),
		CLUSTER_BUTTON_ID, EIconTextures.CLUSTER_ICON);
	ElementLayout clusterButtonLayout = new ElementLayout("clusterButton");
	clusterButtonLayout.setPixelSizeX(BUTTON_WIDTH_PIXELS);
	clusterButtonLayout.setPixelSizeY(BUTTON_HEIGHT_PIXELS);
	clusterButtonLayout.setRenderer(new ButtonRenderer(clusterButton,
		brick, brick.getTextureManager(),
		DefaultBrickLayoutTemplate.BUTTON_Z));

	headerBarElements.add(clusterButtonLayout);
	// headerBarElements.add(spacingLayoutX);

	brick.addIDPickingListener(new APickingListener() {

	    @Override
	    public void clicked(Pick pick) {
		System.out.println("cluster");

		brick.getParentComposite().getDisplay()
			.asyncExec(new Runnable() {
			    @Override
			    public void run() {
				StartClusteringDialog dialog = new StartClusteringDialog(
					new Shell(), brick.getDataDomain());
				DataContainer data = brick.getDimensionGroup()
					.getDataContainer();
				dialog.setDimensionPerspective(data
					.getDimensionPerspective());
				dialog.setRecordPerspective(data
					.getRecordPerspective());
				dialog.open();
				ClusterConfiguration clusterState = dialog
					.getClusterState();
				if (clusterState == null)
				    return;

				// here we create the new record perspective
				// which is
				// intended to be used once the clustering is
				// complete
				RecordPerspective newRecordPerspective = new RecordPerspective(
					data.getDataDomain());
				// we temporarily set the old va to the new
				// perspective,
				// to avoid empty bricks
				newRecordPerspective.setVirtualArray(data
					.getRecordPerspective()
					.getVirtualArray());
				data.setRecordPerspective(newRecordPerspective);
				clusterState
					.setTargetRecordPerspective(newRecordPerspective);

				StartClusteringEvent event = new StartClusteringEvent(
					clusterState);
				event.setDataDomainID(brick.getDataDomain()
					.getDataDomainID());
				GeneralManager.get().getEventPublisher()
					.triggerEvent(event);
			    }
			});
	    }
	}, PickingType.DIMENSION_GROUP_CLUSTER_BUTTON.name(), CLUSTER_BUTTON_ID);

	return headerBarElements;
    }

    protected ArrayList<ElementLayout> createHeaderBarElements(
	    CompactHeaderBrickLayoutTemplate layoutTemplate) {

	ArrayList<ElementLayout> headerBarElements = new ArrayList<ElementLayout>();

	ElementLayout captionLayout = new ElementLayout("caption1");

	captionLayout.setPixelSizeY(CAPTION_HEIGHT_PIXELS);
	captionLayout.setFrameColor(0, 0, 1, 1);

	LabelRenderer captionRenderer = new LabelRenderer(layoutTemplate
		.getDimensionGroup().getVisBricksView(),
		layoutTemplate.getBrick(), PickingType.DIMENSION_GROUP.name(),
		layoutTemplate.getDimensionGroup().getID());
	captionLayout.setRenderer(captionRenderer);

	headerBarElements.add(captionLayout);

	return headerBarElements;
    }

    /**
     * Create the elements which should be shown in the heading of cluster
     * bricks.
     * 
     * @param layoutTemplate
     * @return Returns null if no header bar should be shown, else the elements
     *         for the layout
     */
    protected ArrayList<ElementLayout> createHeaderBarElements(
	    DefaultBrickLayoutTemplate layoutTemplate) {

	ArrayList<ElementLayout> headerBarElements = new ArrayList<ElementLayout>();

	ElementLayout captionLayout = new ElementLayout("caption1");

	captionLayout.setPixelSizeY(CAPTION_HEIGHT_PIXELS);
	captionLayout.setFrameColor(0, 0, 1, 1);

	LabelRenderer captionRenderer = new LabelRenderer(layoutTemplate
		.getDimensionGroup().getVisBricksView(),
		layoutTemplate.getBrick(), PickingType.BRICK.name(),
		layoutTemplate.getBrick().getID());
	// .getDimensionGroup().getVisBricksView(),
	// layoutTemplate.getBrick().getDataContainer().getRecordPerspective().getLabel(),
	// PickingType.DIMENSION_GROUP.name(),
	// layoutTemplate.getDimensionGroup().getID());
	captionLayout.setRenderer(captionRenderer);

	headerBarElements.add(captionLayout);

	return headerBarElements;
    }

    protected void registerViewSwitchingButtons(
	    final ABrickLayoutConfiguration layoutTemplate,
	    final ArrayList<BrickViewSwitchingButton> viewSwitchingButtons,
	    final GLBrick brick, final DimensionGroup dimensionGroup) {

	for (final BrickViewSwitchingButton button : viewSwitchingButtons) {

	    brick.addIDPickingListener(new APickingListener() {

		@Override
		public void clicked(Pick pick) {
		    for (BrickViewSwitchingButton button : viewSwitchingButtons) {
			button.setSelected(false);
		    }
		    button.setSelected(true);

		    if (!(layoutTemplate instanceof HeaderBrickLayoutTemplate)
			    && dimensionGroup.isGlobalViewSwitching()) {
			dimensionGroup.switchBrickViews(button.getViewType());
		    } else {
			brick.setContainedView(button.getViewType());
		    }
		    dimensionGroup.updateLayout();
		}
	    }, button.getPickingType(), button.getButtonID());

	    layoutTemplate.registerViewTypeChangeListener(button);
	}
    }

    protected ArrayList<ElementLayout> createToolBarElements(
	    HeaderBrickLayoutTemplate layoutTemplate,
	    ArrayList<BrickViewSwitchingButton> viewSwitchingButtons) {

	final GLBrick brick = layoutTemplate.getBrick();
	final DimensionGroup dimensionGroup = layoutTemplate
		.getDimensionGroup();

	ArrayList<ElementLayout> toolBarElements = new ArrayList<ElementLayout>();

	ElementLayout spacingLayoutX = new ElementLayout("spacingLayoutX");
	spacingLayoutX.setPixelSizeX(SPACING_PIXELS);
	spacingLayoutX.setRatioSizeY(0);

	for (int i = 0; i < viewSwitchingButtons.size(); i++) {
	    BrickViewSwitchingButton button = viewSwitchingButtons.get(i);
	    ElementLayout buttonLayout = new ElementLayout();
	    buttonLayout.setPixelSizeX(BUTTON_WIDTH_PIXELS);
	    buttonLayout.setPixelSizeY(BUTTON_HEIGHT_PIXELS);
	    buttonLayout.setRenderer(new ButtonRenderer(button, brick, brick
		    .getTextureManager(), DefaultBrickLayoutTemplate.BUTTON_Z));
	    toolBarElements.add(buttonLayout);
	    if (i != viewSwitchingButtons.size() - 1) {
		toolBarElements.add(spacingLayoutX);
	    }
	}

	// layoutTemplate.setViewSwitchingButtons(viewSwitchingButtons);

	registerViewSwitchingButtons(layoutTemplate, viewSwitchingButtons,
		brick, dimensionGroup);

	return toolBarElements;
    }

    protected ArrayList<ElementLayout> createFooterBarElements(
	    HeaderBrickLayoutTemplate layoutTemplate) {
	return createSummaryFooterBarElements(layoutTemplate);

    }

    protected ArrayList<ElementLayout> createFooterBarElements(
	    CompactHeaderBrickLayoutTemplate layoutTemplate) {
	return createSummaryFooterBarElements(layoutTemplate);

    }

    private ArrayList<ElementLayout> createSummaryFooterBarElements(
	    ABrickLayoutConfiguration layoutTemplate) {

	ArrayList<ElementLayout> footerBarElements = new ArrayList<ElementLayout>();

	GLBrick brick = layoutTemplate.getBrick();

	ElementLayout dimensionBarLaylout = new ElementLayout("dimensionBar");
	dimensionBarLaylout.setPixelSizeY(DIMENSION_BAR_HEIGHT_PIXELS);
	dimensionBarLaylout.setRatioSizeX(1);
	// FIXME this is wrong! The first one is the wrong va!
	dimensionBarLaylout
		.setRenderer(new DimensionBarRenderer(brick.getDimensionGroup()
			.getDataContainer().getDimensionPerspective()
			.getVirtualArray(), brick.getDataContainer()
			.getDimensionPerspective().getVirtualArray()));

	footerBarElements.add(dimensionBarLaylout);

	return footerBarElements;

    }

    protected ArrayList<ElementLayout> createFooterBarElements(
	    DefaultBrickLayoutTemplate layoutTemplate) {

	return createDefaultFooterBarElements(layoutTemplate);
    }

    protected ArrayList<ElementLayout> createFooterBarElements(
	    DetailBrickLayoutTemplate layoutTemplate) {

	return createDefaultFooterBarElements(layoutTemplate);
    }

    protected ArrayList<ElementLayout> createFooterBarElements(
	    CompactBrickLayoutTemplate layoutTemplate) {

	return createDefaultFooterBarElements(layoutTemplate);
    }

    private ArrayList<ElementLayout> createDefaultFooterBarElements(
	    ABrickLayoutConfiguration layoutTemplate) {
	ArrayList<ElementLayout> footerBarElements = new ArrayList<ElementLayout>();

	GLBrick brick = layoutTemplate.getBrick();

	ElementLayout fuelBarLayout = new ElementLayout("fuelBarLayout");
	fuelBarLayout.setFrameColor(0, 1, 0, 0);
	fuelBarLayout.setPixelSizeY(FUELBAR_HEIGHT_PIXELS);
	fuelBarLayout.setRenderer(new FuelBarRenderer(brick));

	footerBarElements.add(fuelBarLayout);

	return footerBarElements;
    }

    protected ArrayList<ElementLayout> createToolBarElements(
	    ABrickLayoutConfiguration layoutTemplate,
	    ArrayList<BrickViewSwitchingButton> viewSwitchingButtons) {

	final GLBrick brick = layoutTemplate.getBrick();
	final DimensionGroup dimensionGroup = layoutTemplate
		.getDimensionGroup();

	ArrayList<ElementLayout> toolBarElements = new ArrayList<ElementLayout>();

	ElementLayout spacingLayoutX = new ElementLayout("spacingLayoutX");
	spacingLayoutX.setPixelSizeX(SPACING_PIXELS);
	spacingLayoutX.setRatioSizeY(0);

	for (int i = 0; i < viewSwitchingButtons.size(); i++) {
	    BrickViewSwitchingButton button = viewSwitchingButtons.get(i);
	    ElementLayout buttonLayout = new ElementLayout();
	    buttonLayout.setPixelSizeX(BUTTON_WIDTH_PIXELS);
	    buttonLayout.setPixelSizeY(BUTTON_HEIGHT_PIXELS);
	    buttonLayout.setRenderer(new ButtonRenderer(button, brick, brick
		    .getTextureManager(), DefaultBrickLayoutTemplate.BUTTON_Z));
	    toolBarElements.add(buttonLayout);
	    if (i != viewSwitchingButtons.size() - 1) {
		toolBarElements.add(spacingLayoutX);
	    }
	}

	ElementLayout ratioSpacingLayoutX = new ElementLayout(
		"ratioSpacingLayoutX");
	// ratioSpacingLayoutX.setDebug(true);
	ratioSpacingLayoutX.setRatioSizeX(1);
	ratioSpacingLayoutX.setRatioSizeY(0);

	toolBarElements.add(ratioSpacingLayoutX);

	// layoutTemplate.setViewSwitchingButtons(viewSwitchingButtons);

	registerViewSwitchingButtons(layoutTemplate, viewSwitchingButtons,
		brick, dimensionGroup);

	return toolBarElements;
    }

}
