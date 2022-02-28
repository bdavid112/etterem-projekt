package hu.progmatic.kozos.etterem.szamla;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Controller
public class SzamlaController {
  @Autowired
  private SzamlaService szamlaService;

  @GetMapping("/etterem/asztal/{asztalId}/szamla")
  public String etterem(
      @PathVariable Integer asztalId,
      Model model
  ) {
    SzamlaDto dto = szamlaService.szamlaDtoBuilder(szamlaService.findSzamlaByAsztalId(asztalId));
    model.addAttribute("szamlaDto", dto);
    return "etterem/szamla";
  }

  @PostMapping("/etterem/asztal/{asztalId}/szamlaFizetese")
  public String szamlaFizetese(
      @PathVariable Integer asztalId
  ) {
    szamlaService.szamlaFizetese(asztalId);
    return "etterem/asztal";
  }
  @GetMapping("/etterem/asztal/{asztalId}/szamlaMentese")
  public void szamlaMentese(@PathVariable Integer asztalId, HttpServletResponse response) throws IOException {
    String fileName = szamlaService.szamlaFileNev(szamlaService.findSzamlaByAsztalId(asztalId));
    String fileContent = szamlaService.szamlaFileTartalom(szamlaService.findSzamlaByAsztalId(asztalId));
    response.setContentType("text/plain");
    response.getOutputStream().write(fileContent.getBytes(StandardCharsets.UTF_8));
    response.addHeader("Content-Disposition", "attachment; filename="+fileName);
  }

  @PostMapping("/etterem/asztal/{asztalId}/splitSzamla")
  public String splitSzamla(
      @PathVariable Integer asztalId,
      Model model
  ) {
    szamlaService.splitSzamla(asztalId);
    SzamlaDto dto = szamlaService.szamlaDtoBuilder(szamlaService.findSzamlaByAsztalId(asztalId));
    model.addAttribute("szamlaDto", dto);
    return "etterem/szamla";
  }

  @PostMapping("etterem/asztal/{asztalId}/cancelSplit")
  public String cancelSplit(
      @PathVariable Integer asztalId,
      Model model
  ) {
    szamlaService.cancelSplit(asztalId);
    SzamlaDto dto = szamlaService.szamlaDtoBuilder(szamlaService.findSzamlaByAsztalId(asztalId));
    model.addAttribute("szamlaDto", dto);
    return "etterem/szamla";
  }

  @PostMapping("/etterem/asztal/{asztalId}/addToSzamlaSplit/{termekId}")
  public String addToSzamlaSplit(
      @PathVariable Integer asztalId,
      @PathVariable Integer termekId
  ) {
    szamlaService.addToSzamlaSplit(asztalId, termekId);
    return "redirect:/etterem/asztal/" + asztalId + "/szamla";
  }

  @PostMapping("/etterem/asztal/{asztalId}/removeFromSzamlaSplit/{termekId}")
  public String removeFromSzamlaSplit(
      @PathVariable Integer asztalId,
      @PathVariable Integer termekId
  ) {
    szamlaService.removeFromSzamlaSplit(asztalId, termekId);
    return "redirect:/etterem/asztal/" + asztalId + "/szamla";
  }

  @PostMapping("etterem/asztal/{asztalId}/splitSzamlaFizetese")
  public String splitSzamlaFizetes(
      @PathVariable Integer asztalId
  ) {
    szamlaService.kulonSzamlaFizetese(asztalId);
    return "redirect:/etterem/asztal/" + asztalId + "/szamla";
  }

  @ModelAttribute("szamlaDto")
  SzamlaDto szamlaDto() {
    return SzamlaDto.builder().build();
  }
}
