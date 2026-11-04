package `in`.sandeep.odiabhasa.ganita.utils

import android.content.Context

// Function to convert English numbers to local (Odia) numerals forcefully
fun String.toLocaleNumerals(context: Context? = null): String {
    // We removed the "if (locale != or)" check.
    // Now it will convert to Odia numbers on ANY phone.

    val odiaNumerals = charArrayOf('୦', '୧', '୨', '୩', '୪', '୫', '୬', '୭', '୮', '୯')
    val builder = StringBuilder()
    for (char in this) {
        if (char.isDigit()) {
            val digit = Character.getNumericValue(char)
            if (digit in 0..9) {
                builder.append(odiaNumerals[digit])
            } else {
                builder.append(char)
            }
        } else {
            builder.append(char)
        }
    }
    return builder.toString()
}

// Helper function to allow calling this on Integers as well
fun Int.toLocaleNumerals(context: Context? = null): String {
    return this.toString().toLocaleNumerals(context)
}