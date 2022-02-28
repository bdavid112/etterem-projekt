package hu.progmatic.kozos.etterem;

import hu.progmatic.kozos.etterem.leltar.Termek;
import hu.progmatic.kozos.etterem.leltar.TermekService;
import hu.progmatic.kozos.etterem.leltar.Tipus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser
@SpringBootTest
@AutoConfigureMockMvc
public class LeltarControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TermekService termekService;

    @Test
    @DisplayName("Leltár oldal megjelenik")
    void etelek() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/etterem/leltar?")
                ).andExpect(status().isOk())
                .andExpect(content().string(containsString("Leltár")));
    }

    @Test
    @DisplayName("Termék szerkesztése")
    void szerkesztes() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/etterem/leltar/22?")
                ).andExpect(status().isOk())
                .andExpect(content().string(containsString("Tatárbeefsteak")));
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/etterem/leltar/22?")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .content("nev=Tat%C3%A1rbeefsteak&ar=2400&tipus=ELOETEL")
                )
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Tatárbeefsteak")))
                .andExpect(content().string(containsString("2400")));
    }

    @Test
    @DisplayName("Termék törlése")
    void törlés() throws Exception {
        Termek teszTermek = termekService.create(Termek.builder()
                .ar(1)
                .nev("Teszt termek")
                .tipus(Tipus.LEVES)
                .build());
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/etterem/leltar/delete/"+teszTermek.getId())
                ).andExpect(status().isOk())
                .andExpect(content().string(containsString("Húsleves gazdagon")));
        assertNull(termekService.getByName("Teszt termek"));
    }

    @Test
    @DisplayName("Termék létrehozása")
    void letrehozas() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/etterem/leltar?")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .content("nev=Karfiol+leves&ar=2200&tipus=LEVES")
                ).andExpect(status().isOk())
                .andExpect(content().string(containsString("Karfiol leves")));
    }

    @Test
    @DisplayName("Termék mentése")
    void mentes() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/etterem/leltar/66")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .content("nev=Karfiol+leves&ar=2200&tipus=LEVES")
                ).andExpect(status().isOk())
                .andExpect(content().string(containsString("Karfiol leves")));
    }
}
