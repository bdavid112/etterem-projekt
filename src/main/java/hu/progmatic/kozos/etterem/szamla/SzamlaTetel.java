package hu.progmatic.kozos.etterem.szamla;

import hu.progmatic.kozos.etterem.rendeles.Rendeles;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SzamlaTetel {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;
  @ManyToOne
  private Szamla szamla;
  @OneToOne
  private Rendeles rendeles;
  @NotNull
  private Integer fizetettMennyiseg;
  @NotNull
  private Integer nemFizetettMennyiseg;
}