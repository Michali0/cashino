package pl.wincenciuk.cashino

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import kotlin.random.Random

class SaldoFragment : Fragment() {
    private lateinit var textViewEquation: TextView
    private lateinit var editTextAnswer: EditText
    private lateinit var buttonCheck: Button

    private var liczba1: Int = 0
    private var liczba2: Int = 0
    private var liczba3: Int = 0
    private var znak1: String = ""
    private var znak2: String = ""

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

        generateEquation()

        buttonCheck.setOnClickListener {
            checkAnswer()
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
                textViewEquation.text = "Prawidłowa odpowiedź! Saldo uzupełnione."
            } else {
                textViewEquation.text = "Niepoprawna odpowiedź. Poprawna odpowiedź to $correctAnswer."
            }
        }
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
}
