package cz.dp.share.dto;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UcetKcDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String jmeno;

    private String heslo;
}
