package org.caleydo.core.view.opengl.util.wavefrontobjectloader;

import gleem.linalg.Vec3f;
import gleem.linalg.open.Vec3i;

import java.util.ArrayList;

import javax.media.opengl.GL;

import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.general.GeneralManager;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * Stores and draws a wavefront object group in a model
 * 
 * @author Stefan Sauer
 */
public class ObjectGroup {
	private String sName;
	private ObjectModel model;

	private ArrayList<ArrayList<Vec3i>> faces;

	private ObjectDimensions dim;

	private int iDisplayList;

	public ObjectGroup(ObjectModel model, String name) {
		this.model = model;
		this.sName = name;
		faces = new ArrayList<ArrayList<Vec3i>>();
		dim = new ObjectDimensions();
		iDisplayList = -1;
	}

	public void addFace(ArrayList<Vec3i> face) {
		faces.add(face);

		// update dimensions
		for (Vec3i faceIndices : face) {
			dim.updateX(model.getGeometricVertex(faceIndices.get(0)).get(0));
			dim.updateY(model.getGeometricVertex(faceIndices.get(0)).get(1));
			dim.updateZ(model.getGeometricVertex(faceIndices.get(0)).get(2));
		}
	}

	public ObjectDimensions getDimensions() {
		return new ObjectDimensions(dim);
	}

	public void draw(GL gl) {
		if (iDisplayList < 0) {
			GeneralManager.get().getLogger().log(
				new Status(IStatus.INFO, IGeneralManager.PLUGIN_ID, this.getClass().toString()
					+ ": display list was drawn, before init!"));
			init(gl);
		}

		gl.glCallList(iDisplayList);
	}

	public boolean isInit() {
		return iDisplayList < 0 ? false : true;
	}

	public void init(GL gl) {
		if (iDisplayList >= 0) {
			gl.glDeleteLists(iDisplayList, 1);
		}

		iDisplayList = gl.glGenLists(1);
		gl.glNewList(iDisplayList, GL.GL_COMPILE);

		gl.glPushMatrix();

		// render faces
		for (ArrayList<Vec3i> face : faces) {
			// check face type
			if (face.size() == 3) {
				gl.glBegin(GL.GL_TRIANGLES);
			}
			else if (face.size() == 4) {
				gl.glBegin(GL.GL_QUADS);
			}
			else {
				gl.glBegin(GL.GL_POLYGON);
			}

			// calculate normal for this face
			if (face.size() >= 3) {
				// we need only 3 points
				Vec3f p0 = model.getGeometricVertex(face.get(0).get(0));
				Vec3f p1 = model.getGeometricVertex(face.get(1).get(0));
				Vec3f p2 = model.getGeometricVertex(face.get(2).get(0));

				// to get 2 vectors
				Vec3f v1 = new Vec3f();
				Vec3f v2 = new Vec3f();
				v1.sub(p1, p0);
				v2.sub(p2, p1);

				// to make the normal vector
				Vec3f normal = new Vec3f();
				normal.cross(v1, v2);
				normal.normalize();

				gl.glNormal3f(normal.get(0), normal.get(1), normal.get(2));
			}

			for (Vec3i faceIndices : face) {

				// render normals (if present). This might be the wrong way
				// (normals for every point of the face?)
				if (faceIndices.get(2) != 0) {
					Vec3f normal = model.getNormalVertex(faceIndices.get(2));
					if (normal != null) {
						gl.glNormal3f(normal.get(0), normal.get(1), normal.get(2));
					}
				}

				// render texture
				if (faceIndices.get(1) != 0) {
					Vec3f texture = model.getTextureVertex(faceIndices.get(1));
					if (texture != null) {
						gl.glTexCoord3f(texture.get(0), texture.get(1), texture.get(2));
					}

				}

				// render object
				{
					Vec3f vertex = model.getGeometricVertex(faceIndices.get(0));
					if (vertex != null) {
						gl.glVertex3f(vertex.get(0), vertex.get(1), vertex.get(2));
					}
				}
			}

			gl.glEnd();

		}

		gl.glPopMatrix();
		gl.glEndList();
	}

	public String getName() {
		return sName;
	}

}
