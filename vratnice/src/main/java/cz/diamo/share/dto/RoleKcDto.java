package cz.diamo.share.dto;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RoleKcDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    private String name;
}
