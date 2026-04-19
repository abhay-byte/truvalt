package com.ivarna.truvalt.autofill

import android.text.InputType

internal object AutofillFieldClassifier {

    fun isUsernameField(
        inputType: Int,
        hints: List<String>,
        viewHint: String?,
        htmlAutocomplete: String?,
        htmlInputType: String?
    ): Boolean {
        return inputType and InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS != 0 ||
            inputType and InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS != 0 ||
            hints.any { it.contains("email", ignoreCase = true) || it.contains("username", ignoreCase = true) } ||
            viewHint?.contains("email", ignoreCase = true) == true ||
            viewHint?.contains("username", ignoreCase = true) == true ||
            viewHint?.contains("login", ignoreCase = true) == true ||
            viewHint?.contains("user", ignoreCase = true) == true ||
            htmlAutocomplete?.contains("email", ignoreCase = true) == true ||
            htmlAutocomplete?.contains("username", ignoreCase = true) == true ||
            htmlInputType?.equals("email", ignoreCase = true) == true
    }

    fun isPasswordField(
        inputType: Int,
        hints: List<String>,
        viewHint: String?,
        htmlAutocomplete: String?,
        htmlInputType: String?
    ): Boolean {
        return inputType and InputType.TYPE_TEXT_VARIATION_PASSWORD != 0 ||
            inputType and InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD != 0 ||
            hints.any { it.contains("password", ignoreCase = true) } ||
            viewHint?.contains("password", ignoreCase = true) == true ||
            htmlAutocomplete?.contains("password", ignoreCase = true) == true ||
            htmlInputType?.equals("password", ignoreCase = true) == true
    }
}
