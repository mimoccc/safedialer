package org.mjdev.safedialer.providers.android.media

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import org.mjdev.safedialer.providers.core.AbstractProvider
import org.mjdev.safedialer.providers.core.Data

class MediaProvider(
    context: Context
) : AbstractProvider(context) {
    companion object {
        private const val LIMIT = 250
        private const val ORDER_BY_COLUMN = MediaStore.MediaColumns.DATE_MODIFIED
    }

    fun getFiles(storage: Storage): Data<File>? = when (storage) {
        Storage.INTERNAL -> getContentTableData(File.uriInternal, File::class.java)
        Storage.EXTERNAL -> getContentTableData(
            File.uriExternal,
            null,
            null,
            "$ORDER_BY_COLUMN DESC LIMIT $LIMIT",
            File::class.java
        )
    }

    fun getImages(storage: Storage): Data<Image>? = when (storage) {
        Storage.INTERNAL -> getContentTableData(Image.uriInternal, Image::class.java)
        Storage.EXTERNAL -> getContentTableData(
            Image.uriExternal,
            null,
            null,
            "$ORDER_BY_COLUMN DESC LIMIT $LIMIT",
            Image::class.java
        )
    }

    fun getVideos(storage: Storage): Data<Video>? = when (storage) {
        Storage.INTERNAL -> getContentTableData(Video.uriInternal, Video::class.java)
        Storage.EXTERNAL -> getContentTableData(
            Video.uriExternal,
            null,
            null,
            "$ORDER_BY_COLUMN DESC LIMIT $LIMIT",
            Video::class.java
        )
    }

    fun getAudios(storage: Storage): Data<Audio>? = when (storage) {
        Storage.INTERNAL -> getContentTableData(Audio.uriInternal, Audio::class.java)
        Storage.EXTERNAL -> getContentTableData(
            Audio.uriExternal,
            null,
            null,
            "$ORDER_BY_COLUMN DESC LIMIT $LIMIT",
            Audio::class.java
        )
    }

    fun getAlbums(storage: Storage): Data<Album>? = when (storage) {
        Storage.INTERNAL -> getContentTableData(Album.uriInternal, Album::class.java)
        Storage.EXTERNAL -> getContentTableData(Album.uriExternal, Album::class.java)
    }

    fun getArtists(storage: Storage): Data<Artist>? = when (storage) {
        Storage.INTERNAL -> getContentTableData(Artist.uriInternal, Artist::class.java)
        Storage.EXTERNAL -> getContentTableData(Artist.uriExternal, Artist::class.java)
    }

    fun getGenres(storage: Storage): Data<Genre>? = when (storage) {
        Storage.INTERNAL -> getContentTableData(Genre.uriInternal, Genre::class.java)
        Storage.EXTERNAL -> getContentTableData(Genre.uriExternal, Genre::class.java)
    }

    fun getPlaylists(storage: Storage): Data<Playlist>? = when (storage) {
        Storage.INTERNAL -> getContentTableData(
            Playlist.uriInternal,
            Playlist::class.java
        )

        Storage.EXTERNAL -> getContentTableData(
            Playlist.uriExternal,
            Playlist::class.java
        )
    }

    enum class Storage {
        INTERNAL,
        EXTERNAL
    }

    override fun getUris(): List<Uri> = listOf(
        Album.uri,
        Album.uriExternal,
        Album.uriInternal,
        Artist.uri,
        Artist.uriExternal,
        Artist.uriInternal,
        Audio.uri,
        Audio.uriExternal,
        Audio.uriInternal,
        File.uri,
        File.uriInternal,
        File.uriExternal,
        Genre.uri,
        Genre.uriExternal,
        Genre.uriInternal,
        Image.uri,
        Image.uriExternal,
        Image.uriInternal,
        Playlist.uri,
        Playlist.uriExternal,
        Playlist.uriInternal,
        Video.uri,
        Video.uriExternal,
        Video.uriInternal
    ).distinct().filter { it != Uri.EMPTY }
}
