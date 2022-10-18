package com.example.prakt11

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.edit
import androidx.room.Room
import com.example.prakt11.data.DATABASE_NAME
import com.example.prakt11.data.ExpensesDatabase
import com.example.prakt11.data.models.Expenses
import com.example.prakt11.data.models.TypeExpenses
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {
    private var position: Int = -1

    private var uuidExpenses: UUID? = null
    private var uuidTypeExpenses: UUID? = null

    private var database: ExpensesDatabase? = null
    private var executor = Executors.newSingleThreadExecutor()

    private lateinit var btn_add : Button
    private lateinit var btn_show : Button
    private lateinit var btn_delete : Button

    private lateinit var name : EditText
    private lateinit var costEd : EditText
    private lateinit var typeExpenses : EditText
    private var typesList: MutableList<TypeExpenses> = mutableListOf()

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btn_add = findViewById<Button>(R.id.add_button)
        btn_show = findViewById<Button>(R.id.show_button)
        btn_delete = findViewById<Button>(R.id.delete_button)
        name = findViewById<EditText>(R.id.editText_name)
        costEd = findViewById<EditText>(R.id.editText_cost)
        typeExpenses = findViewById<EditText>(R.id.editText_type)

        database = Room.databaseBuilder(this, ExpensesDatabase::class.java, DATABASE_NAME).build()

        position = intent.getIntExtra("pos", -1)
        val uuidText = intent.getStringExtra("uuid")
        val uuidTypeText = intent.getStringExtra("uuidType")
        converterUUID(uuidText, uuidTypeText)



        selectEditText()
        btn_add.setOnClickListener{
            var repeat: Int = 0
            try {
                var database2:ExpensesDatabase = Room.databaseBuilder(this, ExpensesDatabase::class.java, DATABASE_NAME).build()
                val edao=database2.expensesDAO()
                edao.getAllTypesExpenses().observe(this, androidx.lifecycle.Observer {
                    typesList.addAll(it)
                    Log.d("123zxc",it.toString())
                })
            }catch (e: Exception){

            }
            var database2:ExpensesDatabase = Room.databaseBuilder(this, ExpensesDatabase::class.java, DATABASE_NAME).build()
            val edao=database2.expensesDAO()
            edao.getAllTypesExpenses().observe(this, androidx.lifecycle.Observer {
                typesList.addAll(it)
                Log.d("123zxc",it.toString())
            })

            typesList.forEach()
            {
                if(it.typeExpenses==typeExpenses.text.toString()){
                    repeat++
                }
            }
            if(repeat>0){
                Toast.makeText(this, "Такой тип расходов уже имеется в списке", Toast.LENGTH_LONG).show()
            }
            if (position == -1&&repeat==0) {
                if (name.text.toString() != "" && costEd.text.toString() != "" && typeExpenses.text.toString() != "") {
                    addExpenses(name.text.toString(), costEd.text.toString().toInt(), typeExpenses.text.toString())
                    name.setText("")
                    costEd.setText("")
                    typeExpenses.setText("")
                }
                else {
                    Toast.makeText(this, "Нельзя добавить пустую строку", Toast.LENGTH_SHORT).show()
                }
            }
            else if(position>-1){
                if (name.text.toString() != "" && costEd.text.toString() != "" && typeExpenses.text.toString() != ""){
                    executor.execute {
                        database?.expensesDAO()?.updateTypeExpenses(
                            TypeExpenses(uuidTypeExpenses!!, typeExpenses.text.toString())
                        )
                        database?.expensesDAO()?.updateExpenses(
                            Expenses(uuidExpenses!!, name.text.toString(), costEd.text.toString().toInt(), uuidTypeExpenses!!)
                        )
                    }
                    Toast.makeText(this, "Значения изменены", Toast.LENGTH_SHORT).show()
                    super.onBackPressed()
                }
                else{
                    Toast.makeText(this, "Нельзя добавить пустую строку", Toast.LENGTH_SHORT).show()
                }
            }
        }
        btn_show.setOnClickListener{
            val intent = Intent(this, MainActivity2::class.java)
            startActivity(intent)
        }
        btn_delete.setOnClickListener{
            executor.execute {
                database?.expensesDAO()?.deleteExpenses(
                    Expenses(uuidExpenses!!, name.text.toString(), costEd.text.toString().toInt(), uuidTypeExpenses!!)
                )
                database?.expensesDAO()?.deleteType(
                    TypeExpenses(uuidTypeExpenses!!, typeExpenses.text.toString())
                )
            }
            name.setText("")
            costEd.setText("")
            typeExpenses.setText("")
            Toast.makeText(this, "Удалено", Toast.LENGTH_SHORT).show()
            btn_delete.isEnabled = false
            super.onBackPressed()
        }
    }
    private fun converterUUID(uuidExpensesText: String?, uuidTypeText: String?){
        if(uuidExpensesText != null && uuidTypeText != null){
            uuidExpenses = UUID.fromString(uuidExpensesText)
            uuidTypeExpenses = UUID.fromString(uuidTypeText)
        }
    }

    private fun selectEditText(){
        if(position > -1){
            btn_delete.visibility = View.VISIBLE
            btn_delete.isEnabled = true
            executor.execute {
                val expenses = database?.expensesDAO()?.getExpenses(uuidExpenses!!)
                val typeName = database?.expensesDAO()?.getTypeExpensesName(uuidTypeExpenses!!)

                runOnUiThread(Runnable {
                    kotlin.run {
                        name.setText(expenses?.nameExpenses)
                        costEd.setText(expenses?.costExpenses.toString())
                        typeExpenses.setText(typeName)
                    }
                })
            }
        }
        else btn_delete.visibility = View.INVISIBLE
    }

    private fun addExpenses(name: String, cost: Int, nameType:String)
    {
        val uuidType = UUID.randomUUID()
        executor.execute {
            database?.expensesDAO()?.addTypeExpenses(
                TypeExpenses(uuidType, nameType)
            )
            database?.expensesDAO()?.addExpenses(
                Expenses(UUID.randomUUID(), name, cost, uuidType)
            )
        }
    }

}

