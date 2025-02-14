package mil.army.dcgs.landpage.config;

import io.smallrye.config.ConfigMapping;

import java.util.List;

@ConfigMapping(prefix = "timezones", namingStrategy = ConfigMapping.NamingStrategy.VERBATIM)
public interface TimezoneConfig {

    List<String> identifiers();

}
