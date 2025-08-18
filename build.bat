@echo off
set outfile="SCP - Containment Breach Sandbox Edition.exe"
set blitzpath=.\Blitz3D\
%blitzpath%\bin\blitzcc -q -o %outfile% Main.bb
echo Patching...
4gb_patch %outfile%
del %outfile%.Backup