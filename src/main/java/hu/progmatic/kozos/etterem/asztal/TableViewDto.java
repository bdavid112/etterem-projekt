package hu.progmatic.kozos.etterem.asztal;

import hu.progmatic.kozos.etterem.leltar.Tipus;
import hu.progmatic.kozos.etterem.termekfooldal.AsztalFeluletTipus;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Builder
@Data
@Getter
@Setter
public class TableViewDto implements Serializable {
  private final Integer id;
  @NotEmpty
  private final String nev;
  @Builder.Default
  private final List<RendelesDto> rendelesDtoList = new ArrayList<>();
  private Tipus termekTipus;
  private AsztalFeluletTipus asztalFeluletTipus;
  private String visszaGombLink;
  private Integer asztalSzam;
  private String felszolgalo;
  private boolean leadott;

  @Builder
  @Data
  @Getter
  @Setter
  public static class RendelesDto implements Serializable {
    private final Integer rendelesId;
    private final String etteremTermekNev;
    private final Integer mennyiseg;
  }
}
