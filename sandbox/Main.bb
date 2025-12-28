Include "sandbox\LogSystem.bb"
Include "sandbox\Misc.bb"
Include "sandbox\SFX3D.bb"
Include "sandbox\Lever.bb"
Include "sandbox\DelayedCommands.bb"
;Include "sandbox\Skybox.bb"

Const MOUSE_BUTTON_MIDDLE% = 3

Const NUMPAD_KEY_1% = 79, NUMPAD_KEY_2% = 80, NUMPAD_KEY_3% = 81
Const NUMPAD_KEY_4% = 75, NUMPAD_KEY_5% = 76, NUMPAD_KEY_6% = 77
Const NUMPAD_KEY_7% = 71, NUMPAD_KEY_8% = 72, NUMPAD_KEY_9% = 73

Const KEY_UP_ARROW% = 200, KEY_LEFT_ARROW% = 203, KEY_RIGHT_ARROW% = 205, KEY_DOWN_ARROW% = 208

Const KEY_LEFT_SHIFT% = 42
Const KEY_LEFT_ALT% = 56
Const KEY_LEFT_CONTROL% = 29
Const KEY_RIGHT_ALT% = 184
Const KEY_RIGHT_SHIFT% = 54
Const KEY_RIGHT_CONTROL% = 157
Const KEY_ENTER% = 28
Const KEY_SLASH% = 53

Global FreeCam% = False

; ===========================================================================================================================================================

Function OnGameStart()
    InitLog()
End Function

Function OnGameEnd()
    EndLog()
End Function

Function OnBeforeLoad()
    ctrl_npc = Null
    ctrl_npc_follow = False
    ctrl_npc_follow_entity = 0
End Function

Function OnBeforeSave()
    ;RemoveControllableNPC()
End Function

Function OnLoad(f%)
    GodMode = PresetGodMode
	NoClip = PresetNoclip
	NoTarget = PresetNoTarget
	NoBlinking = PresetNoBlinking
	DebugHUD = PresetDebugHUD
    InfiniteStamina = PresetInfiniteStamina

    If PresetDisableSCP106 Then
		Curr106\Idle = True
		Contained106 = True
	End If

    PDCameraEffect = ReadByte(f)

    flag_maxwellcatspawned = ReadByte(f)

    ;ShowPlayerCrowbar = False

    DoorOpenBypass = False
    CurrDoor = Null
    PlayerCrowbarUsageCooldownTimer = 0
End Function

Function OnSave(f%)
    WriteByte(f, PDCameraEffect)
    WriteByte(f, flag_maxwellcatspawned)
End Function

Function OnInitNewGame()
    GodMode = PresetGodMode
	NoClip = PresetNoclip
	NoTarget = PresetNoTarget
	NoBlinking = PresetNoBlinking
	DebugHUD = PresetDebugHUD
    InfiniteStamina = PresetInfiniteStamina

    If PresetDisableSCP106 Then
		Curr106\Idle = True
		;Curr106\State = 200000
		Contained106 = True
	End If

    PDCameraEffect = False
    flag_maxwellcatspawned = False
    DoorOpenBypass = False
    CurrDoor = Null
    PlayerCrowbarUsageCooldownTimer = 0

    ctrl_npc = Null
    ctrl_npc_follow = False
    ctrl_npc_follow_entity = 0
End Function

Function OnNullGame()
    PDCameraEffect = False
    flag_maxwellcatspawned = False
    CurrDoor = Null
    PlayerCrowbarUsageCooldownTimer = 0

    RemoveControllableNPC()
End Function

; ===========================================================================================================================================================

Global ConsoleBindNextID% = 0

Type ConsoleBind

    Field id%
    Field KeyCode%
    Field Command$

End Type

Function OnUpdate()
    CatchErrors("Uncaught (OnUpdate)")

    UpdateSFX3Ds()
    UpdateDelayedCommands()
    UpdateLevers()
    ;PositionEntity SkyboxMesh, EntityX(Camera, True), EntityY(Camera, True), EntityZ(Camera, True), True

    If KeyDown(KEY_CALL_BIND) Then ; Custom keybinds system.
        For b.ConsoleBind = Each ConsoleBind
            If KeyHit(b\KeyCode) Then
                i% = 0
                For b_.ConsoleBind = Each ConsoleBind
                    If b_\KeyCode = b\KeyCode Then
                        CreateConsoleMsg("Executing command from key bind " + b\KeyCode + " (index " + i + ")...", 255, 127, 0)
                        ExecConsole(b_\Command, True)
                        CreateConsoleMsg("Executing command from key bind " + b\KeyCode + " (index " + i + "). Done.", 255, 127, 0)
                        i = i + 1
                    End If
                Next
            End If
        Next
    End If

    ;If LayingAnimationEndPointEntity <> 0 Then
    ;    If EntityDistance(Camera, LayingAnimationEndPointEntity) > 0.01 Then
    ;        TurnEntity Camera, DeltaPitch(Camera, LayingAnimationEndPointEntity), DeltaYaw(Camera, LayingAnimationEndPointEntity), 0
    ;        MoveEntity Camera, 0, 0, 0.01
    ;    Else
    ;        FreeEntity LayingAnimationEndPointEntity : LayingAnimationEndPointEntity = 0
    ;        If LayingStandUp Then
    ;            ShowEntity Collider
    ;            Laying = False
    ;        End If
    ;    End If
    ;End If

    If PlayerCrowbar <> 0 Then
        ;Local d# = Vec3Length(EntityX(Collider, True), EntityY(Collider, True), EntityZ(Collider, True))

        ;RotateEntity PlayerCrowbar, Sin(d * PlayerCrowbarSwingXCoefficient), EntityYaw(PlayerCrowbar, False), Sin(d * PlayerCrowbarSwingZCoefficient), False
        ;Local up# = (Sin(Shake) / (20.0+CrouchState*20.0))*0.6

        ;MoveEntity PlayerCrowbar, 0, up + 0.6 + CrouchState * -0.3, 0

        ;If KeyHit(64) Then ShowPlayerCrowbar = Not ShowPlayerCrowbar

        If SelectedItem <> Null Then
            If Not SelectedItem\itemtemplate\tempname = "crowbar" Then HideEntity PlayerCrowbar
        Else
            HideEntity PlayerCrowbar
        End If

        ;If ShowPlayerCrowbar then
        ;    ShowEntity PlayerCrowbar
        ;Else
        ;    HideEntity PlayerCrowbar
        ;End If
    End If

    ;If SelectedItem <> Null Then
    ;    If SelectedItem\itemtemplate\tempname = "scp513" Then
    ;        If CountCollisions(SelectedItem\collider) > 0 And SelectedItem\state = 0 Then
    ;            SelectedItem\state = 1
    ;            CreateSFX3D(LoadTempSound("SFX\SCP\513\Bell1.ogg"), SelectedItem\collider, 5)
    ;    End If
    ;End If

    CatchErrors("OnUpdate")
End Function

Global flag_maxwellcatspawned%

Function OnUpdateEvents()
    If PlayerRoom\RoomTemplate\Name = MaxwellCatNaturalSpawnRoom And MaxwellCatNaturalSpawn And (Not flag_maxwellcatspawned) Then
        ExecConsole("maxwellcat", True)
        flag_maxwellcatspawned = True
    End If

    ;If PlayerRoom\RoomTemplate\Name = "gatea" Or (PlayerRoom\RoomTemplate\Name = "exit1" And EntityY(Collider) > 1040.0 * RoomScale) Then
    ;    CreateConsoleMsg("AT GATE A/B", 255, 0, 255)
    ;    SetSkybox(SkyboxSky)
    ;    ShowEntity SkyboxMesh
    ;
    ;Else If PlayerRoom\RoomTemplate\Name = "dimension1499" Then
    ;    SetSkybox(SkyboxSky1499)
    ;    ShowEntity SkyboxMesh
    ;
    ;Else
    ;    ;HideEntity SkyboxMesh
    ;End If

    ;If PlayerCrowbar <> 0 Then
    ;    ;TurnEntity PlayerCrowbar, 0, 0.5, 0
    ;    ;CreateConsoleMsg("Player crowbar yaw: " + EntityYaw(PlayerCrowbar), 255, 0, 255)
    ;    If KeyDown(KEY_UP_ARROW) Then TranslateEntity PlayerCrowbar, 0, 0.001, 0
    ;    If KeyDown(KEY_DOWN_ARROW) Then TranslateEntity PlayerCrowbar, 0, -0.001, 0
    ;    If KeyDown(KEY_RIGHT_ARROW) Then TranslateEntity PlayerCrowbar, 0.001, 0, 0
    ;    If KeyDown(KEY_LEFT_ARROW) Then TranslateEntity PlayerCrowbar, -0.001, 0, 0
    ;    If KeyDown(78) Then TranslateEntity PlayerCrowbar, 0, 0, 0.001
    ;    If KeyDown(74) Then TranslateEntity PlayerCrowbar, 0, 0, -0.001
    ;    If KeyHit(82) Then CreateConsoleMsg("Rel Pos: X = " + EntityX(PlayerCrowbar, False) + ", Y = " + EntityY(PlayerCrowbar, False) + ", Z = " + EntityZ(PlayerCrowbar, False), 255, 0, 255)
    ;End If

    CatchErrors("OnUpdateEvents (before cheat functions)")
    If Not CheatGameControlEnabled Return

    ; ===================================================================================================================================================

    If KeyDown(KEY_RIGHT_CONTROL) ; Right Control key (main control keys for all rooms)

        If KeyHit(NUMPAD_KEY_1) Then
            UseDoor(PlayerRoom\RoomDoors[0])
        Else If KeyHit(NUMPAD_KEY_2) Then
            UseDoor(PlayerRoom\RoomDoors[1])
        Else If KeyHit(NUMPAD_KEY_3) Then
            UseDoor(PlayerRoom\RoomDoors[2])
        Else If KeyHit(NUMPAD_KEY_4) Then
            UseDoor(PlayerRoom\RoomDoors[3])
        Else If KeyHit(NUMPAD_KEY_5) Then
            UseDoor(PlayerRoom\RoomDoors[4])
        Else If KeyHit(NUMPAD_KEY_6) Then
            UseDoor(PlayerRoom\RoomDoors[5])
        Else If KeyHit(NUMPAD_KEY_7) Then
            UseDoor(PlayerRoom\RoomDoors[6])
        End If
    End If

    If ctrl_npc <> Null Then ControllableNPCUpdate()

    CatchErrors("OnUpdateEvents (after cheat functions)")
End Function

Global test_049_currinstance.NPCs = Null
Global test_049_enabled% = False
Global test_049_target = CreatePivot()
Global test_049_movetotarget% = False, test_049_moveanimtype% = 0
Global test_049_enablespeechsfx% = True

Global test_106_currinstance.NPCs = Null
Global test_106_enabled% = False
Global test_106_target = CreatePivot()
Global test_106_movetotarget% = False
Global test_106_disable_gravity% = False

Function OnUpdateNPCs()
    If test_049_enabled Then
        If test_049_currinstance = Null Then
            test_049_currinstance = CreateNPC(NPCtype049, EntityX(Collider), EntityY(Collider) + 0.4, EntityZ(Collider), False)
            test_049_currinstance\State = 0
        End If

        n.NPCs = test_049_currinstance
        prevFrame# = n\Frame
        ;n\Speed = 0.015

        If test_049_movetotarget Then
            n\CurrSpeed = CurveValue(n\Speed, n\CurrSpeed, 10.0)

            Local targetx# = EntityX(test_049_target, True)
            Local targety# = EntityY(test_049_target, True)
            Local targetz# = EntityZ(test_049_target, True)

            If n\PathTimer <= 0 Then ; refind path
                n\PathStatus = FindPath(n, targetx, targety, targetz)
                n\PathTimer = 70 * 5
                n\PathLocation = 0
                n\State3 = 0
            EndIf

            Select n\PathStatus
                Case 0 ; too near to target point.
                    ;AnimateNPC(n, 37, 269, 0.7, False)
                    ;CreateConsoleMsg("Test 049: PathStatus 0.", 255, 0, 0)

                    PointEntity n\obj, test_049_target
                    RotateEntity n\Collider, 0, CurveAngle(EntityYaw(n\obj), EntityYaw(n\Collider), 10.0), 0
                    MoveEntity n\Collider, 0, 0, n\CurrSpeed * FPSfactor

                    ;If EntityFlatDistance(n\obj, test_049_target) <= 0.05 Then
                    If EntityDistance(n\Collider, test_049_target) <= 0.5 Then
                        test_049_movetotarget = False
                    End If

                Case 1 ; if successfully found
                    While n\Path[n\PathLocation]=Null
                        If n\PathLocation > 19 Then 
                            n\PathLocation = 0 : n\PathStatus = 0
                            Exit
                        Else
                            n\PathLocation = n\PathLocation + 1
                        EndIf
                    Wend

                    If n\Path[n\PathLocation] <> Null Then
                        ;closes doors behind him
                        If n\PathLocation>0 Then
                            If n\Path[n\PathLocation-1] <> Null Then
                                If n\Path[n\PathLocation-1]\door <> Null Then
                                    If (Not n\Path[n\PathLocation-1]\door\IsElevatorDoor) Then
                                        If EntityDistance(n\Path[n\PathLocation-1]\obj,n\Collider) > 0.3 Then
                                            If (n\Path[n\PathLocation-1]\door\MTFClose) And (n\Path[n\PathLocation-1]\door\open) And (n\Path[n\PathLocation-1]\door\buttons[0]<>0 Or n\Path[n\PathLocation-1]\door\buttons[1]<>0) Then
                                                If n\Path[n\PathLocation-1]\door\Keycard = 0 And n\Path[n\PathLocation-1]\door\Code = "" And (Not n\Path[n\PathLocation-1]\door\locked) Then UseDoor(n\Path[n\PathLocation-1]\door, False)
                                            EndIf
                                        EndIf
                                    EndIf
                                EndIf
                            EndIf
                        EndIf

                        PointEntity n\obj, n\Path[n\PathLocation]\obj
                        RotateEntity n\Collider, 0, CurveAngle(EntityYaw(n\obj), EntityYaw(n\Collider), 10.0), 0
                        MoveEntity n\Collider, 0, 0, n\CurrSpeed * FPSfactor

                        ;opens doors in front of him
                        dist2# = EntityDistance(n\Collider,n\Path[n\PathLocation]\obj)
                        If dist2 < 0.6 Then
                            Local CanInteractWithDoor% = True
                            If n\Path[n\PathLocation]\door <> Null Then
                                If (Not n\Path[n\PathLocation]\door\IsElevatorDoor)
                                    If (n\Path[n\PathLocation]\door\locked Or n\Path[n\PathLocation]\door\KeyCard<>0 Or n\Path[n\PathLocation]\door\Code<>"") And (Not n\Path[n\PathLocation]\door\open) Then
                                        CanInteractWithDoor = False
                                    Else
                                        If n\Path[n\PathLocation]\door\open = False And (n\Path[n\PathLocation]\door\buttons[0]<>0 Or n\Path[n\PathLocation]\door\buttons[1]<>0) Then
                                            UseDoor(n\Path[n\PathLocation]\door, False)
                                        EndIf
                                    EndIf
                                EndIf
                            EndIf

                            If dist2 < 0.2 And CanInteractWithDoor Then
                                n\PathLocation = n\PathLocation + 1
                            Else If dist2 < 0.5 And (Not CanInteractWithDoor) Then
                                If Int(n\State3) > 0 Then
                                    test_049_target = False
                                    CreateConsoleMsg("Test 049: Can't find available path to target point.", 255, 0, 255)
                                End If

                                n\PathTimer = 0
                                n\State3 = Int(n\State3) + 1
                            End If
                        EndIf
                        
                        ;If EntityDistance(n\Collider, n\Path[n\PathLocation]\obj) < 0.5 Then n\PathLocation = n\PathLocation + 1
                    Else
                        AnimateNPC(n, 37, 269, 0.7, False)
                    EndIf

                Case 2 ; target unreachable
                    test_049_movetotarget = False
                    CreateConsoleMsg("Test 049: Can't find path to target.", 255, 0, 0)
            End Select

            Select test_049_moveanimtype
                Case 0
                    AnimateNPC(n, Max(Min(AnimTime(n\obj), 358.0), 346), 393.0, n\CurrSpeed * 38)

                Case 1
                    AnimateNPC(n, Max(Min(AnimTime(n\obj),428.0),387), 463.0, n\CurrSpeed*38)

                Default
                    test_049_moveanimtype = 0
            End Select

            If test_049_enablespeechsfx Then
                ;Playing a sound if he hears the player
                If n\PrevState = 0 And ChannelPlaying(n\SoundChn2)=False
                    If n\Sound2 <> 0 Then FreeSound_Strict(n\Sound2)
                    If Rand(30)=1
                        n\Sound2 = LoadSound_Strict("SFX\SCP\049\Searching7.ogg")
                    Else
                        n\Sound2 = LoadSound_Strict("SFX\SCP\049\Searching"+Rand(1,6)+".ogg")
                    EndIf
                    n\SoundChn2 = LoopSound2(n\Sound2,n\SoundChn2,Camera,n\obj)
                    n\PrevState = 1
                EndIf
                
                ;Resetting the "PrevState" value randomly, to make 049 talking randomly 
                If Rand(600)=1 And ChannelPlaying(n\SoundChn2)=False Then n\PrevState = 0
                
                If n\PrevState > 1 Then n\PrevState = 1
            End If

            n\PathTimer = Max(n\PathTimer-FPSfactor,0) ; update path refind timer
        Else
            ;AnimateNPC(n, 37, 269, 0.7, False)
            n\PathTimer = 0

            Select test_049_moveanimtype
                Case 0
                    AnimateNPC(n, 37, 269, 0.7, False)
            
                Case 1
                    If AnimTime(n\obj) > 358.0 Then 
                        AnimateNPC(n, Max(Min(AnimTime(n\obj), 358.0), 346), 393.0, -n\CurrSpeed * 38)
                    Else
                        AnimateNPC(n, 37, 269, 0.7, False)
                    End If
            
                Default
                    test_049_moveanimtype = 0
            End Select

            n\CurrSpeed = CurveValue(0, n\CurrSpeed, 20.0)
        End If

        If n\CurrSpeed > 0.005 Then
            If (prevFrame < 361 And n\Frame=>361) Or (prevFrame < 377 And n\Frame=>377) Then
                PlaySound2(StepSFX(3,0,Rand(0,2)),Camera, n\Collider, 8.0, Rnd(0.8,1.0))						
            ElseIf (prevFrame < 431 And n\Frame=>431) Or (prevFrame < 447 And n\Frame=>447)
                PlaySound2(StepSFX(3,0,Rand(0,2)),Camera, n\Collider, 8.0, Rnd(0.8,1.0))
            EndIf
        EndIf
    Else
        If test_049_currinstance <> Null Then
            n.NPCs = test_049_currinstance

            RemoveNPC(test_049_currinstance) : test_049_currinstance = Null
        End If
    End If

    If test_106_enabled Then
        If test_106_currinstance = Null Then
            test_106_currinstance = CreateNPC(NPCtypeOldMan, EntityX(Collider), EntityY(Collider) + 0.4, EntityZ(Collider), False)
            test_106_currinstance\State2 = 1

            test_106_currinstance\Idle = False
            ShowEntity test_106_currinstance\obj
            HideEntity test_106_currinstance\obj2
            PositionEntity test_106_currinstance\obj, EntityX(Collider), EntityY(Collider) + 0.4, EntityZ(Collider)
        End If

        n.NPCs = test_106_currinstance

        If Rand(500) = 1 Then PlaySound2(OldManSFX(Rand(0, 2)), Camera, n\Collider)
		n\SoundChn = LoopSound2(OldManSFX(4), n\SoundChn, Camera, n\Collider, 8.0, 0.8)

        If test_106_movetotarget Then
            n\CurrSpeed = CurveValue(n\Speed, n\CurrSpeed, 10.0)

            PointEntity n\Collider, test_106_target
            MoveEntity n\Collider, 0, 0, n\CurrSpeed * FPSfactor

            If test_106_disable_gravity Then
                ResetEntity(n\Collider)
                n\DropSpeed = 0
            End If
            PositionEntity(n\obj, EntityX(n\Collider), EntityY(n\Collider) - 0.15, EntityZ(n\Collider))
            
            RotateEntity n\obj, 0, EntityYaw(n\Collider), 0
            
            ;PositionEntity(n\obj2, EntityX(n\obj), EntityY(n\obj) , EntityZ(n\obj))
            ;RotateEntity(n\obj2, 0, EntityYaw(n\Collider) - 180, 0)
            ;MoveEntity(n\obj2, 0, 8.6 * 0.11, -1.5 * 0.11)

            prevFrame# = AnimTime(n\obj)
            AnimateNPC(n, 284, 333, n\CurrSpeed*43)
            ;Animate2(n\obj, AnimTime(n\obj), 284, 333, n\CurrSpeed*43)
            If prevFrame =< 286 And n\Frame>286 Then
                PlaySound2(Step2SFX(Rand(0,2)),Camera, n\Collider, 6.0, Rnd(0.8,1.0))	
            ElseIf prevFrame=<311 And n\Frame>311.0 
                PlaySound2(Step2SFX(Rand(0,2)),Camera, n\Collider, 6.0, Rnd(0.8,1.0))
            EndIf
            
            ;If dist2 < 0.2 Then n\PathLocation = n\PathLocation + 1

            If EntityDistance(n\Collider, test_106_target) < 0.2 Then test_106_movetotarget = False
        Else
            ;AnimateNPC(n, 334, 494, 0.3)
            AnimateNPC(n, 334, 494, 0.3)

            n\CurrSpeed = CurveValue(0, n\CurrSpeed, 10.0)
        End If
    Else
        If test_106_currinstance <> Null Then RemoveNPC(test_106_currinstance) : test_106_currinstance = Null
    End If
End Function

Global MaxwellCatOBJ%
Global MaxwellCatLoopedThemeSound% = LoadLoopedSound("sandbox\SFX\NPC\MaxwellCat\theme.ogg")

Global CenterPointerImage%

;Global SkyboxSky.Skybox, SkyboxSky1499.Skybox

Global CrowbarOBJ%
Global DisketteOBJ%

Global PlayerCrowbar%  ;, ShowPlayerCrowbar% = False
Global PlayerCrowbarUsageCooldownTimer# = 0

Global CrowbarHitSFX%[2]
For i% = 0 To 1
    CrowbarHitSFX[i] = LoadSound_Strict("sandbox\SFX\crowbar\hit_" + (i + 1) + ".wav")
Next
Global CrowbarHitBodySFX%[3]
For i% = 0 To 2
    CrowbarHitBodySFX[i] = LoadSound_Strict("sandbox\SFX\crowbar\hitbody_" + (i + 1) + ".wav")
Next
Global CrowbarMissSFX% = LoadSound_Strict("sandbox\SFX\crowbar\miss.wav")

Function OnLoadEntities()
    CatchErrors("Uncaught (OnLoadEntities)")

    ;InitSkyboxMesh()

    ;SkyboxSky = CreateSkybox("sandbox\GFX\skybox_sky.ini", "Sky")
    ;SkyboxSky1499 = CreateSkybox("sandbox\GFX\skybox_1499sky.ini", "Sky 1499")

    ; ===================

    MaxwellCatOBJ = LoadMesh_Strict("sandbox\GFX\NPC\MaxwellCat\models\maxwell.b3d")
    HideEntity MaxwellCatOBJ
    ScaleMesh MaxwellCatOBJ, 0.01, 0.01, 0.01
    EntityTexture MaxwellCatOBJ, LoadTexture_Strict("sandbox\GFX\NPC\MaxwellCat\textures\maxwell.png", TEXTURE_FLAGS_PNG)

    ; ===================

    CenterPointerImage = LoadImage_Strict("sandbox\GFX\center_pointer.png")

    ; ===================

    CrowbarOBJ = LoadMesh_Strict("sandbox\GFX\crowbar\crowbar.b3d")
    HideEntity CrowbarOBJ
    ScaleMesh CrowbarOBJ, 0.1, 0.1, 0.1
    EntityFX CrowbarOBJ, 2

    Local cwbrush_head = CreateBrush()
    Local cwtex_head = LoadTexture_Strict("sandbox\GFX\crowbar\head.png")
    BrushTexture cwbrush_head, cwtex_head
    PaintSurface GetSurface(CrowbarOBJ, 1), cwbrush_head
    FreeTexture cwtex_head
    FreeBrush cwbrush_head

    Local cwbrush_cyl = CreateBrush()
    Local cwtex_cyl = LoadTexture_Strict("sandbox\GFX\crowbar\cyl.png")
    BrushTexture cwbrush_cyl, cwtex_cyl
    PaintSurface GetSurface(CrowbarOBJ, 2), cwbrush_cyl
    FreeTexture cwtex_cyl
    FreeBrush cwbrush_cyl

    ; ===================

    PlayerCrowbar = CopyEntity(CrowbarOBJ)
    EntityParent PlayerCrowbar, Camera, False
    ;TranslateEntity PlayerCrowbar, 0.1, 0, 0.2
    TranslateEntity PlayerCrowbar, PlayerCrowbarOffsetX, PlayerCrowbarOffsetY, PlayerCrowbarOffsetZ
    ;RotateEntity PlayerCrowbar, 0, 42, 0
    RotateEntity PlayerCrowbar, PlayerCrowbarPitch, PlayerCrowbarYaw, PlayerCrowbarRoll
    ;ScaleEntity PlayerCrowbar, 0.05, 0.05, 0.05
    ScaleEntity PlayerCrowbar, PlayerCrowbarScaleX, PlayerCrowbarScaleY, PlayerCrowbarScaleZ
    EntityOrder PlayerCrowbar, -500

    ; ===================

    Local cwit.ItemTemplates = New ItemTemplates
    
    cwit\obj = CopyEntity(CrowbarOBJ)
    HideEntity cwit\obj

    RotateEntity cwit\obj, 90, 0, 0

    cwit\scale = 0.15
    ScaleEntity cwit\obj, cwit\scale, cwit\scale, cwit\scale

    cwit\tempname = "crowbar"
	cwit\name = "Crowbar"
	cwit\sound = 1

    ;cwit\invimg = LoadImage_Strict("sandbox\GFX\crowbar\invimg.png")
    cwit\invimg = LoadImage_Strict(PlayerCrowbarInvImgPath)
	
    ; ===================

    DisketteOBJ = LoadMesh_Strict("sandbox\GFX\diskette\diskette.b3d")
    HideEntity DisketteOBJ
    EntityFX DisketteOBJ, 2
    EntityTexture DisketteOBJ, LoadTexture_Strict("sandbox\GFX\diskette\1.png")
    ScaleEntity DisketteOBJ, 0.031, 0.031, 0.031

    ; ===================

    itt.ItemTemplates = New ItemTemplates
    itt\obj = CopyEntity(DisketteOBJ)
    HideEntity itt\obj

    itt\tempname = "diskette"
    itt\name = "Diskette"
    itt\sound = 1
    itt\scale = 1

    itt\invimg = LoadImage_Strict("sandbox\GFX\diskette\invimg.png")
    
    CatchErrors("OnLoadEntities")
End Function

Global EntTextUI% = False

Function OnDrawGUI()
    CatchErrors("Uncaught (OnDrawGUI)")

    If EntTextUI Then
        Local cx% = GraphicsWidth() / 2
        Local cy% = GraphicsHeight() / 2

        DrawImage CenterPointerImage, cx - ImageWidth(CenterPointerImage) / 2, cy - ImageHeight(CenterPointerImage) / 2

        Local ent% = CameraPick(Camera, cx, cy)
        If ent <> 0 Then
            Color 255, 255, 255
            AASetFont ConsoleFont

            ent = PickedEntity()

            AAText cx + 5, cy + 10, "Entity Name: " + Chr(34) + EntityName(ent) + Chr(34)
            AAText cx + 5, cy + 30, "Entity Class: " + EntityClass(ent)

            AAText cx + 5, cy + 50, "Entity X: " + Chr(34) + f2s(EntityX(ent), 3) + Chr(34)
            AAText cx + 5, cy + 70, "Entity Y: " + Chr(34) + f2s(EntityY(ent), 3) + Chr(34)
            AAText cx + 5, cy + 90, "Entity Z: " + Chr(34) + f2s(EntityZ(ent), 3) + Chr(34)

            AAText cx + 205, cy + 50, "Entity Pitch: " + Chr(34) + f2s(EntityPitch(ent), 3) + Chr(34)
            AAText cx + 205, cy + 70, "Entity Yaw: " + Chr(34) + f2s(EntityYaw(ent), 3) + Chr(34)
            AAText cx + 205, cy + 90, "Entity Roll: " + Chr(34) + f2s(EntityRoll(ent), 3) + Chr(34)
        End If
    End If

    ;If KeyHit(64) Then EntTextUI = Not EntTextUI

    CatchErrors("OnDrawGUI")
End Function

Const SandboxConfigMain$ = "sandbox\config\main.ini"
Const SandboxConfigDebug$ = "sandbox\config\debug.ini"
Const SandboxConfigBinds$ = "sandbox\config\binds.ini"
Const SandboxConfigAudio$ = "sandbox\config\audio.ini"
Const SandboxConfigVisual$ = "sandbox\config\visual.ini"
Const SandboxConfigScreenshot$ = "sandbox\config\screenshot.ini"
Const SandboxConfigSCPs$ = "sandbox\config\scps.ini"
Const SandboxConfigEvents$ = "sandbox\config\events.ini"
Const SandboxConfigPlayer$ = "sandbox\config\player.ini"

Function CheckSandboxConfigFiles()
    If FileType(SandboxConfigMain) <> 1 Then RuntimeError "Can't find config file " + Chr(34) + SandboxConfigMain + Chr(34) + "."
    If FileType(SandboxConfigDebug) <> 1 Then RuntimeError "Can't find config file " + Chr(34) + SandboxConfigDebug + Chr(34) + "."
    If FileType(SandboxConfigBinds) <> 1 Then RuntimeError "Can't find config file " + Chr(34) + SandboxConfigBinds + Chr(34) + "."
    If FileType(SandboxConfigAudio) <> 1 Then RuntimeError "Can't find config file " + Chr(34) + SandboxConfigAudio + Chr(34) + "."
    If FileType(SandboxConfigVisual) <> 1 Then RuntimeError "Can't find config file " + Chr(34) + SandboxConfigVisual + Chr(34) + "."
    If FileType(SandboxConfigScreenshot) <> 1 Then RuntimeError "Can't find config file " + Chr(34) + SandboxConfigScreenshot + Chr(34) + "."
    If FileType(SandboxConfigSCPs) <> 1 Then RuntimeError "Can't find config file " + Chr(34) + SandboxConfigSCPs + Chr(34) + "."
    If FileType(SandboxConfigEvents) <> 1 Then RuntimeError "Can't find config file " + Chr(34) + SandboxConfigEvents + Chr(34) + "."
    If FileType(SandboxConfigPlayer) <> 1 Then RuntimeError "Can't find config file " + Chr(34) + SandboxConfigPlayer + Chr(34) + "."
End Function

; ===========================================================================================================================================================

;Global Laying% = False, LayingStandUp% = False, LayingAnimationEndPointEntity% = 0
;Global XBeforeLay#, YBeforeLay#, ZBeforeLay#, YawBeforeLay#
;
;Function Lay(x#, y#, z#, yaw#)
;    If Laying Then Return
;
;    XBeforeLay = EntityX(Camera)
;    YBeforeLay = EntityY(Camera)
;    ZBeforeLay = EntityZ(Camera)
;    YawBeforeLay = EntityYaw(Camera)
;
;    Laying = True
;    LayingStandUp = False
;    HideEntity Collider
;    LayingAnimationEndPointEntity = CreatePivot()
;    PositionEntity LayingAnimationEndPointEntity, x, y, z
;    RotateEntity LayingAnimationEndPointEntity, 0, yaw, 0
;End Function
;
;Function StandUp()
;    If Not Laying Then Return
;
;    LayingStandUp = True
;    LayingAnimationEndPointEntity = CreatePivot()
;    PositionEntity LayingAnimationEndPointEntity, XBeforeLay, YBeforeLay, ZBeforeLay
;    RotateEntity LayingAnimationEndPointEntity, 0, YawBeforeLay, 0
;End Function

; ===========================================================================================================================================================

Global ctrl_npc.NPCs = Null
Global ctrl_npc_follow% = False, ctrl_npc_follow_entity% = 0, ctrl_npc_follow_enable_player_rotating% = True
Global ctrl_npc_lock_movement% = False
Global ctrl_npc_access_level% = 5

Function RemoveControllableNPC()
    If ctrl_npc = Null Then Return

    OnRemoveControllableNPC()

    RemoveNPC(ctrl_npc)
    ctrl_npc = Null
End Function

Function ControllableNPCUpdate()
    If ctrl_npc_follow Then
        PositionEntity Collider, EntityX(ctrl_npc_follow_entity, True), EntityY(ctrl_npc_follow_entity, True), EntityZ(ctrl_npc_follow_entity, True)
        If ctrl_npc_follow_enable_player_rotating Then RotateEntity Collider, EntityRoll(Collider), EntityYaw(ctrl_npc\Collider), EntityPitch(Collider)
        ResetEntity Collider
    End If

    Local angle#
    If KeyDown(KEY_RIGHT_SHIFT) Then
        angle = 5 * FPSFactor
    Else
        angle = 1 * FPSFactor
    End If


    If KeyDown(KEY_RIGHT_CONTROL) Then
        If KeyHit(KEY_ENTER) Then
            UseDoor(GetNearestDoorToEntityByButtons(ctrl_npc\Collider, 1), False, True, ctrl_npc_access_level)
        End If

        ; === Movement ===

        If Not ctrl_npc_lock_movement Then
            If KeyDown(KEY_RIGHT_SHIFT) And KeyDown(KEY_UP_ARROW) Then
                ctrl_npc\State = 2
            Else If KeyDown(KEY_UP_ARROW) Then
                ctrl_npc\State = 1
            Else
                ctrl_npc\State = 0
            End If

            If KeyDown(KEY_LEFT_ARROW) Then
                TurnEntity ctrl_npc\Collider, 0, angle, 0
            End If
            If KeyDown(KEY_RIGHT_ARROW) Then
                TurnEntity ctrl_npc\Collider, 0, -angle, 0
            End If
        End If

        FlushKeys
    End If
End Function

Function OnRemoveControllableNPC()
    If ctrl_npc_follow_entity <> 0 Then
        FreeEntity ctrl_npc_follow_entity

        ctrl_npc_follow = False
        ctrl_npc_follow_entity = 0
    End If
End Function

; ================================================================================================================

Function CreateLightCone(x#, y#, z#, r%, g%, b%)
    Local lc = CopyEntity(LightConeModel)
    ScaleEntity lc, 0.01, 0.01, 0.01
    EntityColor lc, r, g, b
    EntityAlpha lc, 0.15
    EntityBlend lc, 3
    PositionEntity lc, x, y, z, True
    ;EntityParent lc, room\LightSpritesPivot[i]
    Return lc
End Function

Function Console_SetTextureForAllSCP079Instances(texture%)
    If texture <> 0 Then
        Local ctr% = 1
        For rm.Rooms = Each Rooms
            If rm\RoomTemplate\Name = "room079"
                EntityTexture rm\Objects[1], texture
                ShowEntity rm\Objects[1]

                CreateConsoleMsg("Setted screen texture of SCP-079 instance number " + Str(ctr) + ".", 0, 255, 0)

                ctr = ctr + 1
            End If
        Next
    Else
        CreateConsoleMsg("No image selected.", 255, 0, 0)
    End If
End Function