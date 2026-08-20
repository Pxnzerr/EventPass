#!/usr/bin/env bash
set -e

echo "[EventPass] Compilando codigo-fonte e testes..."
mkdir -p out
javac -encoding UTF-8 -d out src/eventpass/exception/*.java src/eventpass/model/*.java src/eventpass/service/*.java src/eventpass/EventPass.java test/eventpass/EventPassTest.java

echo "[EventPass] Executando suite de testes..."
echo ""
java -cp out eventpass.EventPassTest
