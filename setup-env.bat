@echo off
REM Add Maven and Java to PATH for backend development
set PATH=C:\Users\vgajj\.maven\maven-3.9.14\bin;C:\Program Files\Java\jdk-21\bin;%PATH%
echo Maven and Java paths added successfully
mvn --version
