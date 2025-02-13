package mil.army.dcgs.landpage.database;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Range;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Getter
@Setter
@Entity
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PriorityMessage extends PanacheEntityBase {
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Id
    @GeneratedValue
    public UUID id;

    @NotNull
    @Basic(optional=false)
    public String subject;

    @Builder.Default
    @Range(min=1, max=5)
    @Basic(optional=false)
    public int priority = 5;
    
    @NotNull
    @Basic(optional=false)
    public String content;
    
    @Builder.Default
    @NotNull
    @Basic(optional=false)
    public LocalDateTime startDate = LocalDateTime.now();
    
    @Builder.Default
    @NotNull
    @Basic(optional=false)
    public LocalDateTime endDate = LocalDateTime.now();
    
    @NotNull
    @Basic(optional=false)
    public String createdBy;
    
    @Builder.Default
    @NotNull
    @Basic(optional=false)
    public LocalDateTime createdAt = LocalDateTime.now();
    
    @NotNull
    @Basic(optional=false)
    public String lastUpdatedBy;
    
    @Builder.Default
    @NotNull
    @Basic(optional=false)
    public LocalDateTime lastUpdated = LocalDateTime.now();

    /**
     * Return the start date as a formatted string
     */
    public String getFormattedStartDate() {
        return dateFormatter.format(startDate);
    }

    /**
     * Return the end date as a formatted string
     */
    public String getFormattedEndDate() {
        return dateFormatter.format(endDate);
    }

    /**
     * Return the createdAt datetime as a formatted string
     */
    public String getFormattedCreatedAt() {
        return dateTimeFormatter.format(createdAt);
    }

    /**
     * Return the lastUpdated datetime as a formatted string
     */
    public String getFormattedLastUpdated() {
        return dateTimeFormatter.format(lastUpdated);
    }

    /**
     * Messy toJSON method...use a library?
     * @return JSON formatted message
     */
    public String toJSON() {
        String json = "{";
        json += "\"id\":\"" + id + "\",";
        json += "\"subject\":\"" + subject + "\",";
        json += "\"priority\":\"" + priority + "\",";
        json += "\"startDate\":\"" + getFormattedStartDate() + "\",";
        json += "\"endDate\":\"" + getFormattedEndDate() + "\",";
        json += "\"createdBy\":\"" + createdBy + "\",";
        json += "\"createdAt\":\"" + getFormattedCreatedAt() + "\",";
        json += "\"lastUpdatedBy\":\"" + lastUpdatedBy + "\",";
        json += "\"lastUpdated\":\"" + getFormattedLastUpdated() + "\",";
        json += "\"content\":\"" + content + "\"";
        json += "}";

        return json;
    }
}