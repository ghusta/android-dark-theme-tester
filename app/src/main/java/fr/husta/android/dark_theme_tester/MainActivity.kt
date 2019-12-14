package fr.husta.android.dark_theme_tester

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION_CODES.*
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.util.*
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity() {

    companion object {
        const val PROJECT_GITHUB_URL = "https://github.com/ghusta/android-dark-theme-tester"
        const val KEY_PREF_SAVED_DARK_MODE = "last_dark_mode"
    }

    private val TAG = "MainActivity"

    private var preferences: SharedPreferences? = null

    private var selectedTheme: Int by Delegates.observable(-1,
        { property, oldValue, newValue -> applyTheme(newValue) })

    override fun onCreate(savedInstanceState: Bundle?) {
        // read preferences at start
        this.preferences = getPreferences(Context.MODE_PRIVATE)
        // "Battery Saver" mode introduced in Android 5.0
        val defaultTheme =
            (if (Build.VERSION.SDK_INT >= LOLLIPOP) Theme.BATTERY_SAVER_OR_SYSTEM_DEFAULT else Theme.LIGHT)
        this.selectedTheme =
            this.preferences!!.getInt(KEY_PREF_SAVED_DARK_MODE, defaultTheme)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        text_main.text =
            String.format(
                "API Version : %s (Android %s)",
                Build.VERSION.SDK_INT,
                Build.VERSION.RELEASE
            )


        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
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
                val builder: AlertDialog.Builder? = this.let {
                    AlertDialog.Builder(it)
                }
                builder?.setTitle(R.string.action_choose_theme)
                    ?.setSingleChoiceItems(R.array.themes_list, selectedTheme,
                        { dialog, which ->
                            selectedTheme = which
                            // backup preference
                            val editor = preferences?.edit()
                            editor?.putInt(KEY_PREF_SAVED_DARK_MODE, selectedTheme)
                            editor?.apply()

                            // applyTheme(selectedTheme)
                            dialog.dismiss()
                        })
                    ?.setNegativeButton(android.R.string.cancel, { dialog, id ->
                        dialog.dismiss()
                    })

                val dialogThemeChooser = builder?.create()
                dialogThemeChooser?.show()

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

    fun clickContribute() {
        openUrlInBrowser(Uri.parse(PROJECT_GITHUB_URL))
    }

    fun openUrlInBrowser(uri: Uri) {
        if (Build.VERSION.SDK_INT >= KITKAT) {
            Objects.requireNonNull(uri)
        }
        val intent = Intent(Intent.ACTION_VIEW, uri)

        startActivity(intent)
    }

}
