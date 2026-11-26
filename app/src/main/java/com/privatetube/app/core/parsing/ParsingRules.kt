package com.privatetube.app.core.parsing

/**
 * Every selector and pattern that depends on the site's markup lives in this file.
 * When the site changes its HTML, fix it here; nothing above the parsing layer should need to change.
 * Selectors are deliberately loose (attribute and structure based, not tied to generated CSS classes).
 */
object ParsingRules {
    // ---- video lists ---------------------------------------------------------------------------------
    /** A video card is any link carrying a `viewkey`; this regex pulls the key out of the href. */
    val VIEW_KEY = Regex("""viewkey=([A-Za-z0-9]+)""")

    // The site has two list markups. Until November 2026 the TV user-agent got a compact "TV" layout; since then it
    // gets the same pages as a desktop browser. Both are understood, so a return of the old one breaks nothing.

    /** Tried in order; the first selector that matches restricts parsing to that part of the page. */
    val TV_LIST_SCOPES = listOf(
        "ul.sectionContent", // old TV layout
        "ul.browseVideo-tabSplit", // all videos, a category (the other list on that page is a side slider)
        "ul#videoListSearchResults",
        "ul#videoLists", // a channel
        "ul.latestThumbDesign", // the home page
    )
    val DESKTOP_LIST_SCOPES = listOf(
        "#mostRecentVideosSection", "#uploadedVideosSection", "#videoSearchResult", "#videoCategory",
        "ul.latestThumbDesign", // a model without those sections
    )

    val DURATION = Regex("""\b\d{1,2}(?::\d{2}){1,2}\b""")
    val THUMB_ATTRS = listOf("data-image", "data-mediumthumb", "data-src", "data-thumb_url", "src")
    /** The title link of a card, where the picture link itself carries no title. */
    const val CARD_TITLE_LINK = ".title a[href*=viewkey=]"

    // ---- categories -----------------------------------------------------------------------------------
    const val CATEGORY_ITEM_TV = "li[data-url^=/video?c=]"
    const val CATEGORY_ITEM = "li.catPic[data-category]"
    /** The page has a short "popular" list and then the full one; the last list is the full one. */
    const val CATEGORY_LIST = "ul.categoriesListSection"
    const val CATEGORY_LINK = ".categoryTitleWrapper > a[href]"
    /** Only list paths the app knows how to open. */
    val CATEGORY_PATH = Regex("""^(/video\?c=\d+|/categories/[A-Za-z0-9_-]+)$""")
    val CATEGORY_ID = Regex("""[?&]c=(\d+)""")

    // ---- channels -------------------------------------------------------------------------------------
    const val CHANNEL_ITEM_TV = "li[data-url^=/channels/]"
    /** The id differs between the default page and the sorted ones; the class is the same. */
    const val CHANNEL_ITEM = "ul.channelsListContent > li"
    const val CHANNEL_NAME_LINK = "a.usernameLink[href^=/channels/]"
    val CHANNEL_SLUG = Regex("""^/channels/([^/?#]+)""")
    /** The plain `/channels?page=2` answers 404; pages exist only for an explicit sort order. */
    const val CHANNEL_LIST_PATH = "/channels?o=rk"

    // ---- pornstars ------------------------------------------------------------------------------------
    const val STAR_ITEM_TV = "li[data-url^=/pornstar/]"
    /** The main list; the page also has a slider of trending names, which is skipped. */
    const val STAR_ITEM = "#pornstarListSection > li"
    const val STAR_LINK = "a.pornstarLink[href]"
    val STAR_SLUG = Regex("""^/pornstar/([^/?#]+)""")
    /** The list mixes pornstars and models; each kind has its own address. */
    val PERFORMER_PATH = Regex("""^/(pornstar|model)/([^/?#]+)""")

    // ---- video page -----------------------------------------------------------------------------------
    val FLASHVARS_START = Regex("""var\s+flashvars_\d+\s*=\s*\{""")
    const val MEDIA_DEFINITIONS_KEY = "\"mediaDefinitions\""
    /** Text markers locating the small HTML slices that hold optional metadata. */
    const val VIEWS_MARKER = "class=\"ratingInfo\""
    val CHANNEL_MARKERS = listOf("class=\"video-detailed-info", "class=\"userInfo")
    const val AUTHOR_SLICE_LENGTH = 2500
    /** The uploader is a channel, a model or a pornstar. The avatar is a link of the same kind, but without text. */
    const val AUTHOR_LINK = "a[href^=/channels/], a[href^=/model/], a[href^=/pornstar/]"
    const val AUTHOR_AVATAR = ".userAvatar img"
    /** Lists of these paths are read from the desktop layout. */
    val DESKTOP_LIST_PATHS = listOf("/pornstar/", "/model/")
}
