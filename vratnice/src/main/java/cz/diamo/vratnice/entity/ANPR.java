package cz.diamo.vratnice.entity;

import jakarta.xml.bind.annotation.*;
import lombok.Data;
import lombok.NoArgsConstructor;


@XmlRootElement(name = "ANPR", namespace = "http://www.hikvision.com/ver20/XMLSchema")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
@NoArgsConstructor
public class ANPR {

    @XmlElement(name = "licensePlate")
    private String licensePlate;

    @XmlElement(name = "plateType")
    private String plateType;

    @XmlElement(name = "vehicleType")
    private String vehicleType;

}