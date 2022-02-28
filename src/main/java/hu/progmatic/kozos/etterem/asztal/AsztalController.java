package hu.progmatic.kozos.etterem.asztal;


import hu.progmatic.kozos.felhasznalo.FelhasznaloService;
import hu.progmatic.kozos.felhasznalo.UserType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import hu.progmatic.kozos.etterem.rendeles.RendelesService;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class AsztalController {
    @Autowired
    RendelesService rendelesService;
    @Autowired
    private FelhasznaloService felhasznaloService;

    @GetMapping("/etterem/asztal")
    public String etterem() {
        return "/etterem/asztal";
    }

    @RequestMapping("/")
    public String kezdolap() {
        return "etterem/asztal";
    }

    @ModelAttribute("hasUserWriteRole")
    public boolean userWriteRole() {
        return felhasznaloService.hasRole(UserType.Roles.USER_WRITE_ROLE);
    }
}

