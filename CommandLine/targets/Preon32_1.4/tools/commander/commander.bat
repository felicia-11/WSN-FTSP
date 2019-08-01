@ECHO OFF

SET THIS_DIR=%~dp0
SET CP=%THIS_DIR%/*

java -cp "%CP%" "-Djna.nosys=true" com.virtenio.commander.Commander %*