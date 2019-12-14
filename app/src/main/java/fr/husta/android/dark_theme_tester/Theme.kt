package fr.husta.android.dark_theme_tester

import android.annotation.TargetApi
import android.os.Build.VERSION_CODES.LOLLIPOP

/**
 * See values in themes_list.xml
 */
object Theme {
    const val LIGHT = 0
    const val DARK = 1
    @TargetApi(LOLLIPOP)
    const val BATTERY_SAVER_OR_SYSTEM_DEFAULT = 2
}