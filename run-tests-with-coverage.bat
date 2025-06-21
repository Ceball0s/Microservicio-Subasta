@echo off
echo Ejecutando tests con cobertura JaCoCo...
call mvn clean test jacoco:report
echo.
echo Tests completados. Abriendo reporte de cobertura...
start target\site\jacoco\index.html
echo Reporte de cobertura abierto en el navegador.
pause 