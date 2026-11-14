#!/bin/bash
# Se ejecuta una sola vez, cuando el volumen de PostgreSQL está vacío.
# Crea una base de datos y un usuario propio para cada microservicio
# (principio de mínimo privilegio: cada servicio solo accede a sus datos).
set -euo pipefail

psql -v ON_ERROR_STOP=1 \
     --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" \
     -v menu_pass="$MENU_DB_PASSWORD" \
     -v pedidos_pass="$PEDIDOS_DB_PASSWORD" <<-'EOSQL'
	CREATE USER menu_user WITH PASSWORD :'menu_pass';
	CREATE DATABASE menu_db OWNER menu_user;
	REVOKE ALL ON DATABASE menu_db FROM PUBLIC;

	CREATE USER pedidos_user WITH PASSWORD :'pedidos_pass';
	CREATE DATABASE pedidos_db OWNER pedidos_user;
	REVOKE ALL ON DATABASE pedidos_db FROM PUBLIC;
EOSQL
