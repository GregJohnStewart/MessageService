package mil.army.dcgs.messageService.database;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Range;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PriorityMessage extends PanacheEntityBase {
    @Id
    @GeneratedValue
    public UUID id;

    @NotNull
    @Basic(optional=false)
    public String title;

    @Builder.Default
    @Range(min=1, max=5)
    @Basic(optional=true)
    public int priority = 5;
    
    @NotNull
    @Basic(optional=false)
    public String content;
    
    @Builder.Default
    @Basic(optional=true)
    public LocalDateTime showDateStart = LocalDateTime.now();
    
//    @NotNull
//    @Basic(optional=false)
//    public LocalDateTime showDateEnd;
    
    @NotNull
    @Basic(optional=false)
    public String postingUser;
    
    @Builder.Default
    @NotNull
    @Basic(optional = false)
    public LocalDateTime createdAt = LocalDateTime.now();
    

// Breaks the db call by being added to the call for ex: delete 
// Ex: Caused by: org.hibernate.exception.DataException: could not execute statement [No value specified for parameter 2.] [delete from PriorityMessage where id=? and lastUpdated=?]\r\n\tat 
// removing it 'fixes' the issue but not sure why
//    @Version
//    LocalDateTime lastUpdated;
}
/**
 *
 */