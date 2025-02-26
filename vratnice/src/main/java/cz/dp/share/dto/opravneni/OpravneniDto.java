package cz.dp.share.dto.opravneni;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.context.MessageSource;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;

import cz.dp.share.dto.BudovaDto;
import cz.dp.share.dto.OpravneniTypPristupuBudovaDto;
import cz.dp.share.dto.OpravneniTypPristupuDto;
import cz.dp.share.dto.PracovniPoziceDto;
import cz.dp.share.dto.ZavodDto;
import cz.dp.share.entity.Budova;
import cz.dp.share.entity.Opravneni;
import cz.dp.share.entity.OpravneniTypPristupuBudova;
import cz.dp.share.entity.PracovniPozicePrehled;
import cz.dp.share.entity.Role;
import cz.dp.share.entity.Zavod;
import cz.dp.share.enums.OpravneniTypPristupuBudovaEnum;
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

    private List<BudovaDto> budovy;

    @NotNull(message = "{pristup.k.zamestnancum.require}")
    private OpravneniTypPristupuDto typPristupuKZamestnancum;

    // @NotNull(message = "{pristup.k.budovam.require}")
    private OpravneniTypPristupuBudovaDto typPristupuKBudovam;

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

    public OpravneniDto(Opravneni opravneni, MessageSource messageSource, boolean zavody, boolean budovy) {
        if (opravneni == null)
            return;

        setId(opravneni.getIdOpravneni());
        setKod(opravneni.getKod());
        setNazev(opravneni.getNazev());
        setPoznamka(opravneni.getPoznamka());
        setTypPristupuKZamestnancum(new OpravneniTypPristupuDto(opravneni.getOpravneniTypPristupu()));
        setTypPristupuKBudovam(new OpravneniTypPristupuBudovaDto(opravneni.getOpravneniTypPristupuBudova()));

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

        if (budovy && opravneni.getBudovy() != null && opravneni.getBudovy().size() > 0) {
            setBudovy(new ArrayList<BudovaDto>());
            for (Budova budova : opravneni.getBudovy()) {
                getBudovy().add(new BudovaDto(budova));
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

            if (getBudovy() != null && getBudovy().size() > 0) {
                opravneni.setBudovy(new ArrayList<Budova>());
                for (BudovaDto budovaDto : getBudovy()) {
                    opravneni.getBudovy().add(budovaDto.getBudova(null, true));
                }
            }

            opravneni.setKod(getKod());
            opravneni.setNazev(getNazev());
            opravneni.setPoznamka(getPoznamka());
            opravneni.setAktivita(getAktivita());

            opravneni.setOpravneniTypPristupu(getTypPristupuKZamestnancum().getOpravneniTypPristupu(null));

            if (getTypPristupuKBudovam() != null)
                opravneni.setOpravneniTypPristupuBudova(getTypPristupuKBudovam().getOpravneniTypPristupuBudova(null));
            else 
                opravneni.setOpravneniTypPristupuBudova(new OpravneniTypPristupuBudova(OpravneniTypPristupuBudovaEnum.TYP_PRIST_BUDOVA_OPR_BEZ_PRISTUPU));
        }
        return opravneni;
    }
}
