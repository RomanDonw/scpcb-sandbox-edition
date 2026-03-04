@echo off

set outfile=SCP - Containment Breach Sandbox Edition.exe
set blitzpath=.\Blitz3D\
set buildfolder=.\build\

"%blitzpath%\bin\blitzcc" -q -o "%buildfolder%\%outfile%" Main.bb

if not %ERRORLEVEL% == 0 goto on_error
    echo Patching...
    "%blitzpath%\bin\4gb_patch" "%buildfolder%\%outfile%"
    del "%buildfolder%\%outfile%.Backup"

    echo Changing icon...
    "%blitzpath%\bin\icon-changer" .\logo.ico "%buildfolder%\%outfile%"

    set ERRORLEVEL=0
    goto end

:on_error
    set ERRORLEVEL=-1

:end