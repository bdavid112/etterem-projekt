package hu.progmatic.kozos.etterem.szamla;

import hu.progmatic.kozos.etterem.leltar.TermekDto;
import hu.progmatic.kozos.etterem.rendeles.Rendeles;
import hu.progmatic.kozos.etterem.rendeles.RendelesDto;
import hu.progmatic.kozos.etterem.rendeles.RendelesRepository;
import hu.progmatic.kozos.felhasznalo.FelhasznaloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import hu.progmatic.kozos.etterem.asztal.Asztal;
import hu.progmatic.kozos.etterem.asztal.AsztalService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class SzamlaService {
  @Autowired
  private SzamlaRepository szamlaRepository;
  @Autowired
  private AsztalService asztalService;
  @Autowired
  private SzamlaTetelRepository szamlaTetelRepository;
  @Autowired
  private RendelesRepository rendelesRepository;
  @Autowired
  private FelhasznaloService felhasznaloService;

  public void createSzamlaForAsztal(Integer asztalId) {
    Asztal asztal = asztalService.getById(asztalId);
    if (asztal.getSzamla() != null) {
      szamlaRepository.deleteById(asztal.getSzamla().getId());
    }
    Szamla szamla = Szamla.builder()
        .asztal(asztal)
        .build();
    setTetelek(szamla);
    asztal.setSzamla(szamla);
    szamlaRepository.save(szamla);
  }

  private void setTetelek(Szamla szamla) {
    szamla.setTetelek(szamla.getAsztal().getRendelesek().stream()
        .map(rendeles -> SzamlaTetel.builder()
            .rendeles(rendeles)
            .fizetettMennyiseg(0)
            .nemFizetettMennyiseg(rendeles.getMennyiseg())
            .szamla(szamla)
            .build())
        .toList());
  }

  public Szamla findSzamlaByAsztalId(Integer asztalId) {
    return szamlaRepository.findByAsztal_Id(asztalId);
  }

  public SzamlaDto buildSzamlaDto(Integer asztalId) {
    return szamlaDtoBuilder(asztalService.getById(asztalId).getSzamla());
  }

  public SzamlaDto szamlaDtoBuilder(Szamla szamla) {
    return SzamlaDto.builder()
        .id(szamla.getId())
        .asztalId(szamla.getAsztal().getId())
        .vegosszeg(getVegosszeg(szamla))
        .formazottVegosszeg(osszegFormazasa(getVegosszeg(szamla)))
        .fizetettVegosszeg(getFizetettVegosszeg(szamla))
        .formazottFizetettVegosszeg(osszegFormazasa(getFizetettVegosszeg(szamla)))
        .formazottFizetendoOsszeg(osszegFormazasa(getVegosszeg(szamla) + getVegosszeg(szamla) / 100 * 15))
        .formazottFizetettFizetendoOsszeg(osszegFormazasa(getFizetettVegosszeg(szamla) + getFizetettVegosszeg(szamla) / 100 * 15))
        .formazottSzervizdij(osszegFormazasa(getVegosszeg(szamla) / 100 * 15))
        .formazottFizetettSzervizdij(osszegFormazasa(getFizetettVegosszeg(szamla) / 100 * 15))
        .tetelek(getTetelDtoList(szamla.getTetelek()))
        .split(szamla.isSplit())
        .asztalSzam(szamla.getAsztal().getAsztalSzam())
        .build();
  }

  private List<SzamlaTetelDto> getTetelDtoList(List<SzamlaTetel> tetelek) {
    return tetelek.stream()
        .map(tetel -> SzamlaTetelDto.builder()
            .id(tetel.getId())
            .rendelesDto(RendelesDto.builder()
                .id(tetel.getRendeles().getId())
                .termekDto(TermekDto.builder()
                    .id(tetel.getRendeles().getTermek().getId())
                    .nev(tetel.getRendeles().getTermek().getNev())
                    .ar(tetel.getRendeles().getTermek().getAr())
                    .build())
                .mennyiseg(tetel.getRendeles().getMennyiseg())
                .build())
            .fizetettMennyiseg(tetel.getFizetettMennyiseg())
            .nemFizetettMennyiseg(tetel.getNemFizetettMennyiseg())
            .formazottFizetettAr(osszegFormazasa(tetel.getRendeles().getTermek().getAr() * tetel.getFizetettMennyiseg()))
            .formazottNemFizetettAr(osszegFormazasa(tetel.getRendeles().getTermek().getAr() * tetel.getNemFizetettMennyiseg()))
            .build())
        .toList();
  }

  private Integer getVegosszeg(Szamla szamla) {
    return szamla.getTetelek().stream()
        .mapToInt(tetel -> tetel.getRendeles().getTermek().getAr() * tetel.getNemFizetettMennyiseg())
        .sum();
  }

  private Integer getFizetettVegosszeg(Szamla szamla) {
    return szamla.getTetelek().stream()
        .mapToInt(tetel -> tetel.getRendeles().getTermek().getAr() * tetel.getFizetettMennyiseg())
        .sum();
  }

  public String osszegFormazasa(int osszeg) {
    String osszegString = "" + osszeg;
    String formazottString = "";
    if (osszegString.length() > 4) {
      int megmaradtSzamjegyek = osszegString.length() % 3;
      int charIndex = 0, szamlalo = 0;
      for (int i = 0; i < megmaradtSzamjegyek; i++) {
        formazottString += osszegString.charAt(i);
        charIndex++;
        if (i == megmaradtSzamjegyek - 1) {
          formazottString += ".";
        }
      }
      for (int i = charIndex; i < osszegString.length(); i++) {
        formazottString += osszegString.charAt(i);
        szamlalo++;
        if (szamlalo == 3 && i < osszegString.length() - 1) {
          formazottString += ".";
          szamlalo = 0;
        }
      }
    }
    return osszegString.length() > 4 ? formazottString : osszegString;
  }

  public void splitSzamla(Integer asztalId) {
    asztalService.getById(asztalId).getSzamla().setSplit(true);
  }

  public void cancelSplit(Integer asztalId) {
    createSzamlaForAsztal(asztalId);
    asztalService.getById(asztalId).getSzamla().setSplit(false);
  }

  public void addToSzamlaSplit(Integer asztalId, Integer termekId) {
    Szamla szamla = findSzamlaByAsztalId(asztalId);
    SzamlaTetel szamlaTetel = szamla.getTetelek().stream()
        .filter(tetel -> tetel.getRendeles().getTermek().getId() == termekId)
        .findFirst()
        .orElseThrow();
    szamlaTetel.setFizetettMennyiseg(szamlaTetel.getFizetettMennyiseg() + 1);
    szamlaTetel.setNemFizetettMennyiseg(szamlaTetel.getNemFizetettMennyiseg() - 1);
  }

  public void removeFromSzamlaSplit(Integer asztalId, Integer termekId) {
    Szamla szamla = findSzamlaByAsztalId(asztalId);
    SzamlaTetel szamlaTetel = szamla.getTetelek().stream()
        .filter(tetel -> tetel.getRendeles().getTermek().getId() == termekId)
        .findFirst()
        .orElseThrow();
    szamlaTetel.setFizetettMennyiseg(szamlaTetel.getFizetettMennyiseg() - 1);
    szamlaTetel.setNemFizetettMennyiseg(szamlaTetel.getNemFizetettMennyiseg() + 1);
  }

  public void szamlaTetelEltavolitasa(Integer asztalId, Integer rendelesId) {
    Asztal asztal = asztalService.getById(asztalId);
    SzamlaTetel szamlaTetel = asztal.getSzamla().getTetelek().stream()
        .filter(tetel -> tetel.getRendeles().getId() == rendelesId)
        .findFirst()
        .orElseThrow();
    asztal.getSzamla().getTetelek().remove(szamlaTetel);
    szamlaTetelRepository.delete(szamlaTetel);
  }

  public void kulonSzamlaFizetese(Integer asztalId) {
    List<SzamlaTetel> eltavolitandoTetelek = new ArrayList<>();
    List<Rendeles> eltavolitandoRendelesek = new ArrayList<>();
    Asztal asztal = asztalService.getById(asztalId);
    for (SzamlaTetel tetel : asztal.getSzamla().getTetelek()) {
      for (Rendeles rendeles : asztal.getRendelesek()) {
        if (tetel.getRendeles().getTermek().equals(rendeles.getTermek())) {
          rendeles.setMennyiseg(rendeles.getMennyiseg() - tetel.getFizetettMennyiseg());
          tetel.setFizetettMennyiseg(0);
        }
        if (rendeles.getMennyiseg() == 0) {
          eltavolitandoTetelek.add(tetel);
          eltavolitandoRendelesek.add(rendeles);
        }
      }
    }
    asztal.getSzamla().getTetelek().removeAll(eltavolitandoTetelek);
    asztal.getRendelesek().removeAll(eltavolitandoRendelesek);
    szamlaTetelRepository.deleteAll(eltavolitandoTetelek);
    rendelesRepository.deleteAll(eltavolitandoRendelesek);
    asztal.getSzamla().setSplit(false);
  }

  public void szamlaFizetese(Integer asztalId) {
    Asztal asztal = asztalService.getById(asztalId);
    asztal.getSzamla().getTetelek().removeAll(asztal.getSzamla().getTetelek());
    szamlaTetelRepository.deleteAllBySzamla(asztal.getSzamla());
    rendelesRepository.deleteAll(asztal.getRendelesek());
    asztal.getRendelesek().removeAll(asztal.getRendelesek());
    asztal.setFelhasznalo("");
  }

  public String szamlaFileNev(Szamla szamla) {
    String asztalNev = szamla.getAsztal().getNev();
    Integer szamlaid = szamla.getId();
    Integer split = 0;
    List<SzamlaTetel> tetelek = szamla.getTetelek();
    int vegosszeg = 0;
    for (SzamlaTetel tetel : tetelek) {
      if (szamla.isSplit() && tetel.getFizetettMennyiseg() > 0) {
        split = tetel.getId();
        vegosszeg = getFizetettVegosszeg(szamla);
      } else if (!szamla.isSplit()) {
        vegosszeg = getVegosszeg(szamla);
      }
    }
    return asztalNev + " " + szamlaid + "-" + split + ".txt";
  }
  public String szamlaFileTartalom(Szamla szamla) {
    List<SzamlaTetel> tetelek = szamla.getTetelek();
    String szamlaString = "";
    szamlaString += szamlaFejlec(
        szamla.getAsztal().getNev(),
        felhasznaloService.getById(felhasznaloService.getFelhasznaloId()).getNev()
    );
    if (szamla.isSplit()) {
      szamlaString += splitSzamlaTorzs(tetelek);
      szamlaString += splitSzamlaLab(buildSzamlaDto(szamla.getAsztal().getId()));
    } else {
      szamlaString += szamlaTorzs(tetelek);
      szamlaString += szamlaLab(buildSzamlaDto(szamla.getAsztal().getId()));
    }
    return szamlaString;
  }

  private String szamlaFejlec(String asztalNev, String felszolgaloNev) {
    String rajz = "";
    felszolgaloNev = "Felszolgáló:" + felszolgaloNev;
    int felszolgaloNevKezdoIndex = (35 - felszolgaloNev.length()) / 2;
    for (int sor = 0; sor < 4; sor++) {
      for (int oszlop = 0; oszlop < 35; oszlop++) {
        if ((sor == 0 && oszlop == 0) || (sor == 0 && oszlop == 34) ||
            (sor == 3 && oszlop == 0) || (sor == 3 && oszlop == 34)) {
          rajz += "+";
        } else if ((sor == 1 && oszlop == 0) || (sor == 1 && oszlop == 34) ||
                   (sor == 2 && oszlop == 0) || (sor == 2 && oszlop == 34)) {
          rajz += "|";
        } else if (sor == 0 || sor == 3) {
          rajz += "-";
        } else if (sor == 1 && oszlop == 13) {
          rajz += asztalNev;
          oszlop += asztalNev.length() - 1;
        } else if (sor == 2 && oszlop == felszolgaloNevKezdoIndex) {
          rajz += felszolgaloNev;
          oszlop += felszolgaloNev.length() - 1;
        }
        else {
          rajz += " ";
        }
      }
      rajz += "\n";
    }
    return rajz;
  }

  private String splitSzamlaTorzs(List<SzamlaTetel> tetelek) {
    List<SzamlaTetel> fizetettTetelek = tetelek.stream()
        .filter(tetel -> tetel.getFizetettMennyiseg() > 0)
        .toList();
    String rajz = "";
    int tetelIndex = 0;
    int sorokSzama = fizetettTetelek.size() + 2;
    for (int sor = 0; sor < sorokSzama; sor++) {
      for (int oszlop = 0; oszlop < 35; oszlop++) {
        if ((sor == sorokSzama - 1 && oszlop == 0) || (sor == sorokSzama - 1 && oszlop == 34)) {
          rajz += "+";
        } else if ((sor != sorokSzama - 1 && oszlop == 0) || (sor != sorokSzama - 1 && oszlop == 34)) {
          rajz += "|";
        } else if (sor == sorokSzama - 1) {
          rajz += "-";
        } else if (sor == 0 && oszlop == 1) {
          rajz += "    Tétel         Menny.    Ár   ";
          oszlop += 32;
        } else if (sor != sorokSzama - 1 && oszlop == 2) {
          rajz += fizetettTetelek.get(tetelIndex).getRendeles().getTermek().getNev();
          oszlop += tetelek.get(tetelIndex).getRendeles().getTermek().getNev().length() - 1;
        } else if (sor != sorokSzama - 1 && oszlop == 21) {
          rajz += tetelek.get(tetelIndex).getFizetettMennyiseg();
          oszlop += tetelek.get(tetelIndex).getFizetettMennyiseg().toString().length() - 1;
        } else if (sor != sorokSzama - 1 && oszlop == 28) {
          rajz += tetelek.get(tetelIndex).getRendeles().getTermek().getAr();
          oszlop += tetelek.get(tetelIndex).getRendeles().getTermek().getAr().toString().length() - 1;
          tetelIndex++;
        } else {
          rajz += " ";
        }
      }
      rajz += "\n";
    }
    return rajz;
  }

  private String szamlaTorzs(List<SzamlaTetel> tetelek) {
    List<SzamlaTetel> nemFizetettTetelek = tetelek.stream()
        .filter(tetel -> tetel.getFizetettMennyiseg() == 0 && tetel.getRendeles().getMennyiseg() > 0)
        .toList();
    String rajz = "";
    int tetelIndex = 0;
    int sorokSzama = nemFizetettTetelek.size() + 2;
    for (int sor = 0; sor < sorokSzama; sor++) {
      for (int oszlop = 0; oszlop < 35; oszlop++) {
        if ((sor == sorokSzama - 1 && oszlop == 0) || (sor == sorokSzama - 1 && oszlop == 34)) {
          rajz += "+";
        } else if ((sor != sorokSzama - 1 && oszlop == 0) || (sor != sorokSzama - 1 && oszlop == 34)) {
          rajz += "|";
        } else if (sor == sorokSzama - 1) {
          rajz += "-";
        } else if (sor == 0 && oszlop == 1) {
          rajz += "    Tétel         Menny.    Ár   ";
          oszlop += 32;
        } else if (sor != sorokSzama - 1 && oszlop == 2) {
          rajz += nemFizetettTetelek.get(tetelIndex).getRendeles().getTermek().getNev();
          oszlop += tetelek.get(tetelIndex).getRendeles().getTermek().getNev().length() - 1;
        } else if (sor != sorokSzama - 1 && oszlop == 21) {
          rajz += tetelek.get(tetelIndex).getNemFizetettMennyiseg();
          oszlop += tetelek.get(tetelIndex).getNemFizetettMennyiseg().toString().length() - 1;
        } else if (sor != sorokSzama - 1 && oszlop == 28) {
          rajz += tetelek.get(tetelIndex).getRendeles().getTermek().getAr();
          oszlop += tetelek.get(tetelIndex).getRendeles().getTermek().getAr().toString().length() - 1;
          tetelIndex++;
        } else {
          rajz += " ";
        }
      }
      rajz += "\n";
    }
    return rajz;
  }

  private String splitSzamlaLab(SzamlaDto dto) {
    String rajz = "";
    for (int sor = 0; sor < 4; sor++) {
      for (int oszlop = 0; oszlop < 35; oszlop++) {
        if ((sor == 3 && oszlop == 0) || (sor == 3 && oszlop == 34)) {
          rajz += "+";
        } else if ((sor != 3 && oszlop == 34) || (sor != 3 && oszlop == 0)) {
          rajz += "|";
        } else if (sor == 3) {
          rajz += "-";
        } else if (sor == 0 && oszlop == 2) {
          rajz += "Végösszeg: " + dto.getFormazottFizetettVegosszeg() + " Ft";
          oszlop += 14 + dto.getFormazottFizetettVegosszeg().length() - 1;
        } else if (sor == 1 && oszlop == 2) {
          rajz += "Szervízdíj: " + dto.getFormazottFizetettSzervizdij() + " Ft";
          oszlop += 15 + dto.getFormazottFizetettSzervizdij().length() - 1;
        } else if (sor == 2 && oszlop == 2) {
          rajz += "Fizetendő összeg: " + dto.getFormazottFizetettFizetendoOsszeg() + " Ft";
          oszlop += 21 + dto.getFormazottFizetettFizetendoOsszeg().length() - 1;
        } else {
          rajz += " ";
        }
      }
      rajz += "\n";
    }
    return rajz;
  }

  private String szamlaLab(SzamlaDto dto) {
    String rajz = "";
    for (int sor = 0; sor < 4; sor++) {
      for (int oszlop = 0; oszlop < 35; oszlop++) {
        if ((sor == 3 && oszlop == 0) || (sor == 3 && oszlop == 34)) {
          rajz += "+";
        } else if ((sor != 3 && oszlop == 34) || (sor != 3 && oszlop == 0)) {
          rajz += "|";
        } else if (sor == 3) {
          rajz += "-";
        } else if (sor == 0 && oszlop == 2) {
          rajz += "Végösszeg: " + dto.getFormazottVegosszeg() + " Ft";
          oszlop += 14 + dto.getFormazottVegosszeg().length() - 1;
        } else if (sor == 1 && oszlop == 2) {
          rajz += "Szervízdíj: " + dto.getFormazottSzervizdij() + " Ft";
          oszlop += 15 + dto.getFormazottSzervizdij().length() - 1;
        } else if (sor == 2 && oszlop == 2) {
          rajz += "Fizetendő összeg: " + dto.getFormazottFizetendoOsszeg() + " Ft";
          oszlop += 21 + dto.getFormazottFizetendoOsszeg().length() - 1;
        } else {
          rajz += " ";
        }
      }
      rajz += "\n";
    }
    return rajz;
  }
}
