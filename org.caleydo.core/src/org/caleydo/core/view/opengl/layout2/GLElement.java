/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.layout2;

import gleem.linalg.Vec2f;
import gleem.linalg.Vec4f;

import java.util.Objects;

import org.caleydo.core.view.opengl.layout2.geom.Rect;
import org.caleydo.core.view.opengl.layout2.layout.AGLLayoutElement;
import org.caleydo.core.view.opengl.layout2.layout.GLLayouts;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayout;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayoutElement;
import org.caleydo.core.view.opengl.layout2.layout.IHasGLLayoutData;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.core.view.opengl.layout2.renderer.IGLRenderer;
import org.caleydo.core.view.opengl.picking.IPickingListener;
import org.caleydo.core.view.opengl.picking.PickingListenerComposite;

import com.google.common.base.Predicate;

/**
 * basic layouting element
 *
 * @author Samuel Gratzl
 *
 */
public class GLElement implements IHasGLLayoutData {
	/**
	 * the visibility state of this element
	 *
	 * @author Samuel Gratzl
	 *
	 */
	public enum EVisibility {
		/**
		 * will just be drawn (default)
		 */
		VISIBLE,
		/**
		 * will be drawn and can be picked
		 */
		PICKABLE,
		/**
		 * will not be drawn or pickable but will contribute to the layout
		 */
		HIDDEN,
		/**
		 * invisible and uses no space
		 */
		NONE;

		public boolean doRender() {
			return this == VISIBLE || this == PICKABLE;
		}
	}

	/**
	 * the renderer to use for rendering indirectly
	 */
	private IGLRenderer renderer = GLRenderers.DUMMY;

	/**
	 * location and size of this element determined by parent layout
	 */
	private final Rect bounds_layout = new Rect(0, 0, Float.NaN, Float.NaN);
	/**
	 * location and size set by the user
	 */
	private final Rect bounds_set = new Rect(Float.NaN, Float.NaN, Float.NaN, Float.NaN);

	/**
	 * the current visibility mode, see {@link EVisibility}
	 */
	private EVisibility visibility = EVisibility.VISIBLE;

	/**
	 * my parent element for propagating repaint and relayout requests
	 */
	private IGLElementParent parent;

	/**
	 * global shared context of this hierarchy
	 */
	protected IGLElementContext context;

	/**
	 * layout data for the parent layout
	 */
	private Object layoutData;
	/**
	 * this element as {@link IGLLayoutElement}
	 *
	 */
	final IGLLayoutElement layoutElement = new LayoutElementAdapter();

	/**
	 * cache for managing display lists of rendering
	 */
	private final RenderCache cache = new RenderCache();
	/**
	 * cache for managing display lists of picking
	 */
	private final RenderCache pickCache = new RenderCache();

	/**
	 * the picking ID to use, automatically resolved by settings the visibility mode to {@link EVisibility#PICKABLE)
	 */
	private int pickingID = -1;
	/**
	 * the renderer to use for picking, default: a full sized rect
	 */
	private IGLRenderer picker = GLRenderers.RECT;
	/**
	 * the list of picking listeners, set by {@link #onPick(IPickingListener)}
	 */
	private final PickingListenerComposite pickingListener = new PickingListenerComposite(1);

	/**
	 * indicator whether the layouting should run next time
	 */
	private boolean dirtyLayout = true;

	/**
	 * the z value to add before the value should be rendered
	 */
	private float zDelta = 0;

	public GLElement() {

	}

	public GLElement(IGLRenderer renderer) {
		this.renderer = renderer;
	}

	/**
	 * @param zDelta
	 *            setter, see {@link zDelta}
	 */
	public GLElement setzDelta(float zDelta) {
		if (this.zDelta == zDelta)
			return this;
		this.zDelta = zDelta;
		repaintAll();
		return this;
	}

	/**
	 * @return the zDelta, see {@link #zDelta}
	 */
	public float getzDelta() {
		return zDelta;
	}

	public final GLElement setLayoutData(Object layoutData) {
		if (Objects.equals(this.layoutData, layoutData))
			return this;
		this.layoutData = layoutData;
		return this;
	}

	@Override
	public <T> T getLayoutDataAs(Class<T> clazz, T default_) {
		return GLLayouts.resolveLayoutData(clazz, layoutData, default_);
	}

	/**
	 * renders the current element
	 *
	 * @param g
	 */
	public final void render(GLGraphics g) {
		if (context == null)
			return;
		if (!needToRender()) {
			cache.invalidate(context.getDisplayListPool());
			return;
		}
		float x = bounds_layout.x();
		float y = bounds_layout.y();
		float w = bounds_layout.width();
		float h = bounds_layout.height();

		g.incZ(zDelta);
		g.move(x, y);
		if (!cache.render(context.getDisplayListPool(), g.gl)) {
			cache.begin(context.getDisplayListPool(), g, w, h);
			renderImpl(g, w, h);
			if (context != null) // race condition
				cache.end(context.getDisplayListPool(), g);
			// } else {
			// // cache visualization
			// g.color(1, 0, 1, 0.1f).incZ(1).fillRect(0, 0, w, h).incZ(-1);
		}

		g.move(-x, -y);
		g.incZ(-zDelta);
	}

	/**
	 * hook for rendering directly without a renderer
	 *
	 * @param g
	 * @param w
	 *            width
	 * @param h
	 *            height
	 */
	protected void renderImpl(GLGraphics g, float w, float h) {
		renderer.render(g, w, h, this);
	}

	/**
	 * renders the picking part of this element
	 *
	 * @param g
	 */
	public final void renderPick(GLGraphics g) {
		if (context == null)
			return;
		if (!needToRender() || !hasPickAbles()) {
			pickCache.invalidate(context.getDisplayListPool());
			return;
		}
		float x = bounds_layout.x();
		float y = bounds_layout.y();
		float w = bounds_layout.width();
		float h = bounds_layout.height();

		g.incZ(zDelta);
		g.move(x, y);
		if (!pickCache.render(context.getDisplayListPool(), g.gl)) {
			pickCache.begin(context.getDisplayListPool(), g, w, h);
			boolean pushed = pickingID >= 0;
			if (pushed)
				g.pushName(this.pickingID);
			renderPickImpl(g, w, h);
			if (pushed)
				g.popName();
			pickCache.end(context.getDisplayListPool(), g);
		}
		g.move(-x, -y);
		g.incZ(-zDelta);
	}

	/**
	 * determines if the need to render this and possible sub elements
	 *
	 * @param pickRun
	 * @return
	 */
	private final boolean needToRender() {
		if (!visibility.doRender())
			return false;
		float x = bounds_layout.x();
		float y = bounds_layout.y();
		float w = bounds_layout.width();
		float h = bounds_layout.height();
		return areValidBounds(x, y, w, h);
	}

	/**
	 * determines whether this or a sub element might be pickable
	 *
	 * @return
	 */
	protected boolean hasPickAbles() {
		return visibility == EVisibility.PICKABLE;
	}

	/**
	 * checks if the layout is dirty and it is is so perform the layouting
	 */
	public void layout(int deltaTimeMs) {
		if (dirtyLayout)
			layoutImpl();
	}

	public static boolean areValidBounds(float x, float y, float w, float h) {
		if (w <= 0 || h <= 0 || Float.isNaN(w) || Float.isNaN(h) || Float.isNaN(x) || Float.isNaN(y))
			return false;
		return true;
	}

	public static boolean areValidBounds(Vec4f xywh) {
		return areValidBounds(xywh.x(), xywh.y(), xywh.z(), xywh.w());
	}

	/**
	 * hook for rendering the picking directly without a renderer
	 *
	 * @param g
	 * @param w
	 * @param h
	 */
	protected void renderPickImpl(GLGraphics g, float w, float h) {
		if (visibility == EVisibility.PICKABLE) // check really pickable
			picker.render(g, w, h, this);
	}

	/**
	 * setter for {@link #picker}
	 *
	 * @param picker
	 */
	public final GLElement setPicker(IGLRenderer picker) {
		if (picker == null)
			picker = GLRenderers.DUMMY;
		if (this.picker == picker)
			return this;
		this.picker = picker;
		repaintPick();
		return this;
	}

	/**
	 * @return the picker, see {@link #picker}
	 */
	public final IGLRenderer getPicker() {
		return picker;
	}

	/**
	 * setter for {@link #renderer}
	 *
	 * @param renderer
	 */
	public final GLElement setRenderer(IGLRenderer renderer) {
		if (renderer == null)
			renderer = GLRenderers.DUMMY;
		if (this.renderer == renderer)
			return this;
		this.renderer = renderer;
		repaint();
		return this;
	}

	/**
	 * @return the renderer, see {@link #renderer}
	 */
	public final IGLRenderer getRenderer() {
		return renderer;
	}

	/**
	 * @param display
	 *            setter, see {@link display}
	 */
	public final GLElement setVisibility(EVisibility display) {
		if (this.visibility == display)
			return this;
		final EVisibility old = this.visibility;
		final EVisibility new_ = display;

		this.visibility = display;
		if (old == EVisibility.NONE || new_ == EVisibility.NONE)
			relayoutParent();
		if (old == EVisibility.PICKABLE) {
			//not longer pickable
			if (pickingID >= 0) {
				context.unregisterPickingListener(pickingID);
				pickingID = -1;
			}
		} else if (new_ == EVisibility.PICKABLE) {
			// now pickable
			if (pickingID < 0 && context != null) {
				pickingID = context.registerPickingListener(pickingListener, getPickingObjectId());
			}
		}
		onVisibilityChanged(old, new_);

		repaint();
		return this;
	}

	protected void onVisibilityChanged(EVisibility old, EVisibility new_) {
		// hook
	}

	/**
	 * hook for returning the picking object id to use, default 0
	 *
	 * @return
	 */
	protected int getPickingObjectId() {
		return 0;
	}

	/**
	 * adds picking listener to this element
	 *
	 * @param customPickingListener
	 * @return
	 */
	public final GLElement onPick(IPickingListener customPickingListener) {
		this.pickingListener.add(customPickingListener);
		return this;
	}

	/**
	 * @return the visibility, see {@link #visibility}
	 */
	public final EVisibility getVisibility() {
		return visibility;
	}

	/**
	 * sets the size of this element to a fixed size, to help the {@link IGLLayout}
	 *
	 * @param w
	 * @param h
	 * @return
	 */
	public final GLElement setSize(float w, float h) {
		if (Float.compare(this.bounds_set.width(), w) == 0 && Float.compare(this.bounds_set.height(), h) == 0)
			return this;
		this.bounds_set.width(w);
		this.bounds_layout.width(w);
		this.bounds_set.height(h);
		this.bounds_layout.height(h);
		relayoutParent();
		return this;
	}

	/**
	 * sets the location of this element to a fixed position, to help the {@link IGLLayout}
	 *
	 * @param x
	 * @param y
	 * @return
	 */
	public final GLElement setLocation(float x, float y) {
		if (Float.compare(this.bounds_set.x(), x) == 0 && Float.compare(this.bounds_set.y(), y) == 0)
			return this;
		this.bounds_set.x(x);
		this.bounds_layout.x(x);
		this.bounds_set.y(y);
		this.bounds_layout.y(y);
		relayoutParent();
		return this;
	}

	/**
	 * returns the layouted position
	 *
	 * @return
	 */
	public final Vec2f getLocation() {
		return bounds_layout.xy();
	}

	/**
	 * computes the absolute location of this element using my parent
	 *
	 * @return
	 */
	public final Vec2f getAbsoluteLocation() {
		if (parent == null) {
			return getLocation();
		} else {
			return parent.toAbsolute(getLocation());
		}
	}

	/**
	 * converts a position to absolute pixel coordinates
	 *
	 * @param relative
	 * @return
	 */
	public final Vec2f toAbsolute(Vec2f relative) {
		relative.add(getLocation());
		return parent.toAbsolute(relative);
	}

	/**
	 * converts a position to relative to my absolute position, see {@link #getAbsoluteLocation()}
	 *
	 * @param absolute
	 * @return
	 */
	public final Vec2f toRelative(Vec2f absolute) {
		absolute = parent.toRelative(absolute);
		absolute.sub(getLocation());
		return absolute;
	}

	/**
	 * returns the layouted size of this element
	 *
	 * @return
	 */
	public final Vec2f getSize() {
		return bounds_layout.size();
	}

	/**
	 * combination of {@link #getLocation()} and {@link #getSize()}
	 *
	 * @return
	 */
	public final Vec4f getBounds() {
		return bounds_layout.bounds();
	}

	/**
	 * shortcut for {@link #setLocation(float, float)} and {@link #setSize(float, float)}
	 *
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @return
	 */
	public final GLElement setBounds(float x, float y, float w, float h) {
		return setLocation(x, y).setSize(w, h);
	}

	/**
	 * triggers that I should be relayouted before the next run
	 */
	public void relayout() {
		repaintAll();
		this.dirtyLayout = true;
	}

	protected final void relayoutParent() {
		if (parent != null)
			parent.relayout();
	}

	/**
	 * shortcut to {@link #repaint()} and {@link #repaintAll()}
	 */
	public final void repaintAll() {
		repaint();
		repaintPick();
	}

	/**
	 * triggers that me and my parents get repainted
	 */
	public void repaint() {
		int bak = 0;
		if (context != null)
			bak = cache.invalidate(context.getDisplayListPool());
		if (parent != null /* && bak > 0 */)
			parent.repaint();
	}

	/**
	 * triggers that me and my parents get repaint the picking representation
	 */
	public void repaintPick() {
		int bak = 0;
		if (context != null)
			bak = pickCache.invalidate(context.getDisplayListPool());
		if (parent != null /* && bak > 0 */)
			parent.repaintPick();
	}

	final void setParent(IGLElementParent parent) {
		if (this.parent == parent)
			return;
		this.parent = parent;
	}

	/**
	 * @return the parent, see {@link #parent}
	 */
	public final IGLElementParent getParent() {
		return parent;
	}

	/**
	 * finds a parent in the hierarchy that satisfies the given predicate
	 *
	 * @param satisfies
	 * @return
	 */
	protected final IGLElementParent findParent(Predicate<IGLElementParent> satisfies) {
		IGLElementParent act = parent;
		while (act != null && !(satisfies.apply(act))) {
			act = parent.getParent();
		}
		return act;
	}

	/**
	 * setup method, when adding a child to a parent
	 * @param context
	 */
	protected void init(IGLElementContext context) {
		this.context = context;
		if (visibility == EVisibility.PICKABLE) {
			pickingID = context.registerPickingListener(pickingListener, getPickingObjectId());
		}
		context.init(this);
	}

	/**
	 * take down, after removing a child from its parent
	 */
	protected void takeDown() {
		if (context == null)
			return;
		context.takeDown(this);
		cache.takeDown(context.getDisplayListPool());
		pickCache.takeDown(context.getDisplayListPool());
		if (pickingID >= 0) {
			context.unregisterPickingListener(pickingID);
			pickingID = -1;
		}
		this.parent = null;
		this.context = null;
	}

	/**
	 * trigger to layout this element
	 */
	protected void layoutImpl() {
		dirtyLayout = false;
		if (context != null) {
			cache.invalidate(context.getDisplayListPool());
			pickCache.invalidate(context.getDisplayListPool());
		}
	}

	private void setLayoutSize(float w, float h) {
		if (Float.compare(this.bounds_layout.width(), w) == 0 && Float.compare(this.bounds_layout.height(), h) == 0)
			return;
		this.bounds_layout.width(w);
		this.bounds_layout.height(h);
		if (w > 0 && h > 0)
			relayout();
	}

	public <P, R> R accept(IGLElementVisitor<P, R> visitor, P para) {
		return visitor.visit(this, para);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(getClass().getSimpleName());
		builder.append(" [visibility=");
		builder.append(visibility);
		builder.append(", xywh_layout=").append(this.bounds_layout);
		builder.append(", xywh_set=").append(this.bounds_set);
		builder.append("]");
		return builder.toString();
	}

	/**
	 * implementation of the {@link IGLLayoutElement} spec
	 *
	 * @author Samuel Gratzl
	 *
	 */
	private class LayoutElementAdapter extends AGLLayoutElement {
		@Override
		public void setSize(float w, float h) {
			setLayoutSize(w, h);
		}

		@Override
		public void setLocation(float x, float y) {
			bounds_layout.xy(x, y);
		}

		@Override
		public Vec4f getBounds() {
			return GLElement.this.getBounds();
		}

		@Override
		public <T> T getLayoutDataAs(Class<T> clazz, T default_) {
			return GLElement.this.getLayoutDataAs(clazz, default_);
		}

		@Override
		public GLElement asElement() {
			return GLElement.this;
		}

		@Override
		public String toString() {
			return GLElement.this.toString();
		}

		@Override
		public Vec4f getSetBounds() {
			return bounds_set.bounds();
		}
	}
}




