package com.example.model;

import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import com.example.common.enums.CaseStatus;
import com.example.common.enums.CaseType;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cases")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Case {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "case_seq_generator")
    @SequenceGenerator(name = "case_seq_generator", sequenceName = "case_seq", allocationSize = 1)
    private Long id;

    @Enumerated(EnumType.STRING)
    private CaseType type;

    private String description;

    @Embedded
    private CaseDetails details;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

    @Enumerated(EnumType.STRING)
    private CaseStatus status = CaseStatus.OPEN;

    @ManyToMany
    @JoinTable(
            name = "cases_users",
            joinColumns = {
                    @JoinColumn(name = "case_id", referencedColumnName = "id"),
            },
            inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"))
    private Set<User> assignedUsers = new HashSet<>();

    @OneToMany(mappedBy = "chatCase")
    private Set<Chat> chats = new HashSet<>();

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Case))
            return false;
        Case other = (Case) obj;
        return Objects.equals(id, other.id);
    }
}
