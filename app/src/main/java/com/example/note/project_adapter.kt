package com.example.note

import android.content.Context
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

class project_adapter(activity: project_page,
                      internal var tmpproject:List<project>): BaseAdapter() {

    internal var inflater: LayoutInflater

    init{
        inflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }
    lateinit var name: TextView
    lateinit var lock: ImageView

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val row: View
        row = inflater.inflate(R.layout.project_row, null)

        name = row.findViewById(R.id.txt_name)
        lock = row.findViewById(R.id.islocked)

        name.text = tmpproject[p0].name
        if(tmpproject[p0].need_password == 1)
            lock.visibility = View.VISIBLE
        else
            lock.visibility = View.INVISIBLE

        return row
    }


    override fun getItem(p0: Int): Any {
        return tmpproject[p0]
    }

    override fun getItemId(p0: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        return tmpproject.size
    }
}