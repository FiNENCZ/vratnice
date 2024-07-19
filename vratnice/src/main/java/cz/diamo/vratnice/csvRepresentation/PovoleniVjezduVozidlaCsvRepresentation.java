package cz.diamo.vratnice.csvRepresentation;

import com.opencsv.bean.CsvBindByName;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PovoleniVjezduVozidlaCsvRepresentation {

    @CsvBindByName(column = "jmenoZadatele")
    private String jmenoZadatele;

    @CsvBindByName(column = "prijmeniZadatele")
    private String prijmeniZadatele;

    @CsvBindByName(column = "spolecnostZadatele")
    private String spolecnostZadatele;

    @CsvBindByName(column = "icoZadatele")
    private String icoZadatele;

    @CsvBindByName(column = "duvodZadosti")
    private String duvodZadosti;

    @CsvBindByName(column = "rzVozidla")
    private String[] rzVozidla; // Pole pro více hodnot

    @CsvBindByName(column = "typVozidla")
    private String[] typVozidla; // Pole pro více hodnot

    @CsvBindByName(column = "zemeRegistraceVozidla")
    private String zemeRegistraceVozidla;

    @CsvBindByName(column = "ridic_jmeno")
    private String ridic_jmeno;

    @CsvBindByName(column = "ridic_prijmeni")
    private String ridic_prijmeni;

    @CsvBindByName(column = "ridic_cisloOp")
    private String ridic_cisloOp;

    @CsvBindByName(column = "ridic_firma")
    private String ridic_firma;

    @CsvBindByName(column = "spolecnostVozidla")
    private String spolecnostVozidla;

    @CsvBindByName(column = "datumOd")
    private String datumOd;

    @CsvBindByName(column = "datumDo")
    private String datumDo;

    @CsvBindByName(column = "zavod_nazvy")
    private String[] zavod_nazvy; // Pole pro více hodnot

    @CsvBindByName(column = "opakovanyVjezd")
    private boolean opakovanyVjezd;
}
