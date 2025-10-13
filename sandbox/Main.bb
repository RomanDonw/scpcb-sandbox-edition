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
    RemoveControllableNPC()
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
		;Curr106\State = 200000
		Contained106 = True
	End If

    PDCameraEffect = ReadByte(f)

    flag_maxwellcatspawned = ReadByte(f)
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
End Function

Function OnNullGame()
    PDCameraEffect = False
    flag_maxwellcatspawned = False
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

    CatchErrors("OnUpdate")
End Function

Global flag_maxwellcatspawned%

Function OnUpdateEvents()
    If PlayerRoom\RoomTemplate\Name = "start" And MaxwellCatNaturalSpawn And (Not flag_maxwellcatspawned) Then
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

Global MaxwellCatOBJ%
Global MaxwellCatLoopedThemeSound%

Global CenterPointerImage%

;Global SkyboxSky.Skybox, SkyboxSky1499.Skybox

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

    MaxwellCatLoopedThemeSound = LoadLoopedSound("sandbox\SFX\NPC\MaxwellCat\theme.ogg")

    ; ===================

    CenterPointerImage = LoadImage_Strict("sandbox\GFX\center_pointer.png")
    
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