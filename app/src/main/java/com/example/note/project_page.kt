package com.example.note

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AlertDialog

class project_page : AppCompatActivity() {

    internal lateinit var db: DB
    internal var project: List<project> = ArrayList()
    lateinit var call_add_dialog: ImageButton
    lateinit var listproject: ListView
    lateinit var project_count: TextView
    lateinit var search_project_name: EditText
    lateinit var search_project_button: ImageButton

    override fun onBackPressed() {

    }

    @SuppressLint("CommitPrefEdits")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        db = DB(this)
        call_add_dialog = findViewById(R.id.add_project)
        listproject = findViewById(R.id.list_projects)
        project_count = findViewById(R.id.project_count)
        search_project_name = findViewById(R.id.search_project_name)
        search_project_button = findViewById(R.id.search_project)

    }

    override fun onResume() {
        super.onResume()

        //add project dialog
        var have_password = false
        val add_project_view: View = layoutInflater.inflate(R.layout.add_project_view, null)
        val add_project_alertdialog = AlertDialog.Builder(this)
        add_project_alertdialog.setView(add_project_view)
        val add_project_name = add_project_view.findViewById<EditText>(R.id.add_project_name)
        val need_password = add_project_view.findViewById<ToggleButton>(R.id.need_password)
        val add_password = add_project_view.findViewById<EditText>(R.id.add_password)
        val add_cancel = add_project_view.findViewById<Button>(R.id.cancel)
        val add_complete = add_project_view.findViewById<Button>(R.id.add)
        val add_project_dialog = add_project_alertdialog.create()
        add_project_dialog.setCancelable(false)
        add_project_dialog.setCanceledOnTouchOutside(false)

        refresh()
        call_add_dialog.setOnClickListener {
            add_project_dialog.show()
        }

        need_password.setOnCheckedChangeListener { compoundButton, ischecked ->
            add_password.isEnabled = ischecked
            have_password = ischecked
            add_password.setText("")
        }
        add_cancel.setOnClickListener {
            add_project_name.setText("")
            add_password.setText("")
            add_project_dialog.dismiss()
        }
        add_complete.setOnClickListener {
            if(add_project_name.text.toString() == "")
                Toast.makeText(this, "請輸入項目名稱!", Toast.LENGTH_LONG).show()
            else if(db.checkrepeatname(add_project_name.text.toString(), 0))
                Toast.makeText(this, "項目名稱重複!", Toast.LENGTH_LONG).show()
            else if(add_project_name.text.toString().contains(" "))
                Toast.makeText(this, "項目名稱不可有空白!", Toast.LENGTH_LONG).show()
            else if(have_password && add_password.text.toString() == "")
                Toast.makeText(this, "請輸入密碼!", Toast.LENGTH_LONG).show()
            else if(have_password && add_password.text.toString().contains(" "))
                Toast.makeText(this, "密碼不可有空白!", Toast.LENGTH_LONG).show()
            else{
                val tmp_dialog = android.app.AlertDialog.Builder(this)
                tmp_dialog.setTitle("確認")
                tmp_dialog.setMessage("確定新增?")
                tmp_dialog.setNegativeButton("取消"){ _, _ ->
                }
                tmp_dialog.setPositiveButton("確定") { _, _ ->
                    if(have_password){
                        val project = project(
                                0,
                                add_project_name.text.toString(),
                                1,
                                add_password.text.toString()
                        )
                        db.addproject(project)
                    }
                    else{
                        val project = project(
                                0,
                                add_project_name.text.toString(),
                                0,
                                " "
                        )
                        db.addproject(project)
                    }
                    refresh()
                    add_project_name.setText("")
                    need_password.isChecked = false
                    add_password.setText("")
                    add_project_dialog.dismiss()
                }
                tmp_dialog.show()
            }
        }

        //search
        search_project_button.setOnClickListener {
            if(search_project_name.text.toString() == ""){
                refresh()
            }
            else{
                search_refresh(search_project_name.text.toString())
            }
        }

        //enter_password
        var current_click = 0;
        val enter_password_view: View = layoutInflater.inflate(R.layout.enter_password_view, null)
        val enter_password = android.app.AlertDialog.Builder(this)
        enter_password.setView(enter_password_view)
        val password_dialog = enter_password.create()
        val password_enter = enter_password_view.findViewById<EditText>(R.id.editTextTextPassword)
        val password_cancel = enter_password_view.findViewById<Button>(R.id.enter_password_cancel)
        val password_enter_complete = enter_password_view.findViewById<Button>(R.id.password_enter_button)
        val delete = enter_password_view.findViewById<Button>(R.id.delete)
        password_dialog.setCancelable(false)
        password_dialog.setCanceledOnTouchOutside(false)

        password_cancel.setOnClickListener {
            password_enter.setText("")
            password_dialog.dismiss()
        }
        password_enter_complete.setOnClickListener {
            if(password_enter.text.toString() == project[current_click].password){
                val intent = Intent()
                intent.putExtra("ID", project[current_click].id)
                intent.setClass(this, note_page::class.java)
                startActivity(intent)
            }
            else{
                password_enter.setText("")
                val tmp_dialog = android.app.AlertDialog.Builder(this)
                tmp_dialog.setTitle("錯誤")
                tmp_dialog.setMessage("密碼輸入錯誤!")
                tmp_dialog.show()
            }
        }

        delete.setOnClickListener {
            val tmp_dialog = android.app.AlertDialog.Builder(this)
            tmp_dialog.setTitle("確認")
            tmp_dialog.setMessage("確定刪除?")
            tmp_dialog.setNegativeButton("取消"){ _, _ ->
            }
            tmp_dialog.setPositiveButton("確定") { _, _ ->
                db.deleteproject(project[current_click].id)
                refresh()
                password_dialog.dismiss()
            }
            tmp_dialog.show()
        }

        listproject.setOnItemClickListener { adapterView, view, i, l ->
            current_click = i
            if(project[i].need_password == 1){
                password_dialog.show()
            }
            else{
                val intent = Intent()
                intent.putExtra("ID", project[current_click].id)
                intent.setClass(this, note_page::class.java)
                startActivity(intent)
            }
        }

    }

    fun refresh() {
        project = db.allproject
        val adapter = project_adapter(this, project)
        listproject.adapter = adapter
        project_count.text = project.size.toString()
    }

    fun search_refresh(name: String){
        project = db.findproject(name)
        val adapter = project_adapter(this, project)
        listproject.adapter = adapter
        project_count.text = project.size.toString()
    }
}