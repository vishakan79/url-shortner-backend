package Demo.project.of.sprinboot.Model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "url_mapping", indexes = {
        @Index(name = "idx_url_mapping_shorturl", columnList = "shorturl", unique = true),
        @Index(name = "idx_url_mapping_user", columnList = "user_id")
})
public class UrlMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 2048)
    private String originalurl;

    @Column(length = 32)
    private String shorturl;

    private int clickcount = 0;
    private LocalDateTime createdDate;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "urlmapping")
    private List<clickevent> clickevents;

    @Override
    public int hashCode() { return id == null ? 0 : id.hashCode(); }

    @Override
    public boolean equals(Object o) {
        return o instanceof UrlMapping u && id != null && id.equals(u.id);
    }

    @Override
    public String toString() { return "UrlMapping(" + id + ", " + shorturl + ")"; }
}
