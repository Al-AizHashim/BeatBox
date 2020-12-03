package alaiz.hashim.beatbox

import alaiz.hashim.beatbox.databinding.ActivityMainBinding
import alaiz.hashim.beatbox.databinding.ListItemSoundBinding
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity(), AudioManager.OnAudioFocusChangeListener {
    companion object{
        lateinit var obj:MainActivity
    }

    var audioManager: AudioManager? = null
    var audioFocusRequest: AudioFocusRequest?=null
    @RequiresApi(Build.VERSION_CODES.O)
     fun setAudioFocusChangeListener() {

        audioManager = getSystemService(AUDIO_SERVICE) as AudioManager?


        audioFocusRequest= AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            .setAcceptsDelayedFocusGain(true)
            .setOnAudioFocusChangeListener(this).build()
        audioManager?.requestAudioFocus(audioFocusRequest!!)

    }
    override fun onAudioFocusChange(focusChange: Int) {
        if(focusChange<=0){
            beatBox.pause()
        }else{
            beatBox.resume()
        }
        
    }






    private lateinit var beatBox: BeatBox

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        obj=MainActivity()
        setAudioFocusChangeListener()
        beatBox = BeatBox(assets)


        val binding: ActivityMainBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(context, 3)
            adapter = SoundAdapter(beatBox.sounds)
        }

            binding.seekBar.setOnSeekBarChangeListener(object :
                SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seek: SeekBar, progress: Int, fromUser: Boolean) {

                    if (fromUser) {
                        BeatBox.settingRate(progress.toFloat())
                        binding.playBackSpeed.setText("Playback speed"+progress+"%")


                    }

                }

                override fun onStartTrackingTouch(seek: SeekBar) {

                }

                override fun onStopTrackingTouch(seek: SeekBar) {

            }
        })

    }

    override fun onDestroy() {
        super.onDestroy()
        beatBox.release()
    }

    private inner class SoundHolder(private val binding: ListItemSoundBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.viewModel = SoundViewModel(beatBox)
        }

        fun bind(sound: Sound) {
            binding.apply {
                viewModel?.sound = sound
                executePendingBindings()
            }
        }
    }

    private inner class SoundAdapter(private val sounds: List<Sound>) :
        RecyclerView.Adapter<SoundHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
                SoundHolder {
            val binding = DataBindingUtil.inflate<ListItemSoundBinding>(
                layoutInflater,
                R.layout.list_item_sound,
                parent,
                false
            )
            return SoundHolder(binding)
        }

        override fun onBindViewHolder(holder: SoundHolder, position: Int) {

            val sound = sounds[position]
            holder.bind(sound)
        }

        override fun getItemCount() = sounds.size
    }


}
