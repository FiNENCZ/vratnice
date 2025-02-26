package cz.dp.share.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cz.dp.share.entity.PracovniPozicePrehled;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Pracovní pozice
 */
@Data
@NoArgsConstructor
public class PracovniPoziceNodeDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private PracovniPoziceDto pracovniPozice;

    private List<PracovniPoziceNodeDto> podrizenePracovniPozice;

    public PracovniPoziceNodeDto(PracovniPozicePrehled pracovniPozicePrehled) {

        pracovniPozice = new PracovniPoziceDto(pracovniPozicePrehled);
        podrizenePracovniPozice = new ArrayList<PracovniPoziceNodeDto>();
    }
}
