package hu.bme.aut.retelab2.controller;

import hu.bme.aut.retelab2.domain.Ad;
import hu.bme.aut.retelab2.repository.AdRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/ads")
public class AdController {

    @Autowired
    private AdRepository adRepository;

    @PostMapping
    @ResponseBody
    public ResponseEntity<Ad> addAd(@RequestBody Ad ad){
        ad.setId(null);
        return new ResponseEntity<Ad>(adRepository.save(ad), HttpStatus.OK);
    }

    @GetMapping
    @ResponseBody
    public ResponseEntity<List<Ad>> getAds(@RequestParam(name="min_price", required = false, defaultValue = "0")int minPrice,
                                           @RequestParam(name="max_price", required = false, defaultValue = "10000000")int maxPrice){
        List<Ad> ads = adRepository.findByMinMax(minPrice, maxPrice);
        for(Ad ad : ads) {ad.setSecret(null);}
        return new ResponseEntity<List<Ad>>(adRepository.findByMinMax(minPrice, maxPrice), HttpStatus.OK);
    }

    @GetMapping("{tag}")
    @ResponseBody
    public ResponseEntity<List<Ad>> getTag(@PathVariable String tag){
        List<Ad> ads = adRepository.findAll();
        List<Ad> filteredAds = ads
                .stream()
                .filter(ad -> ad.getTags().stream().anyMatch(t -> t.equals(tag)))
                .collect(Collectors.toList());
        return new ResponseEntity<List<Ad>>(filteredAds, HttpStatus.OK);
    }

    @PutMapping
    @ResponseBody
    public ResponseEntity<Ad> updateAd(@RequestBody Ad updatedAd){
        Ad oldAd = adRepository.findById(updatedAd.getId());
        if(oldAd != null){
            if(!oldAd.getSecret().isEmpty() && updatedAd.getSecret().equals(oldAd.getSecret())){
                updatedAd.setTimeOfCreation(oldAd.getTimeOfCreation());
                return new ResponseEntity<Ad>(adRepository.save(updatedAd), HttpStatus.OK);
            }
            else
                return new ResponseEntity<Ad>(HttpStatus.FORBIDDEN);
        }
        else
            return new ResponseEntity<Ad>(HttpStatus.NOT_FOUND);
    }

    @Scheduled(fixedDelay= 6000)
    public void scheduledDelete(){ adRepository.deleteExpired(); }
}
