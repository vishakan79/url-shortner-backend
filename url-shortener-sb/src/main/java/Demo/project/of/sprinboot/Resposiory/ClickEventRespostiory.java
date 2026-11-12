package Demo.project.of.sprinboot.Resposiory;

import Demo.project.of.sprinboot.DTO.ClickEventDTO;
import Demo.project.of.sprinboot.Model.UrlMapping;
import Demo.project.of.sprinboot.Model.clickevent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ClickEventRespostiory extends JpaRepository<clickevent,Long> {
    void deleteByUrlmapping(UrlMapping mapping);

    List<clickevent> findByUrlmappingAndClickeventBetween(
            UrlMapping mapping,
            LocalDateTime startDate,
            LocalDateTime endDate);

    List<clickevent> findByUrlmappingInAndClickeventBetween(
            List<UrlMapping> urlMappings,
            LocalDateTime startDate,
            LocalDateTime endDate);
}
