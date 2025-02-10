package mil.army.dcgs.landpage.lifecycle;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import mil.army.dcgs.landpage.database.PriorityMessageRepository;

@Singleton
public class DatabasePopulator {
	
	@Inject
	PriorityMessageRepository priorityMessageRepository;
	
	@Transactional
	void onStart(
		@Observes
		StartupEvent ev
	) {
		this.priorityMessageRepository.populate();
	}
}
