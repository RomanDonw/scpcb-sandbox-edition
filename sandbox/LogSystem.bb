Const LOG_CUSTOM% = -1
Const LOG_INFO% = 0
Const LOG_WARNING% = 1
Const LOG_ERROR% = 2
Const LOG_FATAL% = 3

Global CurrentLogFile%, CurrentLogFileName$

Function InitLog(filename$ = "latest.log")
    If Not DebugEnabled Then Return
    If CurrentLogFile <> 0 Then RuntimeError "[LOG SYSTEM/InitLog] Log file already opened."

    CurrentLogFile = WriteFile(filename)
    If CurrentLogFile = 0 Then RuntimeError "[LOG SYSTEM/InitLog] Log file wasn't open."

    CurrentLogFileName = filename
    WriteLine CurrentLogFile, CurrentTime() + " " + CurrentDate() + ": LOGGING STARTED."
End Function

Function EndLog%()
    If (Not DebugEnabled) Or CurrentLogFile = 0 Then Return False
    ;If CurrentLogFile = 0 Then RuntimeError "[LOG SYSTEM/EndLog] Log file wasn't open."

    WriteLine CurrentLogFile, CurrentTime() + " " + CurrentDate() + ": LOGGING ENDED."
    CloseFile CurrentLogFile
    CurrentLogFile = 0
    CurrentLogFileName = ""

    Return True
End Function

Function Log%(location$, message$, errorlevel% = LOG_INFO, errorlevel_custom$ = "UNKNOWN")
    If (Not DebugEnabled) Or CurrentLogFile = 0 Then Return False
    ;If CurrentLogFile = 0 Then RuntimeError "[LOG SYSTEM/Log] Log file wasn't open."

    Local errorlevel_str$

    Select errorlevel
        Case LOG_INFO
            errorlevel_str = "INFO"
        Case LOG_WARNING
            errorlevel_str = "WARN"
        Case LOG_ERROR
            errorlevel_str = "ERROR"
        Case LOG_FATAL
            errorlevel_str = "FATAL"
        
        Default
            errorlevel_str = errorlevel_custom
    End Select

    WriteLine CurrentLogFile, CurrentTime() + " " + CurrentDate() + " " + errorlevel_str + " [" + location + "]: " + message
    Return True
End Function

Function FlushLog%(output_file$ = "dump.log")
    If (Not DebugEnabled) Or CurrentLogFile = 0 Then Return False

    Local fname$ = CurrentLogFileName
    If Not EndLog() Then RuntimeError "[LOG SYSTEM/Log] Can't flush log from file " + Chr(34) + fname + Chr(34) + " to file " + Chr(34) + output_file + Chr(34) + "."
    CopyFile fname, output_file
    InitLog(fname)

    Return True
End Function