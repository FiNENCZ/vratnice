--Vymazání nepotřebné role

DELETE FROM vratnice.zdrojovy_text WHERE id_zdrojovy_text = 33;
DELETE FROM vratnice.role WHERE authority = 'ROLE_SPRAVA_RIDICU';
