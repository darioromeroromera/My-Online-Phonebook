@echo off
for /f "delims=" %%x in ('type .env') do (set %%x)
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-DMYSQL_ROOT_PASSWORD=%MYSQL_ROOT_PASSWORD%"