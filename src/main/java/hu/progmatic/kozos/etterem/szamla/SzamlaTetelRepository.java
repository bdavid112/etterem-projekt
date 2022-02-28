package hu.progmatic.kozos.etterem.szamla;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SzamlaTetelRepository extends JpaRepository<SzamlaTetel, Integer> {
  void deleteAllBySzamla(Szamla szamla);
}
