package com.example.care

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_add.*


class AddActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        var a1= ArrayAdapter.createFromResource(this,R.array.binkcount,R.layout.spinner_item).also {
            arrayAdapter -> arrayAdapter.setDropDownViewResource(R.layout.spinner_item)
        }
        var a2= ArrayAdapter.createFromResource(this,R.array.alert,R.layout.spinner_item).also {
                arrayAdapter -> arrayAdapter.setDropDownViewResource(R.layout.spinner_item)
        }
        var a3= ArrayAdapter.createFromResource(this,R.array.sound,R.layout.spinner_item).also {
                arrayAdapter -> arrayAdapter.setDropDownViewResource(R.layout.spinner_item)
        }
        spinner.adapter = a1
        spinner2.adapter = a2
        spinner3.adapter = a3


        btnSave.setOnClickListener {

            if(addTxtName.text.isEmpty()){
                Toast.makeText(this,"กรุณาใส่หัวข้อการแจ้งเตือน",Toast.LENGTH_SHORT).show()
                addTxtName.requestFocus()
            }
            else{
                val gg = spinner.selectedItemId.toInt()
                val qry = "SELECT * FROM ${DBHandler.NOTIFI_TABLE_NAME} WHERE ${DBHandler.COLUMN_BINK} = $gg"
                val db2 = ListActivity.dbHandler.readableDatabase
                val cursor = db2.rawQuery(qry, null)
                if (cursor.count == 0) {
                    val notifi = Notifi()
                    notifi.notifiName = addTxtName.text.toString()
                    notifi.bink = spinner.selectedItemId.toInt()
                    notifi.stylealert = spinner2.selectedItemId.toInt()
                    notifi.stylesound = spinner3.selectedItemId.toInt()
                    notifi.msg = addTxtMsg.text.toString()
                    ListActivity.dbHandler.addNotifi(this,notifi)
                    addTxtMsg.text.clear()
                    addTxtName.text.clear()
                    addTxtName.requestFocus()
                    finish()
                } else{
                    Toast.makeText(this, " มีการแจ้งเตือนกระพริบตา "+(gg+1)+" ครั้งแล้ว", Toast.LENGTH_SHORT).show()
                }
            }
        }
        btnCC.setOnClickListener {
            addTxtMsg.text.clear()
            addTxtName.text.clear()
            finish()
        }
    }
}


