#!/usr/bin/env bash
set -euo pipefail

# CampusHub MySQL initialization script for macOS/Linux.
# Modify MYSQL_USER and DB_NAME below if your local MySQL uses another account or database name.
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(cd "${SCRIPT_DIR}/.." && pwd)"
ENV_FILE="${ROOT_DIR}/.env"

MYSQL_HOST="${MYSQL_HOST:-localhost}"
MYSQL_PORT="${MYSQL_PORT:-3306}"
MYSQL_USER="${MYSQL_USER:-root}"
DB_NAME="${MYSQL_DATABASE:-campushub}"

if [[ -f "${ENV_FILE}" ]]; then
  echo "Loading local environment from .env"
  set -a
  # shellcheck disable=SC1090
  source "${ENV_FILE}"
  set +a
  MYSQL_HOST="${MYSQL_HOST:-localhost}"
  MYSQL_PORT="${MYSQL_PORT:-3306}"
  MYSQL_USER="${MYSQL_USER:-root}"
  DB_NAME="${MYSQL_DATABASE:-campushub}"
fi

echo "========================================"
echo "CampusHub database initialization"
echo "Database: ${DB_NAME}"
echo "MySQL user: ${MYSQL_USER}"
echo "========================================"
echo
if [[ -z "${MYSQL_PASSWORD:-}" ]]; then
  read -r -s -p "Please enter MySQL password: " MYSQL_PASSWORD
  echo
else
  echo "Using MySQL password from MYSQL_PASSWORD environment variable."
fi

run_sql() {
  local file="$1"
  local args=(--default-character-set=utf8mb4 -h "${MYSQL_HOST}" -P "${MYSQL_PORT}" -u "${MYSQL_USER}")
  if [[ -n "${MYSQL_PASSWORD}" ]]; then
    args+=("-p${MYSQL_PASSWORD}")
  fi
  mysql "${args[@]}" < "${SCRIPT_DIR}/${file}"
}

run_sql_with_db() {
  local file="$1"
  local args=(--default-character-set=utf8mb4 -h "${MYSQL_HOST}" -P "${MYSQL_PORT}" -u "${MYSQL_USER}")
  if [[ -n "${MYSQL_PASSWORD}" ]]; then
    args+=("-p${MYSQL_PASSWORD}")
  fi
  mysql "${args[@]}" "${DB_NAME}" < "${SCRIPT_DIR}/${file}"
}

echo "[1/4] Dropping old tables..."
run_sql "00_drop_tables.sql"

echo "[2/4] Creating database..."
run_sql "01_create_database.sql"

echo "[3/4] Creating tables..."
run_sql_with_db "02_create_tables.sql"

echo "[4/4] Inserting test data..."
run_sql_with_db "03_insert_test_data.sql"

echo
echo "CampusHub database initialization completed successfully."
echo "You can verify with:"
echo "  mysql -u ${MYSQL_USER} -p"
echo "  SHOW DATABASES;"
echo "  USE ${DB_NAME};"
echo "  SHOW TABLES;"
echo "  SELECT * FROM user;"
