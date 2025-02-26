package cz.dp.share.dto;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cz.dp.share.base.Utils;
import cz.dp.share.entity.Role;
import cz.dp.share.entity.Uzivatel;
import cz.dp.share.entity.ZastupSimple;
import cz.dp.share.entity.Zavod;
import cz.dp.share.enums.RoleEnum;
import lombok.Data;

@Data
public class AppUserDto {

    private String name = "";

    private String idUzivatel = "";

    private String sapId;

    private ZavodDto zavod;

    private ZakazkaDto zakazka;

    private String idKmenovehoZavodu;

    private List<GrantedAuthority> authorities;

    private List<String> aplikace;

    /**
     * Závody ke kterým má uživatel přístup, kromě aktuálně vybraného
     */
    private List<ZavodDto> ostatniZavody;

    private String webSocketOznameni;

    private List<ZastupSimpleDto> zastupy;

    private ZastupSimpleDto zastup;

    private boolean pruznaPracDoba;

    public boolean isZastup() {
        return getZastup() != null;
    }

    public UzivatelDto getUzivatelDto() {
        if (StringUtils.isBlank(getIdUzivatel()))
            return null;

        UzivatelDto uzivatelDto = new UzivatelDto();
        uzivatelDto.setId(getIdUzivatel());
        uzivatelDto.setNazev(getName());
        uzivatelDto.setZavod(getZavod());
        uzivatelDto.setSapId(getSapId());
        uzivatelDto.setZakazka(getZakazka());
        uzivatelDto.setPruznaPracDoba(isPruznaPracDoba());

        return uzivatelDto;
    }

    public AppUserDto(Uzivatel uzivatel, List<ZastupSimple> zastupy) {
        setName(uzivatel.getNazev());
        setIdUzivatel(uzivatel.getIdUzivatel());
        setSapId(uzivatel.getSapId());

        if (uzivatel.getZastup() != null) {
            setIdKmenovehoZavodu(uzivatel.getZastup().getZavod().getIdZavod());
            setZavod(new ZavodDto(uzivatel.getZastup().getZavod()));
        } else {
            setIdKmenovehoZavodu(uzivatel.getZavod().getIdZavod());
            setZavod(new ZavodDto(uzivatel.getZavod()));
        }

        setPruznaPracDoba(uzivatel.getPruznaPracDoba());
        if (uzivatel.getZakazka() != null)
            setZakazka(new ZakazkaDto(uzivatel.getZakazka()));

        if (uzivatel.getRole() != null) {
            setAuthorities(new ArrayList<GrantedAuthority>());
            for (Role role : uzivatel.getRole()) {
                getAuthorities().add(new SimpleGrantedAuthority(role.getAuthority()));
            }
        }

        ArrayList<ZavodDto> dostupneZavody = new ArrayList<ZavodDto>();
        if (uzivatel.getZastup() != null)
            dostupneZavody.add(new ZavodDto(uzivatel.getZastup().getZavod()));
        else {
            dostupneZavody.add(new ZavodDto(uzivatel.getZavod()));
            if (uzivatel.getOstatniZavody() != null) {
                for (Zavod zavod : uzivatel.getOstatniZavody()) {
                    dostupneZavody.add(new ZavodDto(zavod));
                }
            }
        }

        if (uzivatel.getZavodVyber() != null) {
            boolean dohledano = getIdKmenovehoZavodu().equals(uzivatel.getZavodVyber().getIdZavod());

            if (uzivatel.getOstatniZavody() != null) {

                for (Zavod zavod : uzivatel.getOstatniZavody()) {
                    if (zavod.getIdZavod().equals(uzivatel.getZavodVyber().getIdZavod())) {
                        dohledano = true;
                        break;
                    }
                }

                if (dohledano)
                    setZavod(new ZavodDto(uzivatel.getZavodVyber()));
            }
        }

        setOstatniZavody(new ArrayList<ZavodDto>());
        for (ZavodDto zavod : dostupneZavody) {
            if (!zavod.getId().equals(getZavod().getId()))
                getOstatniZavody().add(zavod);
        }

        setZastupy(new ArrayList<ZastupSimpleDto>());
        if (zastupy != null && zastupy.size() > 0) {
            for (ZastupSimple zastupSimple : zastupy) {

                if (!StringUtils.isBlank(uzivatel.getIdZastup()) && uzivatel.getIdZastup().equals(zastupSimple.getIdUzivatel()))
                    setZastup(new ZastupSimpleDto(zastupSimple));
                else
                    getZastupy().add(new ZastupSimpleDto(zastupSimple));
            }

            // doplnění bez zástupu
            if (isZastup()) {
                ZastupSimpleDto zastupSimpleDto = new ZastupSimpleDto();
                zastupSimpleDto.setIdUzivatel(getIdUzivatel());
                zastupSimpleDto.setNazev("Zrušit zástup");
                getZastupy().add(0, zastupSimpleDto);
            }
        }

        setWebSocketOznameni(Utils.getWebSocketZmenaOznameniUrl(getSapId()));
        setAplikace(uzivatel.getModuly());
    }

    @JsonIgnore
    public boolean testAuthority(RoleEnum role) {
        return testAuthority(role.toString());
    }

    @JsonIgnore
    public boolean testAuthority(String role) {
        if (getAuthorities() != null && getAuthorities().size() > 0) {
            for (GrantedAuthority grantedAuthority : getAuthorities()) {
                if (grantedAuthority.getAuthority().equals(role))
                    return true;
            }
            return false;
        } else
            return false;
    }
}