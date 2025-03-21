package mil.army.dcgs.landpage.database;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.time.DateTimeException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

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
//        List<String> list = new ArrayList<>(ZoneId.getAvailableZoneIds());
        List<String> list = getSortedTimeZones();


        return list;
    }

    /**
     * Instead of using the complete (and unsorted) list of timezones...do some magic
     * and sort them to more easily find the timezone name.  Also put the common ones 
     * "GMT", "US/Eastern", "US/Central", "US/Pacific" at the beginning to more easily find them.
     * 
     * @return list of sorted timezones
     */
    public static List<String> getSortedTimeZones() {
        // Retrieve all available zone IDs
        List<String> zoneIds = new ArrayList<>(ZoneId.getAvailableZoneIds());

        // Create a list and sort it alphabetically
        List<String> sortedZones = new ArrayList<>(zoneIds);

        // Remove all zones that start with "Etc/"
        sortedZones.removeIf(zone -> zone.startsWith("Etc/"));

        // Sort the remaining zones alphabetically
        Collections.sort(sortedZones);

        // Define the time zones to prioritize
        List<String> prioritizedZones = Arrays.asList("GMT", "US/Eastern", "US/Central", "US/Pacific");

        // Remove prioritized zones from the sorted list (if present)
        sortedZones.removeAll(prioritizedZones);

        // Add the prioritized zones to the beginning
        List<String> finalList = new ArrayList<>(prioritizedZones);
        finalList.addAll(sortedZones);

        return finalList;
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