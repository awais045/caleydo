package org.caleydo.core.event;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.xml.bind.annotation.XmlTransient;

import org.caleydo.core.util.collection.Pair;

/**
 * <p>
 * Base class for event handlers which can not process their event in a pre-existing loop (as views do).
 * </p>
 * <p>
 * All classes of this instance must be started in this fashion:
 * </p>
 * 
 * <pre>
 * AEventHandler handler = new EventHandler();
 * Thread thread = new Thread(handler, &quot;thread name&quot;);
 * thread.start();
 * </pre>
 * <p>
 * This process calls the {@link IListenerOwner#registerEventListeners()} method. It's finalize calls the
 * {@link IListenerOwner#unregisterEventListeners()}
 * </p>
 * 
 * @author Alexander Lex
 */
public abstract class AEventHandler
	implements Runnable, IListenerOwner {

	/**
	 * The queue which holds the events
	 */
	@XmlTransient
	private BlockingQueue<Pair<AEventListener<? extends IListenerOwner>, AEvent>> queue =
		new LinkedBlockingQueue<Pair<AEventListener<? extends IListenerOwner>, AEvent>>();

	@Override
	public synchronized void queueEvent(AEventListener<? extends IListenerOwner> listener, AEvent event) {

		try {
			queue.put(new Pair<AEventListener<? extends IListenerOwner>, AEvent>(listener, event));
		}
		catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void processEvents() {
		while (true) {
			try {
				Pair<AEventListener<? extends IListenerOwner>, AEvent> eventPair =
					queue.poll(Long.MAX_VALUE, TimeUnit.DAYS);
				eventPair.getFirst().handleEvent(eventPair.getSecond());
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
	}

	@Override
	public void run() {
		registerEventListeners();
		processEvents();
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		unregisterEventListeners();
	}

}
