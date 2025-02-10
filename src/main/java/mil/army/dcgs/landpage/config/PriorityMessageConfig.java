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
        String postingUser();
        LocalDateTime startDate();
        LocalDateTime endDate();
        LocalDateTime createdAt();
        LocalDateTime lastUpdated();
    }
}
