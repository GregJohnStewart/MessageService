package mil.army.dcgs.landpage.database;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Range;

import java.time.LocalDateTime;
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
    public String subject;

    @Builder.Default
    @Range(min=1, max=5)
    @Basic(optional=false)
    public int priority = 5;
    
    @NotNull
    @Basic(optional=false)
    public String content;
    
    @NotNull
    @Basic(optional=false)
    public String postingUser;
    
    @Builder.Default
    @NotNull
    @Basic(optional=false)
    public LocalDateTime startDate = LocalDateTime.now();
    
    @Builder.Default
    @NotNull
    @Basic(optional=false)
    public LocalDateTime endDate = LocalDateTime.now();
    
    @Builder.Default
    @NotNull
    @Basic(optional=false)
    public LocalDateTime createdAt = LocalDateTime.now();
    
    @Builder.Default
    @NotNull
    @Basic(optional=false)
    public LocalDateTime lastUpdated = LocalDateTime.now();
}