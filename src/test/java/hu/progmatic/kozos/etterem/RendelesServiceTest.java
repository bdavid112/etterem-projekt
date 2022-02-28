package hu.progmatic.kozos.etterem;

import hu.progmatic.kozos.etterem.asztal.Asztal;
import hu.progmatic.kozos.etterem.asztal.AsztalService;
import hu.progmatic.kozos.etterem.asztal.TableViewDto;
import hu.progmatic.kozos.etterem.leltar.Termek;
import hu.progmatic.kozos.etterem.leltar.TermekService;
import hu.progmatic.kozos.etterem.leltar.Tipus;
import hu.progmatic.kozos.etterem.rendeles.CreateRendelesCommand;
import hu.progmatic.kozos.etterem.rendeles.RendelesService;
import hu.progmatic.kozos.etterem.rendeles.Rendeles;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;

import javax.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@WithUserDetails("admin")
class RendelesServiceTest {
    @Autowired
    RendelesService rendelesService;
    @Autowired
    AsztalService asztalService;
    @Autowired
    TermekService termekService;

    private Asztal tesztAsztal;
    private Termek tesztTermek;
    private CreateRendelesCommand command;

    @BeforeEach
    void setUp() {
        tesztAsztal = asztalService.getById(asztalService.getIdByNev("1. ASZTAL"));
        tesztTermek = termekService.getByName("Paradicsom leves");
        command = CreateRendelesCommand.builder()
                .asztalId(tesztAsztal.getId())
                .etteremTermekId(tesztTermek.getId())
                .mennyiseg(1)
                .build();
    }

    @AfterEach
    void tearDown() {
        tesztAsztal.setRendelesek(new ArrayList<>());
    }

    @Test
    @DisplayName("Rendelés hozzáadása asztalhoz")
    void addOrderToAsztalTest() {
        Rendeles tesztOrder = rendelesService.create(command);
        TableViewDto dto = asztalService.getTableViewDto(tesztAsztal.getId(), Tipus.LEVES);
        assertEquals("1. ASZTAL", dto.getNev());
        assertEquals(tesztAsztal.getId(), dto.getId());
        assertThat(dto.getRendelesDtoList()).hasSize(1);
        TableViewDto.RendelesDto order = dto.getRendelesDtoList().get(0);
        assertEquals("Paradicsom leves", order.getEtteremTermekNev());
        assertEquals(1, order.getMennyiseg());
        assertEquals(tesztOrder.getId(), order.getRendelesId());
    }

    @Test
    @DisplayName("Mennyiség hozzáadása termék névvel")
    void mennyisegHozzaadasaTermekNevvelTest() {
        rendelesService.create(command);
        TableViewDto dto = asztalService.getTableViewDto(tesztAsztal.getId(), Tipus.LEVES);
        TableViewDto.RendelesDto rendelesDto = dto.getRendelesDtoList().stream()
                .findFirst()
                .orElseThrow();
        assertEquals("Paradicsom leves", rendelesDto.getEtteremTermekNev());
        assertEquals(1, rendelesDto.getMennyiseg());
        rendelesService.mennyisegNovelese(tesztAsztal.getId(), tesztTermek.getNev());
        dto = asztalService.getTableViewDto(tesztAsztal.getId(), Tipus.LEVES);
        rendelesDto = dto.getRendelesDtoList().stream()
                .findFirst()
                .orElseThrow();
        assertEquals("Paradicsom leves", rendelesDto.getEtteremTermekNev());
        assertEquals(2, rendelesDto.getMennyiseg());
    }

    @Test
    @DisplayName("Mennyiség hozzáadása termék Id-val")
    void mennyisegHozzaadasaTermekIdvalTest() {
        rendelesService.create(command);
        TableViewDto dto = asztalService.getTableViewDto(tesztAsztal.getId(), Tipus.LEVES);
        TableViewDto.RendelesDto rendelesDto = dto.getRendelesDtoList().stream()
                .findFirst()
                .orElseThrow();
        assertEquals("Paradicsom leves", rendelesDto.getEtteremTermekNev());
        assertEquals(1, rendelesDto.getMennyiseg());
        rendelesService.mennyisegNovelese(tesztAsztal.getId(), tesztTermek.getId());
        dto = asztalService.getTableViewDto(tesztAsztal.getId(), Tipus.LEVES);
        rendelesDto = dto.getRendelesDtoList().stream()
                .findFirst()
                .orElseThrow();
        assertEquals("Paradicsom leves", rendelesDto.getEtteremTermekNev());
        assertEquals(2, rendelesDto.getMennyiseg());
    }

    @Test
    @DisplayName("Mennyiség csökkentése termék névvel")
    void mennyisegcsokkenteseTermekNevvelTest() {
        command.setMennyiseg(2);
        rendelesService.create(command);
        TableViewDto dto = asztalService.getTableViewDto(tesztAsztal.getId(), Tipus.LEVES);
        TableViewDto.RendelesDto rendelesDto = dto.getRendelesDtoList().stream()
                .findFirst()
                .orElseThrow();
        assertEquals("Paradicsom leves", rendelesDto.getEtteremTermekNev());
        assertEquals(2, rendelesDto.getMennyiseg());
        rendelesService.mennyisegCsokkentese(tesztAsztal.getId(), tesztTermek.getNev());
        dto = asztalService.getTableViewDto(tesztAsztal.getId(), Tipus.LEVES);
        rendelesDto = dto.getRendelesDtoList().stream()
                .findFirst()
                .orElseThrow();
        assertEquals("Paradicsom leves", rendelesDto.getEtteremTermekNev());
        assertEquals(1, rendelesDto.getMennyiseg());
    }

    @Test
    @DisplayName("Rendelés keresése asztal alapján")
    void findAllByAsztalTest() {
        rendelesService.create(command);
        List<Rendeles> rendelesek = rendelesService.findAllByAsztal(tesztAsztal);
        assertThat(rendelesek)
                .extracting(Rendeles::getAsztal)
                .extracting(Asztal::getNev)
                .containsExactlyInAnyOrder("1. ASZTAL");
    }

    @Test
    @DisplayName("Rendelés tartalmazza a terméket")
    void rendelesTartalmazzaTest() {
        assertFalse(rendelesService.rendelesTartalmazzaATermeket(command));
        rendelesService.create(command);
        assertTrue(rendelesService.rendelesTartalmazzaATermeket(command));
    }
}