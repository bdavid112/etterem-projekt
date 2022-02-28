package hu.progmatic.kozos.etterem.szamla;

import lombok.*;

import javax.persistence.*;
import hu.progmatic.kozos.etterem.asztal.Asztal;

import java.util.ArrayList;
import java.util.List;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Szamla {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn
    private Asztal asztal;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "szamla")
    @Builder.Default
    private List<SzamlaTetel> tetelek = new ArrayList<>();
    private boolean split;
}