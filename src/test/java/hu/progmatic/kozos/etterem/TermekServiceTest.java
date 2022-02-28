package hu.progmatic.kozos.etterem;

import hu.progmatic.kozos.etterem.leltar.Termek;
import hu.progmatic.kozos.etterem.leltar.TermekDto;
import hu.progmatic.kozos.etterem.leltar.TermekService;
import hu.progmatic.kozos.etterem.leltar.Tipus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TermekServiceTest {
  @Autowired
  TermekService service;

  @Test
  @DisplayName("Létrehozás és kiolvasás teszt")
  void createAndRead() {
    Termek newEntity = Termek.builder()
        .nev("Kóla")
        .ar(400)
        .tipus(Tipus.UDITO)
        .build();
    Termek savedEntity = service.create(newEntity);
    TermekDto dto = service.getDto(savedEntity.getId());

    assertNotNull(dto.getId());
    assertEquals("Kóla", dto.getNev());
    assertEquals(400, dto.getAr());
  }

  @Test
  @DisplayName("Termék mentése")
  void saveTest() {
    Termek newEntity = Termek.builder()
            .nev("Kóla")
            .ar(400)
            .tipus(Tipus.UDITO)
            .build();
    Termek savedEntity = service.create(newEntity);
    service.save(savedEntity);
    TermekDto dto = service.getDto(savedEntity.getId());
    assertNotNull(dto.getId());
    assertEquals("Kóla", dto.getNev());
    assertEquals(400, dto.getAr());
  }
  @Test
  @DisplayName("Törlés és létezés ellenőrzése")
  void existAndDelete() {
    Termek newEntity = Termek.builder()
        .nev("Steak")
        .ar(2700)
        .tipus(Tipus.MARHAETEL)
        .build();
    Termek savedEntity = service.create(newEntity);
    TermekDto dto = service.getDto(savedEntity.getId());

    assertTrue(service.isExists(dto.getId()));
    service.deleteById(dto.getId());
    assertFalse(service.isExists(dto.getId()));
  }

  @Nested
  @DisplayName("Tesztek létező entitással")
  class LetezoEntitasTest {
    Termek entity;

    @BeforeEach
    void setUp() {
      Termek newEntity = Termek.builder()
          .nev("RedBull")
          .ar(450)
          .tipus(Tipus.UDITO)
          .build();
      entity = service.create(newEntity);
    }

    @Test
    @DisplayName("Adatok módosítása")
    void update() {
      service.update(entity.getId(), "Hell", 200, Tipus.UDITO);
      TermekDto dto = service.getDto(entity.getId());
      assertEquals("Hell", dto.getNev());
      assertEquals(200, dto.getAr());
    }
  }
}