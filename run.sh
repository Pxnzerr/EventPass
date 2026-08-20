#!/usr/bin/env bash
set -e

echo "[EventPass] Compilando aplicacao..."
mkdir -p out
javac -encoding UTF-8 -d out src/eventpass/exception/*.java src/eventpass/model/*.java src/eventpass/service/*.java src/eventpass/EventPass.java

echo "[EventPass] Iniciando CLI..."
echo ""
java -cp out eventpass.EventPass
