package hu.progmatic.kozos.etterem;

import hu.progmatic.kozos.etterem.asztal.Asztal;
import hu.progmatic.kozos.etterem.asztal.AsztalDto;
import hu.progmatic.kozos.etterem.asztal.AsztalService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AsztalServiceTest {
    private Asztal tesztAsztal;

    @Autowired
    private AsztalService asztalService;

    @BeforeEach
    void setUp() {
        asztalService.afterPropertiesSet();
        tesztAsztal = asztalService.getById(asztalService.getIdByNev("1. ASZTAL"));
    }

    @AfterEach
    void tearDown() {

    }

    @Test
    @DisplayName("Asztal kikérése id alapján")
    void getByIdTest() {
        Asztal asztal = asztalService.getById(tesztAsztal.getId());
        AsztalDto dto = asztalService.getAsztalDtoById(asztal.getId());
        assertNotNull(dto.getId());
        assertEquals("1. ASZTAL", dto.getNev());
    }
}