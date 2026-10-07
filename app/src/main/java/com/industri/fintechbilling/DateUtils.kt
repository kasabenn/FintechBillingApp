package com.industri.fintechbilling

import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Utility untuk memformat string tanggal transaksi menjadi format ramah pengguna bahasa Indonesia.
 * Tugas Mandiri 2:
 * Input:  "2026-09-23 10:15:00"
 * Output: "Rabu, 23 September 2026 - Pukul 10:15 WIB"
 */
object DateUtils {

    fun formatTransactionDate(rawDate: String?): String {
        if (rawDate.isNullOrBlank()) return "-"
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("EEEE, dd MMMM yyyy - 'Pukul' HH:mm 'WIB'", Locale.forLanguageTag("id-ID"))
            val parsedDate = inputFormat.parse(rawDate)
            if (parsedDate != null) {
                outputFormat.format(parsedDate)
            } else {
                rawDate
            }
        } catch (e: Exception) {
            rawDate
        }
    }
}

// Fungsi ekstensi Kotlin untuk kemudahan pemanggilan
fun String?.toUserFriendlyDate(): String = DateUtils.formatTransactionDate(this)
