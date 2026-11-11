package Demo.project.of.sprinboot.Service;

import Demo.project.of.sprinboot.DTO.ClickEventDTO;
import Demo.project.of.sprinboot.DTO.UrlMappingDto;
import Demo.project.of.sprinboot.Model.UrlMapping;
import Demo.project.of.sprinboot.Model.User;
import Demo.project.of.sprinboot.Model.clickevent;
import Demo.project.of.sprinboot.Resposiory.ClickEventRespostiory;
import Demo.project.of.sprinboot.Resposiory.UrlMappingRespository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class URLmappingService {
    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    private ClickEventRespostiory clickEventRespostiory;
    private UrlMappingRespository urlMappingRespository;

    @Transactional
    public UrlMappingDto createShortUrl(String originalurl, User user) {
        String normalized = normalizeUrl(originalurl);
        UrlMapping urlMapping = new UrlMapping();
        urlMapping.setOriginalurl(normalized);
        urlMapping.setShorturl(generateUniqueCode());
        urlMapping.setUser(user);
        urlMapping.setCreatedDate(LocalDateTime.now());
        return converttoDo(urlMappingRespository.save(urlMapping));
    }

    /** Adds https:// when missing and rejects anything that is not a valid http(s) url. */
    public static String normalizeUrl(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new IllegalArgumentException("URL is required");
        }
        String url = raw.trim();
        if (!url.matches("(?i)^https?://.*")) {
            url = "https://" + url;
        }
        if (url.length() > 2048) {
            throw new IllegalArgumentException("URL is too long");
        }
        try {
            URI uri = URI.create(url);
            String host = uri.getHost();
            if (host == null || !host.contains(".")) {
                throw new IllegalArgumentException("Please enter a valid URL");
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Please enter a valid URL");
        }
        return url;
    }

    private String generateUniqueCode() {
        for (int attempt = 0; attempt < 10; attempt++) {
            int length = attempt < 5 ? 7 : 9;
            StringBuilder sb = new StringBuilder(length);
            for (int i = 0; i < length; i++) {
                sb.append(CHARS.charAt(RANDOM.nextInt(CHARS.length())));
            }
            String code = sb.toString();
            if (!urlMappingRespository.existsByShorturl(code)) {
                return code;
            }
        }
        throw new IllegalStateException("Could not generate a unique short url, try again");
    }

    private UrlMappingDto converttoDo(UrlMapping saved) {
        UrlMappingDto dto = new UrlMappingDto();
        dto.setId(saved.getId());
        dto.setOriginalurl(saved.getOriginalurl());
        dto.setShortnerurl(saved.getShorturl());
        dto.setClickCount(saved.getClickcount());
        dto.setCreatedtime(saved.getCreatedDate());
        dto.setUsername(saved.getUser().getUsername());
        return dto;
    }

    @Transactional(readOnly = true)
    public List<UrlMappingDto> geturlsuser(User user) {
        return urlMappingRespository.findByUserOrderByCreatedDateDesc(user).stream()
                .map(this::converttoDo).toList();
    }

    @Transactional(readOnly = true)
    public List<ClickEventDTO> getclickeventbydate(String shorturl, LocalDateTime startDate, LocalDateTime endDate, User user) {
        UrlMapping urlMapping = urlMappingRespository.findByShorturl(shorturl);
        if (urlMapping == null || !urlMapping.getUser().getId().equals(user.getId())) {
            return List.of();
        }
        Map<LocalDate, Long> grouped = clickEventRespostiory
                .findByUrlmappingAndClickeventBetween(urlMapping, startDate, endDate)
                .stream()
                .collect(Collectors.groupingBy(c -> c.getClickevent().toLocalDate(), TreeMap::new, Collectors.counting()));
        return grouped.entrySet().stream().map(e -> {
            ClickEventDTO d = new ClickEventDTO();
            d.setClickDate(e.getKey());
            d.setCount(e.getValue());
            return d;
        }).toList();
    }

    @Transactional(readOnly = true)
    public Map<LocalDate, Long> getTotalClickByuseranddate(User user, LocalDate startDate, LocalDate endDate) {
        List<UrlMapping> urlMappings = urlMappingRespository.findByUser(user);
        if (urlMappings.isEmpty()) {
            return Map.of();
        }
        List<clickevent> events = clickEventRespostiory.findByUrlmappingInAndClickeventBetween(
                urlMappings, startDate.atStartOfDay(), endDate.plusDays(1).atStartOfDay());
        return events.stream().collect(
                Collectors.groupingBy(c -> c.getClickevent().toLocalDate(), TreeMap::new, Collectors.counting()));
    }

    /** Looks up the target and records the click with a single atomic update. */
    @Transactional
    public UrlMapping getOriginalUrl(String shorturl) {
        UrlMapping urlMapping = urlMappingRespository.findByShorturl(shorturl);
        if (urlMapping != null) {
            urlMappingRespository.incrementClicks(urlMapping.getId());
            clickevent ev = new clickevent();
            ev.setClickevent(LocalDateTime.now());
            ev.setUrlmapping(urlMapping);
            clickEventRespostiory.save(ev);
        }
        return urlMapping;
    }
}
