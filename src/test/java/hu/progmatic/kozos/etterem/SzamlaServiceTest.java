package hu.progmatic.kozos.etterem;

import hu.progmatic.kozos.etterem.asztal.AsztalDto;
import hu.progmatic.kozos.etterem.asztal.AsztalService;
import hu.progmatic.kozos.etterem.leltar.Termek;
import hu.progmatic.kozos.etterem.rendeles.CreateRendelesCommand;
import hu.progmatic.kozos.etterem.rendeles.Rendeles;
import hu.progmatic.kozos.etterem.rendeles.RendelesService;
import hu.progmatic.kozos.etterem.szamla.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;

import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@WithUserDetails("admin")
@SpringBootTest
class SzamlaServiceTest {
  private SzamlaDto tesztSzamlaDto;
  private Integer tesztAsztalId;
  private SzamlaTetelDto tesztSzamlaTetelDto;

  @Autowired
  private SzamlaService szamlaService;
  @Autowired
  private AsztalService asztalService;
  @Autowired
  private RendelesService rendelesService;

  @BeforeEach
  void setUp() {
    tesztAsztalId = asztalService.getIdByNev("2. ASZTAL");
    rendelesService.create(CreateRendelesCommand.builder()
        .asztalId(tesztAsztalId)
        .etteremTermekId(rendelesService.findTermekByNev("Paradicsom leves").getId())
        .mennyiseg(1)
        .build());
  }

  @AfterEach
  void tearDown() {
    try {
      rendelesService.mennyisegCsokkentese(tesztAsztalId, "Paradicsom leves");
    } catch (NoSuchElementException exception) {
      boolean nemLetezikARendeles = true;
    }
  }

  @Test
  @DisplayName("Számla létrehozása asztalhoz")
  void szamlaLetrehozasaAsztalhozTest() {
    Integer letrehozasTestId = asztalService.getIdByNev("3. ASZTAL");
    AsztalDto letrehozasTestDto = asztalService.getAsztalDtoById(letrehozasTestId);
    assertNull(letrehozasTestDto.getSzamlaDto());
    szamlaService.createSzamlaForAsztal(letrehozasTestId);
    letrehozasTestDto = asztalService.getAsztalDtoById(letrehozasTestId);
    assertNotNull(letrehozasTestDto.getSzamlaDto());
  }

  @Test
  @DisplayName("Összeg formázása")
  void osszegFormazasaTest() {
    assertEquals("1000", szamlaService.osszegFormazasa(1000));
    assertEquals("10.000", szamlaService.osszegFormazasa(10000));
    assertEquals("100.000", szamlaService.osszegFormazasa(100000));
    assertEquals("1.000.000", szamlaService.osszegFormazasa(1000000));
    assertEquals("10.000.000", szamlaService.osszegFormazasa(10000000));
    assertEquals("100.000.000", szamlaService.osszegFormazasa(100000000));
  }

  @Nested
  @DisplayName("Tesztek létező számlával")
  class LetezoSzamlaTest {

    @BeforeEach
    void setUp() {
      szamlaService.createSzamlaForAsztal(tesztAsztalId);
      tesztSzamlaDto = szamlaService.buildSzamlaDto(tesztAsztalId);
    }

    @Test
    @DisplayName("Számla ketté választása")
    void splitSzamlaTest() {
      SzamlaDto dto = szamlaService.buildSzamlaDto(tesztAsztalId);
      assertFalse(dto.isSplit());
      szamlaService.splitSzamla(tesztAsztalId);
      dto = szamlaService.buildSzamlaDto(tesztAsztalId);
      assertTrue(dto.isSplit());
    }

    @Test
    @DisplayName("Kettéválasztás visszavonása")
    void cancelSplitTest() {
      szamlaService.splitSzamla(tesztAsztalId);
      SzamlaDto dto = szamlaService.buildSzamlaDto(tesztAsztalId);
      assertTrue(dto.isSplit());
      szamlaService.cancelSplit(tesztAsztalId);
      dto = szamlaService.buildSzamlaDto(tesztAsztalId);
      assertFalse(dto.isSplit());
    }

    @Test
    @DisplayName("Számla megkeresése asztalId alapján")
    void findSzamlaByAsztalIdTest() {
      Szamla talaltSzamla = szamlaService.findSzamlaByAsztalId(tesztAsztalId);
      assertEquals(tesztSzamlaDto.getId(), talaltSzamla.getId());
    }

    @Test
    @DisplayName("Számla fizetése")
    void szamlaFizeteseTest() {
      assertTrue(tesztSzamlaDto.getVegosszeg() != 0);
      szamlaService.szamlaFizetese(tesztAsztalId);
      tesztSzamlaDto = szamlaService.buildSzamlaDto(tesztAsztalId);
      assertEquals(0, tesztSzamlaDto.getVegosszeg());
      assertEquals(0, tesztSzamlaDto.getFizetettVegosszeg());
      assertEquals(0, tesztSzamlaDto.getTetelek().size());
    }

    @Nested
    @DisplayName("Tesztek kettéválasztott számlával")
    class SlitSzamlaTest {

      @BeforeEach
      void setUp() {
        szamlaService.splitSzamla(tesztAsztalId);
        tesztSzamlaTetelDto = getSzamlaTetelDto();
      }

      @AfterEach
      void tearDown() {
        tesztSzamlaTetelDto = SzamlaTetelDto.builder().build();
      }

      @Test
      @DisplayName("Hozzáadás ideiglenes számlához")
      void addToSzamlaSplitTest() {
        tesztSzamlaTetelDto = getSzamlaTetelDto();
        assertEquals(0, tesztSzamlaTetelDto.getFizetettMennyiseg());
        assertEquals(1, tesztSzamlaTetelDto.getNemFizetettMennyiseg());
        addToSplitSzamlaAndBuildDtos();
        assertEquals(1, tesztSzamlaTetelDto.getFizetettMennyiseg());
        assertEquals(0, tesztSzamlaTetelDto.getNemFizetettMennyiseg());
      }

      @Test
      @DisplayName("Elvonás ideiglenes számlából")
      void removeFromSzamlaSplitTest() {
        addToSplitSzamlaAndBuildDtos();
        assertEquals(1, tesztSzamlaTetelDto.getFizetettMennyiseg());
        assertEquals(0, tesztSzamlaTetelDto.getNemFizetettMennyiseg());
        szamlaService.removeFromSzamlaSplit(tesztAsztalId, tesztSzamlaTetelDto.getRendelesDto().getTermekDto().getId());
        tesztSzamlaDto = szamlaService.buildSzamlaDto(tesztAsztalId);
        tesztSzamlaTetelDto = getSzamlaTetelDto();
        assertEquals(0, tesztSzamlaTetelDto.getFizetettMennyiseg());
        assertEquals(1, tesztSzamlaTetelDto.getNemFizetettMennyiseg());
      }

      @Test
      @DisplayName("Számlatétel eltávolítása")
      void szamlaTetelEltavolitasaTest() {
        assertNotNull(tesztSzamlaTetelDto.getId());
        szamlaService.szamlaTetelEltavolitasa(tesztAsztalId, tesztSzamlaTetelDto.getRendelesDto().getId());
        tesztSzamlaDto = szamlaService.buildSzamlaDto(tesztAsztalId);
        String message = "";
        try {
          tesztSzamlaTetelDto = getSzamlaTetelDto();
        } catch (NoSuchElementException exception) {
          message = exception.getMessage();
        }
        assertEquals("No value present", message);
      }

      @Test
      @DisplayName("Külön számla fizetése")
      void kulonSzamlaFizeteseTest() {
        addToSplitSzamlaAndBuildDtos();
        assertTrue(tesztSzamlaDto.isSplit());
        assertEquals(1, tesztSzamlaTetelDto.getFizetettMennyiseg());
        szamlaService.kulonSzamlaFizetese(tesztAsztalId);
        tesztSzamlaDto = szamlaService.buildSzamlaDto(tesztAsztalId);
        assertFalse(tesztSzamlaDto.isSplit());
        String message = "";
        try {
          tesztSzamlaTetelDto = getSzamlaTetelDto();
        } catch (NoSuchElementException exception) {
          message = exception.getMessage();
        }
        assertEquals("No value present", message);
      }

      private void addToSplitSzamlaAndBuildDtos() {
        szamlaService.addToSzamlaSplit(tesztAsztalId, tesztSzamlaTetelDto.getRendelesDto().getTermekDto().getId());
        tesztSzamlaDto = szamlaService.buildSzamlaDto(tesztAsztalId);
        tesztSzamlaTetelDto = getSzamlaTetelDto();
      }

      private SzamlaTetelDto getSzamlaTetelDto() {
        return tesztSzamlaDto.getTetelek().stream()
            .filter(szamlaTetelDto -> szamlaTetelDto.getRendelesDto().getTermekDto().getNev().equals("Paradicsom leves"))
            .findFirst()
            .orElseThrow();
      }
    }
  }
}