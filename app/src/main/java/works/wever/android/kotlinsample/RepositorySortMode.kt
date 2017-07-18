package works.wever.android.kotlinsample

enum class RepositorySortMode(val displayName: String) {
    CREATED("created"),
    UPDATED("updated"),
    PUSHED("pushed"),
    FULL_NAME("full_name");

    override fun toString(): String {
        return displayName
    }
}