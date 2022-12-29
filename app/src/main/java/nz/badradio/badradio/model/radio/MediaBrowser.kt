package nz.badradio.badradio.model.radio

import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import androidx.media.MediaBrowserServiceCompat
import nz.badradio.badradio.viewmodel.RadioVM

class MediaBrowser : MediaBrowserServiceCompat() {

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot {
        return BrowserRoot(
            if (RadioVM.isInitializedAndPlaying()) {
                recentBrowserRoot
            } else {
                emptyBrowserRoot
            }, Bundle()
        )
    }

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {
        if (parentId == recentBrowserRoot) {
            RadioVM.loadRecentMediaItem(result)
            result.detach()
        } else {
            result.sendResult(null)
        }
    }

    private val emptyBrowserRoot = "nz.badradio.badradio.model.radio.BADRADIO_EMPTY_BROWSER_ROOT"
    private val recentBrowserRoot = "nz.badradio.badradio.model.radio.BADRADIO_RECENT_BROWSER_ROOT"

}
