package com.wjx.android.lib_colorpicker

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Rect
import android.util.Log
import androidx.palette.graphics.Palette
import java.io.Serializable
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList

/**
 * Created with Android Studio.
 * Description:
 *
 * @author: Wangjianxian
 * @CreateDate: 2020/12/19 19:44
 */
object ColorPickerUtil {
    private const val TAG = "ColorPickerUtil"

    private const val SWATCH_LENGTH = 20

    private const val HSL_LENGTH = 3

    // HUE in hsl
    private const val HUE_INDEX = 0

    // SATURATION in hsl
    private const val SATURATION_INDEX = 1

    // LIGHTNESS in hsl
    private const val LIGHTNESS_INDEX = 2

    private val BLUE = floatArrayOf(209f, 0.9f, 0.93f)
    private const val SATURATION1 = 0.05f

    private const val SATURATION2 = 0.15f

    private const val LIGHTNESS1 = 0.15f

    private const val LIGHTNESS2 = 0.3f

    private const val CHROMATIC_HSL = 0.95f

    private lateinit var sMainHsl: FloatArray

    private const val FLOAT_COMPARE_VALUE = 1E-6F

    /**
     * Get hsl color from bitmap
     */
    fun getHslColor(context: Context?, bitmap: Bitmap?): Int {
        if (bitmap == null || context == null || context.resources == null) {
            Log.d(
                TAG,
                "getHslColor: Bitmap is null or context is null or resource is null"
            )
            return 0
        }
        val rect =
            Rect(0, 1, bitmap.width, bitmap.height - 1)
        val hsl = FloatArray(HSL_LENGTH)
        val palette = Palette.from(bitmap)
            .setRegion(rect.left, rect.top, rect.right, rect.bottom)
            .clearFilters()
            .maximumColorCount(SWATCH_LENGTH)
            .generate()
        palette?.let {
            val swatches = compare(palette.swatches)
            sMainHsl = getMutiColor(swatches)
            hsl[HUE_INDEX] = sMainHsl[HUE_INDEX]
            hsl[SATURATION_INDEX] = sMainHsl[SATURATION_INDEX]
            hsl[LIGHTNESS_INDEX] = sMainHsl[LIGHTNESS_INDEX]
            // If it is colored hsl you can display the corresponding color according to the current color range.
//            if (Math.abs(hsl[SATURATION_INDEX]) >= FLOAT_COMPARE_VALUE ||
//                Math.abs(hsl[LIGHTNESS_INDEX] - CHROMATIC_HSL) > FLOAT_COMPARE_VALUE) {
//                return handleMutiColor(hsl[HUE_INDEX], context)
//            }
            // Use demo test
            return Color.HSVToColor(hsl)
        }
        return Color.HSVToColor(hsl)
    }

    fun handleMutiColor(hueInHsl: Float, context: Context): Int {
        var hue = hueInHsl
        // Determine the current hue range and return your color,
        // you can get your colorlist from xml or other choice.
        // eg： val colorList = context.resources.obtainAttributes(R.array.)
        return 0
    }

    fun getMutiColor(swatches: List<Palette.Swatch>): FloatArray {
        var hsl = FloatArray(HSL_LENGTH)
        if (swatches == null || swatches.isEmpty()) {
            return hsl
        }
        Color.colorToHSV(swatches.get(0).rgb, hsl)
        if (isGray(hsl)) {
            for (i in 1..swatches.size) {
                Color.colorToHSV(swatches.get(i).rgb, hsl);
                // If there is colored pixel in the pixel, use it else use default blue.
                if ((hsl[SATURATION_INDEX] > SATURATION1 && hsl[LIGHTNESS_INDEX] > LIGHTNESS2) ||
                    hsl[SATURATION_INDEX] > SATURATION2 && hsl[LIGHTNESS_INDEX] > LIGHTNESS1 && hsl[2] < LIGHTNESS2
                ) {
                    return hsl;
                }
            }
            return BLUE
        } else {
            return hsl
        }
    }

    fun isGray(hslColor: FloatArray): Boolean {
        if (hslColor == null || hslColor.size != HSL_LENGTH) {
            Log.d(TAG, "isGray: Hsl is null or hsl color specification is wrong")
            return false
        }
        // The rules of judging black, white and gray.
        if ((hslColor[1] <= SATURATION1 && hslColor[2] > LIGHTNESS2) || (hslColor[1] <= SATURATION2 && hslColor[2] >= LIGHTNESS1
                    && hslColor[2] <= LIGHTNESS2) || hslColor[2] <= LIGHTNESS1
        ) {
            return true
        }
        return false
    }

    fun compare(swatches: List<Palette.Swatch>): List<Palette.Swatch> {
        var swatchesTemp = ArrayList<Palette.Swatch>()
        for (swatch in swatches) {
            swatchesTemp.add(swatch)
        }
        CompareImp(swatchesTemp).compare()
        return swatchesTemp
    }

    class CompareImp(var swatches: List<Palette.Swatch>) : Comparator<Palette.Swatch>,
        Serializable {
        val serialVersionUID = -591923510109787504L
        override fun compare(o1: Palette.Swatch, o2: Palette.Swatch) = o2.population - o1.population
        fun compare() {
            Collections.sort(swatches, this)
        }
    }
}