@echo off
chcp 65001 > nul
echo [EventPass] Compilando codigo-fonte e testes...
if not exist out mkdir out
javac -encoding UTF-8 -d out src/eventpass/exception/*.java src/eventpass/model/*.java src/eventpass/service/*.java src/eventpass/EventPass.java test/eventpass/EventPassTest.java
if %ERRORLEVEL% NEQ 0 (
    echo [ERRO] Falha na compilacao!
    exit /b %ERRORLEVEL%
)
echo [EventPass] Executando suite de testes...
echo.
java -cp out eventpass.EventPassTest
