package academy.prog;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

// DB -> E(20) -> R -> S -> DTO <- C -> View / JSON (5)

@Service
public class UrlService {
    private final UrlRepository urlRepository;

    public UrlService(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    @Transactional
    public long saveUrl(UrlDTO urlDTO) {
        var urlRecord = urlRepository.findByUrl(urlDTO.getUrl());
        if (urlRecord == null) {
            urlRecord = UrlRecord.of(urlDTO);
            urlRepository.save(urlRecord);
        }

        return urlRecord.getId();
    }

    @Transactional
    public String getUrl(long id) {
        var urlOpt = urlRepository.findById(id);
        if (urlOpt.isEmpty())
            return null;

        var urlRecord = urlOpt.get();
        urlRecord.setCount(urlRecord.getCount() + 1);
        urlRecord.setLastAccess(new Date());

        return urlRecord.getUrl();
    }

    //delete URL
    @Transactional
    public boolean deleteUrl(long id){
        Boolean check = false;
        var urlOpt = urlRepository.findById(id);
        if (urlOpt.isEmpty()) {
            return check;
        }
        var urlRecord = urlOpt.get();
        urlRecord.getCount(); // повертаємо значення, скільки разів за весь час було використане посилання перед видаленням
        urlRepository.delete(urlRecord);
        check = true;
        System.out.println("ok");
        return check;
    }
    // delete old URL (age 7 days)
    @Transactional
    public void oldUrl(){
        var urlOpt = urlRepository.findAll();
        for (UrlRecord item: urlOpt) {
          Date access = item.getLastAccess();
          Date today = new Date();
            Duration duration = Duration.ofDays(today.getTime() - access.getTime());
            if(duration.equals(7)){
                urlRepository.delete(item);
            }
        }
    }

    @Transactional(readOnly = true)
    public List<UrlStatDTO> getStatistics() {
        var records = urlRepository.findAll();
        var result = new ArrayList<UrlStatDTO>();

        records.forEach(x -> result.add(x.toStatDTO()));

        return result;
    }
}
