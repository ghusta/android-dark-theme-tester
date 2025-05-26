package fr.husta.android.dark_theme_tester

import android.os.Build.VERSION_CODES.LOLLIPOP
import androidx.annotation.RequiresApi

/**
 * See values in themes_list.xml
 */
object Theme {
    const val LIGHT = 0
    const val DARK = 1

    @RequiresApi(LOLLIPOP)
    const val BATTERY_SAVER_OR_SYSTEM_DEFAULT = 2
}