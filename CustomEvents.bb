Const NUMPAD_KEY_1% = 79, NUMPAD_KEY_2% = 80, NUMPAD_KEY_3% = 81
Const NUMPAD_KEY_4% = 75, NUMPAD_KEY_5% = 76, NUMPAD_KEY_6% = 77
Const NUMPAD_KEY_7% = 71, NUMPAD_KEY_8% = 72, NUMPAD_KEY_9% = 73

Const KEY_UP_ARROW% = 200, KEY_LEFT_ARROW% = 203, KEY_RIGHT_ARROW% = 205, KEY_DOWN_ARROW% = 208

Const KEY_LEFT_ALT% = 56
Const KEY_RIGHT_SHIFT% = 54
Const KEY_RIGHT_CONTROL% = 157
Const KEY_ENTER% = 28
Const KEY_SLASH% = 53

Local event173_fixedblink% = False


Type ConsoleBind

    Field KeyCode%
    Field Command$

End Type

; ===========================================================================================================================================================

Function OnBeforeLoad()
    ctrl_npc = Null
    ctrl_npc_follow = False
    ctrl_npc_follow_entity = 0
End Function

Function OnBeforeSave()
    RemoveControllableNPC()
End Function

; ===========================================================================================================================================================


;Global debug_axises_center = 0
;Global debug_axis_x = 0, debug_axis_y = 0, debug_axis_z = 0

;cube = CreateCube(Camera)
;MoveEntity cube, 0, 1, 0
;EntityColor cube, 255, 0, 255

Function UpdateCustomEvents()
    ;If CurrMountedEntity <> 0 Then
    ;    PositionEntity CurrMountedEntity, PlayerRoom\x - EntityX(Camera), EntityY(Camera), EntityZ(Camera)
    ;    CreateConsoleMsg(Str(EntityX(CurrMountedEntity)) + " " + Str(EntityY(CurrMountedEntity)) + " " + Str(EntityZ(CurrMountedEntity)), 255, 0, 255)
    ;    ;RotateEntity CurrMountedEntity, EntityRoll(Collider), EntityYaw(Collider), EntityPitch(Collider)
    ;    ;ResetEntity CurrMountedEntity
    ;End If

    ;If debug_axises_center = 0 Then
    ;    debug_axises_center = CreatePivot(Collider)
    ;    MoveEntity axises_center, 0, 0, 1
    ;
    ;
    ;    debug_axis_x = CreateCube(debug_axises_center)
    ;    MoveEntity debug_axis_x, 1, 0, 0
    ;    ScaleEntity debug_axis_x, 0.1, 0.1, 0.1
    ;    EntityColor debug_axis_x, 255, 0, 0
    ;
    ;    debug_axis_y = CreateCube(debug_axises_center)
    ;    MoveEntity debug_axis_y, 0, 1, 0
    ;    ScaleEntity debug_axis_y, 0.1, 0.1, 0.1
    ;    EntityColor debug_axis_y, 0, 255, 0
    ;
    ;    debug_axis_z = CreateCube(axises_center)
    ;    MoveEntity debug_axis_z, 0, 0, 1
    ;    ScaleEntity debug_axis_z, 0.1, 0.1, 0.1
    ;    EntityColor debug_axis_z, 0, 0, 255
    ;End If

    ;If DebugHUD And debug_axis_x <> 0 And debug_axis_y <> 0 And debug_axis_z <> 0 Then
    ;    ShowEntity debug_axis_x
    ;    ShowEntity debug_axis_y
    ;    ShowEntity debug_axis_z
    ;
    ;    RotateEntity debug_axis_x, 0, 0, 0, True
    ;    RotateEntity debug_axis_y, 0, 0, 0, True
    ;    RotateEntity debug_axis_z, 0, 0, 0, True
    ;Else
    ;    HideEntity debug_axis_x
    ;    HideEntity debug_axis_y
    ;    HideEntity debug_axis_z
    ;End If

    ;CatchErrors("DebugHUD")

    If Not CheatGameControlEnabled Return

    If ctrl_npc <> Null Then ControllableNPCUpdate()

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

        ;FlushKeys

    Else If KeyDown(KEY_CALL_BIND) Then ; Custom keybinds system.
        For b.ConsoleBind = Each ConsoleBind
            If KeyHit(b\KeyCode) Then
                CreateConsoleMsg("Executing command from key bind " + b\KeyCode + "...", 255, 127, 0)
                ExecConsole(b\Command, True)
                CreateConsoleMsg("Executing command from key bind " + b\KeyCode + ". Done.", 255, 127, 0)
            End If
        Next

        ;FlushKeys

    ;Else ; Custom control keys for special rooms
    ;
    ;    Select PlayerRoom\RoomTemplate\Name
    ;        Case "room079"
    ;            Custom079EventUpdate(PlayerRoom)
    ;
    ;        Case "173":
    ;            If (Not event173_fixedblink) And KeyHit(NUMPAD_KEY_1) Then
    ;                CameraFogMode(Camera, 0)
	; 	            AmbientLight (140, 140, 140)
	;				HideEntity(Fog)
	;				
	;				LightVolume = 4.0
	;				TempLightVolume = 4.0	
    ;
    ;                event173_fixedblink = True
    ;            End If
    ;
    ;
    ;        ;Case "room2tunnel"
    ;        ;    If KeyHit(79) Then
    ;        ;        UseDoor(PlayerRoom\RoomDoors[0])
    ;        ;    Else If KeyHit(80) Then
    ;        ;        UseDoor(PlayerRoom\RoomDoors[2])
    ;        ;    End If
    ;
    ;    End Select

    End If

    ;FlushKeys
End Function

; ===========================================================================================================================================================

Global ctrl_npc.NPCs = Null
Global ctrl_npc_follow% = False, ctrl_npc_follow_entity% = 0, ctrl_npc_follow_enable_player_rotating% = True
Global ctrl_npc_lock_movement% = False

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
            UseDoor(GetNearestDoorToEntityByButtons(ctrl_npc\Collider, 1), False, True, 5)
        ;Else If KeyHit(37) ; 'K' key
        ;    If ctrl_npc\State <> 6 Then
        ;        ctrl_npc\State = 6 ; "Kill" ctrl_npc
        ;        ctrl_npc_lock_movement = True
        ;    Else
        ;        ctrl_npc\State = 0 ; "Revive" ctrl_npc
        ;        ctrl_npc_lock_movement = False
        ;    End If
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

; ===========================================================================================================================================================

;Const SCP079_SCREEN_WIDTH% = 256, SCP079_SCREEN_HEIGHT% = 256
;Global custom_texture_scp079 = CreateTexture(SCP079_SCREEN_WIDTH, SCP079_SCREEN_HEIGHT)
;Global render_custom_texture_scp079% = False
;
;Function Custom079EventUpdate(room.Rooms)
;    If KeyHit(208) Then
;        HideEntity(room\Objects[1])
;        If render_custom_texture_scp079 Then SCP079OnFreeScreen()
;
;    Else If KeyHit(203) Then
;        EntityTexture(room\Objects[1], OldAiPics(0))
;	    ShowEntity (room\Objects[1])
;        If render_custom_texture_scp079 Then SCP079OnFreeScreen()
;
;    Else If KeyHit(205) Then
;        EntityTexture(room\Objects[1], OldAiPics(1))
;	    ShowEntity (room\Objects[1])
;        If render_custom_texture_scp079 Then SCP079OnFreeScreen()
;
;    Else If KeyHit(KEY_UP_ARROW) Then
;        EntityTexture(room\Objects[1], LoadTexture_Strict("GFX\video\bsod.png"))
;
;        ;EntityTexture(room\Objects[1], custom_texture_scp079)
;        ;SCP079OnInitScreen(room)
;
;        ShowEntity (room\Objects[1])
;        
;
;    ;Else If KeyHit(53) Then
;    ;    Local vid = BlitzMovie_OpenDecodeToTexture("GFX\video\test.mp4", custom_texture_scp079, True)
;    ;    vid = BlitzMovie_Play()
;
;    Else If KeyHit(82) and room\SoundCHN <> 0 Then
;        StopStream_Strict(room\SoundCHN)
;        room\SoundCHN = 0
;    Else If KeyHit(79) and room\SoundCHN = 0 Then
;        room\SoundCHN = StreamSound_Strict ("SFX\SCP\079\Broadcast1.ogg", SFXVolume, 1)
;    Else If KeyHit(80) and room\SoundCHN = 0 Then
;        room\SoundCHN = StreamSound_Strict ("SFX\SCP\079\Broadcast2.ogg", SFXVolume, 1)
;    Else If KeyHit(81) and room\SoundCHN = 0 Then
;        room\SoundCHN = StreamSound_Strict ("SFX\SCP\079\Broadcast3.ogg", SFXVolume, 1)
;    Else If KeyHit(75) and room\SoundCHN = 0 Then
;        room\SoundCHN = StreamSound_Strict ("SFX\SCP\079\Broadcast4.ogg", SFXVolume, 1)
;    Else If KeyHit(76) and room\SoundCHN = 0 Then
;        room\SoundCHN = StreamSound_Strict ("SFX\SCP\079\Broadcast5.ogg", SFXVolume, 1)
;    Else If KeyHit(77) and room\SoundCHN = 0 Then
;        room\SoundCHN = StreamSound_Strict ("SFX\SCP\079\Broadcast6.ogg", SFXVolume, 1)
;    Else If KeyHit(71) and room\SoundCHN = 0 Then
;        room\SoundCHN = StreamSound_Strict ("SFX\SCP\079\Broadcast7.ogg", SFXVolume, 1)
;    Else If KeyHit(72) and room\SoundCHN = 0 Then
;        room\SoundCHN = StreamSound_Strict ("SFX\SCP\079\Speech.ogg", SFXVolume, 1)
;
;    End If
;
;    If render_custom_texture_scp079 Then
;        SetBuffer TextureBuffer(custom_texture_scp079)
;        
;        SCP079RenderScreen()
;
;        SetBuffer BackBuffer()
;    End If
;End Function
;
;
;Function SCP079OnInitScreen(room.Rooms)
;    render_custom_texture_scp079 = True
;End Function
;
;Function SCP079OnFreeScreen()
;    render_custom_texture_scp079 = False
;End Function
;
;Function SCP079RenderScreen()
;    ClsColor 0, 0, 255
;    Cls
;    
;    Color 0, 0, 0
;    Rect 0, 0, SCP079_SCREEN_WIDTH, 10, True
;    Color 255, 255, 255
;    AASetFont ConsolasFont
;    AAText 50, 50, "Hello Niger!"
;End Function

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

Function GetNearestDoorToEntityByButtons.Doors(obj, max_distance# = -1)
    Local dist# = 2 ^ 31 - 1
    Local ret.Doors = Null

    For door.Doors = Each Doors
        For btn_index% = 0 To 1
            Local btn = door\buttons[btn_index]

            If btn <> 0 Then
                Local t_dist# = EntityDistance(obj, btn)
                If t_dist < dist And (t_dist <= max_distance Or max_distance < 0) Then
                    dist = t_dist
                    ret = door
                End If
            End If
        Next
    Next
    CatchErrors("GetNearestDoorToEntityByButtons")

    Return ret
End Function


Function GetNearestSCToEntity.SecurityCams(obj, max_distance# = -1)
    Local dist# = 2 ^ 31 - 1
    Local dist_# = dist
    Local ret.SecurityCams = Null

    For sc.SecurityCams = Each SecurityCams
        If sc\Screen Then
            dist_ = EntityDistance(obj, sc\obj)
            If dist_ < dist and (dist_ <= max_distance Or max_distance < 0) Then
                dist = dist_
                ret = sc
            End If
        End If
    Next

    Return ret
End Function

Function GetNearestSCToEntityByMonitor.SecurityCams(obj, max_distance# = -1)
    Local dist# = 2 ^ 31 - 1
    Local dist_# = dist
    Local ret.SecurityCams = Null

    For sc.SecurityCams = Each SecurityCams
        If sc\Screen Then
            dist_ = EntityDistance(obj, sc\ScrObj)
            If dist_ < dist and (dist_ <= max_distance Or max_distance < 0) Then
                dist = dist_
                ret = sc
            End If
        End If
    Next

    Return ret
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

; ========================================================================================================================================================

Function WaitKeyScan()
    FlushKeys
    Repeat
        For i = 1 To 255
            If KeyHit(i) Then
                ;FlushKeys
                Return i
            End If
        Next
    Forever
    FlushKeys
End Function