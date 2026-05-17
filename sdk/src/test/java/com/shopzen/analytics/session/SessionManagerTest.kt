package com.shopzen.analytics.session

import androidx.lifecycle.LifecycleOwner
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest=Config.NONE)
class SessionManagerTest {

    private lateinit var sessionManager: SessionManager
    private lateinit var mockLifecycleOwner: LifecycleOwner

    @Before
    fun setup() {
        sessionManager = SessionManager()
        mockLifecycleOwner = mock(LifecycleOwner::class.java)
    }

    @Test
    fun `onStart generates initial session id`() {
        // Initially null
        val initialSession = sessionManager.getSessionId()
        
        sessionManager.onStart(mockLifecycleOwner)
        
        val newSession = sessionManager.getSessionId()
        assertNotNull(newSession)
        assertNotEquals(initialSession, newSession)
        assert(newSession!!.startsWith("sess_"))
    }

    @Test
    fun `forceNewSession generates a new unique session id`() {
        sessionManager.onStart(mockLifecycleOwner)
        val firstSession = sessionManager.getSessionId()

        sessionManager.forceNewSession()
        val secondSession = sessionManager.getSessionId()

        assertNotNull(secondSession)
        assertNotEquals(firstSession, secondSession)
    }
}
