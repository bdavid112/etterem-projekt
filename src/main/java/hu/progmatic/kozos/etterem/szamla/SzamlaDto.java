package hu.progmatic.kozos.etterem.szamla;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Data
@Getter
@Setter
@Builder
public class SzamlaDto implements Serializable{
    private Integer id;
    private Integer asztalId;

    private Integer vegosszeg;
    private String formazottVegosszeg;
    private String formazottFizetendoOsszeg;

    private Integer fizetettVegosszeg;
    private String formazottFizetettVegosszeg;
    private String formazottFizetettFizetendoOsszeg;

    private String formazottSzervizdij;
    private String formazottFizetettSzervizdij;

    private boolean split;
    private List<SzamlaTetelDto> tetelek;
    private Integer asztalSzam;
}
