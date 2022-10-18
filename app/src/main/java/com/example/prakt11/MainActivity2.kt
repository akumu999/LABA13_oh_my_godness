package com.example.prakt11

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.prakt11.data.DATABASE_NAME
import com.example.prakt11.data.ExpensesDatabase
import com.example.prakt11.data.models.Expenses
import com.example.prakt11.data.models.TypeExpenses

class MainActivity2 : AppCompatActivity() {
    private var expensesList: MutableList<Expenses> = mutableListOf()
    private var typesList: MutableList<TypeExpenses> = mutableListOf()
    private lateinit var rv: RecyclerView
    private var database: ExpensesDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        database = Room.databaseBuilder(this, ExpensesDatabase::class.java, DATABASE_NAME).build()
    }

    private fun getExpenses() {
        expensesList.clear()
        typesList.clear()
        database?.expensesDAO()?.getAllTypesExpenses()?.observe(this, androidx.lifecycle.Observer {
            typesList.addAll(it)
        })
        database?.expensesDAO()?.getAllExpenses()?.observe(this, androidx.lifecycle.Observer {
            expensesList.addAll(it)
            getRecycleView()
        })
    }

    override fun onResume() {
        super.onResume()
        getExpenses()
    }

    private fun getRecycleView(){
        rv = findViewById(R.id.rv)
        val rvListener = object : ExpensesRVAdapter.ItemClickListener {
            override fun onItemClick(view: View?, position: Int) {
                val intent = Intent(this@MainActivity2, MainActivity::class.java)
                intent.putExtra("pos", position)
                intent.putExtra("uuid", expensesList[position].uuid.toString())
                intent.putExtra("uuidType", typesList[position].uuid.toString())
                startActivity(intent)
                Toast.makeText(this@MainActivity2, "position: $position", Toast.LENGTH_SHORT).show()
            }
        }
        val adapter = ExpensesRVAdapter(this, expensesList, typesList)
        adapter.setClickListener(rvListener)
        rv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rv.adapter = adapter
    }
}