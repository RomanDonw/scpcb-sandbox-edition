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

Function FindNearestNPCtoEntityByCollider.NPCs(ent%, max_distance# = -1)
    Local dist# = INFINITY
    Local dist_# = dist
    Local ret.NPCs = Null

    For n.NPCs = Each NPCs
        dist_ = EntityDistance(ent, n\Collider)
        If dist_ < dist And (dist_ <= max_distance Or max_distance < 0) Then
            dist = dist_
            ret = n
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
				d\buttons[i] = CopyEntity(ButtonKeyOBJ)
			ElseIf d\KeyCard < 0
				d\buttons[i] = CopyEntity(ButtonScannerOBJ)	
			Else
				d\buttons[i] = CopyEntity(ButtonOBJ)
			End If
		EndIf
		
		ScaleEntity(d\buttons[i], 0.03, 0.03, 0.03)
	Next

    If btn0_exists Then
        PositionEntity d\buttons[0], btn0_old_x, btn0_old_y, btn0_old_z, True
        RotateEntity d\buttons[0], btn0_old_pitch, btn0_old_yaw, btn0_old_roll, True
        EntityParent(d\buttons[0], d\frameobj)
        EntityPickMode(d\buttons[0], 2)
    End If

    If btn1_exists Then
        PositionEntity d\buttons[1], btn1_old_x, btn1_old_y, btn1_old_z, True
        RotateEntity d\buttons[1], btn1_old_pitch, btn1_old_yaw, btn1_old_roll, True
        EntityParent(d\buttons[1], d\frameobj)
	    EntityPickMode(d\buttons[1], 2)
    End If

    If closest_button_index >= 0 Then ClosestButton = d\buttons[closest_button_index]
End Function

Function TakeScreenshot%(filename$)
    Return SaveBuffer(FrontBuffer(), filename)
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

Function ClampFloat#(value#, min#, max#)
	If value < min Then
		Return min
	Else If value > max Then
		Return max
	End If

	Return value
End Function

Function ClampInt%(value%, min%, max%)
	If value < min Then
		Return min
	Else If value > max Then
		Return max
	End If
	
	Return value
End Function

Function int2bool%(value%)
    If value <> 0 Then Return True
    Return False
End Function

Function GetINIBool%(file$, section$, parameter$, defaultvalue% = False)
    Return int2bool(GetINIInt(file, section, parameter, defaultvalue))
End Function

Function GetINIBool2%(file$, start%, parameter$, defaultvalue% = False)
    Return int2bool(GetINIInt2(file, start, parameter, defaultvalue))
End Function

Function IsINIParameterExist%(file$, section$, parameter$)
	Local TemporaryString$ = ""
	
	Local lfile.INIFile = Null
	For k.INIFile = Each INIFile
		If k\name = Lower(file) Then
			lfile = k
			Exit
		EndIf
	Next
	
	If lfile = Null Then
		DebugLog "CREATE BANK FOR "+file
		lfile = New INIFile
		lfile\name = Lower(file)
		lfile\bank = 0
		UpdateINIFile(lfile\name)
	EndIf
	
	lfile\bankOffset = 0
	
	section = Lower(section)
	
	;While Not Eof(f)
	While lfile\bankOffset<lfile\size
		Local strtemp$ = ReadINILine(lfile)
		If Left(strtemp,1) = "[" Then
			strtemp$ = Lower(strtemp)
			If Mid(strtemp, 2, Len(strtemp)-2)=section Then
				Repeat
					TemporaryString = ReadINILine(lfile)
					If Lower(Trim(Left(TemporaryString, Max(Instr(TemporaryString, "=") - 1, 0)))) = Lower(parameter) Then
						;CloseFile f
						Return True
					EndIf
				Until (Left(TemporaryString, 1) = "[") Or (lfile\bankOffset>=lfile\size)
				
				;CloseFile f
				Return False
			EndIf
		EndIf
	Wend
	
	Return False
End Function

Function IsINIParameterExist2%(file$, start%, parameter$)
	Local TemporaryString$ = ""
	Local f% = ReadFile(file)
	
	Local n%=0
	While Not Eof(f)
		Local strtemp$ = ReadLine(f)
		n=n+1
		If n=start Then 
			Repeat
				TemporaryString = ReadLine(f)
				If Lower(Trim(Left(TemporaryString, Max(Instr(TemporaryString, "=") - 1, 0)))) = Lower(parameter) Then
					CloseFile f
					Return True
				EndIf
			Until Left(TemporaryString, 1) = "[" Or Eof(f)
			CloseFile f
			Return False
		EndIf
	Wend
	
	CloseFile f
	
	Return False
End Function

Function Vec3Length#(x#, y#, z#)
    Return Sqr(x * x + y * y + z * z)
End Function

Function InsertStr$(dest$, src$, index%)
    If index < 0 Then index = 0

    Local sbefore$ = Left(dest, index)
    Local safter$ = Right(dest, Len(dest) - Len(sbefore))

    Return sbefore + src + safter
End Function

Function GetFirstInventoryEmptySlot%()
    Local slot% = -1

    For i% = 0 To MaxItemAmount - 1
        If Inventory(i) = Null Then slot = i : Exit
    Next

    Return slot
End Function

Function FindItemTemplateByName.ItemTemplates(name$)
    Local ret.ItemTemplates = Null

    For itt.ItemTemplates = Each ItemTemplates
        If itt\name = name Then ret = itt : Exit
    Next

    Return ret
End Function

Function FindItemTemplateByTempName.ItemTemplates(tempname$)
    Local ret.ItemTemplates = Null

    For itt.ItemTemplates = Each ItemTemplates
        If itt\tempname = tempname Then ret = itt : Exit
    Next

    Return ret
End Function

Function EntityFlatXZDistance(ent_a%, ent_b%)
    Local ax# = EntityX(ent_a)
    Local az# = EntityZ(ent_a)

    Local bx# = EntityX(ent_b)
    Local bz# = EntityZ(ent_b)

    Return Abs(Sqr(ax * ax + az * az) - Sqr(bx * bx + bz * bz))
End Function

Function NOP()
    Return
End Function

Function TurnEntityGlobal(entity%, pitch#, yaw#, roll#)
    RotateEntity entity, EntityPitch(entity, True) + pitch, EntityYaw(entity, True) + yaw, EntityRoll(entity, True) + roll, True
End Function

Function FindTextureForRMeshInPool(texname$, flags% = 0)
	If FileType(RMeshTexturesFoldersPoolFilePath) <> 1 Then Return 0

	Local poolf% = ReadFile(RMeshTexturesFoldersPoolFilePath)
	Local tex% = 0

	Repeat
		Local folderpath$ = ReadLine(poolf)
		If FileType(folderpath) = 2 Then
            Local filepath$ = folderpath + "\" + texname

            If FileType(filepath) = 1 Then tex = LoadTexture_Strict(filepath, flags) : Exit
        End If
	Until Eof(poolf)
	CloseFile(poolf)

	Return tex
End Function

;Const GIVEITEMERR_OK% = 0
;Const GIVEITEMERR_FULLINV% = 1
;Const GIVEITEMERR_ITEMNOTFOUND% = 2
;
;Function GiveItem%(name_tempname$)
;
;End Function

;Function AdvReadInput$(aString$)
;	If KeyHit(211) Then
;		Return ""
;	End If
;
;	Local value% = GetKey()
;	Local length% = Len(aString$)
;
;	;If last_bksp_press_time < 0 Then last_bksp_press_time = MilliSecs()
;	
;	If value = 8 Then
;	;CreateConsoleMsg(MilliSecs(), 255, 0, 255)
;	;CreateConsoleMsg(last_bksp_press_time)
;	;CreateConsoleMsg(MilliSecs() - last_bksp_press_time, 255, 127, 0)
;	;If KeyDown(14) And MilliSecs() - last_bksp_press_time >= 200 Then ; Backspace
;		;last_bksp_press_time = MilliSecs()
;		value = 0
;		If length > 0 Then aString$ = Left(aString, length - 1)
;	;Else
;	;	value = GetKey()
;	EndIf
;
;	;If Not KeyDown(14) Then value = GetKey()
;	
;	If value = 13 Or value = 0 Then
;		Return aString$
;	ElseIf value > 0 And value < 7 Or value > 26 And value < 32 Or value = 9
;		Return aString$
;	Else
;		aString$ = aString$ + Chr(value)
;		Return aString$
;	End If
;End Function
;
;Function AdvInputBox$(x%, y%, width%, height%, Txt$, ID% = 0)
;	;TextBox(x,y,width,height,Txt$)
;	Color (255, 255, 255)
;	DrawTiledImageRect(MenuWhite, (x Mod 256), (y Mod 256), 512, 512, x, y, width, height)
;	;Rect(x, y, width, height)
;	Color (0, 0, 0)
;	
;	Local MouseOnBox% = False
;	If MouseOn(x, y, width, height) Then
;		Color(50, 50, 50)
;		MouseOnBox = True
;		If MouseHit1 Then SelectedInputBox = ID : FlushKeys
;	EndIf
;	
;	Rect(x + 2, y + 2, width - 4, height - 4)
;	Color (255, 255, 255)	
;	
;	If (Not MouseOnBox) And MouseHit1 And SelectedInputBox = ID Then SelectedInputBox = 0
;	
;	If SelectedInputBox = ID Then
;		Txt = AdvReadInput(Txt)
;		If (MilliSecs2() Mod 800) < 400 Then Rect (x + width / 2 + AAStringWidth(Txt) / 2 + 2, y + height / 2 - 5, 2, 12)
;	EndIf	
;	
;	AAText(x + width / 2, y + height / 2, Txt, True, True)
;	
;	Return Txt
;End Function