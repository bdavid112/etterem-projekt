package hu.progmatic.kozos.etterem.rendeles;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CreateRendelesCommand {
  private Integer asztalId;
  private Integer mennyiseg;
  private Integer etteremTermekId;
}
