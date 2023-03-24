package com.example.chapter3_12

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chapter3_12.databinding.ActivityMainBinding
import com.example.chapter3_12.playerList.PlayerHeader
import com.example.chapter3_12.playerList.PlayerVideoAdapter
import com.example.chapter3_12.playerList.transform
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private lateinit var videoAdapter: VideoAdapter
    private lateinit var playerVideoAdapter: PlayerVideoAdapter
    private val videoList: VideoList by lazy {
        readData("videos.json", VideoList::class.java) ?: VideoList(emptyList())
    }
    private var player: ExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initMotionLayout()
        initVideoRecyclerView()
        initPlayerVideoRecyclerView()
        initControlButton()
        initHideButton()

    }

    private fun initHideButton() {
        binding.hideButton.setOnClickListener {
            binding.motionLayout.transitionToState(R.id.hide)
            player?.pause()
        }
    }

    private fun initPlayerVideoRecyclerView() {
        playerVideoAdapter = PlayerVideoAdapter(context = this) { playerVideo ->
            play(playerVideo.videoUrl, playerVideo.title)

            val headerModel = PlayerHeader(
                id = "H${playerVideo.id}",
                title = playerVideo.title,
                channelName = playerVideo.channelName,
                viewCount = playerVideo.viewCount,
                dateText = playerVideo.dateText,
                channelThumb = playerVideo.channelThumb
            )

            val list = listOf(headerModel) + videoList.videos.filter { it.id != playerVideo.id }
                .map { it.transform() }
            playerVideoAdapter.submitList(list)
            binding.playerRecyclerView.scrollToPosition(0)
        }
        binding.playerRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = playerVideoAdapter
            itemAnimator = null
        }

    }

    private fun initControlButton() {
        binding.controlButton.setOnClickListener {
            //val player = this.player ?: return@setOnClickListener

            player?.let {
                if (it.isPlaying) {
                    it.pause()
                } else {
                    it.play()
                }
            }
        }
    }

    private fun initVideoRecyclerView() {
        videoAdapter = VideoAdapter(context = this) { videoItem ->
            binding.motionLayout.setTransition(R.id.collapse, R.id.expand)
            binding.motionLayout.transitionToEnd()
            val headerModel = PlayerHeader(
                id = "H${videoItem.id}",
                title = videoItem.title,
                channelName = videoItem.channelName,
                viewCount = videoItem.viewCount,
                dateText = videoItem.dateText,
                channelThumb = videoItem.channelThumb
            )
            val list = listOf(headerModel) + videoList.videos.filter { it.id != videoItem.id }
                .map { it.transform() }
            playerVideoAdapter.submitList(list)
            play(videoItem.videoUrl, videoItem.title)
        }



        binding.videoListRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = videoAdapter
        }

        videoAdapter.submitList(videoList.videos)
    }

    private fun initMotionLayout() {
        binding.motionLayout.targetView = binding.videoPlayerContainer
        binding.motionLayout.jumpToState(R.id.hide)
        binding.motionLayout.setTransitionListener(object : MotionLayout.TransitionListener {
            override fun onTransitionStarted(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int
            ) {
            }

            override fun onTransitionChange(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int,
                progress: Float
            ) {
                binding.playerView.useController = false
            }

            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
                binding.playerView.useController = currentId == R.id.expand
            }

            override fun onTransitionTrigger(
                motionLayout: MotionLayout?,
                triggerId: Int,
                positive: Boolean,
                progress: Float
            ) {
            }

        })
    }

    private fun initExoPlayer() {
        player = ExoPlayer.Builder(this).build().also { exoPlayer ->
            binding.playerView.player = exoPlayer
            binding.playerView.useController = false

            exoPlayer.addListener(object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    super.onIsPlayingChanged(isPlaying)

                    if (isPlaying) {
                        binding.controlButton.setImageResource(R.drawable.baseline_pause_24)
                    } else {
                        binding.controlButton.setImageResource(R.drawable.baseline_play_arrow_24)
                    }
                }
            })
        }

    }

    private fun play(url: String, title: String) {
        player?.setMediaItem(MediaItem.fromUri(Uri.parse(url)))
        player?.prepare()
        player?.play()

        binding.videoTitleTextView.text = title
    }

    override fun onStart() {
        super.onStart()

        if (player == null)
            initExoPlayer()
    }

    override fun onResume() {
        super.onResume()

        if (player == null)
            initExoPlayer()
    }

    override fun onStop() {
        super.onStop()

        player?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()

        player?.release()
    }
}