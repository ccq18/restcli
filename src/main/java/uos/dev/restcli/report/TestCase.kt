package uos.dev.restcli.report

import okhttp3.*
import java.util.*

data class TestCase(
    private val name: String,
    var caseitems: MutableList<CaseItem> = ArrayList()

) {
    fun addRequest(  caseItem: CaseItem) {
        this.caseitems.add(caseItem)
    }


}

data class CaseItem(
    private val name: String,
    var request: Request,
    var response: IResponse?

)

data class IResponse (
//    val request: Request,
    val protocol: Protocol,
    val message: String,
    val code: Int,
//    val handshake: Handshake?,
    val headers: Map<String,String>,
    val body: String?,
//    val networkResponse: Response?,
//    val cacheResponse: Response?,
//    val priorResponse: Response?,
    val sentRequestAtMillis: Long,
    val receivedResponseAtMillis: Long,
)
