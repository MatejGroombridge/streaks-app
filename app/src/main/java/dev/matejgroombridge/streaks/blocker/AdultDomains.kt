package dev.matejgroombridge.streaks.blocker

/**
 * Curated adult-domain suffixes used by the built-in "block all" option.
 * This is intentionally stored locally so the app still works offline.
 *
 * No static list can literally cover the whole internet; the VPN blocks these
 * well-known suffixes plus any custom domains the user adds, including all
 * subdomains via suffix matching.
 */
object AdultDomains {
    val suffixes: Set<String> = setOf(
        "pornhub.com", "xvideos.com", "xnxx.com", "xhamster.com", "redtube.com",
        "youporn.com", "tube8.com", "spankbang.com", "eporner.com", "tnaflix.com",
        "hclips.com", "drtuber.com", "sunporno.com", "porn.com", "pornone.com",
        "pornhd.com", "porntrex.com", "porndig.com", "porn300.com", "pornhat.com",
        "pornpics.com", "pornburst.xxx", "pornrabbit.com", "pornrox.com", "pornwhite.com",
        "beeg.com", "nuvid.com", "keezmovies.com", "alphaporno.com", "4tube.com",
        "gotporn.com", "fux.com", "youjizz.com", "motherless.com", "thumbzilla.com",
        "slutload.com", "empflix.com", "brazzers.com", "realitykings.com", "mofos.com",
        "digitalplayground.com", "twistys.com", "teamskeet.com", "bangbros.com", "naughtyamerica.com",
        "onlyfans.com", "fansly.com", "manyvids.com", "chaturbate.com", "stripchat.com",
        "cam4.com", "camsoda.com", "bongacams.com", "myfreecams.com", "livejasmin.com",
        "adultfriendfinder.com", "ashleymadison.com", "literotica.com", "hentaihaven.xxx", "nhentai.net",
        "hanime.tv", "rule34.xxx", "rule34video.com", "f95zone.to", "erome.com",
        "redgifs.com", "xvideos-cdn.com", "phncdn.com", "xhcdn.com", "pornhubpremium.com",
    )
}
