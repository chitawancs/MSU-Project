package com.example.care

import android.content.RestrictionEntry.TYPE_NULL
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_reg.*

class regActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reg)
        val deviceID = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        txtAdID.setText(deviceID)
        txtAdID.setInputType(TYPE_NULL)
        val db = FirebaseFirestore.getInstance()
        btnCC.setOnClickListener {
            btnCC.setImageResource(R.drawable.cc2)
            finish()
        }
            btnSave.setOnClickListener {
                btnSave.setImageResource(R.drawable.ss2)
                if (txtTkID.text.isEmpty()) {
                    Toast.makeText(this, "กรุณาใส่ TinkerBoard ID ", Toast.LENGTH_SHORT).show()
                    txtTkID.requestFocus()
                    btnSave.setImageResource(R.drawable.ss1)
                }else{
                    val tkid = txtTkID.text.toString()
                    val data = hashMapOf(
                        "adID" to deviceID,
                        "tkID" to tkid
                    )
                    val insert = db.collection("users").whereEqualTo("adID", deviceID)
                    insert.get()
                        .addOnSuccessListener { result ->
                            if (result.isEmpty) {
                                Toast.makeText(this, "กำลังลงทะเบียน", Toast.LENGTH_SHORT).show()
                                db.collection("users")
                                    .add(data as Map<String, Any>)
                                    .addOnSuccessListener { documentReference ->
                                        Toast.makeText(
                                            this,
                                            "ลงทะเบียนกับ tkID : " + tkid + " สำเร็จ",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        finish()
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
                                    }
                            }else{
                                for (document in result) {
                                    for (i in 0..2) {
                                        Toast.makeText(this, "อุปกรณ์นี้มีการลงทะเบียนกับ "+"TinkerBoard ID : ${document.data.get("tkID")} แล้ว", Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    finish()
                                }
                            }
                        }
                        .addOnFailureListener { exception ->
                            Toast.makeText(this, "Error getting documents.", Toast.LENGTH_SHORT).show()
                        }
                }
            }
    }
}
