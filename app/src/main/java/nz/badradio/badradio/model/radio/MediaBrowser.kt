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
        // TODO: return empty root when not playing
        return BrowserRoot(BADRADIO_RECENT_BROWSER_ROOT, Bundle())
    }

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {
        if (parentId == BADRADIO_RECENT_BROWSER_ROOT) {
            RadioVM.loadRecentMediaItem(result)
            result.detach()
        } else {
            result.sendResult(null)
        }
    }

}

private const val BADRADIO_EMPTY_BROWSER_ROOT = "nz.badradio.badradio.model.radio.BADRADIO_EMPTY_BROWSER_ROOT"
private const val BADRADIO_RECENT_BROWSER_ROOT = "nz.badradio.badradio.model.radio.BADRADIO_RECENT_BROWSER_ROOT"
