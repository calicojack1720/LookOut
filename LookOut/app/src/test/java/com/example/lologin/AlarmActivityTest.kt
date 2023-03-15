package com.example.lologin

import org.junit.Assert.*
import androidx.test.core.app.ActivityScenario
import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test

class AlarmActivityTest {

    @Test
    fun `onCreate sets ContentView Correctly`() {
        val scenario = ActivityScenario.launch(AlarmActivity::class.java)

        scenario.onActivity { activity ->
            assertNotNull(activity.findViewById(R.id.activity_alarms))
        }


    }
}