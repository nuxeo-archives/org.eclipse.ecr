@ echo off 
::##############################################################################
:: Copyright (c) 2011 EclipseSource Inc. and others.
:: All rights reserved. This program and the accompanying materials
:: are made available under the terms of the Eclipse Public License v1.0
:: which accompanies this distribution, and is available at
:: http://www.eclipse.org/legal/epl-v10.html
:: 
:: Contributors:
::     hmalphettes - initial API and implementation
::##############################################################################
:: This scripts generates a command-line to launch equinox.
:: It uses the arguments defined in the *.ini file

setlocal enabledelayedexpansion

:: Locate the folder of the bat file.
:: Use the short path so we don't have issues with spaces.
set ECLIPSEHOME=%~sdp0

:: find the eclipse.ini file:
for /F "tokens=* delims=" %%A in ('dir /b %ECLIPSEHOME%\*.ini') do set ECLIPSE_INI=%ECLIPSEHOME%\%%A

:: the bat does not assume that we are in the installation folder
:: but the jetty code does because jetty.home=. in the config.ini
cd %ECLIPSEHOME%

goto :assemble

:findargvalue
set argname=-%1
set argfound=false
set "argvalue="
::echo arg: %argname%
for /f "eol= tokens=* delims= usebackq" %%i in (%ECLIPSE_INI%) do (
    if "!argfound!" == "true" (
        set argvalue=%%i
        goto :findargvalue_checkNotFlag
    ) else (
        if %argname%==%%i set argfound=true
    )
)
goto :eof

:findargvalue_checkNotFlag
::some arguments are either flags or parameters( -console).
::if the next line starts with a '-' then consider that a flag
set first=%argvalue:~0,1%
if %first%==- set "argvalue="
goto :eof

:findjavaopts
set argname=-vmargs
set argfound=false
set "argvalue="
for /f "eol= tokens=* delims= usebackq" %%i in (%ECLIPSE_INI%) do (
    if "!argfound!" == "true" (
        set "argvalue=!argvalue! %%i"
    ) else (
        if %argname%==%%i set argfound=true
    )
)
goto :eof


:assemble
:: console
call :findargvalue console
set "console="
if not "%argvalue%"=="" (
    set "console= -console %argvalue%"
) else (
    if %argfound%==true  set "console= -console"
)
echo the console arG %console%

:: startup:
call :findargvalue startup
set argvalue=%argValue:/=\%
set startup=%ECLIPSEHOME%%argvalue%
set install=%ECLIPSEHOME%

:: application
call :findargvalue application
set "application="
if not "%argvalue%"=="" (
    set "application= -application %argvalue%"
)

:: logback config:
set "logback=-Dlogback.configurationFile^=%ECLIPSEHOME%etc\logback.xml"

::-launcher.XXMaxPermSize
call :findargvalue -launcher.XXMaxPermSize
set "XXMaxPermSize="
if not "%argvalue%"=="" (
    set XXMaxPermSize=" -XX:MaxPermSize^=%argvalue%"
)

::vmargs
call :findjavaopts
set "vmargs=%argvalue% "

::JAVA_HOME if defined:
set java=java
if not "%JAVA_HOME%"=="" set java="%JAVA_HOME%\bin\java"

:: print the cmd:
REM Creating a Newline variable (the two blank lines are required!)
set NLM=^


set NL=^^^%NLM%%NLM%^%NLM%%NLM%

:: start Eclipse w/ java
set command=%java% -jar %startup% %vmargs% %logback% -install %install%%application%%console%%XXMaxPermSize% %*
echo %NL%Launching Equinox with: %command%%NL%
%command%
