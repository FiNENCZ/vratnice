package cz.diamo.share.dto.avizace;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cz.diamo.share.base.Utils;
import cz.diamo.share.exceptions.ValidationException;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AvizaceRequestDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "{prijemci.require}")
    private List<AvizacePrijemceRequestDto> prijemci;

    private AvizaceEmailRequestDto email;

    private AvizaceOznameniRequestDto oznameni;

    @JsonIgnore
    public void validate() throws ValidationException {

        if (getEmail() != null)
            Utils.validate(getEmail());
        if (getOznameni() != null)
            Utils.validate(getOznameni());
    }

    @JsonIgnore
    public void pridatPrijemce(AvizacePrijemceRequestDto prijemce) {
        if (getPrijemci() == null)
            setPrijemci(new ArrayList<AvizacePrijemceRequestDto>());
        getPrijemci().add(prijemce);
    }

}
