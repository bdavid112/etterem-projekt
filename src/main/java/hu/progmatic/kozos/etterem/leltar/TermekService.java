package hu.progmatic.kozos.etterem.leltar;


import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import hu.progmatic.kozos.etterem.szamla.SzamlaRepository;

import java.util.List;


@Service
@Transactional
public class TermekService implements InitializingBean {

    @Autowired
    SzamlaRepository szamlaRepository;

    public final List<Termek> initItems = List.of(
            new Termek(null, "Paradicsom leves", 1200, Tipus.LEVES),
            new Termek(null, "Gulyás leves", 1500, Tipus.LEVES),
            new Termek(null, "Húsleves gazdagon", 1600, Tipus.LEVES),
            new Termek(null, "Gyümölcsleves", 1300, Tipus.LEVES),
            new Termek(null, "Tatárbeefsteak", 2200, Tipus.ELOETEL),
            new Termek(null, "Cézár saláta", 2000, Tipus.ELOETEL),
            new Termek(null, "Tonhal tataki", 2000, Tipus.ELOETEL),
            new Termek(null, "Libamáj pástétom", 2500, Tipus.ELOETEL),
            new Termek(null, "Csípős juhsajtkrém", 1400, Tipus.ELOETEL),
            new Termek(null, "Brassói aprópecsenye", 3200, Tipus.SERTESETEL),
            new Termek(null, "Milánói sertésborda", 2800, Tipus.SERTESETEL),
            new Termek(null, "Sertés szűz érmék", 3200, Tipus.SERTESETEL),
            new Termek(null, "Lyon-i tarja", 2800, Tipus.SERTESETEL),
            new Termek(null, "Lazacsteak", 4200, Tipus.HALETEL),
            new Termek(null, "Fish and chips", 1200, Tipus.HALETEL),
            new Termek(null, "Harcsapaprikás", 1600, Tipus.HALETEL),
            new Termek(null, "Rib-eye steak", 6200, Tipus.MARHAETEL),
            new Termek(null, "T-bone steak", 7000, Tipus.MARHAETEL),
            new Termek(null, "Vadasmarha", 4200, Tipus.MARHAETEL),
            new Termek(null, "Somlói", 1200, Tipus.DESSZERT),
            new Termek(null, "Brownie", 1500, Tipus.DESSZERT),
            new Termek(null, "Gundel palacsinta", 1200, Tipus.DESSZERT),
            new Termek(null, "Csöröge fánk", 1400, Tipus.DESSZERT),
            new Termek(null, "Sör", 1200, Tipus.ALKOHOL),
            new Termek(null, "Fehér Bor", 1200, Tipus.ALKOHOL),
            new Termek(null, "Vörös Bor", 1300, Tipus.ALKOHOL),
            new Termek(null, "Rosé", 1200, Tipus.ALKOHOL),
            new Termek(null, "Pálinka", 1800, Tipus.ROVIDITAL),
            new Termek(null, "Whiskey", 2200, Tipus.ROVIDITAL),
            new Termek(null, "Vodka", 2200, Tipus.ROVIDITAL),
            new Termek(null, "Rum", 2200, Tipus.ROVIDITAL),
            new Termek(null, "Titanic", 600, Tipus.KOKTEL),
            new Termek(null, "Mojito", 3500, Tipus.KOKTEL),
            new Termek(null, "Bloody Mary", 3300, Tipus.KOKTEL),
            new Termek(null, "White Russian", 3200, Tipus.KOKTEL),
            new Termek(null, "Fanta", 1200, Tipus.UDITO),
            new Termek(null, "Kóla", 1200, Tipus.UDITO),
            new Termek(null, "Almalé", 1200, Tipus.UDITO),
            new Termek(null, "Narancslé", 1200, Tipus.UDITO),
            new Termek(null, "Tea", 1200, Tipus.FORROITAL),
            new Termek(null, "Forró csokoládé", 1200, Tipus.FORROITAL),
            new Termek(null, "Cappuccino", 1200, Tipus.KAVE),
            new Termek(null, "Espresso", 1200, Tipus.KAVE),
            new Termek(null, "Cortado", 1300, Tipus.KAVE),
            new Termek(null, "Ristretto", 1300, Tipus.KAVE),
            new Termek(null, "Extra 500", 500, Tipus.EXTRA),
            new Termek(null, "Extra 1000", 1000, Tipus.EXTRA),
            new Termek(null, "Extra 2000", 2000, Tipus.EXTRA)
    );

    private final TermekRepository termekRepository;

    public TermekDto getDto(Integer id) {
        Termek entity = termekRepository.getById(id);
        return TermekDto.builder()
                .id(entity.getId())
                .nev(entity.getNev())
                .ar(entity.getAr())
                .build();
    }

    public TermekService(TermekRepository termekRepository) {
        this.termekRepository = termekRepository;
    }

    public List<Termek> findAll() {
        return termekRepository.findAll();
    }

    public Termek create(Termek item) {
        item.setId(null);
        return termekRepository.saveAndFlush(item);
    }

    public void save(Termek item) {
        termekRepository.saveAndFlush(item);
    }

    public void deleteById(Integer id) {
        termekRepository.deleteById(id);
    }

    public Termek getById(Integer id) {
        return termekRepository.getById(id);
    }

    public Termek getByName(String name) {
        return termekRepository.findAllByNev(name);
    }

    public List<Termek> findAllByTipus(Tipus tipus) {
        return termekRepository.findAllByTipus(tipus);
    }

    @Override
    public void afterPropertiesSet() {
        if (termekRepository.count() == 0) {
            termekRepository.saveAll(initItems);
        }
    }

    public boolean isExists(Integer id) {
        return termekRepository.existsById(id);
    }

    public void update(Integer id, String ujNev, Integer ujAr, Tipus ujTipus) {
        Termek entity = termekRepository.getById(id);
        entity.setNev(ujNev);
        entity.setAr(ujAr);
        entity.setTipus(ujTipus);
    }
}
