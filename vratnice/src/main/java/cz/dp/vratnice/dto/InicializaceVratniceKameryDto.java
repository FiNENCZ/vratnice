package cz.dp.vratnice.dto;

import java.io.Serializable;
import java.sql.Timestamp;

import cz.dp.vratnice.entity.InicializaceVratniceKamery;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class InicializaceVratniceKameryDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;

    private String ipAdresa;

    private Timestamp casInicializace;

    public InicializaceVratniceKameryDto(InicializaceVratniceKamery vratniceKamery) {
        if (vratniceKamery == null) {
            return;
        }

        this.id = vratniceKamery.getIdInicializaceVratniceKamery();
        this.ipAdresa = vratniceKamery.getIpAdresa();
        this.casInicializace = vratniceKamery.getCasInicializace();
    }

    public InicializaceVratniceKamery toEntity() {
        InicializaceVratniceKamery vratniceKamery = new InicializaceVratniceKamery();

        vratniceKamery.setIdInicializaceVratniceKamery(this.id);
        vratniceKamery.setIpAdresa(this.ipAdresa);
        vratniceKamery.setCasInicializace(this.casInicializace);

        return vratniceKamery;
    }

}
