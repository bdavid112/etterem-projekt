package hu.progmatic.kozos.etterem.szamla;

import org.springframework.data.jpa.repository.JpaRepository;


public interface SzamlaRepository extends JpaRepository<Szamla, Integer> {
    Szamla findByAsztal_Id(Integer asztalId);
}
