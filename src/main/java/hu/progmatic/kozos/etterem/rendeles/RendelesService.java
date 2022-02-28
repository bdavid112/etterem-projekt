package hu.progmatic.kozos.etterem.rendeles;

import hu.progmatic.kozos.etterem.asztal.Asztal;
import hu.progmatic.kozos.etterem.asztal.AsztalRepository;
import hu.progmatic.kozos.etterem.asztal.AsztalService;
import hu.progmatic.kozos.etterem.asztal.TableViewDto;
import hu.progmatic.kozos.etterem.leltar.Termek;
import hu.progmatic.kozos.etterem.leltar.TermekService;
import hu.progmatic.kozos.etterem.leltar.Tipus;
import hu.progmatic.kozos.etterem.szamla.SzamlaService;
import hu.progmatic.kozos.felhasznalo.FelhasznaloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class RendelesService {
    @Autowired
    private SzamlaService szamlaService;
    @Autowired
    private RendelesRepository rendelesRepository;
    @Autowired
    private AsztalService asztalService;
    @Autowired
    private TermekService termekService;
    @Autowired
    private AsztalRepository asztalRepository;
    @Autowired
    private FelhasznaloService felhasznaloService;

    public Rendeles create(@Valid CreateRendelesCommand command) {
        Asztal asztal = asztalService.getById(command.getAsztalId());
        if(asztal.getRendelesek().isEmpty()){
            asztal.setFelhasznalo(felhasznaloService.getById(felhasznaloService.getFelhasznaloId()).getNev());
        }
        Termek termek = termekService.getById(command.getEtteremTermekId());
        Rendeles rendeles = Rendeles.builder()
                .asztal(asztal)
                .termek(termek)
                .mennyiseg(command.getMennyiseg())
                .nemLeadottMennyiseg(command.getMennyiseg())
                .build();
        asztal.getRendelesek().add(rendeles);
        szamlaService.createSzamlaForAsztal(asztal.getId());
        return rendelesRepository.save(rendeles);
    }
    public void komment(Integer asztalId,String termekNev, String komment){
        Rendeles rendeles= asztalService.getById(asztalId).getRendelesek().stream()
                .filter(keresettRendeles->keresettRendeles.getTermek().getNev().equals(termekNev))
                .findFirst()
                .orElseThrow();
        rendeles.setKomment(komment);
    }

    public RendelesLeadasaCommand rendelesLeadas(Asztal asztal) {
        List<Tipus> etelek = List.of(Tipus.DESSZERT, Tipus.ELOETEL, Tipus.HALETEL, Tipus.LEVES, Tipus.MARHAETEL, Tipus.SERTESETEL);
        List<Tipus> italok = List.of(Tipus.KAVE, Tipus.ALKOHOL, Tipus.FORROITAL, Tipus.UDITO, Tipus.KOKTEL, Tipus.ROVIDITAL);
        String filnev = asztal.getNev() + " ";
        String fileTartalom = asztal.getNev() + "\nFelszolgáló: " + felhasznaloService.getById(felhasznaloService.getFelhasznaloId()).getNev() + "\n\n" + "Ételek:\n";
        for (Rendeles rendeles : asztal.getRendelesek()) {
            if (etelek.contains(rendeles.getTermek().getTipus()) && rendeles.getNemLeadottMennyiseg() > 0) {
                filnev += rendeles.getId();
                fileTartalom += rendeles.getTermek().getNev() + " " + rendeles.getNemLeadottMennyiseg() + "\n";
                fileTartalom += "Megjegyzés: " + rendeles.getKomment() + "\n";
                rendeles.setLeadottMennyiseg(rendeles.getNemLeadottMennyiseg());
                rendeles.setNemLeadottMennyiseg(0);
            }
        }
        fileTartalom += "\n\n\n" + "Italok:\n";
        for (Rendeles rendeles : asztal.getRendelesek()) {
            if (italok.contains(rendeles.getTermek().getTipus()) && rendeles.getNemLeadottMennyiseg() > 0) {
                filnev += rendeles.getId();
                fileTartalom += rendeles.getTermek().getNev() + " " + rendeles.getNemLeadottMennyiseg() + "\n";
                fileTartalom += "Megjegyzés: " + rendeles.getKomment() + "\n";
                rendeles.setLeadottMennyiseg(rendeles.getNemLeadottMennyiseg());
                rendeles.setNemLeadottMennyiseg(0);
            }
        }

        filnev += ".txt";
        RendelesLeadasaCommand.builder().fileNev(filnev).fileTartalom(fileTartalom).build();
        return RendelesLeadasaCommand.builder().fileNev(filnev).fileTartalom(fileTartalom).build();
    }

    public void mennyisegNovelese(Integer asztalId, String termekNeve) {
        Asztal asztal = asztalRepository.getById(asztalId);
        Rendeles aktualisRendeles = asztal.getRendelesek().stream()
                .filter(rendeles -> rendeles.getTermek().getNev().equals(termekNeve))
                .findFirst()
                .orElseThrow();
        aktualisRendeles.setMennyiseg(aktualisRendeles.getMennyiseg() + 1);
        aktualisRendeles.setNemLeadottMennyiseg(aktualisRendeles.getNemLeadottMennyiseg() + 1);
        szamlaService.createSzamlaForAsztal(asztalId);
    }

    public void mennyisegNovelese(Integer asztalId, Integer termekId) {
        Asztal asztal = asztalRepository.getById(asztalId);
        Rendeles aktualisRendeles = asztal.getRendelesek().stream()
                .filter(rendeles -> Objects.equals(rendeles.getTermek().getId(), termekId))
                .findFirst()
                .orElseThrow();
        aktualisRendeles.setMennyiseg(aktualisRendeles.getMennyiseg() + 1);
        aktualisRendeles.setNemLeadottMennyiseg(aktualisRendeles.getNemLeadottMennyiseg() + 1);
        szamlaService.createSzamlaForAsztal(asztalId);
    }

    public void mennyisegCsokkentese(Integer asztalId, String termekNeve) {
        Asztal asztal = asztalRepository.getById(asztalId);
        Rendeles aktualisRendeles = asztal.getRendelesek().stream()
                .filter(rendeles -> rendeles.getTermek().getNev().equals(termekNeve))
                .findFirst()
                .orElseThrow();
        aktualisRendeles.setMennyiseg(aktualisRendeles.getMennyiseg() - 1);
        if (aktualisRendeles.getMennyiseg() == 0) {
            szamlaService.szamlaTetelEltavolitasa(asztalId, aktualisRendeles.getId());
            asztal.getRendelesek().remove(aktualisRendeles);
            rendelesRepository.delete(aktualisRendeles);
        }
        if (asztal.getRendelesek().isEmpty()){
            asztal.setFelhasznalo("");
        }
        szamlaService.createSzamlaForAsztal(asztalId);
    }

    public boolean rendelesTartalmazzaATermeket(CreateRendelesCommand command) {
        Asztal asztal = asztalRepository.getById(command.getAsztalId());
        Termek termek = termekService.getById(command.getEtteremTermekId());
        return asztal.getRendelesek().stream()
                .map(rendeles -> rendeles.getTermek().getNev())
                .toList().contains(termek.getNev());
    }

    public List<Rendeles> findAllByAsztal(Asztal asztal) {
        return rendelesRepository.findAllByAsztal(asztal);
    }

    // csak teszthez használjuk
    public Termek findTermekByNev(String nev) {
        return termekService.getByName(nev);
    }
}
