package org.caleydo.rcp.command.handler.help;

import org.caleydo.core.manager.event.view.browser.ChangeURLEvent;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.view.base.swt.RcpHTMLBrowserView;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;

public class KnownBugsHandler
	extends AbstractHandler
	implements IHandler {

	private final static String URL_KNOWN_BUGS = "https://trac.icg.tugraz.at/projects/org.caleydo/report/1";

	public Object execute(ExecutionEvent event) throws ExecutionException {

		try {
			HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().showView(RcpHTMLBrowserView.ID);
		}
		catch (PartInitException e) {
			e.printStackTrace();
		}

		ChangeURLEvent changeURLEvent = new ChangeURLEvent();
		changeURLEvent.setSender(this);
		changeURLEvent.setUrl(URL_KNOWN_BUGS);
		GeneralManager.get().getEventPublisher().triggerEvent(changeURLEvent);

		return null;
	}
}
