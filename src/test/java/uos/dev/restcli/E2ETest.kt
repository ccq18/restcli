package uos.dev.restcli

import com.google.common.truth.Truth.assertThat
import com.google.gson.Gson
import mu.KotlinLogging
import okhttp3.Response
import org.junit.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import uos.dev.restcli.Resource.getResourcePath
import uos.dev.restcli.parser.Request
import uos.dev.restcli.parser.RequestMethod
import uos.dev.restcli.report.TestReportStore

class E2ETest {
    private val logger = KotlinLogging.logger {}

    /**
     * Tests execute the http request `fileName` in the test resource folder.
     * If you are running the test on the IDE, you have to set the working directory point to the test
     * resource folder.
     */
    @ParameterizedTest
    @CsvSource(
        value = ["demo.http"]
    )
    fun `one demo`(fileName: String) {
        // Given
        println("Test file: $fileName")
        val restCli = RestCli().apply {
            environmentName = "test"
            logLevel = HttpLoggingLevel.BODY
            httpFilePaths = arrayOf(getResourcePath("/requests/${fileName}"))
            environmentFilesDirectory = getResourcePath("/requests/")
            decorator = ConfigDecorator.THREE_STAR
            responsefile="/Users/mac/code/javacode/restcli/src/main/resources/resp.json"
        }

        // When
        val exitCode = restCli.call()

        // Then
        assertThat(exitCode).isEqualTo(0)
    }

    @ParameterizedTest
    @CsvSource(
        value = ["get-requests.http"]
    )
    fun `demotest1`(fileName: String) {
        var testCases = TestCase("name")
        var request = Request(
            method = RequestMethod.GET,
            requestTarget = "{{host}}/get?show_env={{show_env}} eq+ {{show_env}}",
            headers = mapOf("Accept" to "application/json")
        )
//        Response(protocol="http/1.1", code=404, message="NOT FOUND", url="https://httpbin.org/ip+eq++10")
//        var response = Response.Builder.cacheResponse()
        request.body

        testCases.addRequest("hello",request,null)
//        testCases.requests

        val gson = Gson()
        var json = gson.toJson(testCases);
        logger.info("HTTP request file[s] is required{}",json)

    }


    @ParameterizedTest
    @CsvSource(
        value = ["get-requests.http", "post-requests.http", "requests-with-authorization.http", "requests-with-name.http", "requests-with-tests.http"]
    )
    fun `should not fail requests`(fileName: String) {
        // Given
        println("Test file: $fileName")
        val restCli = RestCli().apply {
            environmentName = "test"
            logLevel = HttpLoggingLevel.HEADERS
            httpFilePaths = arrayOf(getResourcePath("/requests/${fileName}"))
            environmentFilesDirectory = getResourcePath("/requests/")
            decorator = ConfigDecorator.THREE_STAR
        }

        // When
        val exitCode = restCli.call()
        // Then
        assertThat(exitCode).isEqualTo(0)
    }

    @ParameterizedTest
    @CsvSource(
        value = ["requests-bad-ssl.http"]
    )
    fun `request with bad ssl`(fileName: String) {
        // Given
        println("Test file: $fileName")
        val restCli = RestCli().apply {
            environmentName = "test"
            logLevel = HttpLoggingLevel.BASIC
            httpFilePaths = arrayOf(getResourcePath("/requests/${fileName}"))
            environmentFilesDirectory = getResourcePath("/requests/")
            insecure = true
        }

        // When
        val exitCode = restCli.call()
        // Then
        assertThat(exitCode).isEqualTo(0)
    }

    @ParameterizedTest
    @CsvSource(
        value = ["requests-with-failing-tests.http"]
    )
    fun `should fail request`(fileName: String) {
        // Given
        println("Test file: $fileName")
        val restCli = RestCli().apply {
            environmentName = "test"
            logLevel = HttpLoggingLevel.BASIC
            httpFilePaths = arrayOf(getResourcePath("/requests/${fileName}"))
            environmentFilesDirectory = getResourcePath("/requests/")
        }

        // When
        val exitCode = restCli.call()

        // Then
        assertThat(exitCode).isEqualTo(1)

        assertThat(TestReportStore.testGroupReports.all { it.testReports.size > 0 }).isTrue()
    }

    @Test
    fun `should share variables between two request files`() {
        val paths = arrayOf(
            "requests-share-var-between-files1.http",
            "requests-share-var-between-files2.http",
        )

        // Given
        val httpFilePaths = paths.map { getResourcePath("/requests/$it") }.toTypedArray()
        val restCli = RestCli().apply {
            environmentName = "test"
            logLevel = HttpLoggingLevel.BASIC
            this.httpFilePaths = httpFilePaths
            environmentFilesDirectory = getResourcePath("/requests/")
        }

        // When
        val exitCode = restCli.call()

        // Then
        assertThat(exitCode).isEqualTo(0)

        assertThat(TestReportStore.testGroupReports.all { it.testReports.isNotEmpty() }).isTrue()
    }
}
