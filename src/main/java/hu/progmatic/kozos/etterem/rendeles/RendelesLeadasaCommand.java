package hu.progmatic.kozos.etterem.rendeles;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RendelesLeadasaCommand {
    private String fileNev;
    private String fileTartalom;
}
