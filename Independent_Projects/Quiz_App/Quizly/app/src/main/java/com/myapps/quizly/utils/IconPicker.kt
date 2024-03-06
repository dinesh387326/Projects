package com.myapps.quizly.utils

import com.myapps.quizly.R

object IconPicker {
    var icon = arrayOf(
        R.drawable.calendar_days_icon,
        R.drawable.file_pdf_document_icon,
        R.drawable.user_icon
    )
    var currentIconIndex = 0
    fun getIcon(): Int {
        currentIconIndex = (currentIconIndex + 1) % icon.size
        return icon[currentIconIndex]
    }
}