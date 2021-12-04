package com.example.note

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*


class add_edit_note : AppCompatActivity() {

    var id: Int = 0
    internal lateinit var db: DB
    lateinit var note_title_enter: EditText
    lateinit var time_notice: ToggleButton
    lateinit var date_choose: Button
    lateinit var time_choose: Button
    lateinit var content: EditText
    lateinit var add_edit: Button
    lateinit var note_cancel: Button
    lateinit var back_to_note: ImageButton
    lateinit var content_view: TextView
    lateinit var tmpnote: note
    val calender = Calendar.getInstance()
    var mode = 0 //0 = add, 1 = edit
    var havenotice = 0
    var enter_edit = false

    override fun onBackPressed() {

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_note)

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        id = this.intent.getIntExtra("ID", 0)
        mode = this.intent.getIntExtra("mode", 0)

        db = DB(this)
        back_to_note = findViewById(R.id.back_to_note)
        note_title_enter = findViewById(R.id.note_title_enter)
        time_notice = findViewById(R.id.time_notice)
        date_choose = findViewById(R.id.date_choose)
        time_choose = findViewById(R.id.time_choose)
        content = findViewById(R.id.content)
        add_edit = findViewById(R.id.add_edit)
        note_cancel = findViewById(R.id.note_cancel)
        content_view = findViewById(R.id.content_view)
        content_view.setMovementMethod(ScrollingMovementMethod.getInstance());

        if(mode == 1){
            val note_id = this.intent.getIntExtra("note_id",0)
            tmpnote = db.get_note(id, note_id)
        }
        else if(mode == 0){
            tmpnote = note(0, 0,"iluedubngkdbfnkjdsfbgnerljbgfbcmvbigjhjhfvghjcbvcrswaqx", 0,  "0", "0", "0")
            date_choose.text = "無"
            time_choose.text = "無"
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()

        if(mode == 1){
            note_title_enter.setText(tmpnote.title)
            havenotice = tmpnote.need_notice
            time_notice.isChecked = havenotice == 1
            content.setText(tmpnote.content)
            content.visibility = View.INVISIBLE
            content_view.text = tmpnote.content
            content_view.visibility = View.VISIBLE
            add_edit.text = "編輯"
            note_cancel.visibility = View.VISIBLE
            if(havenotice == 1){
                date_choose.text = tmpnote.notice_date
                time_choose.text = tmpnote.notice_time
                calender.time = string_to_date("yyyy/MM/dd HH:mm", tmpnote.notice_date!!, tmpnote.notice_time!!)!!
            }
            else{
                date_choose.text = "無"
                time_choose.text = "無"
            }

            note_title_enter.isEnabled = false
            time_notice.isEnabled = false
            date_choose.isEnabled = false
            time_choose.isEnabled = false
            content.isEnabled = false
            content_view.isEnabled = true
        }
        else if (mode == 0){
            content.visibility = View.VISIBLE
            content_view.visibility = View.INVISIBLE
        }

        time_notice.setOnCheckedChangeListener { _, ischecked ->
            if(ischecked) {
                havenotice = 1
                date_choose.isEnabled = true
                time_choose.isEnabled = true
            }
            else{
                havenotice = 0
                date_choose.text = "無"
                time_choose.text = "無"
                date_choose.isEnabled = false
                time_choose.isEnabled = false
            }
        }
        date_choose.setOnClickListener(listener)
        time_choose.setOnClickListener(listener)

        add_edit.setOnClickListener {
            if((enter_edit && mode == 1) || mode == 0){
                if(note_title_enter.text.toString() == "")
                    Toast.makeText(this, "請輸入標題!", Toast.LENGTH_LONG).show()
                else if((note_title_enter.text.toString() == tmpnote.title && db.checkrepeattitle(id, note_title_enter.text.toString(), 1))
                        || (note_title_enter.text.toString() != tmpnote.title && db.checkrepeattitle(id, note_title_enter.text.toString(), 0)))
                    Toast.makeText(this, "標題重複!", Toast.LENGTH_LONG).show()
                else if(note_title_enter.text.toString().contains(" "))
                    Toast.makeText(this, "標題不可有空白!", Toast.LENGTH_LONG).show()
                else if(havenotice == 1 && date_choose.text.toString() == "無")
                    Toast.makeText(this, "請選擇日期!", Toast.LENGTH_LONG).show()
                else if(havenotice == 1 && time_choose.text.toString() == "無")
                    Toast.makeText(this, "請選擇時間!", Toast.LENGTH_LONG).show()
                else{
                    val tmp_dialog = android.app.AlertDialog.Builder(this)
                    tmp_dialog.setTitle("確認")
                    if(mode == 0)
                        tmp_dialog.setMessage("確定新增?")
                    else if(mode == 1)
                        tmp_dialog.setMessage("確定修改?")

                    tmp_dialog.setNegativeButton("取消"){ _, _ ->
                    }
                    tmp_dialog.setPositiveButton("確定") { _, _ ->

                        val del_note = note(
                                id,
                                0,
                                note_title_enter.text.toString(),
                                havenotice,
                                date_choose.text.toString(),
                                time_choose.text.toString(),
                                content.text.toString()
                        )

                        if(mode == 0){
                            db.add_note(del_note)
                            if(havenotice == 1)
                                setAlarm(del_note, false)

                            val intent = Intent()
                            intent.putExtra("ID", id)
                            intent.setClass(this, note_page::class.java)
                            startActivity(intent)
                        }

                        else if(mode == 1){
                            del_note.note_id = tmpnote.note_id

                            val cancel = arrayOf(false, false) //1 start set alarm, 2 cancel alarm
                            if(tmpnote.need_notice == 0 && havenotice == 1){
                                cancel[0] = true
                                cancel[1] = false
                            }
                            else if(tmpnote.need_notice == 1 && havenotice == 0){
                                cancel[0] = true
                                cancel[1] = true
                            }
                            else if(tmpnote.need_notice == 1 && havenotice == 1){
                                cancel[0] = true
                                cancel[1] = false
                            }

                            db.update_note(del_note)
                            if(cancel[0])
                                setAlarm(del_note, cancel[1])


                            val intent = Intent()
                            intent.putExtra("ID", id)
                            intent.putExtra("note_id", tmpnote.note_id)
                            intent.putExtra("mode", 1)
                            intent.setClass(this, add_edit_note::class.java)
                            startActivity(intent)
                        }
                    }
                    tmp_dialog.show()
                }
            }
            else if(!enter_edit && mode == 1){
                enter_edit = true
                add_edit.text = "完成"
                note_cancel.text = "取消"


                note_title_enter.isEnabled = true
                time_notice.isEnabled = true
                if(havenotice == 1) {
                    date_choose.isEnabled = true
                    time_choose.isEnabled = true
                }
                content.isEnabled = true
                content.visibility = View.VISIBLE
                content_view.isEnabled = false
                content_view.visibility = View.INVISIBLE
            }
        }

        note_cancel.setOnClickListener {
            if(enter_edit){
                enter_edit = false
                note_title_enter.setText(tmpnote.title)
                havenotice = tmpnote.need_notice
                time_notice.isChecked = havenotice == 1
                date_choose.text = tmpnote.notice_date
                time_choose.text = tmpnote.notice_time
                content.setText(tmpnote.content)
                add_edit.text = "編輯"
                note_cancel.text = "刪除"

                note_title_enter.isEnabled = false
                time_notice.isEnabled = false
                date_choose.isEnabled =false
                time_choose.isEnabled = false
                content.isEnabled = false
                content.visibility = View.INVISIBLE
                content_view.isEnabled = true
                content_view.visibility = View.VISIBLE
            }
            else{
                val tmp_dialog = AlertDialog.Builder(this)
                tmp_dialog.setTitle("確認")
                tmp_dialog.setMessage("確定刪除?")

                tmp_dialog.setNegativeButton("取消"){ _, _ ->
                }
                tmp_dialog.setPositiveButton("確定") { _, _ ->
                    db.delete_note(tmpnote.id, tmpnote.note_id)
                    setAlarm(tmpnote, true)
                    val intent = Intent()
                    intent.putExtra("ID", id)
                    intent.setClass(this, note_page::class.java)
                    startActivity(intent)
                }
                tmp_dialog.show()
            }
        }

        back_to_note.setOnClickListener {
            val intent = Intent()
            intent.putExtra("ID", id)
            intent.setClass(this, note_page::class.java)
            startActivity(intent)
        }
    }

    val listener = View.OnClickListener {
        when (it) {
            date_choose -> {
                datePicker()
            }

            time_choose -> {
                timePicker()
            }
        }
    }

    fun datePicker() {
        DatePickerDialog(this,
                dateListener,
                calender.get(Calendar.YEAR),
                calender.get(Calendar.MONTH),
                calender.get(Calendar.DAY_OF_MONTH)).show()
    }

    fun timePicker() {
        TimePickerDialog(this,
                timeListener,
                calender.get(Calendar.HOUR_OF_DAY),
                calender.get(Calendar.MINUTE),
                true
        ).show()
    }

    val dateListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
        calender.set(year, month, day)
        format("yyyy/MM/dd", date_choose)
    }

    val timeListener = TimePickerDialog.OnTimeSetListener { _, hour, min->
        calender.set(Calendar.HOUR_OF_DAY, hour)
        calender.set(Calendar.MINUTE, min)
        format("HH:mm", time_choose)
    }

    fun format(format: String, view: View) {
        val time = SimpleDateFormat(format, Locale.TAIWAN)
        (view as Button).setText(time.format(calender.time))
    }

    fun string_to_date(format: String, enter_date: String, enter_time: String): Date? {
        val time = SimpleDateFormat(format, Locale.TAIWAN)
        val returntime = time.parse("$enter_date $enter_time")
        return returntime
    }

    @SuppressLint("CommitPrefEdits")
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun setAlarm(note: note, cancel: Boolean) {

        val alarmmanager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent1 = Intent(this, alarmReceiver::class.java)
        var tmp_note_id = 0
        intent1.putExtra("ID", note.id)

        if(mode == 1) {
            tmp_note_id = note.note_id
        }
        else if(mode == 0){
            tmp_note_id = db.get_note_id(note.id, note.title!!)
        }
        intent1.putExtra("note_id", tmp_note_id)

        val pendingIntent = PendingIntent.getBroadcast(this, tmp_note_id, intent1, 0)
        if(!cancel)
            alarmmanager.setExact(AlarmManager.RTC_WAKEUP, calender.timeInMillis, pendingIntent)
        else if(cancel){
            try {
                alarmmanager.cancel(pendingIntent)
                Log.e("success", "Alarm is Canceled: " + note.title);
            }
            catch (e: Exception){
                Log.e("error", "Alarm is not Canceled: $e");
            }
        }
    }
}