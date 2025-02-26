package cz.dp.share.events;

import org.springframework.context.ApplicationEvent;

import cz.dp.share.entity.Uzivatel;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class UkonceniUzivateleEvent extends ApplicationEvent {
    private Uzivatel uzivatel;

    public UkonceniUzivateleEvent(Object source, Uzivatel uzivatel) {
        super(source);
        setUzivatel(uzivatel);
    }
}
