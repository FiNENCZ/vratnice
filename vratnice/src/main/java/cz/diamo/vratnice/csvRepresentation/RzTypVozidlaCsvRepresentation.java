package cz.diamo.vratnice.csvRepresentation;

import com.opencsv.bean.CsvBindByName;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RzTypVozidlaCsvRepresentation {

    @CsvBindByName(column = "rzVozidla")
    private String[] rzVozidla;

    @CsvBindByName(column = "typVozidla")
    private String[] typVozidla;

}
