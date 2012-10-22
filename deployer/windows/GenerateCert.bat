@echo off
setlocal

FOR /F "eol=; tokens=2,2 delims==" %%i IN ('findstr /i "commune.certification.file.mycertificatefilepath" %1') DO set CERTPATH=%%i

IF NOT [%CERTPATH%] == [] (
	exit
) ELSE (
	set CERTPATH=%4
)

IF EXIST %CERTPATH% (
	cscript ReplaceVariables.vbs "%1" "commune.certification.file.mycertificatefilepath=%CERTPATH%" "="
	exit
)

javaw -Djava.ext.dirs=%5 -Xms64m -Xmx1024m org.ourgrid.common.util.SelfSignedSetup %2 %3 %CERTPATH% > keys.out

for /f "tokens=1,2" %%a in (keys.out) do set PUBLIC_KEY=%%a&set PRIVATE_KEY=%%b
DEL keys.out

cscript ReplaceVariables.vbs "%1" "commune.privatekey=%PRIVATE_KEY%;commune.publickey=%PUBLIC_KEY%;commune.certification.file.mycertificatefilepath=%CERTPATH%" "="

