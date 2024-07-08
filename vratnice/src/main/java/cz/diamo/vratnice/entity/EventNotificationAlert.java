package cz.diamo.vratnice.entity;

import javax.xml.bind.annotation.*;

import lombok.Data;
import lombok.NoArgsConstructor;

@XmlRootElement(name = "EventNotificationAlert", namespace = "http://www.hikvision.com/ver20/XMLSchema")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
@NoArgsConstructor
public class EventNotificationAlert {

    @XmlElement(name = "dateTime")
    private String dateTime;

    @XmlElement(name = "ANPR")
    private ANPR anpr;

}
