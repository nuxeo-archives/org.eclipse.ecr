@echo off

REM the Java binary
set JAVA=java

REM the class path
REM FOR %%c in (plugins\org.eclipse.equinox.launcher_*.jar) DO set JAVA_CP=%%c
set JAVA_CP=plugins\${launcher.bundle}

REM current directory
set WD=%~dp0

set JAVA_OPTS=-Dnuxeo.home=%WD%ecr -Declipse.ignoreApp=true -Dosgi.noShutdown=true

REM uncomment this to enable debug
REM set JAVA_OPTS=%JAVA_OPTS% -Xdebug -Xrunjdwp:transport=dt_socket,address=8787,server=y,suspend=n

REM The launcher arguments
set DIRS=-configuration file:configuration/ -install file:./
set ARGS=-arch x86_64 -nl en_US -consoleLog


%JAVA% %JAVA_OPTS% -classpath %JAVA_CP% org.eclipse.equinox.launcher.Main %DIRS% %ARGS% %*
