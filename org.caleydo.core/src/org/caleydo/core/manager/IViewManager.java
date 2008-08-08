package org.caleydo.core.manager;

import java.util.ArrayList;
import java.util.Collection;
import javax.swing.JFrame;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.view.IView;
import org.caleydo.core.view.ViewType;

/**
 * Manage all canvas, view, ViewRep's and GLCanvas objects.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 * @author Alexander Lex
 */
public interface IViewManager
	extends IManager<IView>
{

	public IView createView(final EManagedObjectType useViewType,
			final int iParentContainerId, final String sLabel);

	public void addViewRep(IView view);

	public void removeViewRep(IView view);

	public ArrayList<IView> getViewRepByType(ViewType viewType);
}