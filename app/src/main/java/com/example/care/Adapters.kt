package com.example.care

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.lo_notifi_update.view.*
import kotlinx.android.synthetic.main.lo_notifis.view.*

class NotifiAdapter(mCtx : Context,val notifis : ArrayList<Notifi>) : RecyclerView.Adapter<NotifiAdapter.ViewHolder>(){

    val mCtx = mCtx

    class  ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtNotifiName = itemView.txtNotifiName
        val txtMsg = itemView.txtMsg
        val btnDelete = itemView.btnDelete
        val list_a = itemView.list_a

    }
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): NotifiAdapter.ViewHolder {
       val v = LayoutInflater.from(p0.context).inflate(R.layout.lo_notifis,p0,false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return  notifis.size
    }


    override fun onBindViewHolder(p0: NotifiAdapter.ViewHolder, p1: Int) {
        val notifi: Notifi = notifis[p1]
        p0.txtNotifiName.text = notifi.notifiName
        p0.txtMsg.text = notifi.msg
        p0.btnDelete.setOnClickListener {
            val notifiName : String = notifi.notifiName

            val  alertDialog = AlertDialog.Builder(mCtx)
                .setTitle("คำเตือน")
                .setMessage("คุณแน่ใจที่จะลบการแจ้งเตือน : $notifiName ?")
                .setPositiveButton("ใฃ่",DialogInterface.OnClickListener { dialogInterface, which ->
                    if (ListActivity.dbHandler.deleteNotifi(notifi.notifiID))
                    {
                        notifis.removeAt(p1)
                        notifyItemRemoved(p1)
                        notifyItemRangeChanged(p1,notifis.size)
                        Toast.makeText(mCtx,"ทำการลบการแจ้งเตือน $notifiName สำเร็จ",Toast.LENGTH_SHORT).show()
                    }else
                        Toast.makeText(mCtx,"ไม่สามารถลบได้ กรุณราลองอีกครั้ง",Toast.LENGTH_SHORT).show()
                })
                .setNegativeButton("ไม่", DialogInterface.OnClickListener { dialogInterface, i ->  })
                .setIcon(R.drawable.ic_warning_black_24dp)
                .show()
        }

        p0.list_a.setOnClickListener{
            val inflater = LayoutInflater.from(mCtx)
            val view = inflater.inflate(R.layout.lo_notifi_update, null)
            val txtName : TextView = view.findViewById(R.id.addTxtNameUpdate)
            val txtMsg : TextView = view.findViewById(R.id.addTxtMsgUpdate)
            val bink : Spinner = view.findViewById(R.id.spinner)
            val alert : Spinner = view.findViewById(R.id.spinner2update)
            val sound : Spinner = view.findViewById(R.id.spinner3update)

            var a1= ArrayAdapter.createFromResource(mCtx,R.array.binkcount,R.layout.spinner_item).also {
                    arrayAdapter -> arrayAdapter.setDropDownViewResource(R.layout.spinner_item)
            }
            var a2= ArrayAdapter.createFromResource(mCtx,R.array.alert,R.layout.spinner_item).also {
                    arrayAdapter -> arrayAdapter.setDropDownViewResource(R.layout.spinner_item)
            }
            var a3= ArrayAdapter.createFromResource(mCtx,R.array.sound,R.layout.spinner_item).also {
                    arrayAdapter -> arrayAdapter.setDropDownViewResource(R.layout.spinner_item)
            }

            txtName.text = notifi.notifiName
            bink.adapter = a1
            bink.setSelection(notifi.bink)
            alert.adapter = a2
            alert.setSelection(notifi.stylealert)
            sound.adapter = a3
            sound.setSelection(notifi.stylesound)
            txtMsg.text = notifi.msg

            val same = notifi.bink


            val builder =  AlertDialog.Builder(mCtx,android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen)
                .setTitle(("แก้ไขการแจ้งเตือน"))
                .setView(view)

                .setPositiveButton("บันทึก",DialogInterface.OnClickListener { doalog, which ->
                    if(view.addTxtNameUpdate.text.isEmpty()){
                        Toast.makeText(mCtx,"กรุณาใส่หัวข้อการแจ้งเตือน",Toast.LENGTH_SHORT).show()
                        view.addTxtNameUpdate.requestFocus()
                    }
                    else{
                        val gg =  view.spinner.selectedItemId.toInt()
                        val qry = "SELECT * FROM ${DBHandler.NOTIFI_TABLE_NAME} WHERE ${DBHandler.COLUMN_BINK} = $gg"
                        val db2 = ListActivity.dbHandler.readableDatabase
                        val cursor = db2.rawQuery(qry, null)

                        if(cursor.count==0){
                            val isUpdate : Boolean = ListActivity.dbHandler.UpdateNotifi(
                                notifi.notifiID.toString(),
                                view.addTxtNameUpdate.text.toString(),
                                view.spinner.selectedItemId.toInt(),
                                view.spinner2update.selectedItemId.toInt(),
                                view.spinner3update.selectedItemId.toInt(),
                                view.addTxtMsgUpdate.text.toString())

                            if(isUpdate == true){
                                notifis[p1].notifiName = view.addTxtNameUpdate.text.toString()
                                notifis[p1].bink = view.spinner.selectedItemId.toInt()
                                notifis[p1].stylealert = view.spinner2update.selectedItemId.toInt()
                                notifis[p1].stylesound  = view.spinner3update.selectedItemId.toInt()
                                notifis[p1].msg =  view.addTxtMsgUpdate.text.toString()
                                notifyDataSetChanged()
                                Toast.makeText(mCtx,"ทำการแก้ไขเรียบร้อย", Toast.LENGTH_SHORT).show()

                            }else{
                                Toast.makeText(mCtx,"ไม่สามารถแก้ไขได้ กรุณาลองอีกครั้ง", Toast.LENGTH_SHORT).show()
                            }
                        }else if(gg == same){
                            val isUpdate : Boolean = ListActivity.dbHandler.UpdateNotifi(
                                notifi.notifiID.toString(),
                                view.addTxtNameUpdate.text.toString(),
                                view.spinner.selectedItemId.toInt(),
                                view.spinner2update.selectedItemId.toInt(),
                                view.spinner3update.selectedItemId.toInt(),
                                view.addTxtMsgUpdate.text.toString())

                            if(isUpdate == true){
                                notifis[p1].notifiName = view.addTxtNameUpdate.text.toString()
                                notifis[p1].bink = view.spinner.selectedItemId.toInt()
                                notifis[p1].stylealert = view.spinner2update.selectedItemId.toInt()
                                notifis[p1].stylesound  = view.spinner3update.selectedItemId.toInt()
                                notifis[p1].msg =  view.addTxtMsgUpdate.text.toString()
                                notifyDataSetChanged()
                                Toast.makeText(mCtx,"ทำการแก้ไขเรียบร้อย", Toast.LENGTH_SHORT).show()

                            }else{
                                Toast.makeText(mCtx,"ไม่สามารถแก้ไขได้ กรุณาลองอีกครั้ง", Toast.LENGTH_SHORT).show()
                            }
                        }
                        else{
                            Toast.makeText(mCtx, " มีการแจ้งเตือนกระพริบตา "+(gg+1)+" ครั้งแล้ว", Toast.LENGTH_SHORT).show()
                        }

                }})
                .setNegativeButton("ยกเเลิก",DialogInterface.OnClickListener { dialog, which ->
                })
            val alerts = builder.create()
            alerts.show()
            val gg = builder.setIcon(R.drawable.common_google_signin_btn_icon_dark)

        }


    }



}