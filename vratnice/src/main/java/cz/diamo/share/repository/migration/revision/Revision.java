package cz.diamo.share.repository.migration.revision;

import java.sql.SQLException;

import org.flywaydb.core.api.FlywayException;
import org.flywaydb.core.api.callback.Callback;
import org.flywaydb.core.api.callback.Context;
import org.flywaydb.core.api.callback.Event;
import org.springframework.stereotype.Component;

import cz.diamo.share.constants.Constants;

/**
 * Slouží primárně pro zachování kompatibility.
 * <p>
 * Doplňuje popřípadě aktualizuje záznam v tabulce "databaze" po každé migraci.
 * Nahrazuje ruční přidávání této aktualizace do každého migračního skriptu.
 *
 */
@Component("Database-Revision")
public class Revision implements Callback {

    @Override
    public boolean supports(Event event, Context context) {
        return event == Event.AFTER_EACH_MIGRATE;
    }

    @Override
    public boolean canHandleInTransaction(Event event, Context context) {
        return true;
    }

    @Override
    public void handle(Event event, Context context) {
        if (event == Event.AFTER_EACH_MIGRATE) {
            var update = "UPDATE " + Constants.SCHEMA + ".databaze set verze_db = %s, sub_verze_db = %s, " +
                    "cas_zmn = now(), zmenu_provedl = 'pgadmin';";
            var insert = "insert into " + Constants.SCHEMA + "databaze (id_databaze, verze_db, sub_verze_db, " +
                    "cas_zmn, zmenu_provedl) values (0, %s, %s, now(), 'goradmin');";

            var version = context.getMigrationInfo().getVersion().getVersion().split("\\.");
            try (var st = context.getConnection().createStatement()) {
                var exists = st
                        .executeQuery("SELECT id_databaze FROM " + Constants.SCHEMA + ".databaze WHERE id_databaze = 0")
                        .next();
                if (exists) {
                    st.execute(String.format(update, version[0], version[1]));
                } else {
                    st.execute(String.format(insert, version[0], version[1]));
                }
            } catch (SQLException e) {
                throw new FlywayException(e);
            }
        }
    }

    @Override
    public String getCallbackName() {
        return "Plnění verze databáze";
    }
}