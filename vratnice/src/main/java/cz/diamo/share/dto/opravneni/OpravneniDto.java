package cz.diamo.share.dto.opravneni;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.context.MessageSource;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;

import cz.diamo.share.dto.OpravneniTypPristupuDto;
import cz.diamo.share.dto.PracovniPoziceDto;
import cz.diamo.share.dto.ZavodDto;
import cz.diamo.share.entity.Opravneni;
import cz.diamo.share.entity.PracovniPozicePrehled;
import cz.diamo.share.entity.Role;
import cz.diamo.share.entity.Zavod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OpravneniDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    private List<RoleDto> role;

    private List<PracovniPoziceDto> pracovniPozice;

    @JsonAlias("zavody") // kvuli absenci ve swaggeru
    @Size(max = Integer.MAX_VALUE, message = "*")
    private List<ZavodDto> zavody;

    @NotNull(message = "{pristup.k.zamestnancum.require}")
    private OpravneniTypPristupuDto typPristupuKZamestnancum;

    @Size(max = 100, message = "{kod.max.100}")
    @NotBlank(message = "{kod.require}")
    private String kod;

    @Size(max = 4000, message = "{poznamka.max.4000}")
    private String poznamka;

    @Size(max = 100, message = "{nazev.max.100}")
    @NotBlank(message = "{nazev.require}")
    private String nazev;

    @NotNull(message = "{aktivita.require}")
    private Boolean aktivita;

    public OpravneniDto(Opravneni opravneni, MessageSource messageSource, boolean zavody) {
        if (opravneni == null)
            return;

        setId(opravneni.getIdOpravneni());
        setKod(opravneni.getKod());
        setNazev(opravneni.getNazev());
        setPoznamka(opravneni.getPoznamka());
        setTypPristupuKZamestnancum(new OpravneniTypPristupuDto(opravneni.getOpravneniTypPristupu()));

        if (opravneni.getRole() != null && opravneni.getRole().size() > 0) {
            setRole(new ArrayList<RoleDto>());
            for (Role role : opravneni.getRole()) {
                getRole().add(new RoleDto(role));
            }
        }

        if (opravneni.getPracovniPozice() != null && opravneni.getPracovniPozice().size() > 0) {
            setPracovniPozice(new ArrayList<PracovniPoziceDto>());
            for (PracovniPozicePrehled pracovniPozicePrehled : opravneni.getPracovniPozice()) {
                getPracovniPozice().add(new PracovniPoziceDto(pracovniPozicePrehled));
            }
        }

        if (zavody && opravneni.getZavody() != null && opravneni.getZavody().size() > 0) {
            setZavody(new ArrayList<ZavodDto>());
            for (Zavod zavod : opravneni.getZavody()) {
                getZavody().add(new ZavodDto(zavod));
            }
        }

        setAktivita(opravneni.getAktivita());
    }

    @JsonIgnore
    public Opravneni getOpravneni(Opravneni opravneni, boolean pouzeId) {
        if (opravneni == null)
            opravneni = new Opravneni();

        opravneni.setIdOpravneni(getId());

        if (!pouzeId) {

            if (getRole() != null && getRole().size() > 0) {
                opravneni.setRole(new ArrayList<Role>());
                for (RoleDto roleDto : getRole()) {
                    opravneni.getRole().add(roleDto.getRole(null));
                }
            }
            if (getPracovniPozice() != null && getPracovniPozice().size() > 0) {
                opravneni.setPracovniPozice(new ArrayList<PracovniPozicePrehled>());
                for (PracovniPoziceDto pracovniPoziceDto : getPracovniPozice()) {
                    opravneni.getPracovniPozice().add(pracovniPoziceDto.getPracovniPozicePrehled(null));
                }
            }

            if (getZavody() != null && getZavody().size() > 0) {
                opravneni.setZavody(new ArrayList<Zavod>());
                for (ZavodDto zavodDto : getZavody()) {
                    opravneni.getZavody().add(zavodDto.getZavod(null, true));
                }
            }

            opravneni.setKod(getKod());
            opravneni.setNazev(getNazev());
            opravneni.setPoznamka(getPoznamka());
            opravneni.setAktivita(getAktivita());

            opravneni.setOpravneniTypPristupu(getTypPristupuKZamestnancum().getOpravneniTypPristupu(null));
        }
        return opravneni;
    }
}
