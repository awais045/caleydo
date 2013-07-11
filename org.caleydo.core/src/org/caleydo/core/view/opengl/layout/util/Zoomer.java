/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.layout.util;

import gleem.linalg.Vec2f;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.IGLCanvas;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.canvas.listener.IMouseWheelHandler;
import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.LayoutManager;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.picking.PickingType;
import org.caleydo.core.view.opengl.util.draganddrop.DragAndDropController;
import org.caleydo.core.view.opengl.util.scrollbar.IScrollBarUpdateHandler;
import org.caleydo.core.view.opengl.util.scrollbar.ScrollBar;
import org.caleydo.core.view.opengl.util.scrollbar.ScrollBarRenderer;

public final class Zoomer implements IMouseWheelHandler, IScrollBarUpdateHandler {

	private final PixelGLConverter pixelGLConverter;
	private final AGLView parentView;
	private final DragAndDropController scrollBarDragAndDropController;

	private Rectangle2D.Float viewArea_dip;

	private float previousZoomScale = 1.0f;
	private float currentZoomScale = 1.0f;

	private ScrollBar hScrollBar;
	private ScrollBar vScrollBar;
	private LayoutManager hScrollBarLayoutManager;
	// private LayoutConfiguration hScrollBarTemplate;
	private LayoutManager vScrollBarLayoutManager;
	// private LayoutConfiguration vScrollBarTemplate;

	private boolean wasMouseWheeled = false;

	private Vec2f mouseWheelPosition;

	private float relativeViewTranlateX;

	private float relativeViewTranlateY;


	private ViewFrustum viewFrustum;

	private GLMouseListener glMouseListener;

	private ElementLayout parentLayout;

	public Zoomer(AGLView parentView, ElementLayout parentLayout) {
		this.parentView = parentView;
		this.parentLayout = parentLayout;
		this.glMouseListener = parentView.getGLMouseListener();
		parentView.registerMouseWheelListener(this);

		pixelGLConverter = parentView.getPixelGLConverter();
		scrollBarDragAndDropController = new DragAndDropController(parentView);
		viewFrustum = new ViewFrustum();
		initScrollBars();
	}

	public void destroy() {
		parentView.unregisterRemoteViewMouseWheelListener(this);
	}

	private void initScrollBars() {

		hScrollBarLayoutManager = new LayoutManager(viewFrustum, pixelGLConverter);
		// hScrollBarTemplate = new LayoutConfiguration();
		hScrollBar = new ScrollBar(0, 10, 5, 5, PickingType.ZOOM_SCROLLBAR, parentView.createNewScrollBarID(), this);

		Column baseColumn = new Column();

		ElementLayout hScrollBarLayout = new ElementLayout("horizontalScrollBar");
		hScrollBarLayout.setPixelSizeY(10);
		hScrollBarLayout.setRatioSizeX(1.0f);
		hScrollBarLayout.setRenderer(new ScrollBarRenderer(hScrollBar, parentView, true,
				scrollBarDragAndDropController, Color.BLACK));

		ElementLayout hSpacingLayout = new ElementLayout("horizontalSpacing");
		hSpacingLayout.setRatioSizeX(1.0f);

		baseColumn.append(hScrollBarLayout);
		baseColumn.append(hSpacingLayout);

		hScrollBarLayoutManager.setBaseElementLayout(baseColumn);

		// hScrollBarLayoutManager.setTemplate(hScrollBarTemplate);

		vScrollBarLayoutManager = new LayoutManager(viewFrustum, pixelGLConverter);
		// vScrollBarTemplate = new LayoutConfiguration();
		vScrollBar = new ScrollBar(0, 10, 5, 5, PickingType.ZOOM_SCROLLBAR, parentView.createNewScrollBarID(), this);

		Row baseRow = new Row();

		ElementLayout vScrollBarLayout = new ElementLayout("horizontalScrollBar");
		vScrollBarLayout.setPixelSizeX(10);
		vScrollBarLayout.setRatioSizeY(1.0f);
		vScrollBarLayout.setRenderer(new ScrollBarRenderer(vScrollBar, parentView, false,
				scrollBarDragAndDropController, Color.BLACK));

		ElementLayout vSpacingLayout = new ElementLayout("verticalSpacing");
		vSpacingLayout.setRatioSizeX(1.0f);

		baseRow.append(vSpacingLayout);
		baseRow.append(vScrollBarLayout);

		vScrollBarLayoutManager.setBaseElementLayout(baseRow);

		// vScrollBarLayoutManager.setTemplate(vScrollBarTemplate);
	}

	/**
	 * This method shall be called before the view is rendered in order to be zoomed.
	 *
	 * @param gl
	 */
	public void beginZoom(GL2 gl) {
		viewArea_dip = new Rectangle2D.Float();
		Vec2f pos = pixelGLConverter.getCurrentPixelPos(gl);
		viewArea_dip.x = pos.x();
		viewArea_dip.y = pos.y();

		final float w = parentLayout.getSizeScaledX();
		final float h = parentLayout.getSizeScaledY();
		viewArea_dip.width = pixelGLConverter.getPixelWidthForGLWidth(w);
		viewArea_dip.height = pixelGLConverter.getPixelHeightForGLHeight(h);

		// double[] clipPlane1 = new double[] { 0.0, 1.0, 0.0, 0.0 };
		// double[] clipPlane2 = new double[] { 1.0, 0.0, 0.0, 0.0 };
		// double[] clipPlane3 = new double[] { -1.0, 0.0, 0.0, parentLayout.getSizeScaledX() };
		// double[] clipPlane4 = new double[] { 0.0, -1.0, 0.0, parentLayout.getSizeScaledY() };
		//
		// gl.glClipPlane(GL2ES1.GL_CLIP_PLANE0, clipPlane1, 0);
		// gl.glClipPlane(GL2ES1.GL_CLIP_PLANE1, clipPlane2, 0);
		// gl.glClipPlane(GL2ES1.GL_CLIP_PLANE2, clipPlane3, 0);
		// gl.glClipPlane(GL2ES1.GL_CLIP_PLANE3, clipPlane4, 0);
		// gl.glEnable(GL2ES1.GL_CLIP_PLANE0);
		// gl.glEnable(GL2ES1.GL_CLIP_PLANE1);
		// gl.glEnable(GL2ES1.GL_CLIP_PLANE2);
		// gl.glEnable(GL2ES1.GL_CLIP_PLANE3);
		// gl.glScissor(viewportPositionX, viewportPositionY, 2000, 1000);

		// System.out.println(currentZoomScale);

		if (currentZoomScale == 1.0f) {
			relativeViewTranlateX = 0;
			relativeViewTranlateY = 0;
			return;
		}

		gl.glEnable(GL.GL_SCISSOR_TEST);
		{
			IGLCanvas canvas = parentView.getParentGLCanvas();
			Rectangle viewArea = canvas.toRawPixel(viewArea_dip);
			gl.glScissor(viewArea.x, viewArea.y, viewArea.width, viewArea.height);
		}

		float viewTranslateX = relativeViewTranlateX * w;
		float viewTranslateY = relativeViewTranlateY * h;

		// float zoomCenterX = relativeZoomCenterX * viewFrustum.getWidth();
		// float zoomCenterY = relativeZoomCenterY * viewFrustum.getHeight();
		//
		// float viewTranslateX;
		// float viewTranslateY;

		if (wasMouseWheeled) {

			float viewPositionX = viewArea_dip.x;
			float viewPositionY = viewArea_dip.y;
			Vec2f wheelPosition = pixelGLConverter.convertMouseCoord2GL(mouseWheelPosition);

			// viewTranslateX =
			// (viewFrustum.getWidth() / 2.0f) - zoomCenterX - (previousZoomScale - 1) * zoomCenterX;
			// viewTranslateY =
			// (viewFrustum.getHeight() / 2.0f) - zoomCenterY - (previousZoomScale - 1) * zoomCenterY;

			float zoomCenterMouseX = wheelPosition.x() - viewPositionX;
			float zoomCenterMouseY = wheelPosition.y() - viewPositionY;

			float relativeImageCenterX = (-viewTranslateX + zoomCenterMouseX) / (w * previousZoomScale);
			float relativeImageCenterY = (-viewTranslateY + zoomCenterMouseY) / (h * previousZoomScale);

			float zoomCenterX = relativeImageCenterX * w;
			float zoomCenterY = relativeImageCenterY * h;

			// zoomCenterX = viewPositionX + viewFrustum.getWidth() - wheelPositionX;
			// zoomCenterY = viewPositionY + viewFrustum.getHeight() - wheelPositionY;
			viewTranslateX = (w / 2.0f) - zoomCenterX - (currentZoomScale - 1) * zoomCenterX;
			viewTranslateY = (h / 2.0f) - zoomCenterY - (currentZoomScale - 1) * zoomCenterY;

			if (viewTranslateX > 0)
				viewTranslateX = 0;
			if (viewTranslateY > 0)
				viewTranslateY = 0;

			if (viewTranslateX < -(w * (currentZoomScale - 1)))
				viewTranslateX = -(w * (currentZoomScale - 1));
			if (viewTranslateY < -(h * (currentZoomScale - 1)))
				viewTranslateY = -(h * (currentZoomScale - 1));

			relativeViewTranlateX = viewTranslateX / w;
			relativeViewTranlateY = viewTranslateY / h;

			System.out.println("=========================================");
			// System.out.println("viewPos: " + viewPositionX + "," + viewPositionY + "\n zoomCenter: "
			// + zoomCenterX + "," + zoomCenterY + "\n Frustum: " + viewFrustum.getWidth() + ","
			// + viewFrustum.getHeight() + "\n Translate: " + viewTranlateX + "," + viewTranlateY
			// + "\n currentZoom: " + currentZoomScale + "; prevZoom: " + previousZoomScale);
		}

		float relativeImageCenterX = (-viewTranslateX + w / 2.0f) / (w * currentZoomScale);
		float relativeImageCenterY = (-viewTranslateY + h / 2.0f) / (h * currentZoomScale);

		float zoomCenterX = relativeImageCenterX * w;
		float zoomCenterY = relativeImageCenterY * h;

		hScrollBar.setPageSize(pixelGLConverter.getPixelWidthForGLWidth((w - w / currentZoomScale) / currentZoomScale));
		hScrollBar.setMaxValue(pixelGLConverter.getPixelWidthForGLWidth(w - w / (currentZoomScale * 2.0f)));
		hScrollBar.setMinValue(pixelGLConverter.getPixelWidthForGLWidth(w / (currentZoomScale * 2.0f)));
		hScrollBar.setSelection(pixelGLConverter.getPixelWidthForGLWidth(zoomCenterX));

		vScrollBar.setPageSize(pixelGLConverter.getPixelWidthForGLWidth((h - h / currentZoomScale) / currentZoomScale));
		vScrollBar.setMaxValue(pixelGLConverter.getPixelWidthForGLWidth(h - h / (currentZoomScale * 2.0f)));
		vScrollBar.setMinValue(pixelGLConverter.getPixelWidthForGLWidth(h / (currentZoomScale * 2.0f)));
		vScrollBar.setSelection(pixelGLConverter.getPixelWidthForGLWidth(zoomCenterY));

		// viewTranslateX = (viewFrustum.getWidth() / 2.0f) - zoomCenterX - (currentZoomScale - 1) *
		// zoomCenterX;
		// viewTranslateY =
		// (viewFrustum.getHeight() / 2.0f) - zoomCenterY - (currentZoomScale - 1) * zoomCenterY;
		//
		// relativeZoomCenterX = zoomCenterX / viewFrustum.getWidth();
		// relativeZoomCenterY = zoomCenterY / viewFrustum.getHeight();

		gl.glPushMatrix();
		gl.glTranslatef(viewTranslateX, viewTranslateY, 0);
		gl.glScalef(currentZoomScale, currentZoomScale, 1);

		// JUST FOR TESTING OF 1D ZOOM IN Z-DIRECTION
		// TODO: ADD MODE FOR X-ZOOM, Y-ZOOM OR BOTH
		// gl.glTranslatef(0, viewTranslateY, 0);
		// gl.glScalef(1, currentZoomScale, 1);

	}

	/**
	 * This method shall be called after the view has been rendered, if beginZoom(GL) has been called beforehand.
	 *
	 * @param gl
	 */
	public void endZoom(GL2 gl) {
		previousZoomScale = currentZoomScale;

		// gl.glDisable(GL2ES1.GL_CLIP_PLANE0);
		// gl.glDisable(GL2ES1.GL_CLIP_PLANE1);
		// gl.glDisable(GL2ES1.GL_CLIP_PLANE2);
		// gl.glDisable(GL2ES1.GL_CLIP_PLANE3);
		gl.glDisable(GL.GL_SCISSOR_TEST);

		if (currentZoomScale == 1.0f)
			return;
		gl.glPopMatrix();
		wasMouseWheeled = false;

		hScrollBarLayoutManager.render(gl);
		vScrollBarLayoutManager.render(gl);

		scrollBarDragAndDropController.handleDragging(gl, glMouseListener);

	}

	@Override
	public void handleMouseWheel(int wheelAmount, Vec2f mousePos_dip) {
		float mouse_x = mousePos_dip.x();
		float mouse_y = parentView.getParentGLCanvas().getDIPHeight() - mousePos_dip.y(); // as we start from the bottom
		if (viewArea_dip != null && viewArea_dip.contains(mouse_x, mouse_y)) {
			currentZoomScale += (wheelAmount / 3.0f);
			if (currentZoomScale < 1.0f)
				currentZoomScale = 1.0f;
			wasMouseWheeled = true;
			mouseWheelPosition = mousePos_dip;
		}
	}

	@Override
	public void handleScrollBarUpdate(ScrollBar scrollBar) {
		if (scrollBar == hScrollBar) {
			float zoomCenterX = pixelGLConverter.getGLWidthForPixelWidth(scrollBar.getSelection());
			float viewTranslateX = (viewFrustum.getWidth() / 2.0f) - zoomCenterX - (currentZoomScale - 1) * zoomCenterX;

			if (viewTranslateX > 0)
				viewTranslateX = 0;
			if (viewTranslateX < -(viewFrustum.getWidth() * (currentZoomScale - 1)))
				viewTranslateX = -(viewFrustum.getWidth() * (currentZoomScale - 1));

			relativeViewTranlateX = viewTranslateX / viewFrustum.getWidth();
		}
		if (scrollBar == vScrollBar) {
			float zoomCenterY = pixelGLConverter.getGLHeightForPixelHeight(scrollBar.getSelection());
			float viewTranslateY = (viewFrustum.getHeight() / 2.0f) - zoomCenterY - (currentZoomScale - 1)
					* zoomCenterY;

			if (viewTranslateY > 0)
				viewTranslateY = 0;
			if (viewTranslateY < -(viewFrustum.getHeight() * (currentZoomScale - 1)))
				viewTranslateY = -(viewFrustum.getHeight() * (currentZoomScale - 1));

			relativeViewTranlateY = viewTranslateY / viewFrustum.getHeight();
		}
	}

	public void setLimits(float x, float y) {
		viewFrustum.setLeft(0);
		viewFrustum.setBottom(0);
		viewFrustum.setRight(x);
		viewFrustum.setTop(y);
		hScrollBarLayoutManager.setViewFrustum(viewFrustum);
		vScrollBarLayoutManager.setViewFrustum(viewFrustum);

		hScrollBarLayoutManager.updateLayout();
		vScrollBarLayoutManager.updateLayout();
	}
}
