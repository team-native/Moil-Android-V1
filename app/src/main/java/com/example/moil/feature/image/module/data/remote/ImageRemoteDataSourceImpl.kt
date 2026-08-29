package com.example.moil.feature.image.module.data.remote

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import com.example.moil.core.network.ApiExecutor
import com.example.moil.core.network.NetworkResult
import com.example.moil.feature.image.module.data.dto.ImageUploadResponseDto
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.IOException
import java.util.Locale
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okio.BufferedSink
import okio.source

class ImageRemoteDataSourceImpl @Inject constructor(
    @ApplicationContext appContext: Context,
    private val imageApiService: ImageApiService,
    private val apiExecutor: ApiExecutor,
) : ImageRemoteDataSource {
    private val contentResolver: ContentResolver = appContext.contentResolver

    override suspend fun uploadProfileImage(contentUri: String): NetworkResult<ImageUploadResponseDto> {
        val sourceUri = Uri.parse(contentUri)
        val mediaType = contentResolver.getType(sourceUri)
            ?.lowercase(Locale.ROOT)
            ?.takeIf { type -> type.startsWith("image/") }
            ?.toMediaTypeOrNull()
            ?: "application/octet-stream".toMediaTypeOrNull()
        val imagePart = MultipartBody.Part.createFormData(
            "image",
            queryDisplayName(sourceUri) ?: "profile-image",
            ContentUriRequestBody(
                contentResolver = contentResolver,
                sourceUri = sourceUri,
                mediaType = mediaType,
                contentLength = querySize(sourceUri),
            ),
        )

        return apiExecutor.execute {
            imageApiService.uploadProfileImage(imagePart)
        }
    }

    private fun queryDisplayName(sourceUri: Uri): String? = queryOpenableColumn(sourceUri, OpenableColumns.DISPLAY_NAME) { cursor ->
        cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
    }

    private fun querySize(sourceUri: Uri): Long = queryOpenableColumn(sourceUri, OpenableColumns.SIZE) { cursor ->
        cursor.getLong(cursor.getColumnIndexOrThrow(OpenableColumns.SIZE))
    } ?: -1L

    private fun <T> queryOpenableColumn(
        sourceUri: Uri,
        columnName: String,
        valueReader: (Cursor) -> T,
    ): T? {
        return contentResolver.query(
            sourceUri,
            arrayOf(columnName),
            null,
            null,
            null,
        )?.use { cursor ->
            if (cursor.moveToFirst()) valueReader(cursor) else null
        }
    }
}

/** Content URI를 요청 본문에 스트리밍해 이미지 전체를 메모리에 적재하지 않습니다. */
private class ContentUriRequestBody(
    private val contentResolver: ContentResolver,
    private val sourceUri: Uri,
    private val mediaType: okhttp3.MediaType?,
    private val contentLength: Long,
) : RequestBody() {
    override fun contentType(): okhttp3.MediaType? = mediaType

    override fun contentLength(): Long = contentLength

    override fun writeTo(sink: BufferedSink) {
        try {
            contentResolver.openInputStream(sourceUri)?.use { inputStream ->
                sink.writeAll(inputStream.source())
            } ?: throw IOException("이미지 URI를 열 수 없습니다.")
        } catch (cancellationException: CancellationException) {
            throw cancellationException
        }
    }
}
