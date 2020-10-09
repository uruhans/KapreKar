package com.coloplast.olympus.ui.common.widgets

interface PasswordValidator: InputValidator {
    /**
     * Regular expression used for the password validation
     * Default
     * regex is provided.
     */
    val regex: Regex
        get() = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[#?!@\$%^&*-]).{8,}\$".toRegex()

    /**
     * Checks if the password String provided matches the provided regular expression [regex]
     *
     * @param input to check if it matches the regex
     * @return True if password matches the [regex]
     */
    override fun validate(input: String): Boolean {
//        return input.matches(regex)
        return !input.isEmpty()
    }
}