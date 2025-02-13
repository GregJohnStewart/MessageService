package mil.army.dcgs.landpage.database;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.validator.constraints.Range;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.annotation.JsonFormat;

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
    @Size(max=255)
    @Column(length=255)
    public String subject;

    @Builder.Default
    @Range(min=1, max=5)
    @Basic(optional=false)
    public int priority = 5;
    
    @NotNull
    @Basic(optional=false)
    @Size(max=1024)
    @Column(length=1024)
    public String content;
    
    @Builder.Default
    @NotNull
    @Basic(optional=false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    public LocalDateTime startDate = LocalDateTime.now();
    
    @Builder.Default
    @NotNull
    @Basic(optional=false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    public LocalDateTime endDate = LocalDateTime.now();
    
    @NotNull
    @Basic(optional=false)
    public String createdBy;
    
    @Builder.Default
    @NotNull
    @Basic(optional=false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:MM:SS")
    public LocalDateTime createdAt = LocalDateTime.now();
    
    @NotNull
    @Basic(optional=false)
    public String lastUpdatedBy;
    
    @Builder.Default
    @NotNull
    @Basic(optional=false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:MM:SS")
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
        ObjectMapper mapper = new ObjectMapper();
        // Register the JavaTimeModule to properly handle LocalDateTime serialization
        mapper.registerModule(new JavaTimeModule());
        // Disable serialization of dates as timestamps for a more human-readable format
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting PriorityMessage to JSON", e);
        }
    }
}