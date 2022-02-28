package hu.progmatic.kozos.felhasznalo;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class LoginService implements InitializingBean {
    @Autowired
    FelhasznaloRepository felhasznaloRepository;

    public final List<Felhasznalo> felhasznalok = List.of(
            new Felhasznalo(null, "admin", "adminpass", UserType.ADMIN),
            new Felhasznalo(null, "bence", "bence", UserType.ADMIN),
            new Felhasznalo(null, "benji", "benji", UserType.ADMIN),
            new Felhasznalo(null, "attila", "attila", UserType.ADMIN),
            new Felhasznalo(null, "olivér", "olivér", UserType.ADMIN),
            new Felhasznalo(null, "dávid", "dávid", UserType.ADMIN),
            new Felhasznalo(null, "miska", "miska", UserType.ADMIN),
            new Felhasznalo(null, "felszolgáló", "felszolgáló", UserType.FELSZOLGALO)
    );

    @Override
    public void afterPropertiesSet() {
        if (felhasznaloRepository.count() == 0) {
            felhasznaloRepository.saveAll(felhasznalok);
        }
    }
}
