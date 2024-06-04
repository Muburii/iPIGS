package com.example.zufffinalyear.models

import android.widget.AutoCompleteTextView
import java.util.Arrays

class AutocompleteValidator(private val validItems: Array<String>) : AutoCompleteTextView.Validator {

    init {
        Arrays.sort(validItems) // Sort the array for binary search
    }

    override fun isValid(text: CharSequence?): Boolean {
        return text != null && Arrays.binarySearch(validItems, text.toString()) >= 0
    }

    override fun fixText(invalidText: CharSequence?): CharSequence {
        return ""
    }
}
