package com.stork.blockspam.extension


import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.widget.EditText

val EditText.value: String get() = text.toString().trim()

fun EditText.addCharacter(char: Char) {
    dispatchKeyEvent(getKeyEvent(getCharKeyCode(char)))
}
fun EditText.onTextChangeListener(onTextChangedAction: (newText: String) -> Unit) = addTextChangedListener(object : TextWatcher {
    override fun afterTextChanged(s: Editable?) {
        onTextChangedAction(s.toString())
    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
})

fun EditText.getKeyEvent(keyCode: Int) = KeyEvent(0, 0, KeyEvent.ACTION_DOWN, keyCode, 0)

private fun getCharKeyCode(char: Char) = when (char) {
    '0' -> KeyEvent.KEYCODE_0
    '1' -> KeyEvent.KEYCODE_1
    '2' -> KeyEvent.KEYCODE_2
    '3' -> KeyEvent.KEYCODE_3
    '4' -> KeyEvent.KEYCODE_4
    '5' -> KeyEvent.KEYCODE_5
    '6' -> KeyEvent.KEYCODE_6
    '7' -> KeyEvent.KEYCODE_7
    '8' -> KeyEvent.KEYCODE_8
    '9' -> KeyEvent.KEYCODE_9
    '*' -> KeyEvent.KEYCODE_STAR
    '+' -> KeyEvent.KEYCODE_PLUS
    else -> KeyEvent.KEYCODE_POUND
}