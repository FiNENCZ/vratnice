-- jmeno_korektura
COMMENT ON TABLE vratnice.jmeno_korektura IS 'Čtečka pasů čte jména bez diakritiky s pouze velkými písmenami. Tato tabulka obsahuje korektoru pro správný přepis jména.';
COMMENT ON COLUMN vratnice.jmeno_korektura.jmeno_vstup IS 'jméno ze čtečky';

-- inicializace_vratnice_kamery
COMMENT ON TABLE vratnice.inicializace_vratnice_kamery IS 'Slouží pro inicializaci vratnice-kamery na jaké IP a v jaký čas. Následně podle IP adresy lze kamery nastavit, konfigurovat';

-- povoleni_vjezdu_vozidla_zmena_stavu
COMMENT ON TABLE vratnice.povoleni_vjezdu_vozidla_zmena_stavu IS 'Slouží pro zaznamenání změna stavu žádosti povolení vjezdu nebo aktivity. Tato informace se využívá např pro oznámení žadatele o změnu stavu jeho žádosti.';

-- navsteva_osoba 
COMMENT ON TABLE vratnice.navsteva_osoba IS 'Tabulka, která zaznamenává informace o osobě, která jde navštívit daného zaměstnance';

-- klic_typ
COMMENT ON TABLE vratnice.klic_typ IS 'Typy klíčů: přidělený, zástup, záložní, ostraha, trezor, úklid';

-- zadost_stav
COMMENT ON TABLE vratnice.zadost_stav IS 'Stav zadosti: schválena, zamitnuta, pripravena, ukončena, pozastavena';

-- navstevni_listek_stav
COMMENT ON TABLE vratnice.navstevni_listek_stav IS 'Stavy: proběhla, neproběhla, ke zpracování';

-- sluzebni_vozidlo_kategorie
COMMENT ON TABLE vratnice.sluzebni_vozidlo_kategorie IS 'Kategorie: referentské, manažerské a jiné.
Referentské může vjet pouze do jemu předělené lokality, manažerské může kdekoliv';

-- sluzebni_vozidlo_funkce
COMMENT ON TABLE vratnice.sluzebni_vozidlo_funkce IS 'funkce: ředitel, náměstek;  pouze u kategorie manažerské';

-- sluzebni_vozidlo_stav
COMMENT ON TABLE vratnice.sluzebni_vozidlo_stav IS 'stav: např blokované (např. je vozidlo v opravě)';

-- historie_sluzebni_vozidlo 
COMMENT ON TABLE vratnice.historie_sluzebni_vozidlo IS 'Historie o změně stavu služebního vozidla: např. obnoveno, smazáno, blokováno, upraveno, atd..';

-- budova
COMMENT ON COLUMN vratnice.budova.id_externi IS 'je id te stejné budovy v jiném modulu, např v Žádostech';

-- navstevni_listek_typ
COMMENT ON TABLE vratnice.navstevni_listek_typ IS 'Typ: papírový, elektronický';

-- navstevni_listek_uzivatel_stav
COMMENT ON TABLE vratnice.navstevni_listek_uzivatel_stav IS 'Slouží k potvrzení/nepotvrzení proběhlé návštěvy daného zaměstnance';

-- uzivatel_vratnice
COMMENT ON TABLE vratnice.uzivatel_vratnice IS 'Tabulka slouží k přidružení dané vrátnice danému uživateli - zaměstnanci. Jedná se o tvorbu oprávnění "vrátných"';

-- uzivatel_navstevni_listek_typ
COMMENT ON TABLE vratnice.uzivatel_navstevni_listek_typ IS 'Nastavení typu návštěvního lístku pro daného uživatele';

--historie_klic
COMMENT ON TABLE vratnice.historie_klic IS 'Tabulka pro záznam historie klíčů - změna aktivita, úprava, odstranění, obnovení atd...';

--specialni_klic_oznameni_vypujcky
COMMENT ON TABLE vratnice.specialni_klic_oznameni_vypujcky IS 'Jedná se o vazbu speciálního klíče a uživatele. Pokud je vypůjčen a nebo vrácen tento speciální klíč, tak je uživatel (dozor) o této akci obeznámen';

