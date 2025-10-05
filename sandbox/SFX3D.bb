Type SFX3D
    ; Writable section

    Field EmitterEntity%
    Field Radius#, Volume#
    Field Paused% = False

    ; Read-only section

    Field Channel%
End Type

Function CreateSFX3D.SFX3D(sound%, emitter_entity%, radius# = 10, volume# = 1)
    Local ret.SFX3D = New SFX3D

    ret\Channel = PlaySound(sound)
    ChannelVolume ret\Channel, 0
    ret\EmitterEntity = emitter_entity
    ret\Radius = radius
    ret\Volume = volume

    Return ret
End Function

Function RemoveSFX3D(sound.SFX3D)
    If sound = Null Then Return

    If ChannelPlaying(sound\Channel) Then
        StopChannel sound\Channel
    End If

    Delete sound
End Function

Global SFX3DsCount%

Function UpdateSFX3Ds()
    CatchErrors("Uncaught (UpdateSFX3Ds)")

    SFX3DsCount = 0

    For sfx.SFX3D = Each SFX3D
        Local removed% = False
        If (Not ChannelPlaying(sfx\Channel)) And (Not sfx\Paused) Then RemoveSFX3D(sfx) : removed = True
        If Not removed Then
            If Not IsEntityExists(sfx\EmitterEntity) Then RemoveSFX3D(sfx) : removed = True
        End If

        If Not removed Then
            If Not (sfx\Paused Or MenuOpen Or ConsoleOpen) Then
                ResumeChannel sfx\Channel
                If sfx\Radius > 0 Then
                    Local dist# = EntityDistance(Camera, sfx\EmitterEntity) / sfx\Radius
                    
                    If sfx\Volume > 0 And dist <= sfx\Radius Then
                        Local pan# = Sin(-DeltaYaw(Camera, sfx\EmitterEntity))
                        Local vol# = sfx\Volume * (1 - dist) * SFXVolume

                        ChannelVolume sfx\Channel, vol
                        ChannelPan sfx\Channel, pan
                    Else
                        ChannelVolume sfx\Channel, 0
                    End If
                Else
                    ChannelVolume sfx\Channel, 0
                End If
            Else
                PauseChannel sfx\Channel
            End If

            SFX3DsCount = SFX3DsCount + 1
        End If
    Next

    CatchErrors("UpdateSFX3Ds")
End Function