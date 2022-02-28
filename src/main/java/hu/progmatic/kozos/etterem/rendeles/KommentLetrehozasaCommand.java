package hu.progmatic.kozos.etterem.rendeles;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class KommentLetrehozasaCommand {
  private String uzenet;
}
