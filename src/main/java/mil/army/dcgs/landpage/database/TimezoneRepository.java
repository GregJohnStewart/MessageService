package mil.army.dcgs.landpage.database;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.time.DateTimeException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import mil.army.dcgs.landpage.config.TimezoneConfig;

@Slf4j
@Named("TimezoneRepository")
@ApplicationScoped
public class TimezoneRepository implements PanacheRepository<Timezone> {
    
    @Inject
    TimezoneConfig timezoneConfig;
    
    public void populate() {
        for(String currTimezone : this.timezoneConfig.identifiers()){

            Timezone timezone = Timezone.builder()
                                                  .identifier(currTimezone)
                                                  .build();

            if(this.find("identifier", timezone.getIdentifier()).count() > 0){
                log.warn("Time zone already exists: {}", timezone.getIdentifier());
            }
            else if(!isValidZoneId(currTimezone)) {
                log.warn("Invalid timezone: " + currTimezone);
            } else {
                getSingleOffset(currTimezone);
                this.persist(timezone);
                log.info("Time zone created: {}", timezone);
            }  
        }
    
    }
      
    public List<Timezone> getTimezonesToDisplay(){
        return this.findAll().stream().toList();
    }
    
    public List<String> getAllZoneIds() {
        List<String> list = new ArrayList<>(ZoneId.getAvailableZoneIds());
        return list;
    }
    
    public float getSingleOffset(String identifier) {
        // Replace spaces with underscores and convert to uppercase
        String zoneIdStr = identifier.replace(" ", "_");

        // Get the ZoneId and the current time in that zone
        ZoneId zoneId = ZoneId.of(zoneIdStr);
        ZonedDateTime now = ZonedDateTime.now(zoneId);

        // Calculate the UTC offset in hours
        float utcOffset = (float) (now.getOffset().getTotalSeconds() / 3600.0);

        return utcOffset;
    }
    
    public boolean isValidZoneId(String zoneId) {
        try {
            ZoneId.of(zoneId);
            return true;
        } catch (DateTimeException e) {
            return false;
        }
    }
}