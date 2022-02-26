package uos.dev.restcli.report

import okhttp3.*
import java.util.*

data class IResponse (
//    val request: Request,
    val protocol: Protocol,
    val message: String,
    val code: Int,
    val handshake: Handshake?,
    val headers: Headers,
    val body: String?,
//    val networkResponse: Response?,
//    val cacheResponse: Response?,
//    val priorResponse: Response?,
    val sentRequestAtMillis: Long,
    val receivedResponseAtMillis: Long,
)
