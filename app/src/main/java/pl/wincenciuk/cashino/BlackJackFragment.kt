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
import java.util.*

class BlackJackFragment : Fragment() {
    private lateinit var textViewResult: TextView
    private lateinit var buttonDeal: Button
    private lateinit var buttonHit: Button
    private lateinit var buttonStand: Button
    private lateinit var saldoT: TextView
    private lateinit var stawka: EditText

    private var saldo = 0

    private val cardColors = arrayOf("♠", "♣", "♦", "♥")
    private val cardNumbers = arrayOf("A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K")

    private val deck = mutableListOf<String>()
    private val playerHand = mutableListOf<String>()
    private val dealerHand = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_blackjack, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textViewResult = view.findViewById(R.id.textViewResult)
        buttonDeal = view.findViewById(R.id.buttonDeal)
        buttonHit = view.findViewById(R.id.buttonHit)
        buttonStand = view.findViewById(R.id.buttonStand)
        saldoT = view.findViewById(R.id.saldoText)
        stawka = view.findViewById(R.id.stawkaText)

        readSaldo()
        resetDeck()

        buttonDeal.setOnClickListener { deal() }
        buttonHit.setOnClickListener { hit() }
        buttonStand.setOnClickListener { stand() }

        buttonHit.isEnabled = false
        buttonStand.isEnabled = false
    }

    private fun resetDeck() {
        deck.clear()
        for (color in cardColors) {
            for (number in cardNumbers) {
                deck.add("$color$number")
            }
        }
        deck.shuffle()
    }

    private fun deal() {
        val stawkaA = stawka.text.toString().toInt()
        readSaldo()
        if (stawkaA > saldo || stawkaA < 1) {
            Toast.makeText(requireContext(), "Niepoprawna stawka", Toast.LENGTH_SHORT).show()
            return
        }
        stawka.isEnabled = false
        writeSaldo(-stawkaA)
        playerHand.clear()
        dealerHand.clear()

        repeat(2) {
            playerHand.add(drawCard())
            dealerHand.add(drawCard())
        }

        textViewResult.text =
            "Ręka Gracza: ${playerHand.joinToString()} \n" +
                    "Ręka Krupiera: ${dealerHand.first()} ?"

        buttonHit.isEnabled = true
        buttonStand.isEnabled = true
        buttonDeal.isEnabled = false

        val handValue = calculateHandValue(playerHand)
        if (handValue == 21) {
            stand()
        }
    }

    private fun hit() {
        val stawkaA = stawka.text.toString().toInt()
        playerHand.add(drawCard())

        textViewResult.text =
            "Ręka Gracza: ${playerHand.joinToString()} \n" +
                    "Ręka Krupiera: ${dealerHand.joinToString()} \n" +
                    "Ręka Gracza suma: ${calculateHandValue(playerHand)}"

        val handValue = calculateHandValue(playerHand)
        val dealerValue = calculateHandValue(dealerHand)
        if (handValue == 21 && dealerValue == 21) {
            textViewResult.append("\nRemis")
            endGame()
        } else if (handValue == 21) {
            textViewResult.append("\nBlackjack! Wygrałeś.")
            writeSaldo(2*stawkaA)
            endGame()
        } else if (handValue > 21) {
            textViewResult.append("\nSuma > 21! Przegrałeś.")
            endGame()
        }
    }

    private fun stand() {
        val stawkaA = stawka.text.toString().toInt()
        var dealerValue = calculateHandValue(dealerHand)
        val playerValue = calculateHandValue(playerHand)
        if (dealerValue == 21) {
            textViewResult.append("\nWygrywa Krupier. Przegrałeś.")
            writeSaldo(-stawkaA)
        } else {
            while (dealerValue < playerValue) {
                dealerHand.add(drawCard())
                dealerValue = calculateHandValue(dealerHand)
            }
            if (dealerValue == playerValue && dealerValue < 17) {
                dealerHand.add(drawCard())
                dealerValue = calculateHandValue(dealerHand)
            }

            textViewResult.text =
                "Ręka Gracza: ${playerHand.joinToString()} \n" +
                        "Ręka Krupiera: ${dealerHand.joinToString()} \n" +
                        "Ręka Gracza suma: ${calculateHandValue(playerHand)}"

            if (dealerValue > 21) {
                textViewResult.append("\nKrupier suma > 21! Wygrałeś.")
                writeSaldo(2*stawkaA)
            } else if (dealerValue > playerValue) {
                textViewResult.append("\nWygrywa Krupier. Przegrałeś.")
            } else if (dealerValue < playerValue) {
                textViewResult.append("\nWygrałeś.")
                writeSaldo(2*stawkaA)
            } else {
                textViewResult.append("\nRemis")
                writeSaldo(stawkaA)
            }
        }
        endGame()
    }

    private fun drawCard(): String {
        val randomColor = (0 until cardColors.size).random()
        val randomNumber = (0 until cardNumbers.size).random()
        val card = "${cardColors[randomColor]}${cardNumbers[randomNumber]}"
        deck.remove(card)
        return card
    }

    private fun calculateHandValue(hand: List<String>): Int {
        var value = 0
        var numAces = 0

        for (card in hand) {
            val number = card.substring(1)
            when (number) {
                "A" -> {
                    value += 11
                    numAces++
                }
                "K", "Q", "J" -> value += 10
                else -> value += number.toInt()
            }
        }

        while (value > 21 && numAces > 0) {
            value -= 10
            numAces--
        }

        return value
    }

    private fun endGame() {
        readSaldo()
        stawka.isEnabled = true
        buttonHit.isEnabled = false
        buttonStand.isEnabled = false
        buttonDeal.isEnabled = true
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