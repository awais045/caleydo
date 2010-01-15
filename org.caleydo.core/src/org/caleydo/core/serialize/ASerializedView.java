package org.caleydo.core.serialize;

import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.manager.usecase.EDataDomain;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.glyph.gridview.SerializedGlyphView;
import org.caleydo.core.view.opengl.canvas.glyph.sliderview.SerializedGlyphSliderView;
import org.caleydo.core.view.opengl.canvas.histogram.SerializedHistogramView;
import org.caleydo.core.view.opengl.canvas.pathway.SerializedPathwayView;
import org.caleydo.core.view.opengl.canvas.radial.SerializedRadialHierarchyView;
import org.caleydo.core.view.opengl.canvas.remote.SerializedRemoteRenderingView;
import org.caleydo.core.view.opengl.canvas.remote.dataflipper.SerializedDataFlipperView;
import org.caleydo.core.view.opengl.canvas.storagebased.heatmap.SerializedDendogramHorizontalView;
import org.caleydo.core.view.opengl.canvas.storagebased.heatmap.SerializedDendogramVerticalView;
import org.caleydo.core.view.opengl.canvas.storagebased.heatmap.SerializedHeatMapView;
import org.caleydo.core.view.opengl.canvas.storagebased.heatmap.SerializedHierarchicalHeatMapView;
import org.caleydo.core.view.opengl.canvas.storagebased.parallelcoordinates.SerializedParallelCoordinatesView;
import org.caleydo.core.view.swt.browser.SerializedHTMLBrowserView;

/**
 * Basic abstract class for all serialized view representations. A serialized view is used to store a view to
 * disk or transmit it over network.
 * 
 * @author Werner Puff
 * @author Alexander Lex
 * @author Marc Streit
 */
@XmlType
@XmlSeeAlso( { SerializedHistogramView.class, SerializedRadialHierarchyView.class,
		SerializedRemoteRenderingView.class, SerializedHierarchicalHeatMapView.class,
		SerializedParallelCoordinatesView.class, SerializedHeatMapView.class, SerializedPathwayView.class,
		SerializedGlyphView.class, SerializedGlyphSliderView.class, SerializedDendogramVerticalView.class,
		SerializedDendogramHorizontalView.class, SerializedHTMLBrowserView.class,
		SerializedDataFlipperView.class })
public abstract class ASerializedView {

	public ASerializedView() {
	}

	public ASerializedView(EDataDomain dataDomain) {
		this.dataDomain = dataDomain;
	}

	protected int viewID;

	protected String viewGUIID;

	protected EDataDomain dataDomain;

	/**
	 * Gets the command-type for the command-factory to create that creates a according view
	 * 
	 * @return command-type as used by command-factory
	 */
	public abstract ECommandType getCreationCommandType();

	/**
	 * Sets the data domain associated with a view
	 * 
	 * @param dataDomain
	 */
	public void setDataDomain(EDataDomain dataDomain) {
		this.dataDomain = dataDomain;
	}

	/**
	 * Returns the data domain a view is associated with
	 * 
	 * @return
	 */
	public EDataDomain getDataDomain() {
		return dataDomain;
	}

	/**
	 * Gets the according view frustum for the view
	 * 
	 * @return ViewFrustum for open-gl rendering
	 */
	public abstract ViewFrustum getViewFrustum();

	/**
	 * Gets the view-id as used by IViewManager implementations
	 * 
	 * @return view-id of the serialized view
	 */
	public int getViewID() {
		return viewID;
	}

	/**
	 * Sets the view-id as used by IViewManager implementations
	 * 
	 * @param view
	 *            -id of the serialized view
	 */
	public void setViewID(int viewID) {
		this.viewID = viewID;
	}

	/**
	 * Retrieves the id of the view as used within the GUI-framework.
	 * 
	 * @return GUI-related view-id.
	 */
	public abstract String getViewGUIID();
}
