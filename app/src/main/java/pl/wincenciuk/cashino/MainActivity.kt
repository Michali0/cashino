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
private lateinit var btnRegister: Button

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

        binding.btnRegister.setOnClickListener {
            val email = findViewById<EditText>(R.id.loginEt).text.toString()
            val pass = findViewById<EditText>(R.id.textPasswordEt).text.toString()
            if (email == "" || pass == "") {
                Toast.makeText(
                    baseContext,
                    "Puste pole logowania",
                    Toast.LENGTH_SHORT,
                ).show()
            } else if (email.indexOf('@') == -1 || email.indexOf('.') == -1) {
                Toast.makeText(
                    baseContext,
                    "Niepoprawny email",
                    Toast.LENGTH_SHORT,
                ).show()
            } else if (pass.length < 6) {
                Toast.makeText(
                    baseContext,
                    "Za krótkie hasło (min. 6 znaków)",
                    Toast.LENGTH_SHORT,
                ).show()
            } else {
                auth.fetchSignInMethodsForEmail(email)
                    .addOnCompleteListener { task ->
                        val isNewUser = task.result?.signInMethods?.isEmpty() ?: true

                        if (isNewUser) {
                            auth.createUserWithEmailAndPassword(email, pass)
                                .addOnCompleteListener(this) { task ->
                                    if (task.isSuccessful) {
                                        Log.d(TAG, "createUserWithEmail:success")
                                        val database =
                                            FirebaseDatabase.getInstance("https://cashino-rrww-default-rtdb.europe-west1.firebasedatabase.app")
                                        val user = Firebase.auth.currentUser
                                        user?.let { database.getReference(it.uid) }?.setValue(1000)
                                        findViewById<EditText>(R.id.textPasswordEt).text.clear()
                                        val intent = Intent(this@MainActivity, HomeActivity::class.java)
                                        startActivity(intent)
                                    } else {
                                        Log.w(TAG, "createUserWithEmail:failure", task.exception)
                                        Toast.makeText(
                                            baseContext,
                                            "Błąd rejestracji",
                                            Toast.LENGTH_SHORT,
                                        ).show()
                                    }
                                }
                        } else {
                            Toast.makeText(
                                baseContext,
                                "Konto o takim emailu już istnieje",
                                Toast.LENGTH_SHORT,
                            ).show()
                        }
                    }
            }
        }
    }


}