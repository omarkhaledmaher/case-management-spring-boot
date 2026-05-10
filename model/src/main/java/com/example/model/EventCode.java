package com.example.model;

import java.util.Objects;
import com.example.common.enums.EventType;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EventCode {
    private String entityName;
    private EventType type;

    @Override
    public int hashCode() {
        return Objects.hash(entityName, type);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        EventCode other = (EventCode) obj;
        return Objects.equals(entityName, other.entityName) && type == other.type;
    }

}
