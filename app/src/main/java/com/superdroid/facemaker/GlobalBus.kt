package com.superdroid.facemaker

import com.squareup.otto.Bus

class GlobalBus{

    companion object {
        var sBus: Bus? = null

        fun getBus(): Bus? {
            if (sBus == null)
                sBus = Bus()
            return sBus
        }
    }
}