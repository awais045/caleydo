package org.caleydo.core.data.filter;

import org.caleydo.core.data.filter.event.NewContentFilterEvent;
import org.caleydo.core.data.filter.event.ReEvaluateContentFilterListEvent;
import org.caleydo.core.data.virtualarray.ContentVAType;
import org.caleydo.core.data.virtualarray.ContentVirtualArray;
import org.caleydo.core.data.virtualarray.delta.ContentVADelta;
import org.caleydo.core.manager.GeneralManager;

/**
 * Static type for {@link Filter}s handling {@link ContentVirtualArray}s.
 * 
 * @author Alexander Lex
 */
public class ContentFilter
	extends Filter<ContentVAType, ContentVADelta> {

	public void updateFilterManager() {

		if (!isRegistered()) {
			NewContentFilterEvent filterEvent = new NewContentFilterEvent();
			filterEvent.setFilter(this);
			filterEvent.setSender(this);
			filterEvent.setDataDomainType(dataDomain.getDataDomainType());

			GeneralManager.get().getEventPublisher().triggerEvent(filterEvent);
		}
		else {

			ReEvaluateContentFilterListEvent reevaluateEvent = new ReEvaluateContentFilterListEvent();
			// reevaluateEvent.addFilter(filter);
			reevaluateEvent.setSender(this);
			reevaluateEvent.setDataDomainType(dataDomain.getDataDomainType());

			GeneralManager.get().getEventPublisher().triggerEvent(reevaluateEvent);
		}
	}
}
