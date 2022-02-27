package uos.dev.restcli

import com.google.common.truth.Truth.assertThat
import com.google.gson.Gson
import mu.KotlinLogging
import org.junit.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import uos.dev.restcli.Resource.getResourcePath
import uos.dev.restcli.parser.Request
import uos.dev.restcli.parser.RequestMethod
import uos.dev.restcli.report.CaseItem
import uos.dev.restcli.report.TestCase
import uos.dev.restcli.report.TestReportStore

class E2ETest {
    private val logger = KotlinLogging.logger {}


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
            responsefile="./resp.json"
        }

        // When
        val exitCode = restCli.call()

        // Then
        assertThat(exitCode).isEqualTo(0)
    }

    @ParameterizedTest
    @CsvSource(
        value = ["one-test-case.http"]
    )
    fun `one test case`(fileName: String) {
        // Given
        println("Test file: $fileName")
        val restCli = RestCli().apply {
            environmentName = "test"
            logLevel = HttpLoggingLevel.BODY
            httpFilePaths = arrayOf(getResourcePath("/requests/${fileName}"))
            environmentFilesDirectory = getResourcePath("/requests/")
            decorator = ConfigDecorator.THREE_STAR
            responsefile="./resp.json"
        }

        // When
        val exitCode = restCli.call()

        // Then
        assertThat(exitCode).isEqualTo(0)
    }

    /**
     * Tests execute the http request `fileName` in the test resource folder.
     * If you are running the test on the IDE, you have to set the working directory point to the test
     * resource folder.
     */
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
