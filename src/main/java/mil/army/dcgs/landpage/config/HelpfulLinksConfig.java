package mil.army.dcgs.landpage.config;

import io.smallrye.config.ConfigMapping;

import java.util.List;

@ConfigMapping(prefix = "linkCategories", namingStrategy = ConfigMapping.NamingStrategy.VERBATIM)
public interface HelpfulLinksConfig {

    List<category> categories();
    
    interface category {
       String categoryName();
       List<nameAndLinkPair> links();
    }
    
    interface nameAndLinkPair {
        String name();
        String address();
    }

}
