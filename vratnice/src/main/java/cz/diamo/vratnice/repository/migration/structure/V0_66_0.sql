-- Přídání číselníku stat
create table vratnice.stat (
    id_stat INTEGER not null,
    nazev_resx VARCHAR(100) not null,
    constraint pk_stat primary key (id_stat)            
);

insert into vratnice.zdrojovy_text (hash, text) values ('STAT_CESKA_REPUBLIKA','Česká republika');
insert into vratnice.stat (id_stat, nazev_resx) values (1, 'STAT_CESKA_REPUBLIKA');
insert into vratnice.zdrojovy_text (hash, text) values ('STAT_SLOVENSKO','Slovensko');
insert into vratnice.stat (id_stat, nazev_resx) values (2, 'STAT_SLOVENSKO');
insert into vratnice.zdrojovy_text (hash, text) values ('STAT_POLSKO','Polsko');
insert into vratnice.stat (id_stat, nazev_resx) values (3, 'STAT_POLSKO');
insert into vratnice.zdrojovy_text (hash, text) values ('STAT_NEMECKO','Německo');
insert into vratnice.stat (id_stat, nazev_resx) values (4, 'STAT_NEMECKO');
insert into vratnice.zdrojovy_text (hash, text) values ('STAT_RAKOUSKO','Rakousko');
insert into vratnice.stat (id_stat, nazev_resx) values (5, 'STAT_RAKOUSKO');

ALTER TABLE vratnice.povoleni_vjezdu_vozidla 
    DROP COLUMN zeme_registrace_vozidla;


ALTER TABLE vratnice.povoleni_vjezdu_vozidla
    ADD COLUMN id_stat INTEGER;

ALTER TABLE vratnice.povoleni_vjezdu_vozidla ADD CONSTRAINT fk_povoleni_vjezdu_vozidla_id_stat
	FOREIGN KEY (id_stat) REFERENCES vratnice.stat (id_stat) ON DELETE No Action ON UPDATE No Action;

UPDATE vratnice.povoleni_vjezdu_vozidla
    SET id_stat = 1;