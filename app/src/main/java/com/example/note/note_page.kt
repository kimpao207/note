package com.example.note

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AlertDialog

class note_page : AppCompatActivity() {

    var id: Int = 0
    internal lateinit var db: DB
    internal var total_note: List<note> = ArrayList()
    lateinit var listnote: ListView
    lateinit var call_edit_dialog: ImageButton
    lateinit var back: ImageButton
    lateinit var add_note: ImageButton
    lateinit var note_title: TextView
    lateinit var note_count: TextView
    lateinit var currentproject: project
    lateinit var search_note_name: EditText
    lateinit var search_note_button: ImageButton

    override fun onBackPressed() {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_page)

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        id = this.intent.getIntExtra("ID", 0)
        db = DB(this)
        currentproject = db.getproject(id)
        listnote = findViewById(R.id.list_notes)
        note_count = findViewById(R.id.note_count)
        add_note = findViewById(R.id.add_note)
        back = findViewById(R.id.back_to_project)
        call_edit_dialog = findViewById(R.id.note_edit)
        note_title = findViewById(R.id.project_title)
        note_title.text = currentproject.name
        search_note_name = findViewById(R.id.search_note_name)
        search_note_button = findViewById(R.id.search_note)
    }

    override fun onResume() {
        super.onResume()

        refresh()
        //edit project dialog
        var have_password = currentproject.need_password == 1
        val edit_project_view: View = layoutInflater.inflate(R.layout.add_project_view, null)
        val edit_project_alertdialog = AlertDialog.Builder(this)
        edit_project_alertdialog.setView(edit_project_view)
        val edit_project_name = edit_project_view.findViewById<EditText>(R.id.add_project_name)
        val need_password = edit_project_view.findViewById<ToggleButton>(R.id.need_password)
        val edit_password = edit_project_view.findViewById<EditText>(R.id.add_password)
        val edit_cancel = edit_project_view.findViewById<Button>(R.id.cancel)
        val edit_complete = edit_project_view.findViewById<Button>(R.id.add)
        val edit_delete = edit_project_view.findViewById<Button>(R.id.delete)
        val edit_project_dialog = edit_project_alertdialog.create()
        edit_project_dialog.setCancelable(false)
        edit_project_dialog.setCanceledOnTouchOutside(false)

        //set edit data
        edit_delete.visibility = View.VISIBLE
        edit_project_name.setText(currentproject.name)
        if(currentproject.need_password == 1){
            edit_password.isEnabled = true
            need_password.isChecked = true
            edit_password.setText(currentproject.password)
        }
        else if(currentproject.need_password == 0){
            edit_password.isEnabled = false
            need_password.isChecked = false
        }
        //start edit dialog
        call_edit_dialog.setOnClickListener {
            edit_project_dialog.show()
        }

        need_password.setOnCheckedChangeListener { compoundButton, ischecked ->
            edit_password.isEnabled = ischecked
            have_password = ischecked
            edit_password.setText("")
        }

        edit_cancel.setOnClickListener {
            edit_project_name.setText(currentproject.name)
            if(currentproject.need_password == 1){
                edit_password.isEnabled = true
                need_password.isChecked = true
                edit_password.setText(currentproject.password)
            }
            else if(currentproject.need_password == 0){
                edit_password.isEnabled = false
                need_password.isChecked = false
            }
            edit_project_dialog.dismiss()
        }

        edit_delete.setOnClickListener {
            val tmp_dialog = android.app.AlertDialog.Builder(this)
            tmp_dialog.setTitle("確認")
            tmp_dialog.setMessage("確定刪除?")
            tmp_dialog.setNegativeButton("取消"){ _, _ ->
            }
            tmp_dialog.setPositiveButton("確定") { _, _ ->
                db.deleteproject(currentproject.id)
                val intent = Intent()
                intent.setClass(this, project_page::class.java)
                startActivity(intent)
            }
            tmp_dialog.show()
        }

        edit_complete.setOnClickListener {
            if(edit_project_name.text.toString() == "")
                Toast.makeText(this, "請輸入項目名稱!", Toast.LENGTH_LONG).show()
            else if((edit_project_name.text.toString() == currentproject.name && db.checkrepeatname(edit_project_name.text.toString(), 1))
                    || (edit_project_name.text.toString() != currentproject.name && db.checkrepeatname(edit_project_name.text.toString(), 0)))
                Toast.makeText(this, "項目名稱重複!", Toast.LENGTH_LONG).show()
            else if(edit_project_name.text.toString().contains(" "))
                Toast.makeText(this, "項目名稱不可有空白!", Toast.LENGTH_LONG).show()
            else if(have_password && edit_password.text.toString() == "")
                Toast.makeText(this, "請輸入密碼!", Toast.LENGTH_LONG).show()
            else if(have_password && edit_password.text.toString().contains(" "))
                Toast.makeText(this, "密碼不可有空白!", Toast.LENGTH_LONG).show()
            else{
                val tmp_dialog = android.app.AlertDialog.Builder(this)
                tmp_dialog.setTitle("確認")
                tmp_dialog.setMessage("確定修改?")
                tmp_dialog.setNegativeButton("取消"){ _, _ ->
                }
                tmp_dialog.setPositiveButton("確定") { _, _ ->
                    if(have_password){
                        val project = project(
                                id,
                                edit_project_name.text.toString(),
                                1,
                                edit_password.text.toString()
                        )
                        db.updateproject(project)
                    }
                    else{
                        val project = project(
                                id,
                                edit_project_name.text.toString(),
                                0,
                                " "
                        )
                        db.updateproject(project)
                    }
                    val intent = Intent()
                    intent.setClass(this, project_page::class.java)
                    startActivity(intent)
                }
                tmp_dialog.show()
            }
        }

        search_note_button.setOnClickListener {
            if(search_note_name.text.toString() == "")
                refresh()
            else
                search_refresh(search_note_name.text.toString())
        }

        listnote.setOnItemClickListener { adapterView, view, i, l ->
            val intent = Intent()
            intent.putExtra("ID", total_note[i].id)
            intent.putExtra("note_id", total_note[i].note_id)
            intent.putExtra("mode", 1)
            intent.setClass(this, add_edit_note::class.java)
            startActivity(intent)
        }

        add_note.setOnClickListener {
            val intent = Intent()
            intent.setClass(this, add_edit_note::class.java)
            intent.putExtra("ID", id)
            intent.putExtra("mode", 0)
            startActivity(intent)
        }

        back.setOnClickListener {
            val intent = Intent()
            intent.setClass(this, project_page::class.java)
            startActivity(intent)
        }
    }

    fun refresh(){
        total_note = db.allnote(id)
        val adapter = note_adapter(this, total_note)
        listnote.adapter = adapter
        note_count.text = total_note.size.toString()
    }
    fun search_refresh(name: String){
        total_note = db.findnote(id, name)
        val adapter = note_adapter(this, total_note)
        listnote.adapter = adapter
        note_count.text = total_note.size.toString()
    }
}