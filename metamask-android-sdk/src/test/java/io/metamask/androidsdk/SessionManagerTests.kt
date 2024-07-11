package io.metamask.androidsdk

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SessionManagerTests {
    private val sessionConfigFile: String = "SESSION_CONFIG_FILE"
    private lateinit var keyStorage: SecureStorage
    private lateinit var sessionManager: SessionManager
    @Before
    fun setUp() {
        keyStorage = MockKeyStorage()
        keyStorage.clear(sessionConfigFile)
        sessionManager = SessionManager(store = keyStorage, logger = TestLogger)
    }
    @Test
    fun testInitLoadsSessionConfig() = runTest {
        assertNotNull(sessionManager.sessionId)
    }
    @Test
    fun testDefaultSessionDuration() = runTest {
        val sessionConfig = sessionManager.getSessionConfig()
        val defaultDuration = 30 * 24 * 3600L // 30 days
        assertEquals(sessionConfig.expiryDate, System.currentTimeMillis() + defaultDuration * 1000)
    }
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testUpdateSessionDuration() = runTest {
        val newDuration: Long = 14 * 24 * 3600
        sessionManager.updateSessionDuration(newDuration)
        advanceUntilIdle()
        val sessionConfig = sessionManager.getSessionConfig()
        assertEquals(sessionConfig.expiryDate/1000, System.currentTimeMillis()/1000L + newDuration)
    }
    @Test
    fun testSessionConfigIsValid() = runTest {
        val sessionConfig = sessionManager.getSessionConfig()
        assertTrue(sessionConfig.isValid())
    }
    @Test
    fun testSessionConfigReset() = runTest {
        val initialSessionConfig = sessionManager.getSessionConfig()
        val resetSessionConfig = sessionManager.getSessionConfig(reset = true)

        assertNotEquals(initialSessionConfig.sessionId, resetSessionConfig.sessionId)
    }
    @Test
    fun testSaveSessionConfig() = runTest {
        val sessionConfig = SessionConfig("test_session", System.currentTimeMillis() + 1000L)
        sessionManager.saveSessionConfig(sessionConfig)

        val savedConfig = sessionManager.getSessionConfig()
        assertEquals(sessionConfig.sessionId, savedConfig.sessionId)
    }
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testClearSession() = runTest {
        sessionManager.clearSession { }
        advanceUntilIdle()

        val sessionConfig = sessionManager.getSessionConfig()
        assertNotEquals("", sessionManager.sessionId)
        assertNotEquals("", sessionConfig.sessionId)
    }
    @Test
    fun testMakeNewSessionConfig() = runTest {
        val newConfig = sessionManager.makeNewSessionConfig()

        assertTrue(newConfig.isValid())
        assertNotEquals("", newConfig.sessionId)
    }
}