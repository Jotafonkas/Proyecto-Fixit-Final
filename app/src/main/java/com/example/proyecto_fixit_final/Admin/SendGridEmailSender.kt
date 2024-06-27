package com.example.proyecto_fixit_final.Admin

import android.os.AsyncTask
import com.sendgrid.SendGrid
import com.sendgrid.Request
import com.sendgrid.Method
import com.sendgrid.helpers.mail.Mail
import com.sendgrid.helpers.mail.objects.Content
import com.sendgrid.helpers.mail.objects.Email
import com.sendgrid.helpers.mail.objects.Personalization

class SendGridEmailSender(
    private val apiKey: String,
    private val to: String,
    private val subject: String,
    private val body: String
) : AsyncTask<Void, Void, Void>() {

    override fun doInBackground(vararg params: Void?): Void? {
        try {
            val fromEmail = Email("your_email@example.com")
            val toEmail = Email(to)
            val content = Content("text/plain", body)
            val mail = Mail(fromEmail, subject, toEmail, content)
            val personalization = Personalization()
            personalization.addTo(toEmail)
            mail.addPersonalization(personalization)

            val sendGrid = SendGrid(apiKey)
            val request = Request()
            request.method = Method.POST
            request.endpoint = "mail/send"
            request.body = mail.build()

            val response = sendGrid.api(request)
            println("Status Code: ${response.statusCode}")
            println("Body: ${response.body}")
            println("Headers: ${response.headers}")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}
