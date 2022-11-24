package nz.badradio.badradio.utilities

import android.os.Handler
import android.os.Looper

fun generateFunExecuteWhen(c: () -> Boolean): (Runnable) -> Unit {
    fun generated(r: Runnable) {
        if (c()) {
            r.run()
        } else {
            Handler(Looper.getMainLooper()).postDelayed({
                generated(r)
            }, 100)
        }
    }

    return ::generated
}

fun generateFunExecuteIf(c: () -> Boolean): (Runnable) -> Unit {
    return {
        if (c()) {
            it.run()
        }
    }
}
