Const CAM_MODE_TRANSFORM% = 0
Const CAM_MODE_LOOKAT% = 1

Const CAM_TRANSFORM_POS% = %00000001
Const CAM_TRANSFORM_ROT% = %00000010
Const CAM_TRANSFORM_SMOOTH% = %00000100

Const CAM_LOOKAT_SMOOTH% = %00000001

Type CameraPoint
    Field NextPoint.CameraPoint = Null
    Field Mode% = -1
    Field Flags% = 0
    Field Time# = 0
    Field DelayBeforeNext# = 0

    Field X#, Y#, Z#
    Field Pitch#, Yaw#, Roll#
End Type

Global CurrCamPoint.CameraPoint = Null

Global PrevCameraX#, PrevCameraY#, PrevCameraZ#
Global PrevCameraPitch#, PrevCameraYaw#, PrevCameraRoll#

Global CurrCamPoint_timer# = -1
Global CurrCamPoint_timer2# = -1

Function StartCameraMoving(cp.CameraPoint)
    CurrCamPoint = cp
    UpdateCameraPoints__SavePrevState()

    CurrCamPoint_timer = -1
    CurrCamPoint_timer2 = -1
End Function

Function UpdateCameraPoints()
    If FPSFactor = 0 Then Return

    If CurrCamPoint_timer2 > 0 Then Goto end

    If CurrCamPoint <> Null Then
        If Not IsEntityExists(Camera) Then
            CreateConsoleMsg("Camera entity doesn't exist.", 255, 0, 0)
            StartCameraMoving(Null)
            Return
        End If

        Playable = False
        FreeCam = True

        Select CurrCamPoint\Mode
            Case CAM_MODE_TRANSFORM
                If (CurrCamPoint\Flags And CAM_TRANSFORM_POS) <> 0 And (CurrCamPoint\Flags And CAM_TRANSFORM_ROT) <> 0 Then
                    If CurrCamPoint\Time > 0 Then
                        If UpdateCameraPoints__ProcessAndCheckTimer() Then
                            UpdateCameraPoints__SetPos()
                            UpdateCameraPoints__SetRot()
                            UpdateCameraPoints__SwitchToNextPoint()
                        Else
                            UpdateCameraPoints__StepPos()
                            UpdateCameraPoints__StepRot()
                        End If
                    Else
                        UpdateCameraPoints__SetPos()
                        UpdateCameraPoints__SetRot()
                        UpdateCameraPoints__SwitchToNextPoint()
                    End If

                Else If (CurrCamPoint\Flags And CAM_TRANSFORM_POS) <> 0 Then
                    If CurrCamPoint\Time > 0 Then
                        If UpdateCameraPoints__ProcessAndCheckTimer() Then
                            UpdateCameraPoints__SetPos()
                            UpdateCameraPoints__SwitchToNextPoint()
                        Else
                            UpdateCameraPoints__StepPos()
                        End If
                    Else
                        UpdateCameraPoints__SetPos()
                        UpdateCameraPoints__SwitchToNextPoint()
                    End If

                Else If (CurrCamPoint\Flags And CAM_TRANSFORM_ROT) <> 0 Then
                    If CurrCamPoint\Time > 0 Then
                        If UpdateCameraPoints__ProcessAndCheckTimer() Then
                            UpdateCameraPoints__SetRot()
                            UpdateCameraPoints__SwitchToNextPoint()
                        Else
                            UpdateCameraPoints__StepRot()
                        End If
                    Else
                        UpdateCameraPoints__SetRot()
                        UpdateCameraPoints__SwitchToNextPoint()
                    End If
                End If

            Case CAM_MODE_LOOKAT
                If CurrCamPoint\Time > 0 Then
                    If UpdateCameraPoints__ProcessAndCheckTimer() Then
                        UpdateCameraPoints__LookAt()
                        UpdateCameraPoints__SwitchToNextPoint()
                    Else
                        UpdateCameraPoints__StepLookAt()
                    End If
                Else
                    UpdateCameraPoints__LookAt()
                    UpdateCameraPoints__SwitchToNextPoint()
                End If

            Default
                CreateConsoleMsg("Unknown camera point mode.", 255, 0, 0)
                StartCameraMoving(Null)
        End Select
    Else
        Playable = True
        FreeCam = False
    End If

    .end

    If CurrCamPoint_timer > 0 Then CurrCamPoint_timer = Max(0, CurrCamPoint_timer - ElapsedTime)
    If CurrCamPoint_timer2 > 0 Then CurrCamPoint_timer2 = Max(0, CurrCamPoint_timer2 - ElapsedTime)
End Function

Function UpdateCameraPoints__StepPos()
    Local stepX# = (CurrCamPoint\X - PrevCameraX) / CurrCamPoint\Time
    Local stepY# = (CurrCamPoint\Y - PrevCameraY) / CurrCamPoint\Time
    Local stepZ# = (CurrCamPoint\Z - PrevCameraZ) / CurrCamPoint\Time

    Local speed_mul# = 1
    If CurrCamPoint\Flags And CAM_TRANSFORM_SMOOTH Then speed_mul = (EXT_PI / 2) * Sin(180 * (CurrCamPoint_timer / CurrCamPoint\Time))
    TranslateEntity Camera, stepX * ElapsedTime * speed_mul, stepY * ElapsedTime * speed_mul, stepZ * ElapsedTime * speed_mul, True
End Function

Function UpdateCameraPoints__StepRot()
    Local stepPitch# = (CurrCamPoint\Pitch - PrevCameraPitch) / CurrCamPoint\Time
    Local stepYaw# = (CurrCamPoint\Yaw - PrevCameraYaw) / CurrCamPoint\Time
    Local stepRoll# = (CurrCamPoint\Roll - PrevCameraRoll) / CurrCamPoint\Time

    Local speed_mul# = 1
    If CurrCamPoint\Flags And CAM_TRANSFORM_SMOOTH Then speed_mul = (EXT_PI / 2) * Sin(180 * (CurrCamPoint_timer / CurrCamPoint\Time))
    TurnEntityGlobal(Camera, stepPitch * ElapsedTime * speed_mul, stepYaw * ElapsedTime * speed_mul, stepRoll * ElapsedTime * speed_mul)
End Function

Function UpdateCameraPoints__StepLookAt()
    Local p_src = CreatePivot()
    Local p_target = CreatePivot()
        PositionEntity p_src, PrevCameraX, PrevCameraY, PrevCameraZ : RotateEntity p_src, PrevCameraPitch, PrevCameraYaw, PrevCameraRoll
        PositionEntity p_target, CurrCamPoint\X, CurrCamPoint\Y, CurrCamPoint\Z
        
        Local stepPitch# = DeltaPitch(p_src, p_target) / CurrCamPoint\Time
        Local stepYaw# = DeltaYaw(p_src, p_target) / CurrCamPoint\Time

        Local speed_mul# = 1
        If CurrCamPoint\Flags And CAM_LOOKAT_SMOOTH Then speed_mul = (EXT_PI / 2) * Sin(180 * (CurrCamPoint_timer / CurrCamPoint\Time))
        TurnEntityGlobal(Camera, stepPitch * ElapsedTime * speed_mul, stepYaw * ElapsedTime * speed_mul, 0)
    FreeEntity p_target
    FreeEntity p_src
End Function

Function UpdateCameraPoints__SetPos()
    PositionEntity Camera, CurrCamPoint\X, CurrCamPoint\Y, CurrCamPoint\Z, True
End Function

Function UpdateCameraPoints__SetRot()
    RotateEntity Camera, CurrCamPoint\Pitch, CurrCamPoint\Yaw, CurrCamPoint\Roll, True
End Function

Function UpdateCameraPoints__LookAt()
    p = CreatePivot()
        PositionEntity p, CurrCamPoint\X, CurrCamPoint\Y, CurrCamPoint\Z
        PointEntity Camera, p
    FreeEntity p
End Function

Function UpdateCameraPoints__SwitchToNextPoint()
    If CurrCamPoint\DelayBeforeNext > 0 And CurrCamPoint_timer2 < 0 Then CurrCamPoint_timer2 = CurrCamPoint\DelayBeforeNext : Return

    CurrCamPoint = CurrCamPoint\NextPoint
    UpdateCameraPoints__SavePrevState()

    CurrCamPoint_timer = -1
    CurrCamPoint_timer2 = -1
End Function

Function UpdateCameraPoints__ProcessAndCheckTimer%()
    If CurrCamPoint_timer < 0 Then CurrCamPoint_timer = CurrCamPoint\Time : Return False

    Return (CurrCamPoint_timer = 0)
End Function

Function UpdateCameraPoints__SavePrevState()
    PrevCameraX = EntityX(Camera, True)
    PrevCameraY = EntityY(Camera, True)
    PrevCameraZ = EntityZ(Camera, True)

    PrevCameraPitch = EntityPitch(Camera, True)
    PrevCameraYaw = EntityYaw(Camera, True)
    PrevCameraRoll = EntityRoll(Camera, True)
End Function