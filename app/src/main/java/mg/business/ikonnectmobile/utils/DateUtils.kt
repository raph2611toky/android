package mg.business.ikonnectmobile.utils

import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

object DateUtils {

    fun formatDiscussionDate(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val elapsedTime = now - timestamp

        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp

        return when {
            elapsedTime < TimeUnit.MINUTES.toMillis(1) -> "Maintenant"
            elapsedTime < TimeUnit.HOURS.toMillis(1) -> "${TimeUnit.MILLISECONDS.toMinutes(elapsedTime)} minutes"
            elapsedTime < TimeUnit.DAYS.toMillis(1) -> "${TimeUnit.MILLISECONDS.toHours(elapsedTime)} heures"
            isToday(calendar) -> SimpleDateFormat("HH'h'mm", Locale.getDefault()).format(Date(timestamp))
            isThisWeek(calendar) -> {
                val dayOfWeekFormat = SimpleDateFormat("EEEE 'à' HH'h'mm", Locale.getDefault())
                when (calendar.get(Calendar.DAY_OF_WEEK)) {
                    Calendar.MONDAY -> dayOfWeekFormat.format(Date(timestamp))
                    Calendar.TUESDAY -> dayOfWeekFormat.format(Date(timestamp))
                    Calendar.WEDNESDAY -> dayOfWeekFormat.format(Date(timestamp))
                    Calendar.THURSDAY -> dayOfWeekFormat.format(Date(timestamp))
                    Calendar.FRIDAY -> dayOfWeekFormat.format(Date(timestamp))
                    Calendar.SATURDAY -> dayOfWeekFormat.format(Date(timestamp))
                    Calendar.SUNDAY -> dayOfWeekFormat.format(Date(timestamp))
                    else -> dayOfWeekFormat.format(Date(timestamp))
                }
            }
            else -> SimpleDateFormat("dd/MM/yyyy 'à' HH'h'mm", Locale.getDefault()).format(Date(timestamp))
        }
    }

    private fun isToday(calendar: Calendar): Boolean {
        val today = Calendar.getInstance()
        return today.get(Calendar.YEAR) == calendar.get(Calendar.YEAR) &&
                today.get(Calendar.DAY_OF_YEAR) == calendar.get(Calendar.DAY_OF_YEAR)
    }

    private fun isThisWeek(calendar: Calendar): Boolean {
        val now = Calendar.getInstance()
        val thisWeek = now.get(Calendar.WEEK_OF_YEAR)
        return now.get(Calendar.YEAR) == calendar.get(Calendar.YEAR) &&
                thisWeek == calendar.get(Calendar.WEEK_OF_YEAR)
    }

    /**
     * Format a timestamp into a readable date string.
     * @param timestamp The timestamp to be formatted.
     * @param pattern The pattern to format the date. Defaults to "dd/MM/yyyy HH:mm:ss".
     * @return A formatted date string.
     */
    fun formatDate(timestamp: Long, pattern: String = "dd/MM/yyyy HH:mm:ss"): String {
        val sdf = SimpleDateFormat(pattern, Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    /**
     * Calculate the difference between two timestamps in human-readable form.
     * @param start The start timestamp.
     * @param end The end timestamp. Defaults to the current time.
     * @return A human-readable string indicating the time difference.
     */
    fun getTimeDifference(start: Long, end: Long = System.currentTimeMillis()): String {
        val diffInMillis = end - start

        val days = TimeUnit.MILLISECONDS.toDays(diffInMillis)
        val hours = TimeUnit.MILLISECONDS.toHours(diffInMillis) % 24
        val minutes = TimeUnit.MILLISECONDS.toMinutes(diffInMillis) % 60

        return when {
            days > 0 -> "$days days ago"
            hours > 0 -> "$hours hours ago"
            minutes > 0 -> "$minutes minutes ago"
            else -> "Just now"
        }
    }

    fun parseDate(dateStr: String): Long? {
        return try {
            val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = format.parse(dateStr)
            date?.time
        } catch (e: Exception) {
            null
        }
    }
}
