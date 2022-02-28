package hu.progmatic.kozos.etterem.leltar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;

@Controller
public class LeltarController {
    @Autowired
    private TermekService termekService;

    @GetMapping("/etterem/leltar")
    public String items(Model model) {
        return items();
    }

    private String items() {
        return "/etterem/leltar";
    }

    @GetMapping("/etterem/leltar/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        Termek formItem = termekService.getById(id);
        model.addAttribute("formItem", formItem);
        return items();
    }


    @PostMapping("/etterem/leltar/delete/{id}")
    public String delete(@PathVariable Integer id, Model model) {
        termekService.deleteById(id);
        refreshAllTermek(model);
        return items();
    }

    @PostMapping("/etterem/leltar")
    public String create(
            @ModelAttribute("formItem") @Valid Termek formItem,
            BindingResult bindingResult,
            Model model) {
        if (!bindingResult.hasErrors()) {
            termekService.create(formItem);
            refreshAllTermek(model);
            clearFormItem(model);
        }
        return items();
    }

    @PostMapping("/etterem/leltar/{id}")
    public String save(
            @PathVariable Integer id,
            @ModelAttribute("formItem") @Valid Termek formItem,
            BindingResult bindingResult,
            Model model) {
        if (!bindingResult.hasErrors()) {
            termekService.save(formItem);
            refreshAllTermek(model);
            clearFormItem(model);
        }
        return items();
    }

    @ModelAttribute("allItem")
    List<Termek> allItem() {
        return termekService.findAll();
    }

    @ModelAttribute("formItem")
    public Termek formItem() {
        return new Termek();
    }

    @ModelAttribute("allTipus")
    List<Tipus> allTypes() {
        return Arrays.stream(Tipus.values()).toList();
    }

    private void clearFormItem(Model model) {
        model.addAttribute("formItem", formItem());
    }

    private void refreshAllTermek(Model model) {
        model.addAttribute("allItem", allItem());
    }
}
