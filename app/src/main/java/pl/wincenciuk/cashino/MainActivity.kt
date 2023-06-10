package pl.wincenciuk.cashino

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import pl.wincenciuk.cashino.databinding.ActivityMainBinding

private lateinit var btnLogin: Button

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        val firebase : DatabaseReference = FirebaseDatabase.getInstance().getReference()

        binding.btnLogin.setOnClickListener {
            val email = findViewById<EditText>(R.id.loginEt).text.toString()
            val pass = findViewById<EditText>(R.id.textPasswordEt).text.toString()
            if (email != "" && pass != "") {
                auth.signInWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            Log.d(TAG, "signInWithEmail:success")
                            val user = auth.currentUser
                            findViewById<EditText>(R.id.textPasswordEt).text.clear()
                            val intent = Intent(this@MainActivity, HomeActivity::class.java)
                            startActivity(intent)
                        } else {
                            Log.w(TAG, "signInWithEmail:failure", task.exception)
                            Toast.makeText(
                                baseContext,
                                "Nieudane logowanie",
                                Toast.LENGTH_SHORT,
                            ).show()
                            findViewById<EditText>(R.id.textPasswordEt).text.clear()
                        }
                    }
            } else {
                Toast.makeText(
                    baseContext,
                    "Puste pole logowania",
                    Toast.LENGTH_SHORT,
                ).show()
            }
        }
    }


}