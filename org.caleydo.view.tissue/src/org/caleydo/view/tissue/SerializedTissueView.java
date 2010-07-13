package org.caleydo.view.tissue;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.camera.EProjectionMode;
import org.caleydo.core.view.opengl.camera.ViewFrustum;

/**
 * Serialized form of a heatmap-view.
 * 
 * @author Werner Puff
 */
@XmlRootElement
@XmlType
public class SerializedTissueView extends ASerializedView {

	private String texturePath;

	private String label;

	private int experimentIndex;

	/**
	 * Default constructor with default initialization
	 */
	public SerializedTissueView() {

	}

	public SerializedTissueView(String dataDomainType) {
		super(dataDomainType);
	}
	
	@Override
	public ViewFrustum getViewFrustum() {
		ViewFrustum viewFrustum = new ViewFrustum(EProjectionMode.ORTHOGRAPHIC,
				0, 8, 0, 8, -20, 20);
		return viewFrustum;
	}

	@Override
	public String getViewType() {
		return GLTissue.VIEW_ID;
	}

	public void setTexturePath(String texturePath) {
		this.texturePath = texturePath;
	}

	public String getTexturePath() {
		return texturePath;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public int getExperimentIndex() {
		return experimentIndex;
	}

	public void setExperimentIndex(int experimentIndex) {
		this.experimentIndex = experimentIndex;
	}
}
