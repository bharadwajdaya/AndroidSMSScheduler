package com.vavyIndia.sms

import android.content.Context
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Properties
import javax.mail.Message
import javax.mail.MessagingException
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

object EmailUtils {
    private const val TAG = "EmailUtils"

    fun sendEmail(context: Context?, subject: String, body: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val username = "barrycode143@gmail.com"
            val password = "xagrxmmjjmtikiyv" // App-specific password

            val props = Properties().apply {
                put("mail.smtp.auth", "true")
                put("mail.smtp.starttls.enable", "true")
                put("mail.smtp.host", "smtp.gmail.com")
                put("mail.smtp.port", "587")
            }

            val session = Session.getInstance(props,
                object : javax.mail.Authenticator() {
                    override fun getPasswordAuthentication(): PasswordAuthentication {
                        return PasswordAuthentication(username, password)
                    }
                })

            try {
                val message = MimeMessage(session).apply {
                    setFrom(InternetAddress(username))
                    setRecipients(Message.RecipientType.TO, InternetAddress.parse("bharadwajdaya@gmail.com"))
                    setSubject(subject)
                    setText(body)
                }

                Transport.send(message)
                withContext(Dispatchers.Main) {
                    Log.d(TAG, "Email sent successfully")
                    Toast.makeText(context, "Email sent successfully", Toast.LENGTH_SHORT).show()
                }
            } catch (e: MessagingException) {
                withContext(Dispatchers.Main) {
                    Log.e(TAG, "Error sending email", e)
                    Toast.makeText(context, "Failed to send email", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}