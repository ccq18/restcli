package uos.dev.restcli.report

import com.google.gson.annotations.SerializedName
import okhttp3.Protocol
import okhttp3.Request
import uos.dev.restcli.parser.RequestMethod

data class TestCase(
    private val name: String,
    var caseitems: MutableList<CaseItem> = ArrayList()
) {
    fun addRequest(caseItem: CaseItem) {
        this.caseitems.add(caseItem)
    }


}

data class CaseItem(
    private val name: String?,
    var isPassed: Boolean,
    var request: Request?,
    var response: IResponse?

) {
    var reports = ArrayList<IReport>()

    fun addTestReport(
        isPassed: Boolean,
        exception: String?,
        detail: String?
    ) {
        if (!isPassed) {
            this.isPassed = false
        }
        reports.add(IReport(isPassed, exception, detail))
    }
}

data class IReport(
    var isPassed: Boolean?,
    var exception: String?,
    var detail: String?,
)

data class IResponse(
//    val request: Request,
    val protocol: Protocol,
    val message: String,
    val code: Int,
//    val handshake: Handshake?,
    val headers: Map<String, String>,
    val body: String?,
//    val networkResponse: Response?,
//    val cacheResponse: Response?,
//    val priorResponse: Response?,
    val sentRequestAtMillis: Long,
    val receivedResponseAtMillis: Long,
)

data class IRequest(
    val method: RequestMethod = RequestMethod.GET,
    val url: String,
    val httpVersion: String = uos.dev.restcli.parser.Request.DEFAULT_HTTP_VERSION,
    val headers: Map<String, String> = emptyMap(),
    val body: String? = null,
)