package hu.progmatic.kozos.etterem.szamla;

import hu.progmatic.kozos.etterem.asztal.AsztalService;
import hu.progmatic.kozos.etterem.rendeles.CreateRendelesCommand;
import hu.progmatic.kozos.etterem.rendeles.RendelesService;
import org.junit.jupiter.api.BeforeEach;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@WithMockUser
@AutoConfigureMockMvc
class SzamlaControllerTest {

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private SzamlaService szamlaService;
  @Autowired
  private AsztalService asztalService;

  @BeforeEach
  void setUp() {
    szamlaService.createSzamlaForAsztal(asztalService.getIdByNev("1. ASZTAL"));
  }

  @Test
  @DisplayName("Számla oldal megjelenik")
  void szamlaTest() throws Exception {
    mockMvc.perform(
            MockMvcRequestBuilders.get("/etterem/asztal/1/szamla?")
        ).andExpect(status().isOk())
        .andExpect(content().string(containsString("Név")))
        .andExpect(content().string(containsString("Mennyiség")))
        .andExpect(content().string(containsString("Ár")));
  }

  @Test
  @DisplayName("Számla fizetése után az asztal oldal megjelenik")
  void szamlaFizeteseTest() throws Exception {
    mockMvc.perform(
            post("/etterem/asztal/1/szamlaFizetese")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        ).andDo(print())
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(content().string(containsString("1")))
        .andExpect(content().string(containsString("KIJELENTKEZÉS")));
  }

  @Test
  @DisplayName("Külön számla megjelenik a számla oldalon")
  void splitSzamlaTest() throws Exception {
    mockMvc.perform(
            post("/etterem/asztal/1/splitSzamla")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        ).andDo(print())
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(content().string(containsString("CANCEL SPLIT")))
        .andExpect(content().string(containsString("KÜLÖN SZÁMLA FIZETÉSE")));
  }

  @Test
  @DisplayName("Szétválasztás visszavonásakor a külön számla eltűnik")
  void cancelSplitTest() throws Exception {
    mockMvc.perform(
            post("/etterem/asztal/1/cancelSplit")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        ).andDo(print())
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(content().string(containsString("SPLIT")))
        .andExpect(content().string(containsString("VISSZA")));
  }

  @Test
  @DisplayName("Külön számla fizetésekor eltűnik a külön számla")
  void splitSzamlaFizeteseTest() throws Exception {
    mockMvc.perform(
            post("/etterem/asztal/1/splitSzamlaFizetese")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        ).andDo(print())
        .andExpect(MockMvcResultMatchers.status().is3xxRedirection());
    mockMvc.perform(
            MockMvcRequestBuilders.get("/etterem/asztal/1/szamla")
        )
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("SPLIT")))
        .andExpect(content().string(containsString("VISSZA")));
  }
}