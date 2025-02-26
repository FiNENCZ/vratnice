package cz.dp.vratnice.dto;

import java.io.Serializable;

import cz.dp.vratnice.entity.JmenoKorektura;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class JmenoKorekturaDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;

    @NotBlank(message = "{jmeno_korektura.jmeno_vstup.require}")
    @Size(max = 50, message = "{jmeno_korektura.jmeno_vstup.max.50}")
    private String jmenoVstup;

    @NotBlank(message = "{jmeno_korektura.korektura.require}")
    @Size(max = 50, message = "{jmeno_korektura.korektura.max.50}")
    private String korektura;


    public JmenoKorekturaDto(JmenoKorektura jmenoKorektura) {
        if (jmenoKorektura == null) {
            return;
        }

        this.id = jmenoKorektura.getIdJmenoKorektura();
        this.jmenoVstup = jmenoKorektura.getJmenoVstup();
        this.korektura = jmenoKorektura.getKorektura();
  
    }

    public JmenoKorektura toEntity() {
        JmenoKorektura jmenoKorektura = new JmenoKorektura();
        
        jmenoKorektura.setIdJmenoKorektura(this.id);
        jmenoKorektura.setJmenoVstup(this.jmenoVstup);
        jmenoKorektura.setKorektura(this.korektura);

        return jmenoKorektura;
    }

}
