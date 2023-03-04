package com.example.budgettracker



import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.budgettracker.databinding.ActivityMainBinding
import com.example.budgettracker.ui.dashboard.DashboardFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.math.BigDecimal


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
//        navController.navigate(R.id.navigation_dashboard)

        //Checking if there is already an existing main financial object save file. if none create one.
        var mainFinancialObject: FinancialObject?  = FileManager.loadFinancialObject(applicationContext.filesDir.absolutePath,Constants.SAVINGS_FILENAME);
        if (mainFinancialObject == null){
            mainFinancialObject = FinancialObject("Income Stream", "My Income Stream ", BigDecimal.ZERO)
            FileManager.saveFinancialObject(mainFinancialObject,applicationContext.filesDir.absolutePath,Constants.SAVINGS_FILENAME);
            Toast.makeText(this,"Main Financial Object Created", Toast.LENGTH_SHORT)
        }

        //Handles navigation requests from other activities through bundle.
        if (intent != null){
            val bundle = intent.extras
            val navigateTo = bundle?.getString(Constants.INTENT_KEY_NAVIGATE_TO)
            val navController = findNavController(R.id.nav_host_fragment_activity_main)
            val fragmentManager = supportFragmentManager
            navController.navigateUp()

            if(navigateTo.equals(Constants.NAVIGATE_TO_DEBT)){
                navController.navigate(R.id.action_nav_to_debts)
            }
            else if(navigateTo.equals(Constants.NAVIGATE_TO_WALLET)){
                navController.navigate(R.id.navigation_dashboard)
            }
            else if(navigateTo.equals(Constants.NAVIGATE_TO_SAVINGS)){
                navController.navigate(R.id.action_nav_to_savings)
            }

            fragmentManager.popBackStack()

        }



    }

    override fun onRestart() {
        super.onRestart()
        finish()
        startActivity(intent)
    }


}