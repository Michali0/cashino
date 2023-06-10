package pl.wincenciuk.cashino

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import kotlin.random.Random

class SaldoFragment : Fragment() {
    private lateinit var textViewEquation: TextView
    private lateinit var editTextAnswer: EditText
    private lateinit var buttonCheck: Button
    private lateinit var buttonReset: Button
    private lateinit var saldoT: TextView

    private var saldo = 0
    private var liczba1: Int = 0
    private var liczba2: Int = 0
    private var liczba3: Int = 0
    private var znak1: String = ""
    private var znak2: String = ""
    private var isButtonClicked = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_saldo, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textViewEquation = view.findViewById(R.id.textViewEquation)
        editTextAnswer = view.findViewById(R.id.editTextAnswer)
        buttonCheck = view.findViewById(R.id.buttonCheck)
        buttonReset = view.findViewById(R.id.buttonReset)
        saldoT = view.findViewById(R.id.saldoText)

        readSaldo()

        generateEquation()

        buttonCheck.setOnClickListener {
            checkAnswer()
            isButtonClicked = true
            buttonCheck.isEnabled = false
        }
        buttonReset.setOnClickListener {
            resetFragment()
        }
    }

    private fun generateEquation() {
        liczba1 = Random.nextInt(1, 100)
        liczba2 = Random.nextInt(1, 100)
        liczba3 = Random.nextInt(1, 100)

        val operators = arrayOf("+", "-", "*")
        znak1 = operators.random()
        znak2 = operators.random()

        val equation = "$liczba1 $znak1 $liczba2 $znak2 $liczba3 = "
        textViewEquation.text = equation
    }

    private fun checkAnswer() {
        val userAnswer = editTextAnswer.text.toString().toIntOrNull()

        if (userAnswer == null) {
            textViewEquation.text = "Wprowadź odpowiedź!."
        } else {
            val correctAnswer = Equation()

            if (userAnswer == correctAnswer) {
                textViewEquation.text = "Prawidłowa odpowiedź! Saldo uzupełnione o 100."
                readSaldo()
                writeSaldo(100)
            } else {
                textViewEquation.text = "Niepoprawna odpowiedź. Poprawna odpowiedź to $correctAnswer."
            }
        }
        readSaldo()
    }

    private fun Equation(): Int {
        val wynik: Int

        when (znak1) {
            "+" -> {
                when (znak2) {
                    "+" -> wynik = liczba1 + liczba2 + liczba3
                    "-" -> wynik = liczba1 + liczba2 - liczba3
                    "*" -> wynik = liczba1 + liczba2 * liczba3
                    else -> wynik = 0
                }
            }
            "-" -> {
                when (znak2) {
                    "+" -> wynik = liczba1 - liczba2 + liczba3
                    "-" -> wynik = liczba1 - liczba2 - liczba3
                    "*" -> wynik = liczba1 - liczba2 * liczba3
                    else -> wynik = 0
                }
            }
            "*" -> {
                when (znak2) {
                    "+" -> wynik = liczba1 * liczba2 + liczba3
                    "-" -> wynik = liczba1 * liczba2 - liczba3
                    "*" -> wynik = liczba1 * liczba2 * liczba3
                    else -> wynik = 0
                }
            }

            else -> wynik = 0
        }

        return wynik
    }
    private fun resetFragment() {
        isButtonClicked = false
        buttonCheck.isEnabled = true
        editTextAnswer.text.clear()
        generateEquation()
    }

    fun readSaldo() {
        val database = FirebaseDatabase.getInstance("https://cashino-rrww-default-rtdb.europe-west1.firebasedatabase.app")
        val user = Firebase.auth.currentUser
        val ref = user?.let { database.getReference(it.uid) }

        if (ref != null) {
            ref.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    saldo = snapshot.getValue(Int::class.java)!!
                    saldoT.text = saldo.toString()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        requireContext(),
                        "Błąd odczytu z bazy",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            })
        }
    }
    fun writeSaldo(stawka: Int) {
        val database =
            FirebaseDatabase.getInstance("https://cashino-rrww-default-rtdb.europe-west1.firebasedatabase.app")
        val user = Firebase.auth.currentUser
        user?.let { database.getReference(it.uid) }?.setValue(saldo + stawka)
    }

}
