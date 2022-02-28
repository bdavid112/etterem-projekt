package hu.progmatic.kozos.etterem.asztal;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AsztalRepository extends JpaRepository<Asztal, Integer> {
  Asztal getAsztalByNevEquals(String name);
}
