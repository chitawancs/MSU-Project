package com.example.care
import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_list.*
class ListActivity : AppCompatActivity() {

    companion object{
        lateinit var dbHandler: DBHandler
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        dbHandler = DBHandler(this,null,null,1)

        viewNotifis()
        flo.setOnClickListener {
            val intent_mainpage = Intent(this, AddActivity::class.java)
            startActivity(intent_mainpage)
        }
    }

    @SuppressLint("WrongConstant")
    private fun viewNotifis(){
        val notifilist : ArrayList<Notifi> = dbHandler.getNotifis(this)
        val adapter = NotifiAdapter(this,notifilist)
        val rv : RecyclerView = findViewById(R.id.rv)
        rv.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        rv.adapter = adapter
    }

    override fun onResume(){
        viewNotifis()
        super.onResume()
    }
}
