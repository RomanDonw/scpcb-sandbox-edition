Type Lever
    ; Writable section
    Field locked%

    ; Read-only section
    Field enabled%

    Field BaseObj%, LeverObj%
End Type

Function CreateLever.Lever(locked%, x#, y#, z#, pitch# = 0, yaw# = 0, roll# = 0)
    Local ret.Lever = New Lever

    ret\locked = locked
    ret\BaseObj = CopyEntity(LeverBaseOBJ)
    ret\LeverObj = CopyEntity(LeverOBJ, ret\BaseObj)

    PositionEntity ret\BaseObj, x, y, z
    RotateEntity ret\BaseObj, pitch, yaw, roll
    ScaleEntity ret\BaseObj, 0.04, 0.04, 0.04

    RotateEntity ret\LeverObj, 0, 180, 0

    EntityPickMode ret\LeverObj, 1, False
	EntityRadius ret\LeverObj, 0.1

    Return ret
End Function

Function RemoveLever(lever.Lever)
    If lever = Null Then Return

    FreeEntity lever\LeverObj
    FreeEntity lever\BaseObj

    Delete lever
End Function

Function UpdateLevers()
    For lever.Lever = Each Lever
        lever\enabled = UpdateLever(lever\LeverObj, lever\locked)
    Next
End Function