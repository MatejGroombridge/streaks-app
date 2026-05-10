package dev.matejgroombridge.streaks.blocker

object DomainMatcher {
    fun normalize(raw: String): String? {
        val normalized = raw.trim().lowercase()
            .removePrefix("https://")
            .removePrefix("http://")
            .removePrefix("www.")
            .substringBefore('/')
            .substringBefore('?')
            .trim('.')
        return normalized.takeIf { it.contains('.') && it.length >= 4 }
    }

    fun isBlocked(host: String, blockAllPornSites: Boolean, customSites: List<String>): Boolean {
        val normalized = normalize(host) ?: return false
        val suffixes = buildSet {
            if (blockAllPornSites) addAll(AdultDomains.suffixes)
            customSites.mapNotNullTo(this) { normalize(it) }
        }
        return suffixes.any { suffix -> normalized == suffix || normalized.endsWith(".$suffix") }
    }
}
