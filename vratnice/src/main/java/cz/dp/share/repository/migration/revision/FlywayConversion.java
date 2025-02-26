package cz.dp.share.repository.migration.revision;

import java.sql.SQLException;
import java.sql.Statement;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.flywaydb.core.api.FlywayException;
import org.flywaydb.core.api.callback.Callback;
import org.flywaydb.core.api.callback.Context;
import org.flywaydb.core.api.callback.Event;
import org.springframework.stereotype.Component;

import cz.dp.share.constants.Constants;

/**
 * Callback pro přechod na flyway.
 * <p>
 * Pro databáze, které ještě neměly flyway, je potřeba přidat záznam do tabulky
 * "flyway_schema_history". Dále je ještě potřeba přidat
 * "flyway_data_history".
 */
@Component("Database-FlywayConversion")
public class FlywayConversion implements Callback {

    @SuppressWarnings("FieldCanBeLocal")
    private final String REQUIRED_DATABASE_VERSION_FOR_FIRST_FLYWAY = "0.1.0";
    @SuppressWarnings("FieldCanBeLocal")
    private final String BASE_INIT_POSTGRES_CHECKSUM = "-1852017116";
    // private final String BASE_INIT_POSTGRES_CHECKSUM = "1328644371";

    @Override
    public boolean supports(Event event, Context context) {
        return event == Event.BEFORE_MIGRATE;
    }

    @Override
    public boolean canHandleInTransaction(Event event, Context context) {
        return true;
    }

    @Override
    public void handle(Event event, Context context) {
        if (event == Event.BEFORE_MIGRATE) {
            var connection = context.getConnection();
            try {
                connection.setAutoCommit(false);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            try (var st = connection.createStatement()) {
                if (isNewFlyway(st) && isInitialized(st)) {
                    checkVersion(st);
                    initFlywayForExistingDatabase(st, context);
                    connection.commit();
                }
            } catch (SQLException e) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    e.addSuppressed(ex);
                }

                try {
                    connection.setAutoCommit(true);
                } catch (SQLException ex) {
                    e.addSuppressed(e);
                }

                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Tabulka "flyway_schema_history" musí vždy existovat. Měl by se o to postarat
     * flyway. Zárověň musí vždy existovat jeden záznam << Flyway Baseline >>
     *
     * @param st databázový statement
     * @return true pokud se jedná o první inicializaci flyway.
     */
    private boolean isNewFlyway(Statement st) throws SQLException {
        try (var rs = st
                .executeQuery("SELECT COUNT(*) as row_count FROM " + Constants.SCHEMA + ".flyway_schema_history")) {
            if (rs.next()) {
                return "0".equals(rs.getString("row_count"));
            } else {
                throw new FlywayException("Missing << Flyway Baseline >> record in flyway_schema_history");
            }
        }
    }

    /**
     * Kontrola, zda se jedná o novou instanci nebo starou podle tabulky
     * databaze.
     *
     * @param st databázový statement
     * @return true pokud exituje tabulka database a obsahuje alespoň jeden
     *         záznam
     */
    private boolean isInitialized(Statement st) throws SQLException {
        // Kontrola, zda existuje tabulka databaze
        try (var rs = st.executeQuery(
                "SELECT table_name FROM information_schema.tables WHERE table_schema"
                        + " LIKE '" + Constants.SCHEMA
                        + "' AND table_type LIKE 'BASE TABLE' AND table_name = 'databaze';")) {
            if (!rs.next())
                return false;
        }
        // Kontrola, zda tabulka obsahuje nějaký záznam
        try (var rs = st.executeQuery("SELECT * FROM " + Constants.SCHEMA + ".databaze;")) {
            if (!rs.next())
                return false;
        }

        return true;
    }

    /**
     * Kontrola nutné verze pro první použití flyway.
     *
     * @param st databázový statement
     */
    private void checkVersion(Statement st) throws SQLException {
        try (var rs = st.executeQuery("SELECT * FROM " + Constants.SCHEMA + ".databaze;")) {
            if (rs.next()) {
                // V tabulce "databaze" existuje již záznam s nějakou verzí
                var currentDatabaseVersion = String.join(".", rs.getString("verze_db"), rs.getString("sub_verze_db"),
                        "0");

                if (!REQUIRED_DATABASE_VERSION_FOR_FIRST_FLYWAY.equals(currentDatabaseVersion)) {
                    throw new FlywayException("First flyway start required " + Constants.SCHEMA + " database version: "
                            + REQUIRED_DATABASE_VERSION_FOR_FIRST_FLYWAY + ", Current version is: "
                            + currentDatabaseVersion);
                }
            } else {
                // V tabulce "databaze" neexistuje záznam s nějakou verzí
                throw new FlywayException("First flyway start required \"+Constants.SCHEMA+\" database version: "
                        + REQUIRED_DATABASE_VERSION_FOR_FIRST_FLYWAY + ", Current version missing");
            }
        }
    }

    /**
     * Vložení záznamu pro fyway pokud se jedná o již inicializovanou databázi.
     *
     * @param st databázový statement
     */
    private void initFlywayForExistingDatabase(Statement st, Context context) throws SQLException {
        var insert = "INSERT INTO " + Constants.SCHEMA
                + ".flyway_schema_history (installed_rank, \"version\", description, \"type\","
                + " script, checksum, installed_by, installed_on, execution_time, success) "
                + "VALUES(0, '0.1.0', 'BaseInit', 'SQL', 'V0_1_0.sql', '" + BASE_INIT_POSTGRES_CHECKSUM
                + "', '" + Constants.PROJECT_NAME + "', '"
                + ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                + "', -1, '1');";
        var create = "CREATE TABLE " + Constants.SCHEMA + ".flyway_data_history (\n"
                + "    description varchar(200) NOT NULL,\n"
                + "    installed_on timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,\n" + "    success int NOT NULL,\n"
                + "    CONSTRAINT pk_flyway_data_history PRIMARY KEY (description)\n" + ");";
        var update = "UPDATE " + Constants.SCHEMA + ".databaze set verze_db = %s, sub_verze_db = %s"
                + ",cas_zmn = now(), zmenu_provedl = 'pgadmin';";

        st.execute(insert);
        st.execute(create);
        st.execute(String.format(update, 3, 0));
    }

    @Override
    public String getCallbackName() {
        return "Inicializace flyway";
    }
}