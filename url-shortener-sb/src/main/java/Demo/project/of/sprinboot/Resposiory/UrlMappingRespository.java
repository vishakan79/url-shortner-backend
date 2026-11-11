package Demo.project.of.sprinboot.Resposiory;

import Demo.project.of.sprinboot.Model.UrlMapping;
import Demo.project.of.sprinboot.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UrlMappingRespository extends JpaRepository<UrlMapping, Long> {
    UrlMapping findByShorturl(String shorturl);

    boolean existsByShorturl(String shorturl);

    List<UrlMapping> findByUserOrderByCreatedDateDesc(User user);

    List<UrlMapping> findByUser(User user);

    @Modifying
    @Query("update UrlMapping u set u.clickcount = u.clickcount + 1 where u.id = :id")
    int incrementClicks(@Param("id") Long id);
}
