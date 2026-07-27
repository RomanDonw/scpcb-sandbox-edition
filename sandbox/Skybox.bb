Type Skybox
    Field Name%

    ; Brushes
    Field upside% = 0
    Field downside% = 0
    Field leftside% = 0
    Field rightside% = 0
    Field frontside% = 0
    Field backside% = 0
End Type

Global SkyboxMesh%

Const SKYBOX_SURFACE_UP% = 1, SKYBOX_SURFACE_DOWN% = 2, SKYBOX_SURFACE_LEFT% = 3, SKYBOX_SURFACE_RIGHT% = 4, SKYBOX_SURFACE_FRONT% = 5, SKYBOX_SURFACE_BACK% = 6

Function InitSkyboxMesh()
    SkyboxMesh = CreateMesh()
    HideEntity SkyboxMesh
    Local s%

    ; upside surface (1)
    s = CreateSurface(SkyboxMesh)

    AddVertex s, -1, 1, 1, 1, 0
    AddVertex s, 1, 1, 1, 0, 0
    AddVertex s, 1, 1, -1, 0, 1
    AddVertex s, -1, 1, -1, 1, 1

    AddTriangle s, 0, 1, 2
    AddTriangle s, 0, 2, 3

    ; downside surface (2)
    s = CreateSurface(SkyboxMesh)

    AddVertex s, -1, -1, -1, 1, 1
    AddVertex s, 1, -1, -1, 0, 1
    AddVertex s, 1, -1, 1, 0, 0
    AddVertex s, -1, -1, 1, 1, 0

    AddTriangle s, 0, 1, 2
    AddTriangle s, 0, 2, 3

    ; leftside surface (3)
    s = CreateSurface(SkyboxMesh)

    AddVertex s, 1, 1, -1, 1, 0
    AddVertex s, 1, 1, 1, 0, 0
    AddVertex s, 1, -1, 1, 0, 1
    AddVertex s, 1, -1, -1, 1, 1

    AddTriangle s, 0, 1, 2
    AddTriangle s, 0, 2, 3

    ; rightside surface (4)
    s = CreateSurface(SkyboxMesh)

    AddVertex s, -1, 1, 1, 1, 0
    AddVertex s, -1, 1, -1, 0, 0
    AddVertex s, -1, -1, -1, 0, 1
    AddVertex s, -1, -1, 1, 1, 1

    AddTriangle s, 0, 1, 2
    AddTriangle s, 0, 2, 3

    ; frontside surface (5)
    s = CreateSurface(SkyboxMesh)

    AddVertex s, 1, 1, 1, 1, 0
    AddVertex s, -1, 1, 1, 0, 0
    AddVertex s, -1, -1, 1, 0, 1
    AddVertex s, 1, -1, 1, 1, 1

    AddTriangle s, 0, 1, 2
    AddTriangle s, 0, 2, 3

    ; backside surface (6)
    s = CreateSurface(SkyboxMesh)

    AddVertex s, -1, 1, -1, 1, 0
    AddVertex s, 1, 1, -1, 0, 0
    AddVertex s, 1, -1, -1, 0, 1
    AddVertex s, -1, -1, -1, 1, 1

    AddTriangle s, 0, 1, 2
    AddTriangle s, 0, 2, 3

    FlipMesh SkyboxMesh
	EntityFX SkyboxMesh, 1 Or 8
	EntityOrder SkyboxMesh, 1000
End Function

Function CreateSkybox.Skybox(declfilename$, skyboxname$)
    Local ret.Skybox = New Skybox

    If FindSkybox(skyboxname) <> Null Then RuntimeError "CreateSkybox: skybox with this name already exist."
    ret\Name = skyboxname

    If FileType(declfilename) <> 1 Then RuntimeError "CreateSkybox: skybox declaration file doesn't exist."
    
    Local texsfolder$ = GetINIString(declfilename, "skybox", "textures folder", ".")
    Local texfilename$, b%
    
    If IsINIParameterExist(declfilename, "skybox", "upside texture filename") Then
        texfilename$ = texsfolder + "\" + GetINIString(declfilename, "skybox", "upside texture filename")
        If FileType(texfilename) <> 1 Then RuntimeError "CreateSkybox: declarated upside texture file doesn't exist."
        
        ret\upside = LoadBrush_Strict(texfilename, %110001)
    End If

    If IsINIParameterExist(declfilename, "skybox", "downside texture filename") Then
        texfilename$ = texsfolder + "\" + GetINIString(declfilename, "skybox", "downside texture filename")
        If FileType(texfilename) <> 1 Then RuntimeError "CreateSkybox: declarated downside texture file doesn't exist."
        
        ret\downside = LoadBrush_Strict(texfilename, %110001)
    End If

    If IsINIParameterExist(declfilename, "skybox", "leftside texture filename") Then
        texfilename$ = texsfolder + "\" + GetINIString(declfilename, "skybox", "leftside texture filename")
        If FileType(texfilename) <> 1 Then RuntimeError "CreateSkybox: declarated leftside texture file doesn't exist."
        
        ret\leftside = LoadBrush_Strict(texfilename, %110001)
    End If

    If IsINIParameterExist(declfilename, "skybox", "rightside texture filename") Then
        texfilename$ = texsfolder + "\" + GetINIString(declfilename, "skybox", "rightside texture filename")
        If FileType(texfilename) <> 1 Then RuntimeError "CreateSkybox: declarated rightside texture file doesn't exist."
        
        ret\rightside = LoadBrush_Strict(texfilename, %110001)
    End If

    If IsINIParameterExist(declfilename, "skybox", "frontside texture filename") Then
        texfilename$ = texsfolder + "\" + GetINIString(declfilename, "skybox", "frontside texture filename")
        If FileType(texfilename) <> 1 Then RuntimeError "CreateSkybox: declarated frontside texture file doesn't exist."
        
        ret\frontside = LoadBrush_Strict(texfilename, %110001)
    End If

    If IsINIParameterExist(declfilename, "skybox", "backside texture filename") Then
        texfilename$ = texsfolder + "\" + GetINIString(declfilename, "skybox", "backside texture filename")
        If FileType(texfilename) <> 1 Then RuntimeError "CreateSkybox: declarated backside texture file doesn't exist."
        
        ret\backside = LoadBrush_Strict(texfilename, %110001)
    End If

    Return ret
End Function

Function RemoveSkybox(skybox.Skybox)
    If skybox = Null Then Return

    If skybox\upside <> 0 Then FreeBrush skybox\upside
    If skybox\downside <> 0 Then FreeBrush skybox\downside
    If skybox\leftside <> 0 Then FreeBrush skybox\leftside
    If skybox\rightside <> 0 Then FreeBrush skybox\rightside
    If skybox\frontside <> 0 Then FreeBrush skybox\frontside
    If skybox\backside <> 0 Then FreeBrush skybox\backside

    Delete skybox
End Function

Function SetSkybox(skybox.Skybox)
    If skybox = Null Then Return

    If skybox\upside <> 0 Then PaintSurface GetSurface(SkyboxMesh, SKYBOX_SURFACE_UP), skybox\upside
    If skybox\downside <> 0 Then PaintSurface GetSurface(SkyboxMesh, SKYBOX_SURFACE_DOWN), skybox\downside
    If skybox\leftside <> 0 Then PaintSurface GetSurface(SkyboxMesh, SKYBOX_SURFACE_LEFT), skybox\leftside
    If skybox\rightside <> 0 Then PaintSurface GetSurface(SkyboxMesh, SKYBOX_SURFACE_RIGHT), skybox\rightside
    If skybox\frontside <> 0 Then PaintSurface GetSurface(SkyboxMesh, SKYBOX_SURFACE_FRONT), skybox\frontside
    If skybox\backside <> 0 Then PaintSurface GetSurface(SkyboxMesh, SKYBOX_SURFACE_BACK), skybox\backside
End Function

Function FindSkybox.Skybox(name$)
    For skybox.Skybox = Each Skybox
        If skybox\Name = name Then Return skybox
    Next
    Return Null
End Function