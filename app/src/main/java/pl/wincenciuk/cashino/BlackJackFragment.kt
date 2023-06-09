package pl.wincenciuk.cashino

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import java.util.*

class BlackJackFragment : Fragment() {
    private lateinit var textViewResult: TextView
    private lateinit var buttonDeal: Button
    private lateinit var buttonHit: Button
    private lateinit var buttonStand: Button

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
    }

    private fun hit() {
        playerHand.add(drawCard())

        textViewResult.text =
            "Ręka Gracza: ${playerHand.joinToString()} \n" +
                    "Ręka Krupiera: ${dealerHand.joinToString()} \n" +
                    "Ręka Gracza suma: ${calculateHandValue(playerHand)}"

        val handValue = calculateHandValue(playerHand)
        if (handValue == 21) {
            textViewResult.append("\nBlackjack! Wygrałeś.")
            endGame()
        } else if (handValue > 21) {
            textViewResult.append("\nSuma > 21! Przegrałeś.")
            endGame()
        }
    }

    private fun stand() {
        dealerHand[1] = drawCard()

        textViewResult.text =
            "Ręka Gracza: ${playerHand.joinToString()} \n" +
                    "Ręka Krupiera: ${dealerHand.joinToString()} \n" +
                    "Ręka Gracza suma: ${calculateHandValue(playerHand)}"

        while (calculateHandValue(dealerHand) < 17) {
            dealerHand.add(drawCard())
        }

        val dealerValue = calculateHandValue(dealerHand)
        val playerValue = calculateHandValue(playerHand)

        if (dealerValue > 21) {
            textViewResult.append("\nKrupier suma > 21! Wygrałeś.")
        } else if (dealerValue > playerValue) {
            textViewResult.append("\nWygrywa Krupier. Przegrałeś.")
        } else if (dealerValue < playerValue) {
            textViewResult.append("\nWygrałeś.")
        } else {
            textViewResult.append("\nRemis")
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
        buttonHit.isEnabled = false
        buttonStand.isEnabled = false
        buttonDeal.isEnabled = true
    }
}