package hu.bme.aut.retelab2.repository;

import hu.bme.aut.retelab2.domain.Ad;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class AdRepository {
    @PersistenceContext
    private EntityManager em;

    @Transactional
    public Ad save(Ad ad) {
        return em.merge(ad);
    }

    public List<Ad> findByMinMax(int min, int max){
            return em.createQuery("SELECT a FROM Ad a WHERE a.price >= ?1 AND a.price <= ?2")
            .setParameter(1, min)
            .setParameter(2, max)
            .getResultList();
    }

    public Ad findById(long id){return em.find(Ad.class, id);}

    public List<Ad> findAll(){
        return em.createQuery("SELECT a FROM Ad a")
                .getResultList();
    }

    public List<Ad> findExpired(){
        LocalDateTime actualTime = LocalDateTime.now();
        return em.createQuery("SELECT a FROM Ad a WHERE a.expirationDate < ?1")
                .setParameter(1, actualTime)
                .getResultList();
    }

    @Transactional
    public void deleteExpired(){
        List<Ad> expiredAds = findExpired();
        for(Ad ad : expiredAds){ em.remove(ad); }
    }
}
