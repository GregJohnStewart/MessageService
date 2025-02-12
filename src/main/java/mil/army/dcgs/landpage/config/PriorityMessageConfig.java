package mil.army.dcgs.landpage.config;

import io.smallrye.config.ConfigMapping;
import java.time.LocalDateTime;

import java.util.List;

@ConfigMapping(prefix = "priorityMessages", namingStrategy = ConfigMapping.NamingStrategy.VERBATIM)
public interface PriorityMessageConfig {

    List<Message> messages();

    interface Message {
        String subject();
        int priority();
        String content();
        LocalDateTime startDate();
        LocalDateTime endDate();
        String createdBy();
        LocalDateTime createdAt();
        String lastUpdatedBy();
        LocalDateTime lastUpdated();
    }
}
