package cz.diamo.vratnice.base;

import java.io.Serializable;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import cz.diamo.share.base.Utils;
import cz.diamo.share.constants.Constants;
import cz.diamo.share.entity.Databaze;
import cz.diamo.vratnice.enums.TableEnum;

public class VratniceIdentifierGenerator implements IdentifierGenerator {

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {

        String[] names = object.getClass().getName().split("\\.");
        String tableName = "";
        if (names.length > 0)
            tableName = names[names.length - 1];
        else
            throw new HibernateException("Neočekávaný název třídy.");

        TableEnum tableEnum = null;
        try {
            tableEnum = TableEnum.valueOf(tableName);
        } catch (Exception e) {

        }
        if (tableEnum == null)
            throw new HibernateException("Tabulka není evidována ve výčtovém typu (" + tableName + ").");
        String tablePrefix = tableEnum.getPrefix();

        try {

            String seqName = Constants.SCHEMA + ".seq_" + Utils.camelToSnake(tableName) + "_id_"
                    + Utils.camelToSnake(tableName);
            Long poradi = session.createQuery("SELECT nextval ('" + seqName + "') as nextval", Long.class)
                    .getSingleResult();
            Databaze databaze = session
                    .createQuery("SELECT m from Databaze m where m.idDatabaze = 0", Databaze.class)
                    .getSingleResult();
            String identifikator = databaze.getDbPrefix() + tablePrefix + String.format("%010d", poradi);
            return identifikator;

        } catch (Exception e) {
            throw new HibernateException("Neočekávaná chyba při generování identifikátoru. " + e.toString());
        }
    }
}
