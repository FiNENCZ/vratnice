package cz.diamo.vratnice.dto;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RzTypVozidlaDto {

    @NotNull(message = "{povoleni.vjezdu.vozidla.rz_vozidla.require}")
    private List<String> rzVozidla;

    @NotNull(message = "{povoleni.vjezdu.vozidla.typ_vozidla.require}")
    @NotEmpty(message = "{povoleni.vjezdu.vozidla.typ_vozidla.require}")
    private List<VozidloTypDto> typVozidla;

    @AssertTrue(message = "{povoleni.vjezdu.vozidla.rz_typ_vozidla.require}")
    private boolean isRzVozidlaTypVozidlaCountEqual() {
        if (rzVozidla == null || typVozidla == null) {
            return false;
        }
        return rzVozidla.size() == typVozidla.size();
    }

    @AssertTrue(message = "{povoleni.vjezdu.vozidla.rz_vozidla.unique}")
    private boolean isRzVozidlaUnique() {
        if (rzVozidla == null) {
            return true; // pokud je seznam null, nechceme aby validace selhala zde
        }
        Set<String> uniqueRzVozidla = new HashSet<>(rzVozidla);
        return uniqueRzVozidla.size() == rzVozidla.size();
    }
    

}
