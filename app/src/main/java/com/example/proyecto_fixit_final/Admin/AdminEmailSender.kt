package com.example.proyecto_fixit_final.utils

import android.os.AsyncTask
import java.util.Properties
import javax.mail.Message
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class AdminEmailSender(
    private val to: String,
    private val subject: String,
    private val body: String,
    body1: String
) : AsyncTask<Void, Void, Void>() {

    override fun doInBackground(vararg params: Void?): Void? {
        try {
            val props = Properties()
            props["mail.smtp.host"] = "smtp.gmail.com"
            props["mail.smtp.socketFactory.port"] = "465"
            props["mail.smtp.socketFactory.class"] = "javax.net.ssl.SSLSocketFactory"
            props["mail.smtp.auth"] = "true"
            props["mail.smtp.port"] = "465"

            val session = Session.getDefaultInstance(props,
                object : javax.mail.Authenticator() {
                    override fun getPasswordAuthentication(): PasswordAuthentication {
                        return PasswordAuthentication("your_email@gmail.com", "your_password")
                    }
                })

            val mm = MimeMessage(session)
            mm.setFrom(InternetAddress("your_email@gmail.com"))
            mm.addRecipient(Message.RecipientType.TO, InternetAddress(to))
            mm.subject = subject
            mm.setText(body)

            Transport.send(mm)

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}
