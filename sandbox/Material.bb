Const SANDBOXMATERIAL_MAX_HIT_SOUNDS% = 4
Const SANDBOXMATERIAL_MAX_TEXPATHES% = 32

Type SandboxMaterial
    Field texpathes$[SANDBOXMATERIAL_MAX_TEXPATHES], texpathescount% = 0

    Field hitsfx%[SANDBOXMATERIAL_MAX_HIT_SOUNDS], hitsfxcount% = 0
    Field EnableElectricSparkSFXOnHit% = False
End Type

Function LoadSandboxMaterials(filename$)
    CatchErrors("Uncaught (LoadSandboxMaterials)")

    If FileType(filename) <> 1 Then Return

    Local f% = ReadFile(filename)

    Local texpathes$[SANDBOXMATERIAL_MAX_TEXPATHES], texpathcount% = 0
    Local soundpathes$[SANDBOXMATERIAL_MAX_HIT_SOUNDS], soundpathcount% = 0

    Local opt_electricspark% = False

    Repeat
        Local line$ = ReadLine(f)

        If Len(line) <> 0 Then
            If Left(line, 1) = " " Then
                If soundpathcount >= SANDBOXMATERIAL_MAX_HIT_SOUNDS Then
                    CreateConsoleMsg("LoadSandboxMaterials(" + Chr(34) + filename + Chr(34) + "): declared too many hit sounds for single material.", 255, 255, 0)
                Else
                    Local soundpath$ = Trim(line)
                    If Left(soundpath, 1) = "@" Then
                        Local option$ = Right(soundpath, Max(0, Len(soundpath) - 1))

                        Select option
                            Case "ElectricSpark"
                                opt_electricspark = True
                        End Select
                    Else
                        If FileType(soundpath) <> 1 Then
                            CreateConsoleMsg("LoadSandboxMaterials(" + Chr(34) + filename + Chr(34) + "): can't find sound file " + Chr(34) + soundpath + Chr(34) + ".", 255, 255, 0)
                        Else
                            soundpathes[soundpathcount] = soundpath
                            soundpathcount = soundpathcount + 1
                        End If
                    End If
                End If
            Else
                If texpathcount >= SANDBOXMATERIAL_MAX_TEXPATHES Then
                    CreateConsoleMsg("LoadSandboxMaterials(" + Chr(34) + filename + Chr(34) + "): declared too many textures on a single material.", 255, 255, 0)
                Else
                    If FileType(line) <> 1 Then
                        CreateConsoleMsg("LoadSandboxMaterials(" + Chr(34) + filename + Chr(34) + "): can't find texture file " + Chr(34) + line + Chr(34) + ".", 255, 255, 0)
                    Else
                        texpathes[texpathcount] = Trim(line)
                        texpathcount = texpathcount + 1
                    End If
                End If
            End If

        Else
            If texpathcount > 0 And soundpathcount > 0 Then
                Local mat.SandboxMaterial = New SandboxMaterial

                For i% = 0 To texpathcount - 1
                    mat\texpathes[i] = texpathes[i]
                Next

                For i% = 0 To soundpathcount - 1
                    mat\hitsfx[i] = SafeLoadSound(soundpathes[i])
                Next

                mat\EnableElectricSparkSFXOnHit = opt_electricspark
            End If

            texpathcount = 0
            soundpathcount = 0
            opt_electricspark = False
        End If
    Until Eof(f)

    CloseFile(f)

    CatchErrors("LoadSandboxMaterials")
End Function

Function DeleteSandboxMaterial(mat.SandboxMaterial)
    If mat = Null Then Return

    For i% = 1 To mat\hitsfxcount
        FreeSound(mat\hitsfx[i - 1])
    Next

    Delete mat
End Function

Function DeleteAllSandboxMaterials()
    For mat.SandboxMaterial = Each SandboxMaterial
        DeleteSandboxMaterial(mat)
    Next
End Function

Function GetRandomSFXForTexture%(texpath$)
    For mat.SandboxMaterial = Each SandboxMaterial
        For i% = 0 To mat\texpathescount - 1
            If mat\texpathes[i] = texpath Then
                If mat\hitsfxcount <= 0 Then Return 0

                Return mat\hitsfx[Rand(0, mat\hitsfxcount - 1)]
            End If
        Next
    Next

    Return 0
End Function