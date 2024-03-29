package com.example.budgettracker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class SavingsAddActivity : AppCompatActivity() {

    private lateinit var sqLiteHelper: SQLiteHelper
    private lateinit var bCreateSavings: Button
    private lateinit var etName: EditText
    private lateinit var etDescription: EditText
    private lateinit var etGoalAmount: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_savings_add)

        //Fetch all views
        initViews()
        sqLiteHelper = SQLiteHelper(this)

        bCreateSavings.setOnClickListener {
            //On button Click, get the name, description, and goal amount of the savings object to be created.
            val newSavingsName = etName.text.toString()
            val newSavingsDescription = etDescription.text.toString()
            //Validations
            //Setting up the Alert dialog which will serve as error popup for validation.
            val builder = AlertDialog.Builder(this)
            builder.setPositiveButton("OK") { dialog, which ->
                dialog.dismiss()
            }

            if (StringTools.isNullOrEmpty(newSavingsName)){
                builder.setTitle("Savings Name must not be blank")
                builder.show()
                return@setOnClickListener
            }

            if(StringTools.isNullOrEmpty(etGoalAmount.text.toString())){
                builder.setTitle("Savings Goal Amount must not be blank")
                builder.show()
                return@setOnClickListener
            }
            val newSavingsGoal = etGoalAmount.text.toString().replace(",","").toDouble()

            //Create the savings object using the collected data above
            val financialSavingsObject = FinancialObjectModel(
                type = Const.FO_TYPE_SAVINGS,
                name = newSavingsName,
                description =  newSavingsDescription,
                targetAmount = newSavingsGoal
            )

            val newSavingsId = financialSavingsObject.id
            //Add new object to database.
            sqLiteHelper.insertFinancialObject(financialSavingsObject)

            //Send toast to user which confirms that the file has been saved.
            Toast.makeText(
                this,
                "$newSavingsName, $newSavingsDescription, $newSavingsGoal",
                Toast.LENGTH_SHORT
            ).show()


            val intent = Intent(applicationContext, MainSavingsActivity::class.java)
            intent.putExtra(Const.INTENT_KEY_SAVINGS_ID, newSavingsId)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            setResult(RESULT_OK)
            finish()
        }

    }

    private fun initViews() {
        bCreateSavings = findViewById(R.id.bCreateLend)
        etName = findViewById(R.id.etLendName)
        etDescription = findViewById(R.id.etLendDescription)
        etGoalAmount = findViewById(R.id.etAddLendAmount)
        etGoalAmount.addTextChangedListener(NumberTextWatcher(etGoalAmount))
    }
}