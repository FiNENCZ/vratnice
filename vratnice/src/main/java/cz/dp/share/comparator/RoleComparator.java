package cz.dp.share.comparator;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

import cz.dp.share.entity.Role;

public class RoleComparator implements Comparator<Role> {

    @Override
    public int compare(Role zaznam1, Role zaznam2) {
        Collator collator = Collator.getInstance(new Locale("cs", "CZ"));
        return collator.compare(zaznam1.getNazev(), zaznam2.getNazev());
    }
}