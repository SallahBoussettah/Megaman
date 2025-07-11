@echo off
echo Compiling Megaman Clone...

if not exist bin mkdir bin
javac -d bin src\*.java
if %errorlevel% neq 0 (
    echo Compilation failed!
    pause
    exit /b %errorlevel%
)
echo Running game...
java -cp bin Game
pause 