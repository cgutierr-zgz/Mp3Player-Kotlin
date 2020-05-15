package es.carlosgutimo.musicplayer

import android.annotation.SuppressLint
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.animation.AnimationUtils
import android.widget.SeekBar
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    var mediaPlayer = MediaPlayer()
    private var totalTime: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val songTitle = "Safari Queen | Sismo X Mario M"
        val songFileName = "safari_queen"

        tv_SongTitle.text = songTitle
        val description = this.assets.openFd("$songFileName.mp3")
        val mp = MediaPlayer()
        mp.setDataSource(description.fileDescriptor, description.startOffset, description.length)
        description.close()
        totalTime = mp.duration


        playbutton_stop.setOnClickListener {
            try {
                if (mediaPlayer.isPlaying) {
                    mediaPlayer.pause()
                } else {
                    val descriptor = this.assets.openFd("$songFileName.mp3")
                    mediaPlayer.isLooping = false
                    mediaPlayer.setVolume(0.5f, 0.5f)
                    mediaPlayer.setDataSource(descriptor.fileDescriptor, descriptor.startOffset, descriptor.length)
                    descriptor.close()
                    mediaPlayer.prepare()
                    mediaPlayer.setOnPreparedListener {
                        mediaPlayer.start()

                    }
                    totalTime = mediaPlayer.duration
                    progressBar_SONG.max = totalTime

                }
            } catch (ex: java.lang.Exception) {

            }
        }



        progressBar_VOLUME.setOnSeekBarChangeListener(
                object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                        if (fromUser) {
                            var volumeNum = progress / 100.0f
                            mediaPlayer.setVolume(volumeNum, volumeNum)
                        }
                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    }

                    override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    }

                }
        )
        progressBar_SONG.setOnSeekBarChangeListener(
                object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                        if (fromUser) {
                            mediaPlayer.seekTo(progress)
                        }
                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    }

                    override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    }

                }
        )


        var isRotating = false

        @SuppressLint("HandlerLeak")
        val handler = object : Handler() {
            override fun handleMessage(msg: Message) {
                val currentPosition = msg.what

                if (!mediaPlayer.isPlaying) {
                    isRotating = false

                    progressBar_SONG.progress = 0
                    playbutton_stop.setImageResource(R.mipmap.playsong_icono_dashboard)
                    mediaPlayer.reset()
                    iv_SongImage.rotation = 0.0f
                    try {
                        iv_SongImage.animation.cancel()
                    } catch (e: java.lang.Exception) {
                    }
                    iv_SongImage.rotation = 0.0f
                    iv_SongImage.setImageResource(R.drawable.pic_01)
                } else {
                    progressBar_SONG.progress = currentPosition
                    playbutton_stop.setImageResource(R.mipmap.stopsong_icono_dashboard)
                    if (!isRotating) {

                        iv_SongImage.setImageResource(R.drawable.pic_02)
                        iv_SongImage.startAnimation(AnimationUtils.loadAnimation(this@MainActivity, R.anim.rotation_anim))
                        iv_SongImage.animation.cancel()
                        iv_SongImage.animation.reset()
                        Run.after(500) {
                            iv_SongImage.startAnimation(AnimationUtils.loadAnimation(this@MainActivity, R.anim.rotation_anim))
                        }
                        isRotating = true
                    }
                }
            }

        }
        //Thread
        Thread(Runnable
        {
            while (mediaPlayer != null) {
                try {
                    var msg = Message()
                    msg.what = mediaPlayer.currentPosition
                    handler.sendMessage(msg)
                    Thread.sleep(1000)

                } catch (e: Exception) {

                }
            }

        }).start()

    }

    override fun onPause() {
        super.onPause()
        mediaPlayer.stop()
    }

    override fun onStop() {
        super.onStop()
        mediaPlayer.stop()
    }

}