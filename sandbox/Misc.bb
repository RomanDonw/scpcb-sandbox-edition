Function GetNearestDoorToEntityByButtons.Doors(obj%, max_distance# = -1)
    Local dist# = INFINITY;2 ^ 31 - 1
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

Function GetNearestDoorToPointByFrame.Doors(x#, y#, z#, max_distance# = -1)
    Local dist# = INFINITY;2 ^ 31 - 1
    Local ret.Doors = Null

    For door.Doors = Each Doors
        Local t_dist# = EntityDistanceToPoint(door\frameobj, x, y, z)
        If t_dist < dist And (t_dist <= max_distance Or max_distance < 0) Then
            dist = t_dist
            ret = door
        End If
    Next
    CatchErrors("GetNearestDoorToPointByFrame")

    Return ret
End Function

Function GetNearestDoorToEntityByFrame.Doors(obj%, max_distance# = -1)
    Local dist# = INFINITY;2 ^ 31 - 1
    Local ret.Doors = Null

    For door.Doors = Each Doors
        Local t_dist# = EntityDistance(obj, door\frameobj)
        If t_dist < dist And (t_dist <= max_distance Or max_distance < 0) Then
            dist = t_dist
            ret = door
        End If
    Next
    CatchErrors("GetNearestDoorToEntityByFrame")

    Return ret
End Function


Function GetNearestSCToEntity.SecurityCams(obj%, max_distance# = -1)
    Local dist# = INFINITY;2 ^ 31 - 1
    Local dist_# = dist
    Local ret.SecurityCams = Null

    For sc.SecurityCams = Each SecurityCams
        dist_ = EntityDistance(obj, sc\obj)
        If dist_ < dist and (dist_ <= max_distance Or max_distance < 0) Then
            dist = dist_
            ret = sc
        End If
    Next

    Return ret
End Function

Function GetNearestSCToEntityByMonitor.SecurityCams(obj%, max_distance# = -1)
    Local dist# = INFINITY;2 ^ 31 - 1
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

Function GetNearestItemToEntity.Items(obj%, max_distance# = -1)
    Local dist# = INFINITY;2 ^ 31 - 1
    Local dist_# = dist
    Local ret.Items = Null

    For it.Items = Each Items
        dist_ = EntityDistance(obj, it\collider)
        If dist_ < dist and (dist_ <= max_distance Or max_distance < 0) Then
            dist = dist_
            ret = it
        End If
    Next

    Return ret
End Function

Function ReloadDoorButtons(d.Doors)
    closest_button_index% = -1
    
    Local btn0_exists% = d\buttons[0] <> 0
    Local btn1_exists% = d\buttons[1] <> 0
    Local btn0_old_x#, btn0_old_y#, btn0_old_z#, btn0_old_pitch#, btn0_old_yaw#, btn0_old_roll#
    Local btn1_old_x#, btn1_old_y#, btn1_old_z#, btn1_old_pitch#, btn1_old_yaw#, btn1_old_roll#

    If btn0_exists Then
        btn0_old_x = EntityX(d\buttons[0], True)
        btn0_old_y = EntityY(d\buttons[0], True)
        btn0_old_z = EntityZ(d\buttons[0], True)

        btn0_old_pitch = EntityPitch(d\buttons[0], True)
        btn0_old_yaw = EntityYaw(d\buttons[0], True)
        btn0_old_roll = EntityRoll(d\buttons[0], True)
    End If

    If btn1_exists Then
        btn1_old_x = EntityX(d\buttons[1], True)
        btn1_old_y = EntityY(d\buttons[1], True)
        btn1_old_z = EntityZ(d\buttons[1], True)

        btn1_old_pitch = EntityPitch(d\buttons[1], True)
        btn1_old_yaw = EntityYaw(d\buttons[1], True)
        btn1_old_roll = EntityRoll(d\buttons[1], True)
    End If
    
    For i% = 0 To 1
        If d\buttons[i] = ClosestButton Then ClosestButton = 0 : closest_button_index = i 
        If d\buttons[i] <> 0 Then FreeEntity d\buttons[i]

		If d\Code <> "" Then 
			d\buttons[i]= CopyEntity(ButtonCodeOBJ)
			EntityFX(d\buttons[i], 1)
		Else
			If d\KeyCard > 0 Then
				d\buttons[i]= CopyEntity(ButtonKeyOBJ)
			ElseIf d\KeyCard < 0
				d\buttons[i]= CopyEntity(ButtonScannerOBJ)	
			Else
				d\buttons[i] = CopyEntity(ButtonOBJ)
			End If
		EndIf
		
		ScaleEntity(d\buttons[i], 0.03, 0.03, 0.03)
	Next

    If btn0_exists Then
        PositionEntity d\buttons[0], btn0_old_x, btn0_old_y, btn0_old_z, True
        RotateEntity d\buttons[0], btn0_old_pitch, btn0_old_yaw, btn0_old_roll, True
    End If

    If btn1_exists Then
        PositionEntity d\buttons[1], btn1_old_x, btn1_old_y, btn1_old_z, True
        RotateEntity d\buttons[1], btn1_old_pitch, btn1_old_yaw, btn1_old_roll, True
    End If

	EntityParent(d\buttons[0], d\frameobj)
	EntityParent(d\buttons[1], d\frameobj)
	EntityPickMode(d\buttons[0], 2)
	EntityPickMode(d\buttons[1], 2)

    If closest_button_index >= 0 Then ClosestButton = d\buttons[closest_button_index]
End Function

Function TakeScreenshot%(filename$)
    Return SaveBuffer(FrontBuffer(), filename)
End Function

Function LoadLoopedSound(filepath$)
    If FileType(filepath) <> 1 Then RuntimeError "Can't find file " + Chr(34) + filepath + Chr(34) + "."

    Local sound% = LoadSound(filepath)
    If sound = 0 Then RuntimeError "Can't load sound from file " + Chr(34) + filepath + Chr(34) + "."
    LoopSound sound

    Return sound
End Function

Function WaitKeyScan%()
    FlushKeys
    Repeat
        For i = 1 To 255
            If KeyHit(i) Then
                Return i
            End If
        Next
    Forever
    FlushKeys
End Function

Function EntityDistanceToPoint#(entity%, x#, y#, z#)
    Local ex# = EntityX(entity)
    Local ey# = EntityY(entity)
    Local ez# = EntityZ(entity)

    Return Abs(Sqr(x * x + y * y + z * z) - Sqr(ex * ex + ey * ey + ez * ez))
End Function

Function CalculateCharCountInString%(string$, char$)
    ctr% = 0
    
    For i% = 1 To Len(string)
        If Mid(string, i, 1) = char Then ctr = ctr + 1
    Next

    Return ctr
End Function

Function PackARGB%(r%, g%, b%, a%)
    Return ((a And 255) Shl 24) Or ((r And 255) Shl 16) Or ((g And 255) Shl 8) Or (b And 255)
End Function

Function UnpackARGBa%(argb%)
    Return (argb Shr 24) And 255
End Function

Function UnpackARGBr%(argb%)
    Return (argb Shr 16) And 255
End Function

Function UnpackARGBg%(argb%)
    Return (argb Shr 8) And 255  
End Function

Function UnpackARGBb%(argb%)
    Return argb And 255
End Function

Function bool2s$(bool%)
    If bool Then
        Return "True"
    Else
        Return "False"
    End If
End Function

Function IsEntityExists%(entity%)
    CatchErrors("Uncaught (IsEntityExists)")

    EntityName(entity)
    Return Len(ErrorLog()) = 0
End Function

Function FindItemInInventoryByName.Items(name$, deep_recursive_search% = True)
    For i% = 0 To MaxItemAmount - 1
        Local it.Items = Inventory(i)
        If it <> Null Then
            If it\itemtemplate\name = name Then Return it
            If deep_recursive_search And it\invSlots > 0 Then
                Local it2.Items = FindItemInSecondInventoryByName(it, name)
                If it2 <> Null Then Return it2
            End If
        End If
    Next
    Return Null
End Function

Function FindItemInSecondInventoryByName.Items(item.Items, name$, deep_recursive_search% = True)
    For i% = 0 To item\invSlots - 1
        Local it.Items = item\SecondInv[i]
        If it <> Null Then
            If it\itemtemplate\name = name Then Return it
            If deep_recursive_search And it\invSlots > 0 Then
                Local it2.Items = FindItemInSecondInventoryByName(it, name)
                If it2 <> Null Then Return it2
            End If
        End If
    Next
    Return Null
End Function

Function FindItemInInventoryByTemplateName.Items(tempname$, deep_recursive_search% = True)
    For i% = 0 To MaxItemAmount - 1
        Local it.Items = Inventory(i)
        If it <> Null Then
            If it\itemtemplate\tempname = tempname Then Return it
            If deep_recursive_search And it\invSlots > 0 Then
                Local it2.Items = FindItemInSecondInventoryByTemplateName(it, tempname)
                if it2 <> Null Then Return it2
            End If
        End If
    Next
    Return Null
End Function

Function FindItemInSecondInventoryByTemplateName.Items(item.Items, tempname$, deep_recursive_search% = True)
    For i% = 0 To item\invSlots - 1
        Local it.Items = item\SecondInv[i]
        If it <> Null Then
            If it\itemtemplate\tempname = tempname Then Return it
            If deep_recursive_search And it\invSlots > 0 Then
                Local it2.Items = FindItemInSecondInventoryByTemplateName(it, tempname)
                If it2 <> Null Then Return it2
            End If
        End If
    Next
    Return Null
End Function