package com.vavyIndia.sms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.telephony.SmsMessage
import android.util.Log
import android.widget.Toast

class SmsReceiver : BroadcastReceiver() {

    companion object {
        const val TAG = "SmsReceiver"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == "android.provider.Telephony.SMS_RECEIVED") {
            handleSmsReceived(context, intent)
        } else if (intent?.action == ConnectivityManager.CONNECTIVITY_ACTION) {
            Log.d("Debug","Message to be sent")
            if (isInternetAvailable(context)) {
                sendPendingMessages(context)
            }
        }
    }

    private fun handleSmsReceived(context: Context?, intent: Intent?) {
        val bundle = intent?.extras
        val messages: Array<SmsMessage?>
        var str = ""

        if (bundle != null) {
            val pdus = bundle["pdus"] as Array<*>
            messages = arrayOfNulls(pdus.size)

            for (i in messages.indices) {
                messages[i] = SmsMessage.createFromPdu(pdus[i] as ByteArray, bundle.getString("format"))
                str += "SMS from ${messages[i]?.originatingAddress}"
                str += " :"
                str += messages[i]?.messageBody
                str += "\n"
            }

            Toast.makeText(context, str, Toast.LENGTH_SHORT).show()
            Log.d(TAG, "Received SMS: $str")

            if (isInternetAvailable(context)) {
                EmailUtils.sendEmail(context, "New SMS", str)
            } else {
                saveMessageToDatabase(context, "New SMS", str)
            }
        }
    }

    private fun saveMessageToDatabase(context: Context?, subject: String, body: String) {
        context?.let {
            val dbHelper = DatabaseHelper(it)
            dbHelper.addMessage(subject, body)
        }
    }

    private fun sendPendingMessages(context: Context?) {
        context?.let {
            val dbHelper = DatabaseHelper(it)
            val cursor = dbHelper.getAllMessages()

            if (cursor.moveToFirst()) {
                do {
                    val id = cursor.getInt(cursor.getColumnIndexOrThrow("_id"))
                    val subject = cursor.getString(cursor.getColumnIndexOrThrow("subject"))
                    val body = cursor.getString(cursor.getColumnIndexOrThrow("body"))
                    EmailUtils.sendEmail(it, subject, body)
                    dbHelper.deleteMessage(id)
                } while (cursor.moveToNext())
            }
            cursor.close()
        }
    }

    private fun isInternetAvailable(context: Context?): Boolean {
        val connectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        val networkInfo = connectivityManager?.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }
}
