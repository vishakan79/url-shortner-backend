package Demo.project.of.sprinboot.Controller;

import Demo.project.of.sprinboot.DTO.ClickEventDTO;
import Demo.project.of.sprinboot.DTO.UrlMappingDto;
import Demo.project.of.sprinboot.Model.User;
import Demo.project.of.sprinboot.Resposiory.UserResposiory;
import Demo.project.of.sprinboot.Service.URLmappingService;
import Demo.project.of.sprinboot.Service.Userservice;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.text.DateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/urls")
@AllArgsConstructor
public class UrlMappingController {
    private URLmappingService  urlmappingService;
    private Userservice userservice;

    @PostMapping("/shorten")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UrlMappingDto> createshorturl(@RequestBody Map<String ,String> request, Principal principal)
    {
        String originalurl = request.get("originalUrl");
        User user =userservice.findbyusername(principal.getName());
        UrlMappingDto urlMappingDto = urlmappingService.createShortUrl(originalurl,user);
        return ResponseEntity.ok(urlMappingDto);

    }
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> deleteUrl(@PathVariable Long id, Principal principal) {
        User user = userservice.findbyusername(principal.getName());
        urlmappingService.deleteUrl(id, user);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/myurls")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<UrlMappingDto>> getMyurls(Principal principal){
        User user =userservice.findbyusername(principal.getName());
        List<UrlMappingDto> urls = urlmappingService.geturlsuser(user);
        return ResponseEntity.ok(urls);
    }



    @GetMapping("/analytics/{shortUrl}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<ClickEventDTO>> geturlAnalystics(@PathVariable String shortUrl,@RequestParam("startdate") String startdate,@RequestParam("enddate") String enddate, Principal principal){
        User user = userservice.findbyusername(principal.getName());
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        LocalDateTime startDate = LocalDateTime.parse(startdate,formatter);
        LocalDateTime endDate = LocalDateTime.parse(enddate,formatter);
        List<ClickEventDTO> clickEventDTOS =urlmappingService.getclickeventbydate(shortUrl,startDate,endDate,user);
        return ResponseEntity.ok(clickEventDTOS);
    }


    @GetMapping("/totalClicks")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Map<LocalDate,Long>> getTotalClick(Principal principal
                                                               ,@RequestParam("startdate") String stardate,@RequestParam("enddate") String enddate){
       DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
       User user = userservice.findbyusername(principal.getName());
       LocalDate startDate = LocalDate.parse(stardate,formatter);
       LocalDate endDate = LocalDate.parse(enddate,formatter);
       Map<LocalDate,Long> totalclicks = urlmappingService.getTotalClickByuseranddate(user,startDate,endDate);
       return ResponseEntity.ok(totalclicks);
    }
}
