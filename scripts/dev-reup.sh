#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."

echo ">>> Building and restarting backend"
mvn -q clean package -DskipTests
docker compose down
docker compose up -d --build
docker compose logs -f app