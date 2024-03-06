package com.myapps.quizly.utils

object ColorPicker {
    var color = arrayOf("#FFFF00", "#00FFFF", "#FF00FF", "#0000FF")
    var currentColorIndex = 0
    fun getColor(): String {
        currentColorIndex = (currentColorIndex + 1) % color.size
        return color[currentColorIndex]
    }
}