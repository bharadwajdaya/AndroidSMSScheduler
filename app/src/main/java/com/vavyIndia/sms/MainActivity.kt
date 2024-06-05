package com.vavyIndia.sms

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Bundle
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    companion object {
        const val SMS_PERMISSION_CODE = 123
    }

    private lateinit var smsAdapter: SmsAdapter
    private lateinit var listView: ListView
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var emptyView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        listView = findViewById(R.id.lv_sms)
        dbHelper = DatabaseHelper(this)
        emptyView = findViewById(R.id.empty_view)
        listView.emptyView = emptyView

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS),
                SMS_PERMISSION_CODE)
        }

        loadPendingMessagesAndRefresh()

        if (isInternetAvailable(this)) {
            sendPendingMessages()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == SMS_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
            } else {
                // Permission denied
            }
        }
    }

    private fun isInternetAvailable(context: Context?): Boolean {
        val connectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        val networkInfo = connectivityManager?.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    private fun sendPendingMessages() {
        val cursor = dbHelper.getAllMessages()

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("_id"))
                val subject = cursor.getString(cursor.getColumnIndexOrThrow("subject"))
                val body = cursor.getString(cursor.getColumnIndexOrThrow("body"))
                EmailUtils.sendEmail(this, subject, body)
                dbHelper.deleteMessage(id)
            } while (cursor.moveToNext())
        }
        cursor.close()

        // Refresh the messages list
        loadPendingMessagesAndRefresh()
    }

    private fun loadPendingMessagesAndRefresh() {
        val messages = loadPendingMessages(this)
        smsAdapter = SmsAdapter(this, messages)
        listView.adapter = smsAdapter
        smsAdapter.notifyDataSetChanged()
    }

    private fun loadPendingMessages(context: Context): List<SmsMessage> {
        val cursor = dbHelper.getAllMessages()
        val messages = mutableListOf<SmsMessage>()

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("_id"))
                val subject = cursor.getString(cursor.getColumnIndexOrThrow("subject"))
                val body = cursor.getString(cursor.getColumnIndexOrThrow("body"))
                messages.add(SmsMessage(id, subject, body))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return messages
    }
}
