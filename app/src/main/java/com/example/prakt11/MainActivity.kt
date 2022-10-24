package com.example.prakt11

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
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
import kotlin.math.exp

class MainActivity : AppCompatActivity() {
    private var position: Int = -1

    private var uuidExpenses: UUID? = null
    private var uuidTypeExpenses: UUID? = null

    private var database: ExpensesDatabase? = null
    private var executor = Executors.newSingleThreadExecutor()

    private lateinit var btn_add : Button
    private lateinit var btn_show : Button
    private lateinit var btn_delete : Button
    private lateinit var spinner: Spinner
    private lateinit var name : EditText
    private lateinit var costEd : EditText
    private lateinit var typeExpenses : EditText
    private var typesList: MutableList<TypeExpenses> = mutableListOf()
    private var expensesList: MutableList<Expenses> = mutableListOf()
    var isExpensesDublicate : Boolean = false
    var isTypesDublicate : Boolean = false

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
        spinner = findViewById(R.id.spinnerText_type2)
        database = Room.databaseBuilder(this, ExpensesDatabase::class.java, DATABASE_NAME).build()

        position = intent.getIntExtra("pos", -1)
        val uuidText = intent.getStringExtra("uuid")
        val uuidTypeText = intent.getStringExtra("uuidType")
        converterUUID(uuidText, uuidTypeText)

        getExpenses()
        selectEditText()
        if(position==-1)
        {
            typeExpenses.visibility = View.VISIBLE
            spinner.visibility = View.INVISIBLE
        }
        else
        {
            addTypesSpinner()
            typeExpenses.visibility = View.INVISIBLE
        }
        btn_add.setOnClickListener{
            if (position == -1) {
                if (name.text.toString() != "" && costEd.text.toString() != "" && typeExpenses.text.toString() != "") {
                    addExpenses(name.text.toString(), costEd.text.toString().toInt(), typeExpenses.text.toString())
                    name.setText("")
                    costEd.setText("")
                    typeExpenses.setText("")
                     isExpensesDublicate  = false
                     isTypesDublicate  = false
                }
                else {
                    Toast.makeText(this, "Нельзя добавить пустую строку", Toast.LENGTH_SHORT).show()
                }
            }
            else if(position>-1){
                if (name.text.toString() != "" && costEd.text.toString() != "" && typeExpenses.text.toString() != ""){


                    executor.execute {
                        val uuid = database?.expensesDAO()?.getType(spinner.selectedItem.toString())
                        database?.expensesDAO()?.updateExpenses(
                            Expenses(uuidExpenses!!, name.text.toString(), costEd.text.toString().toInt(), uuid!!)
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
        val typeExpenses = TypeExpenses(uuidType, nameType)
        val expenses = Expenses(UUID.randomUUID(), name, cost, uuidType)
        expensesList.forEach(){
            if(it.nameExpenses==expenses.nameExpenses)
            {
                isExpensesDublicate=true
            }
        }
        typesList.forEach(){
            if(it.typeExpenses==typeExpenses.typeExpenses)
            {
                isTypesDublicate=true
            }
        }
        if(!isExpensesDublicate&&!isTypesDublicate)
        {
            executor.execute {
                database?.expensesDAO()?.addTypeExpenses(
                    TypeExpenses(uuidType, nameType)
                )
                database?.expensesDAO()?.addExpenses(
                     Expenses(UUID.randomUUID(), name, cost, uuidType)
                )
            }
        }
        else if(!isExpensesDublicate&&isTypesDublicate)
        {
            executor.execute {
                val uuid = database?.expensesDAO()?.getType(nameType)
                database?.expensesDAO()?.addExpenses(
                   Expenses(UUID.randomUUID(), name, cost, uuid!!)
                )
            }
        }
        else
        {
            Toast.makeText(this, "Уже содержится",Toast.LENGTH_SHORT).show()
        }
    }
    private fun getExpenses() {
        expensesList.clear()
        typesList.clear()
        database?.expensesDAO()?.getAllTypesExpenses()?.observe(this, androidx.lifecycle.Observer {
            typesList.addAll(it)
        })
        database?.expensesDAO()?.getAllExpenses()?.observe(this, androidx.lifecycle.Observer {
            expensesList.addAll(it)
        })
    }

    override fun onResume() {
        super.onResume()
        isExpensesDublicate  = false
        isTypesDublicate  = false
        getExpenses()
    }
    private fun addTypesSpinner(){
        database?.expensesDAO()?.getTypeString()?.observe(this, androidx.lifecycle.Observer { it ->
            spinner.adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, it)
        })
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                Toast.makeText(applicationContext, "$position ${spinner.selectedItem}", Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }
    }
}

