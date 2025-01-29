package mil.army.dcgs.messageService.lifecycle;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import mil.army.dcgs.messageService.database.PriorityMessageRepository;

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
