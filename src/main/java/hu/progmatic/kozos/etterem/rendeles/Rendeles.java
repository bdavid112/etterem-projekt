package hu.progmatic.kozos.etterem.rendeles;


import hu.progmatic.kozos.etterem.asztal.Asztal;
import hu.progmatic.kozos.etterem.leltar.Termek;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Rendeles {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @ManyToOne
    private Termek termek;
    @NotNull
    private Integer mennyiseg;
    @ManyToOne
    private Asztal asztal;
    private Integer nemLeadottMennyiseg;
    @Builder.Default
    private Integer leadottMennyiseg =0;
    @Builder.Default
    private String komment="";
}
