package hu.progmatic.kozos.etterem.leltar;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TermekRepository extends JpaRepository<Termek, Integer> {
    List<Termek> findAllByTipus(Tipus tipus);
    Termek findAllByNev(String nev);
}
