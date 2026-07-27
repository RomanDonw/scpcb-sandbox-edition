Type DelayedCommand

    Field time#
    Field command$
    Field silent%

End Type

Function CreateDelayedCommand.DelayedCommand(time#, command$, silent% = False)
    ret.DelayedCommand = New DelayedCommand

    ret\time = time
    ret\command = command
    ret\silent = silent

    Return ret
End Function

Function RemoveDelayedCommand(dc.DelayedCommand)
    If dc = Null Then Return

    Delete dc
End Function

Function UpdateDelayedCommands()
    For dc.DelayedCommand = Each DelayedCommand
        If MilliSecs() / 1000 >= dc\time Then
            command$ = dc\command
            silent% = dc\silent
            RemoveDelayedCommand(dc)

            If Not silent Then CreateConsoleMsg("Executing delayed command " + Chr(34) + command + Chr(34) + "...", 255, 127, 0)
            ExecConsole(command, True)
            If Not silent Then CreateConsoleMsg("Executing delayed command " + Chr(34) + command + Chr(34) + ". Done.", 255, 127, 0)
        End If
    Next
End Function