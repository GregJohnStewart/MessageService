package mil.army.dcgs.messageService.config;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ConfigMapping(prefix = "priorityMessages", namingStrategy = ConfigMapping.NamingStrategy.VERBATIM)
public interface PriorityMessageConfig {

    List<Message> messages();

    interface Message {
        String title();
        @WithDefault("1")
        int priority();
        String content();
        Optional<LocalDateTime> showStartDate();
    }
}
