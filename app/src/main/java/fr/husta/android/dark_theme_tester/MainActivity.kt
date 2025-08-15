package fr.husta.android.dark_theme_tester

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION_CODES.P
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.view.menu.MenuBuilder
import androidx.core.content.edit
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import com.google.android.material.snackbar.Snackbar
import fr.husta.android.dark_theme_tester.databinding.ActivityMainBinding
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity() {

    companion object {
        const val PROJECT_GITHUB_URL = "https://github.com/ghusta/android-dark-theme-tester"
        const val KEY_PREF_SAVED_DARK_MODE = "last_dark_mode"
    }

    private val TAG = "MainActivity"

    private lateinit var binding: ActivityMainBinding

    private var preferences: SharedPreferences? = null

    private var selectedTheme: Int by Delegates.observable(
        -1,
        { property, oldValue, newValue -> applyTheme(newValue) })

    override fun onCreate(savedInstanceState: Bundle?) {
        // read preferences at start
        this.preferences = getPreferences(Context.MODE_PRIVATE)
        // "Battery Saver" mode introduced in Android 5.0
        val defaultTheme = Theme.BATTERY_SAVER_OR_SYSTEM_DEFAULT
        this.selectedTheme =
            this.preferences!!.getInt(KEY_PREF_SAVED_DARK_MODE, defaultTheme)

        // https://developer.android.com/develop/ui/views/layout/edge-to-edge
//        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        binding.includedContentMain.textApiInfo.text =
            String.format(
                "API Version : %s (Android %s)",
                Build.VERSION.SDK_INT,
                Build.VERSION.RELEASE
            )

        ViewCompat.setOnApplyWindowInsetsListener(binding.appBarLayout) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            // Apply the insets as a margin to the view. This solution sets
            // only the bottom, left, and right dimensions, but you can apply whichever
            // insets are appropriate to your layout. You can also update the view padding
            // if that's more appropriate.
            view.updatePadding(
                left = insets.left,
                top = insets.top,
                right = insets.right,
            )
            // Return CONSUMED if you don't want the window insets to keep passing
            // down to descendant views.
            WindowInsetsCompat.CONSUMED
        }

        val originalFabMargin = resources.getDimensionPixelSize(R.dimen.fab_margin)
        ViewCompat.setOnApplyWindowInsetsListener(binding.fab) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            // Apply the insets as a margin to the view. This solution sets
            // only the bottom, left, and right dimensions, but you can apply whichever
            // insets are appropriate to your layout. You can also update the view padding
            // if that's more appropriate.
            view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                leftMargin = insets.left
                bottomMargin = insets.bottom + originalFabMargin
                rightMargin = insets.right + originalFabMargin
            }
            // Return CONSUMED if you don't want the window insets to keep passing
            // down to descendant views.
            WindowInsetsCompat.CONSUMED
        }

        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAnchorView(R.id.fab)
                .setAction("Action", null)
                .show()
        }
    }

    @SuppressLint("RestrictedApi")
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)

        // Attempt to force icons in overflow menu (may not work on all themes/devices perfectly)
        if (menu is MenuBuilder) {
            menu.setOptionalIconsVisible(true)
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> {
                Toast.makeText(this, "Nothing's happening here...", Toast.LENGTH_SHORT).show()
                true
            }

            R.id.action_choose_theme -> {
                // create Dialog
                val builder: AlertDialog.Builder = this.let {
                    AlertDialog.Builder(it)
                }
                builder.setTitle(R.string.action_choose_theme)
                    ?.setSingleChoiceItems(
                        R.array.themes_list, selectedTheme,
                        { dialog, which ->
                            selectedTheme = which
                            // backup preference
                            preferences?.edit {
                                this.putInt(KEY_PREF_SAVED_DARK_MODE, selectedTheme)
                            }

                            // applyTheme(selectedTheme)
                            dialog.dismiss()
                        })
                    ?.setNegativeButton(android.R.string.cancel, { dialog, id ->
                        dialog.dismiss()
                    })

                val dialogThemeChooser = builder.create()
                dialogThemeChooser.show()

                true
            }

            R.id.action_open_github_project -> {
                clickContribute()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    fun applyTheme(theme: Int) {
        // Toast.makeText(this, "Which = ${theme}", Toast.LENGTH_SHORT).show()
        Log.d(TAG, "applyTheme() : which = ${theme}")

        // See https://developer.android.com/guide/topics/ui/look-and-feel/darktheme#changing_themes_in-app
        when (selectedTheme) {
            Theme.LIGHT -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            Theme.DARK -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            Theme.BATTERY_SAVER_OR_SYSTEM_DEFAULT -> if (Build.VERSION.SDK_INT <= P) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)
            } else {
                // API 29+
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
        }
    }

    fun getSdkVersion(): Int {
        return Build.VERSION.SDK_INT
    }

    private fun clickContribute() {
        openUrlInBrowser(PROJECT_GITHUB_URL.toUri())
    }

    private fun openUrlInBrowser(uri: Uri) {
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    }

    private fun hideSystemBars() {
        val windowInsetsController =
            WindowCompat.getInsetsController(window, window.decorView)
        // Hide the system bars.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
        }
    }

}
