package com.example.note

import android.content.Context
import android.os.strictmode.WebViewMethodCalledOnWrongThreadViolation
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class note_adapter(activity: note_page,
                   internal var tmpnote:List<note>): BaseAdapter() {

    internal var inflater: LayoutInflater
    init{
        inflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }
    lateinit var note_title: TextView
    lateinit var txt_time: TextView
    lateinit var note_time: TextView

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val row: View
        row = inflater.inflate(R.layout.note_row, null)

        note_title = row.findViewById(R.id.txt_title)
        txt_time = row.findViewById(R.id.txt_time)
        note_time = row.findViewById(R.id.set_time)

        note_title.text = tmpnote[p0].title
        if(tmpnote[p0].need_notice == 1){
            txt_time.visibility = View.VISIBLE
            note_time.visibility = View.VISIBLE
            note_time.text = tmpnote[p0].notice_date + " " + tmpnote[p0].notice_time
        }
        else{
            txt_time.visibility = View.INVISIBLE
            note_time.visibility = View.INVISIBLE
        }
        return row
    }

    override fun getItem(p0: Int): Any {
        return tmpnote[p0]
    }

    override fun getItemId(p0: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        return tmpnote.size
    }
}