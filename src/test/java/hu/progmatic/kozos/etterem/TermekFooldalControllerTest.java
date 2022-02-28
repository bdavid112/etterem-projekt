package hu.progmatic.kozos.etterem;

import hu.progmatic.kozos.etterem.leltar.TermekService;
import hu.progmatic.kozos.etterem.leltar.Tipus;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithUserDetails("admin")
@SpringBootTest
@AutoConfigureMockMvc
class TermekFooldalControllerTest {

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private TermekService termekService;

  @AfterEach
  void tearDown() throws Exception {
    mockMvc.perform(
        post("/etterem/asztal/1/szamlaFizetese")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
    );
  }

  @Test
  @DisplayName("Termék főoldal megjlenik")
  void termekIndex() throws Exception {
    mockMvc.perform(
            MockMvcRequestBuilders.get("/etterem/asztal/3/KEZDOLAP")
        ).andExpect(status().isOk())
        .andExpect(content().string(containsString("ÉTEL")));
  }

  @Test
  @DisplayName("Levesből típus főoldalra")
  void levesbolTipusra() throws Exception {
    mockMvc.perform(
            MockMvcRequestBuilders.get("/etterem/asztal/3/ETEL?")
        ).andExpect(status().isOk())
        .andExpect(content().string(containsString("LEVES")));
  }

  @Test
  @DisplayName("Típus oldal megjelenik")
  void tipusMegjelenik() throws Exception {
    mockMvc.perform(
                    MockMvcRequestBuilders.get("/etterem/asztal/5/ETEL/tipus/LEVES?")
            ).andExpect(status().isOk())
            .andExpect(content().string(containsString("Gulyás leves")));
  }

  @Test
  @DisplayName("Ha a rendelés tartalmazza a terméket új rendelés létrehozásakor a mennyiség nő")
  void createOrderTest() throws Exception {
      int tesztId = termekService.findAllByTipus(Tipus.LEVES).stream().findFirst().get().getId();
    mockMvc.perform(
            post("/etterem/asztal/1/ETEL/tipus/LEVES")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content("etteremTermekId="+tesztId+"&mennyiseg=1")
        ).andDo(print())
        .andExpect(MockMvcResultMatchers.status().is3xxRedirection());
    mockMvc.perform(
            MockMvcRequestBuilders.get("/etterem/asztal/1/ETEL/tipus/LEVES")
        ).andExpect(status().isOk())
        .andExpect(content().string(containsString("Mennyiség")))
        .andExpect(content().string(containsString("1")));
    mockMvc.perform(
            post("/etterem/asztal/1/ETEL/tipus/LEVES")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content("etteremTermekId="+tesztId+"&mennyiseg=1")
        ).andDo(print())
        .andExpect(MockMvcResultMatchers.status().is3xxRedirection());
    mockMvc.perform(
            MockMvcRequestBuilders.get("/etterem/asztal/1/ETEL/tipus/LEVES")
        ).andExpect(status().isOk())
        .andExpect(content().string(containsString("Mennyiség")))
        .andExpect(content().string(containsString("2")));
  }

  @Test
  @DisplayName("Mennyiség növelésekor a mennyiség változik")
  @Disabled
  void mennyisegNoveleseTest() throws Exception {
    mockMvc.perform(
            post("/etterem/asztal/1/ETEL/tipus/LEVES")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content("etteremTermekId=10&mennyiseg=1")
        ).andDo(print())
        .andExpect(MockMvcResultMatchers.status().is3xxRedirection());
    mockMvc.perform(
            MockMvcRequestBuilders.get("/etterem/asztal/1/ETEL/tipus/LEVES")
        ).andExpect(status().isOk())
        .andExpect(content().string(containsString("Mennyiség")))
        .andExpect(content().string(containsString("1")));
    mockMvc.perform(
            post("/etterem/asztal/1/mennyisegNoveleseTipusOldalon/ETEL/LEVES/Paradicsom%20leves")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        ).andDo(print())
        .andExpect(MockMvcResultMatchers.status().is3xxRedirection());
    mockMvc.perform(
            MockMvcRequestBuilders.get("/etterem/asztal/1/ETEL/tipus/LEVES")
        ).andExpect(status().isOk())
        .andExpect(content().string(containsString("Mennyiség")))
        .andExpect(content().string(containsString("2")));
  }
}