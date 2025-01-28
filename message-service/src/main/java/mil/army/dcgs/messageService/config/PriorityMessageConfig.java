package mil.army.dcgs.messageService.config;

import io.smallrye.config.ConfigMapping;

import java.util.List;

@ConfigMapping(prefix = "priorityMessages", namingStrategy = ConfigMapping.NamingStrategy.VERBATIM)
public interface PriorityMessageConfig {

    List<Message> messages();

    interface Message {
        String title();
        String content();
        String date();
        String priority();
    }
}
