package hu.progmatic.kozos.etterem.termekfooldal;

import hu.progmatic.kozos.etterem.asztal.AsztalService;
import hu.progmatic.kozos.etterem.asztal.TableViewDto;
import hu.progmatic.kozos.etterem.leltar.Termek;
import hu.progmatic.kozos.etterem.leltar.TermekDto;
import hu.progmatic.kozos.etterem.leltar.TermekService;
import hu.progmatic.kozos.etterem.leltar.Tipus;
import hu.progmatic.kozos.etterem.rendeles.*;
import hu.progmatic.kozos.felhasznalo.FelhasznaloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Controller
public class TermekFooldalController {
    @Autowired
    private AsztalService asztalService;
    @Autowired
    private RendelesService rendelesService;
    @Autowired
    private TermekService termekService;
    @Autowired
    private FelhasznaloService felhasznaloService;

    @GetMapping("/etterem/asztal/{asztalSzam}/{asztalFeluletTipus}")
    public String itemMain(
            @PathVariable Integer asztalSzam,
            @PathVariable AsztalFeluletTipus asztalFeluletTipus,
            Model model) {
        Integer asztalId = asztalService.getIdByAsztalSzam(asztalSzam);
        TableViewDto dto = asztalService.getTableViewDto(asztalId, asztalFeluletTipus);
        model.addAttribute("tableViewDto", dto);
        if (asztalFeluletTipus == AsztalFeluletTipus.ITAL || asztalFeluletTipus == AsztalFeluletTipus.ETEL) {
            dto.setVisszaGombLink("/etterem/asztal/" + asztalSzam + "/KEZDOLAP");
        } else if (asztalFeluletTipus == AsztalFeluletTipus.KEZDOLAP) {
            dto.setVisszaGombLink("/etterem/asztal/");
        }
        getGombDtoList(asztalSzam, asztalFeluletTipus, model);
        return "etterem/termek_fooldal";
    }

    @GetMapping("/etterem/asztal/{asztalId}/rendelesLeadasa")
    public void rendelesLeadasa(@PathVariable Integer asztalId, HttpServletResponse response) throws IOException {
        RendelesLeadasaCommand cmd = rendelesService.rendelesLeadas(asztalService.getById(asztalId));
        response.setContentType("text/plain");
        response.getOutputStream().write(cmd.getFileTartalom().getBytes(StandardCharsets.UTF_8));
        response.addHeader("Content-Disposition", "attachment; filename=" + cmd.getFileNev());
    }

    @GetMapping("/etterem/asztal/{asztalSzam}/{asztalFeluletTipus}/tipus/{tipus}")
    public String dishes(
            @PathVariable Integer asztalSzam,
            @PathVariable AsztalFeluletTipus asztalFeluletTipus,
            @PathVariable Tipus tipus,
            Model model
    ) {
        model.addAttribute(
                "filteredByTipus",
                termekService.findAllByTipus(tipus)
        );
        Integer asztalId = asztalService.getIdByAsztalSzam(asztalSzam);
        TableViewDto dto = asztalService.getTableViewDto(asztalId, tipus, asztalFeluletTipus);
        model.addAttribute("tableViewDto", dto);
        dto.setVisszaGombLink("/etterem/asztal/" + asztalSzam + "/" + asztalFeluletTipus.name());
        return "etterem/termek_fooldal";
    }

    @PostMapping("/etterem/asztal/{asztalSzam}/{asztalFeluletTipus}/tipus/{tipus}")
    public String createOrder(
            @PathVariable Integer asztalSzam,
            @PathVariable Tipus tipus,
            @PathVariable AsztalFeluletTipus asztalFeluletTipus,
            @ModelAttribute("createRendelesCommand") @Valid CreateRendelesCommand command) {
        Integer asztalId = asztalService.getIdByAsztalSzam(asztalSzam);
        command.setAsztalId(asztalId);
        if (rendelesService.rendelesTartalmazzaATermeket(command)) {
            rendelesService.mennyisegNovelese(asztalId, command.getEtteremTermekId());
        } else {
            rendelesService.create(command);
        }
        return "redirect:/etterem/asztal/" + asztalSzam + "/" + asztalFeluletTipus.name() + "/tipus/" + tipus;
    }

    @PostMapping("/etterem/asztal/{asztalId}/mennyisegNoveleseTipusOldalon/{asztalFeluletTipus}/{tipus}/{termekNeve}")
    public String mennyisegNoveleseTipusOldalon(
            @PathVariable Integer asztalId,
            @PathVariable AsztalFeluletTipus asztalFeluletTipus,
            @PathVariable Tipus tipus,
            @PathVariable String termekNeve,
            Model model
    ) {
        rendelesService.mennyisegNovelese(asztalId, termekNeve);
        TableViewDto dto = asztalService.getTableViewDto(asztalId, tipus, asztalFeluletTipus);
        model.addAttribute("tableViewDto", dto);
        model.addAttribute("filteredByTipus", termekService.findAllByTipus(tipus));
        return "redirect:/etterem/asztal/" + dto.getAsztalSzam() + "/" + asztalFeluletTipus.name() + "/tipus/" + tipus;
    }

    @PostMapping("/etterem/asztal/{asztalId}/mennyisegCsokkenteseTipusOldalon/{asztalFeluletTipus}/{tipus}/{termekNeve}")
    public String mennyisegCsokkenteseTipusOldalon(
            @PathVariable Integer asztalId,
            @PathVariable AsztalFeluletTipus asztalFeluletTipus,
            @PathVariable Tipus tipus,
            @PathVariable String termekNeve,
            Model model
    ) {
        rendelesService.mennyisegCsokkentese(asztalId, termekNeve);
        TableViewDto dto = asztalService.getTableViewDto(asztalId, tipus, asztalFeluletTipus);
        model.addAttribute("tableViewDto", dto);
        model.addAttribute("filteredByTipus", termekService.findAllByTipus(tipus));
        return "redirect:/etterem/asztal/" + dto.getAsztalSzam() + "/" + asztalFeluletTipus.name() + "/tipus/" + tipus;
    }

    @PostMapping("/etterem/asztal/{asztalId}/mennyisegCsokkenteseKezdolapon/{asztalFeluletTipus}/{termekNeve}")
    public String mennyisegCsokkenteseKezdolapon(
            @PathVariable Integer asztalId,
            @PathVariable String termekNeve,
            @PathVariable AsztalFeluletTipus asztalFeluletTipus,
            Model model
    ) {
        rendelesService.mennyisegCsokkentese(asztalId, termekNeve);
        TableViewDto dto = asztalService.getTableViewDto(asztalId, asztalFeluletTipus);
        model.addAttribute("tableViewDto", dto);
        getGombDtoList(asztalId, asztalFeluletTipus, model);
        return "redirect:/etterem/asztal/" + dto.getAsztalSzam() + "/" + asztalFeluletTipus.name();
    }

    @PostMapping("/etterem/asztal/{asztalId}/mennyisegNoveleseKezdolapon/{asztalFeluletTipus}/{termekNeve}")
    public String mennyisegNoveleseKezdolapon(
            @PathVariable Integer asztalId,
            @PathVariable String termekNeve,
            @PathVariable AsztalFeluletTipus asztalFeluletTipus,
            Model model
    ) {
        rendelesService.mennyisegNovelese(asztalId, termekNeve);
        TableViewDto dto = asztalService.getTableViewDto(asztalId, asztalFeluletTipus);
        model.addAttribute("tableViewDto", dto);
        getGombDtoList(asztalId, asztalFeluletTipus, model);
        return "redirect:/etterem/asztal/" + dto.getAsztalSzam() + "/" + asztalFeluletTipus.name();
    }

    private void getGombDtoList(Integer asztalSzam, AsztalFeluletTipus tipus, Model model) {
        List<GombDto> gombok;
        if (tipus == AsztalFeluletTipus.ETEL) {
            gombok = List.of(
                    new GombDto("LEVES", "/etterem/asztal/" + asztalSzam + "/" + tipus + "/tipus/" + Tipus.LEVES.name()),
                    new GombDto("ELŐÉTEL", "/etterem/asztal/" + asztalSzam + "/" + tipus + "/tipus/" + Tipus.ELOETEL.name()),
                    new GombDto("SERTÉSÉTEL", "/etterem/asztal/" + asztalSzam + "/" + tipus + "/tipus/" + Tipus.SERTESETEL.name()),
                    new GombDto("HALÉTEL", "/etterem/asztal/" + asztalSzam + "/" + tipus + "/tipus/" + Tipus.HALETEL.name()),
                    new GombDto("MARHA", "/etterem/asztal/" + asztalSzam + "/" + tipus + "/tipus/" + Tipus.MARHAETEL.name()),
                    new GombDto("DESSZERT", "/etterem/asztal/" + asztalSzam + "/" + tipus + "/tipus/" + Tipus.DESSZERT.name()),
                    new GombDto("EXTRA", "/etterem/asztal/" + asztalSzam + "/" + tipus + "/tipus/" + Tipus.EXTRA.name())
            );
        } else if (tipus == AsztalFeluletTipus.ITAL) {
            gombok = List.of(
                    new GombDto("ALKOHOL", "/etterem/asztal/" + asztalSzam + "/" + tipus + "/tipus/" + Tipus.ALKOHOL.name()),
                    new GombDto("RÖVIDITAL", "/etterem/asztal/" + asztalSzam + "/" + tipus + "/tipus/" + Tipus.ROVIDITAL.name()),
                    new GombDto("KOKTÉL", "/etterem/asztal/" + asztalSzam + "/" + tipus + "/tipus/" + Tipus.KOKTEL.name()),
                    new GombDto("ÜDÍTŐ", "/etterem/asztal/" + asztalSzam + "/" + tipus + "/tipus/" + Tipus.UDITO.name()),
                    new GombDto("FORRÓ ITAL", "/etterem/asztal/" + asztalSzam + "/" + tipus + "/tipus/" + Tipus.FORROITAL.name()),
                    new GombDto("KÁVÉ", "/etterem/asztal/" + asztalSzam + "/" + tipus + "/tipus/" + Tipus.KAVE.name()),
                    new GombDto("EXTRA", "/etterem/asztal/" + asztalSzam + "/" + tipus + "/tipus/" + Tipus.EXTRA.name())
            );
        } else {
            gombok = List.of(
                    new GombDto("ÉTEL", "/etterem/asztal/" + asztalSzam + "/" + AsztalFeluletTipus.ETEL.name()),
                    new GombDto("ITAL", "/etterem/asztal/" + asztalSzam + "/" + AsztalFeluletTipus.ITAL.name())
            );
        }
        model.addAttribute(
                "gombDtoList",
                gombok
        );
    }

    @PostMapping("/etterem/asztal/{asztalId}/rendeles/{termekNev}/comment/{asztalFeluletTipus}")
    public String kommentKezdolapon(
            @PathVariable Integer asztalId,
            @PathVariable String termekNev,
            @PathVariable AsztalFeluletTipus asztalFeluletTipus,
            @ModelAttribute("kommentLetrehozasaCommand") @Valid KommentLetrehozasaCommand command
    ) {
        rendelesService.komment(asztalId, termekNev, command.getUzenet());
        return "redirect:/etterem/asztal/" + asztalId + "/" + asztalFeluletTipus.name();
    }

    @PostMapping("/etterem/asztal/{asztalId}/rendeles/{termekNev}/comment/{asztalFeluletTipus}/{tipus}")
    public String kommentTipusOldalon(
            @PathVariable Integer asztalId,
            @PathVariable String termekNev,
            @PathVariable AsztalFeluletTipus asztalFeluletTipus,
            @PathVariable Tipus tipus,
            @ModelAttribute("kommentLetrehozasaCommand") @Valid KommentLetrehozasaCommand command
    ) {
        rendelesService.komment(asztalId, termekNev, command.getUzenet());
        return "redirect:/etterem/asztal/" + asztalId + "/" + asztalFeluletTipus.name() + "/" + tipus.name();

    }

    @ModelAttribute("allItem")
    List<Termek> allItem() {
        return termekService.findAll();
    }

    @ModelAttribute("tableViewDto")
    public TableViewDto tableViewDto() {
        return TableViewDto.builder().build();
    }

    @ModelAttribute("gombDtoList")
    public List<GombDto> gombDtoList() {
        return List.of();
    }

    @ModelAttribute("rendelesItems")
    List<TermekDto> rendelesItems() {
        return List.of();
    }

    @ModelAttribute("formItem")
    public Rendeles formItem() {
        return new Rendeles();
    }

    @ModelAttribute("filteredByTipus")
    public List<Termek> filteredByTipus() {
        return List.of();
    }

    @ModelAttribute("kommentLetrehozasaCommand")
    public KommentLetrehozasaCommand kommentLetrehozasaCommand() {
        return KommentLetrehozasaCommand.builder().build();
    }
}
