package dan.ngoding.aiwallpapershd.data.dao

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Fav(
    @PrimaryKey(autoGenerate = false)
    var uid: Int = 0,
    @ColumnInfo(name = "link")
    var link: String? = "",
) {
    constructor(link: String?) : this(uid = link!!.hashCode(), link = link)
}
