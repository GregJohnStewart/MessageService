package mil.army.dcgs.landpage.lifecycle;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import mil.army.dcgs.landpage.database.PriorityMessageRepository;
import mil.army.dcgs.landpage.database.TimezoneRepository;

@Singleton
public class DatabasePopulator {
	
	@Inject
	PriorityMessageRepository priorityMessageRepository;
	
	@Inject
	TimezoneRepository timezoneRepository;

	@Transactional
	void onStart(
		@Observes
		StartupEvent ev
	) {
		this.priorityMessageRepository.populate();
		this.timezoneRepository.populate();
	}
}
