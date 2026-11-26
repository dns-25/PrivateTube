package com.privatetube.app.core.network

/** Everything that identifies the remote site lives here, so a change of host or user-agent touches one file. */
object SiteConfig {
    /**
     * A language edition of the site: its own host returns the interface and the video titles in that language
     * (checked against the live site). The host names do not always repeat the language code.
     */
    private class Edition(val host: String, val locale: String)

    private val ENGLISH = Edition("www", "en-US")
    private val RUSSIAN = Edition("rt", "ru-RU")

    private val EDITIONS = mapOf(
        "en" to ENGLISH,
        "ru" to RUSSIAN,
        // There is no Ukrainian edition: with the app in Ukrainian the titles are shown in Russian.
        "uk" to RUSSIAN,
        "es" to Edition("es", "es-ES"),
        "pt" to Edition("pt", "pt-BR"),
        "fr" to Edition("fr", "fr-FR"),
        "de" to Edition("de", "de-DE"),
        "it" to Edition("it", "it-IT"),
        "pl" to Edition("pl", "pl-PL"),
        "nl" to Edition("nl", "nl-NL"),
        "cs" to Edition("cz", "cs-CZ"),
        "ja" to Edition("jp", "ja-JP"),
        "zh" to Edition("cn", "zh-CN"),
    )

    /** The language of the app; the site (and the titles it returns) follows it. */
    @Volatile
    var language: String = "en"

    private val edition: Edition get() = EDITIONS[language] ?: ENGLISH

    val BASE_URL: String get() = "https://${edition.host}.pornhub.com"

    val ACCEPT_LANGUAGE: String
        get() = edition.locale.let { locale ->
            if (locale == ENGLISH.locale) "en-US,en;q=0.9" else "$locale,${locale.substringBefore('-')};q=0.9,en;q=0.5"
        }

    /** The site serves a compact, plain "TV" layout to this user-agent; list pages are parsed from it. */
    const val UA_TV =
        "Mozilla/5.0 (Linux; Android 11; AFTMM) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"

    /** Desktop layout; required for the video page (it embeds the stream list) and for model pages. */
    const val UA_DESKTOP =
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"

    /**
     * A Samsung (Tizen) TV. Used for the channel list only: to this user-agent the site still serves the old TV page,
     * the one with the wide channel covers (checked 26.11.2026; the other pages only have square logos).
     */
    const val UA_SMART_TV =
        "Mozilla/5.0 (SMART-TV; LINUX; Tizen 6.0) AppleWebKit/537.36 (KHTML, like Gecko) 76.0.3809.146/6.0 TV Safari/537.36"

    fun absolute(pathOrUrl: String): String = when {
        pathOrUrl.startsWith("http://") || pathOrUrl.startsWith("https://") -> pathOrUrl
        pathOrUrl.startsWith("//") -> "https:$pathOrUrl"
        pathOrUrl.startsWith("/") -> BASE_URL + pathOrUrl
        else -> "$BASE_URL/$pathOrUrl"
    }

    private val SORT_PARAM = Regex("[?&]o=")

    /**
     * Category lists must carry an explicit sort order: the site drops the connection for `/video?c=N&page=2`
     * but serves `/video?c=N&o=mv&page=2` (the form its own TV client uses). The same order is used for page 1,
     * so that all pages of one list are consistent.
     */
    fun withDefaultSort(path: String): String =
        if (path.startsWith("/video?c=") && !SORT_PARAM.containsMatchIn(path)) "$path&o=mv" else path

    fun withPage(pathOrUrl: String, page: Int): String {
        if (page <= 1) return pathOrUrl
        val sep = if (pathOrUrl.contains('?')) '&' else '?'
        return "$pathOrUrl${sep}page=$page"
    }

    fun videoPage(viewKey: String) = "$BASE_URL/view_video.php?viewkey=$viewKey"
}

enum class Layout(val userAgent: String) {
    TV(SiteConfig.UA_TV),
    DESKTOP(SiteConfig.UA_DESKTOP),
    SMART_TV(SiteConfig.UA_SMART_TV),
}
