package com.privatetube.app.data

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

/**
 * The language of the app's own texts. [Auto] follows the system (see [forSystem]). The order here is the order of the
 * list in Settings; [id] is what is saved, so it never changes for an existing language.
 *
 * [code] is the resource language, [ownName] is how speakers of the language call it.
 */
enum class AppLanguage(val id: Int, val code: String?, val ownName: String) {
    Auto(0, null, ""),
    English(3, "en", "English"),
    Russian(1, "ru", "Русский"),
    Ukrainian(2, "uk", "Українська"),
    Spanish(4, "es", "Español"),
    Portuguese(5, "pt", "Português"),
    French(6, "fr", "Français"),
    German(7, "de", "Deutsch"),
    Italian(8, "it", "Italiano"),
    Polish(9, "pl", "Polski"),
    Dutch(10, "nl", "Nederlands"),
    Czech(11, "cs", "Čeština"),
    Japanese(12, "ja", "日本語"),
    Chinese(13, "zh", "中文");

    /** The concrete language code for the given system language code. */
    fun tag(systemLanguage: String?): String = code ?: forSystem(systemLanguage)

    companion object {
        fun fromId(id: Int?): AppLanguage = entries.firstOrNull { it.id == id } ?: Auto

        /** A language the app has is used as it is; Russian also for Kazakh, Kyrgyz and Belarusian; else English. */
        fun forSystem(system: String?): String {
            val wanted = system?.lowercase()
            if (wanted in setOf("kk", "ky", "be")) return "ru"
            return entries.firstNotNullOfOrNull { it.code?.takeIf { code -> code == wanted } } ?: "en"
        }
    }
}
/**
 * The language choice is kept in plain SharedPreferences, not DataStore: it has to be known synchronously when the
 * activity is created, before any text is shown.
 */
object LanguagePrefs {
    private const val FILE = "language"
    private const val KEY = "app_language"

    /** Set when the language was changed in Settings: after the re-creation focus returns to that button. */
    var justChanged = false

    fun get(context: Context): AppLanguage =
        AppLanguage.fromId(context.getSharedPreferences(FILE, Context.MODE_PRIVATE).getInt(KEY, 0))

    fun set(context: Context, language: AppLanguage) {
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE).edit().putInt(KEY, language.id).commit()
    }

    /** The code of the language the app uses right now. */
    fun tag(context: Context): String =
        get(context).tag(context.resources.configuration.locales[0].language)

    /** A context whose resources use the chosen language. */
    fun wrap(base: Context): Context {
        val config = Configuration(base.resources.configuration)
        config.setLocale(Locale.forLanguageTag(tag(base)))
        TvScale.apply(config, base.resources.displayMetrics)
        return base.createConfigurationContext(config)
    }
}
