package com.ivarna.truvalt.autofill

import android.text.InputType
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AutofillFieldClassifierTest {

    @Test
    fun `username hint matches username field`() {
        assertTrue(
            AutofillFieldClassifier.isUsernameField(
                inputType = InputType.TYPE_CLASS_TEXT,
                hints = emptyList(),
                viewHint = "Username or email address, required",
                htmlAutocomplete = null,
                htmlInputType = null
            )
        )
    }

    @Test
    fun `password hint matches password field`() {
        assertTrue(
            AutofillFieldClassifier.isPasswordField(
                inputType = InputType.TYPE_CLASS_TEXT,
                hints = emptyList(),
                viewHint = "Password, required",
                htmlAutocomplete = null,
                htmlInputType = null
            )
        )
    }

    @Test
    fun `password hint does not match username field`() {
        assertFalse(
            AutofillFieldClassifier.isUsernameField(
                inputType = InputType.TYPE_CLASS_TEXT,
                hints = emptyList(),
                viewHint = "Password, required",
                htmlAutocomplete = null,
                htmlInputType = null
            )
        )
    }
}
