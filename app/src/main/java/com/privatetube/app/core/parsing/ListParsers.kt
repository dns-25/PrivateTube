package com.privatetube.app.core.parsing

import com.privatetube.app.core.network.SiteConfig
import com.privatetube.app.domain.CategoryItem
import com.privatetube.app.domain.ChannelItem
import com.privatetube.app.domain.StarItem
import com.privatetube.app.domain.VideoItem
import org.jsoup.Jsoup
import org.jsoup.nodes.Element

private fun Element.thumb(): String? {
    val img = selectFirst("img") ?: return null
    for (attr in ParsingRules.THUMB_ATTRS) {
        val raw = img.attr(attr).trim()
        if (raw.isNotEmpty() && !raw.startsWith("data:")) return SiteConfig.absolute(raw)
    }
    return null
}

private fun String?.clean(): String? = this?.replace(Regex("\\s+"), " ")?.trim()?.takeIf { it.isNotEmpty() }

/** Parses video cards from both the TV layout and the desktop layout. Returns an empty list for pages without videos. */
object VideoListParser {
    fun parse(html: String, scopes: List<String> = ParsingRules.TV_LIST_SCOPES): List<VideoItem> {
        val doc = Jsoup.parse(html, SiteConfig.BASE_URL)
        val roots = scopes.firstNotNullOfOrNull { sel -> doc.select(sel).takeIf { it.isNotEmpty() } }
            ?: listOf(doc.body())
        val byKey = LinkedHashMap<String, VideoItem>()
        for (root in roots) {
            for (anchor in root.select("a[href*=viewkey=]")) {
                val key = ParsingRules.VIEW_KEY.find(anchor.attr("href"))?.groupValues?.get(1) ?: continue
                val card = anchor.closest("li") ?: anchor.parent() ?: anchor
                val title = (anchor.attr("title").clean()
                    // Before the looser rule below: a wrapper of the title also holds the uploader and the views.
                    ?: card.selectFirst(ParsingRules.CARD_TITLE_LINK)?.text().clean()
                    ?: card.selectFirst("[class*=title]")?.text().clean()
                    ?: card.selectFirst("img")?.attr("alt").clean()) ?: continue
                val thumb = card.thumb() ?: anchor.thumb()
                val duration = card.select("[class*=duration]")
                    .firstNotNullOfOrNull { ParsingRules.DURATION.find(it.text())?.value }
                    ?: ParsingRules.DURATION.find(card.text())?.value
                val item = VideoItem(key, title, thumb, duration)
                val old = byKey[key]
                // A key can appear twice in a card (image link + title link); keep the richer record.
                if (old == null || (old.thumbUrl == null && item.thumbUrl != null) ||
                    (old.duration == null && item.duration != null)
                ) {
                    byKey[key] = item.copy(
                        thumbUrl = item.thumbUrl ?: old?.thumbUrl,
                        duration = item.duration ?: old?.duration,
                    )
                }
            }
        }
        return byKey.values.toList()
    }
}

object CategoryParser {
    fun parse(html: String): List<CategoryItem> {
        val doc = Jsoup.parse(html, SiteConfig.BASE_URL)
        return parseTv(doc).ifEmpty { parseSite(doc) }
    }

    private fun parseTv(doc: Element): List<CategoryItem> {
        val seen = LinkedHashMap<String, CategoryItem>()
        for (li in doc.select(ParsingRules.CATEGORY_ITEM_TV)) {
            val path = li.attr("data-url")
            val id = ParsingRules.CATEGORY_ID.find(path)?.groupValues?.get(1) ?: continue
            val name = li.selectFirst(".catName")?.text().clean()
                ?: li.selectFirst("img")?.attr("title").clean()
                ?: li.selectFirst("img")?.attr("alt").clean()
                ?: continue
            seen.putIfAbsent(id, CategoryItem(id, name, path, li.thumb(), li.selectFirst("var")?.text().clean()))
        }
        return seen.values.toList()
    }

    private fun parseSite(doc: Element): List<CategoryItem> {
        val root = doc.select(ParsingRules.CATEGORY_LIST).lastOrNull() ?: doc
        val seen = LinkedHashMap<String, CategoryItem>()
        for (li in root.select(ParsingRules.CATEGORY_ITEM)) {
            val id = li.attr("data-category").trim().takeIf { it.isNotEmpty() } ?: continue
            val link = li.selectFirst(ParsingRules.CATEGORY_LINK) ?: continue
            val path = link.attr("href").trim().takeIf { ParsingRules.CATEGORY_PATH.matches(it) } ?: continue
            val name = link.selectFirst("strong")?.text().clean()
                ?: link.ownText().clean()
                ?: continue
            seen.putIfAbsent(id, CategoryItem(id, name, path, li.thumb(), link.selectFirst("var")?.text().clean()))
        }
        return seen.values.toList()
    }
}

object ChannelParser {
    fun parse(html: String): List<ChannelItem> {
        val doc = Jsoup.parse(html, SiteConfig.BASE_URL)
        return parseTv(doc).ifEmpty { parseSite(doc) }
    }

    private fun parseTv(doc: Element): List<ChannelItem> {
        val seen = LinkedHashMap<String, ChannelItem>()
        for (li in doc.select(ParsingRules.CHANNEL_ITEM_TV)) {
            val path = li.attr("data-url")
            val slug = ParsingRules.CHANNEL_SLUG.find(path)?.groupValues?.get(1) ?: continue
            val name = li.selectFirst(".title")?.text().clean()
                ?: li.selectFirst("img")?.attr("alt").clean() ?: continue
            // The stat lines ("8.5M Subscribers", "4.9K Videos", ...) are worded per site language, so they are
            // recognised by shape (a leading number in a <span>) and position, not by their text.
            val stats = li.select(".descriptionContainer div")
                .filter { div ->
                    div.children().none { it.tagName() == "div" } &&
                        div.children().any { it.tagName() == "span" && !it.hasClass("title") }
                }
                .map { it.text().clean().orEmpty() }
            seen.putIfAbsent(
                slug,
                ChannelItem(
                    slug = slug,
                    name = name,
                    path = path,
                    coverUrl = li.image(".cover img"),
                    avatarUrl = li.image(".avatar img"),
                    subscribers = stats.getOrNull(0),
                    videos = stats.getOrNull(1),
                ),
            )
        }
        return seen.values.toList()
    }

    private fun parseSite(doc: Element): List<ChannelItem> {
        val seen = LinkedHashMap<String, ChannelItem>()
        for (li in doc.select(ParsingRules.CHANNEL_ITEM)) {
            val link = li.selectFirst(ParsingRules.CHANNEL_NAME_LINK) ?: continue
            val slug = ParsingRules.CHANNEL_SLUG.find(link.attr("href"))?.groupValues?.get(1) ?: continue
            val name = link.text().clean() ?: continue
            // "<li class=info>Subscribers<span>8.5M</span></li>": subscribers, videos, views - in that order.
            val stats = li.select("li.info").map { info ->
                listOfNotNull(info.ownText().clean(), info.selectFirst("span")?.text().clean()).joinToString(" ").clean()
            }
            seen.putIfAbsent(
                slug,
                ChannelItem(
                    slug = slug,
                    name = name,
                    path = "/channels/$slug/videos",
                    coverUrl = null, // this layout has no cover picture
                    avatarUrl = li.image(".avatar img"),
                    subscribers = stats.getOrNull(0),
                    videos = stats.getOrNull(1),
                ),
            )
        }
        return seen.values.toList()
    }

    private fun Element.image(selector: String): String? =
        selectFirst(selector)?.attr("src")?.takeIf { it.isNotBlank() && !it.startsWith("data:") }?.let(SiteConfig::absolute)
}

object StarParser {
    fun parse(html: String): List<StarItem> {
        val doc = Jsoup.parse(html, SiteConfig.BASE_URL)
        return parseTv(doc).ifEmpty { parseSite(doc) }
    }

    private fun parseTv(doc: Element): List<StarItem> {
        val seen = LinkedHashMap<String, StarItem>()
        for (li in doc.select(ParsingRules.STAR_ITEM_TV)) {
            val path = li.attr("data-url")
            val slug = ParsingRules.STAR_SLUG.find(path)?.groupValues?.get(1) ?: continue
            val name = li.selectFirst("[class*=Title]")?.text().clean()
                ?: li.selectFirst("img")?.attr("alt").clean() ?: continue
            val rank = li.selectFirst("[class*=rank]")?.text().clean()
            seen.putIfAbsent(slug, StarItem(slug, name, "/pornstar/$slug/videos", li.thumb(), rank))
        }
        return seen.values.toList()
    }

    private fun parseSite(doc: Element): List<StarItem> {
        val seen = LinkedHashMap<String, StarItem>()
        for (li in doc.select(ParsingRules.STAR_ITEM)) {
            val link = li.selectFirst(ParsingRules.STAR_LINK) ?: continue
            val match = ParsingRules.PERFORMER_PATH.find(link.attr("href")) ?: continue
            val (kind, slug) = match.destructured
            val name = li.selectFirst(".performerNameInfo")?.text().clean()
                ?: li.selectFirst("img")?.attr("alt").clean() ?: continue
            val rank = li.selectFirst(".rank_number")?.text().clean()
            seen.putIfAbsent(slug, StarItem(slug, name, "/$kind/$slug/videos", li.thumb(), rank))
        }
        return seen.values.toList()
    }
}
