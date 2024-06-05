package com.vavyIndia.sms

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class SmsAdapter(private val context: Context, private val messages: List<SmsMessage>) : BaseAdapter() {

    override fun getCount(): Int {
        return messages.size
    }

    override fun getItem(position: Int): Any {
        return messages[position]
    }

    override fun getItemId(position: Int): Long {
        return messages[position].id.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_item_sms, parent, false)

        val tvSubject = view.findViewById<TextView>(R.id.tv_subject)
        val tvBody = view.findViewById<TextView>(R.id.tv_body)

        val message = messages[position]
        tvSubject.text = message.subject
        tvBody.text = message.body

        return view
    }
}