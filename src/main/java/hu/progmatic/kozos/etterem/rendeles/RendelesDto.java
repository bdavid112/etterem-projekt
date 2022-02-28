package hu.progmatic.kozos.etterem.rendeles;

import hu.progmatic.kozos.etterem.leltar.TermekDto;
import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RendelesDto {
  private Integer id;
  private TermekDto termekDto;
  private Integer mennyiseg;
  private String comment;
}
