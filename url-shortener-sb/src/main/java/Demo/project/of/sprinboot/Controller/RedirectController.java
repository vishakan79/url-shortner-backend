package Demo.project.of.sprinboot.Controller;

import Demo.project.of.sprinboot.Model.UrlMapping;
import Demo.project.of.sprinboot.Service.URLmappingService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@AllArgsConstructor
public class RedirectController {
    private URLmappingService urlmappingService;

    @GetMapping("/{shorturl}")
    public ResponseEntity<Void> redirect(@PathVariable String shorturl) {
        if (!shorturl.matches("[A-Za-z0-9]{4,12}")) {
            return ResponseEntity.notFound().build();
        }
        UrlMapping urlMapping = urlmappingService.getOriginalUrl(shorturl);
        if (urlMapping == null) {
            return ResponseEntity.notFound().build();
        }
        String target = urlMapping.getOriginalurl();
        if (!target.matches("(?i)^https?://.*")) {
            target = "https://" + target;
        }
        return ResponseEntity.status(302)
                .header(HttpHeaders.LOCATION, URI.create(target).toASCIIString())
                .header(HttpHeaders.CACHE_CONTROL, "no-store")
                .build();
    }

    @GetMapping("/api/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("ok");
    }
}
