package alaiz.hashim.beatbox

import android.content.Context
import androidx.lifecycle.ViewModel

class BeatBoxViewModel : ViewModel() {

    var beatBox: BeatBox

    init {

        beatBox = BeatBox(context.assets)
    }

    companion object {
        private lateinit var context: Context

        fun passContext(context: Context) {
            this.context = context
        }
    }

}
