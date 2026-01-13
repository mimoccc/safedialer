package org.mjdev.safedialer.service.media

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.ContentUris
import android.content.Intent
import android.content.IntentFilter
import android.os.Binder
import android.os.IBinder
import android.provider.MediaStore
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.ExoPlayer.*
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaStyleNotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.mjdev.safedialer.R
import org.mjdev.safedialer.widget.media.MediaPlayer
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.annotation.MainThread
import androidx.annotation.OptIn
import androidx.media3.common.MediaMetadata
import androidx.media3.common.util.UnstableApi
import kotlinx.coroutines.withContext

@Suppress("unused")
class MediaService : Service() {
    private val binder = MediaBinder()
    private val receiver = MediaCommandReceiver()

    private val _playbackState = MutableStateFlow(EmptyState)
    private val _playlist = MutableStateFlow<MutableList<MediaItem>?>(null)

    private var mediaSession: MediaSession? = null

    private val player: ExoPlayer by lazy {
        Builder(this).build().apply {
            addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(state: Int) {
                    updatePlaybackState()
                }

                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    updatePlaybackState()
                }

                override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                    updatePlaybackState()
                }

                override fun onPositionDiscontinuity(
                    oldPosition: Player.PositionInfo,
                    newPosition: Player.PositionInfo,
                    reason: Int
                ) {
                    val now = System.currentTimeMillis()
                    if (now - lastWidgetUpdate > 2000L) {
                        lastWidgetUpdate = now
                        updatePlaybackState()
                    }
                }

                override fun onIsLoadingChanged(isLoading: Boolean) {
                    updatePlaybackState()
                }
            })
        }
    }

    private var lastWidgetUpdate: Long = 0

    val playbackState: StateFlow<PlaybackState> = _playbackState.asStateFlow()
    val playlist: StateFlow<List<MediaItem>?> = _playlist.asStateFlow()

    init {
        instance = this
    }

    override fun onCreate() {
        super.onCreate()
        initializeMediaSession()
        registerReceiver()
        startForeground()
        CoroutineScope(Dispatchers.IO).launch {
            loadMediaFiles()
        }
    }

    private fun initializeMediaSession() {
        mediaSession = MediaSession.Builder(this, player)
            .setCallback(object : MediaSession.Callback {
                // Handle custom commands if needed
            })
            .build()
    }

    private suspend fun loadMediaFiles() {
        setPlaylist(scanMediaFiles())
    }

    // todo flow
    @Suppress("RedundantSuspendModifier")
    private suspend fun scanMediaFiles(): MutableList<MediaItem> {
        val mediaList = mutableListOf<MediaItem>()
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM_ID,
        )
        contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            "${MediaStore.Audio.Media.TITLE} ASC"
        )?.use { cursor ->
            val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
            while (cursor.moveToNext()) {
                val albumId = cursor.getLong(albumIdColumn)
                val albumArtUri = ContentUris.withAppendedId(
                    Uri.parse("content://media/external/audio/albumart"),
                    albumId
                )
                mediaList.add(
                    MediaItem.Builder()
                        .setUri(cursor.getString(dataColumn))
                        .setMediaMetadata(
                            MediaMetadata.Builder()
                                .setTitle(cursor.getString(titleColumn))
                                .setArtist(cursor.getString(artistColumn))
                                .setArtworkUri(albumArtUri)
                                .build()
                        )
                        .build()
                )
            }
        }
        return mediaList
    }

    @MainThread
    private fun registerReceiver() {
        ContextCompat.registerReceiver(
            this,
            receiver,
            IntentFilter().apply {
                MediaCommand.entries.forEach {
                    addAction(it.action)
                }
            },
            ContextCompat.RECEIVER_EXPORTED
        )
    }

    @MainThread
    private fun startForeground() {
        startForeground(NOTIFICATION_ID, createNotification())
    }

    @OptIn(UnstableApi::class)
    @MainThread
    private fun createNotification(): Notification {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Media Player",
            NotificationManager.IMPORTANCE_LOW
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
        return mediaSession?.let { session ->
            NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(getCurrentMediaTitle())
                .setContentText(getCurrentMediaArtist())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(getCurrentAlbumArt())
                .setStyle(
                    MediaStyleNotificationHelper.MediaStyle(session)
                        .setShowActionsInCompactView(0, 1, 2)
                )
                .addAction(createAction(MediaCommand.PREVIOUS, "Previous", android.R.drawable.ic_media_previous))
                .addAction(
                    if (player.isPlaying)
                        createAction(MediaCommand.PAUSE, "Pause", android.R.drawable.ic_media_pause)
                    else
                        createAction(MediaCommand.PLAY, "Play", android.R.drawable.ic_media_play)
                )
                .addAction(createAction(MediaCommand.NEXT, "Next", android.R.drawable.ic_media_next))
                .setOngoing(player.isPlaying)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .build()
        } ?: NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Media Player")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setOngoing(true)
            .build()
    }

    private fun createAction(command: MediaCommand, title: String, icon: Int): NotificationCompat.Action {
        val intent = Intent(command.action).apply {
            setPackage(packageName)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            command.ordinal,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        return NotificationCompat.Action.Builder(icon, title, pendingIntent).build()
    }

    private fun getCurrentMediaTitle(): String {
        return player.currentMediaItem?.mediaMetadata?.title?.toString() ?: "Media Player"
    }

    private fun getCurrentMediaArtist(): String {
        return player.currentMediaItem?.mediaMetadata?.artist?.toString() ?: "Unknown Artist"
    }

    // todo : check if it is working
    private fun getCurrentAlbumArt(): Bitmap? {
        return runCatching {
            player.currentMediaItem?.mediaMetadata?.artworkUri?.let { uri ->
                contentResolver.openInputStream(uri)?.use { inputStream ->
                    BitmapFactory.decodeStream(inputStream)
                }
            }
        }.getOrNull()
    }

    @MainThread
    private fun updatePlaybackState() {
        _playbackState.value = _playbackState.value.copy(
            isPlaying = player.isPlaying,
            currentMediaIndex = player.currentMediaItemIndex,
            currentPosition = player.currentPosition,
            duration = player.duration,
        )
        updateNotification()
        updateWidget()
    }

    private fun updateNotification() {
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.notify(NOTIFICATION_ID, createNotification())
    }

    private fun updateWidget() {
        CoroutineScope(Dispatchers.Main).launch {
            GlanceAppWidgetManager(baseContext)
                .getGlanceIds(MediaPlayer::class.java)
                .forEach { id ->
                    MediaPlayer().update(baseContext, id)
                }
        }
    }

    // todo why ?
    inner class MediaBinder : Binder() {
        @Suppress("unused")
        fun getService(): MediaService = this@MediaService
    }

    override fun onBind(intent: Intent?): IBinder = binder

    @MainThread
    override fun onDestroy() {
        unregisterReceiver(receiver)
        mediaSession?.release()
        mediaSession = null
        player.release()
        super.onDestroy()
    }

    inner class MediaCommandReceiver : BroadcastReceiver() {
        override fun onReceive(
            context: Context,
            intent: Intent
        ) {
            when (MediaCommand.fromAction(intent.action)) {
                MediaCommand.PLAY -> playIndex(_playbackState.value.currentMediaIndex)
                MediaCommand.PAUSE -> pausePlayer()
                MediaCommand.NEXT -> seekToNext()
                MediaCommand.PREVIOUS -> seekToPrevious()
                MediaCommand.STOP -> stopPlayer()
                MediaCommand.PLAY_INDEX -> playIndex(intent.getIntExtra(EXTRA_INDEX, 0))
                MediaCommand.TOGGLE_SHUFFLE -> toggleShuffle()
                null -> Unit
            }
        }
    }

    private suspend fun setPlaylist(
        items: MutableList<MediaItem>
    ) {
        _playlist.value = items
        withContext(Dispatchers.Main) {
            updatePlaybackState()
        }
    }

    private fun playIndex(index: Int) {
        stopPlayer()
        val size = _playlist.value?.size ?: 0
        if (size > 0 && index < size) {
            player.setMediaItems(_playlist.value ?: emptyList())
            player.prepare()
            player.seekTo(index, 0)
            player.play()
        }
    }

    private fun stopPlayer() {
        if (player.isPlaying) {
            player.stop()
        }
    }

    private fun pausePlayer() {
        stopPlayer()
    }

    private fun seekToNext() {
        stopPlayer()
        var nextIndex = _playbackState.value.currentMediaIndex + 1
        if (nextIndex > ((_playlist.value?.size ?: 0) - 1)) nextIndex = 0
        playIndex(nextIndex)
    }

    private fun seekToPrevious() {
        stopPlayer()
        var nextIndex = _playbackState.value.currentMediaIndex - 1
        if (nextIndex < 0) nextIndex = 0
        playIndex(nextIndex)
    }

    private fun toggleShuffle() {
        val enabled = _playbackState.value.isShuffleEnabled
        _playbackState.value = _playbackState.value.copy(
            isShuffleEnabled = enabled.not()
        )
    }

    companion object {
        const val CHANNEL_ID = "media_player_channel"
        const val NOTIFICATION_ID = 1001
        const val EXTRA_INDEX = "extra_index"

        // todo need to improve?
        val EmptyState = PlaybackState()

        @Volatile
        private var instance: MediaService? = null

        // todo remove
        fun getInstance(): MediaService? = instance

        fun start(context: Context) {
            runCatching {
                Intent(context, MediaService::class.java).let { intent ->
                    ContextCompat.startForegroundService(context, intent)
                }
            }.onFailure { e ->
                e.printStackTrace()
            }
        }

        fun stop(context: Context) {
            runCatching {
                Intent(context, MediaService::class.java).let { intent ->
                    context.stopService(intent)
                }
            }.onFailure { e ->
                e.printStackTrace()
            }
        }
    }
}
