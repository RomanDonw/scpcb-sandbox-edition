; ====================
; CODE IN THIS FILE IS OUTDATED AND WILL BE REMOVED IN THE FUTURE.
; ====================

Type RGB
    ; R, G, B = [0, 255]
    Field R%, G%, B%
End Type

Type HSV
    ; Hue, Saturation, Value = [0.0, 1.0]
    Field Hue#, Saturation#, Value#
End Type

Function HSV2RGB.RGB(color.HSV)
    color\Hue = color\Hue Mod 1.0

    color\Saturation = ClampFloat(color\Saturation, 0.0, 1.0)
    color\Value = ClampFloat(color\Value, 0.0, 1.0)

    Local vmin# = (1 - color\Saturation) * color\Value
    Local a = (color\Value - vmin) * (((color\Hue * 360) Mod 60) / 60)
    Local vinc = vmin + a
    Local vdec = color\Value - a

    Local r#, g#, b#

    Select Int(Floor(color\Hue * 6))
        Case 0
            r = color\Value
            g = vinc
            b = vmin

        Case 1
            r = vdec
            g = color\Value
            b = vmin

        Case 2
            r = vmin
            g = color\Value
            b = vinc

        Case 3
            r = vmin
            g = vdec
            b = color\Value

        Case 4
            r = vinc
            g = vmin
            b = color\Value

        Case 5
            r = color\Value
            g = vmin
            b = vdec
    End Select

    Local ret.RGB = New RGB
    ret\R = Floor(r * 255)
    ret\G = Floor(g * 255)
    ret\B = Floor(b * 255)

    Return ret
End Function