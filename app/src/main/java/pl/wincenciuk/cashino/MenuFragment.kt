package pl.wincenciuk.cashino

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView


class MenuFragment : Fragment() {
    private lateinit var cardView1: CardView
    private lateinit var cardView2: CardView
    private lateinit var cardView3: CardView


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_menu, container, false)

        cardView1 = view.findViewById(R.id.cardViewRoulette)
        cardView2 = view.findViewById(R.id.cardViewBlackjack)
        cardView3 = view.findViewById(R.id.cardViewSlots)


        cardView1.setOnClickListener {
            // Przejdź do Ruletki
            val fragment1 = RouletteFragment()
            val fragmentManager = requireActivity().supportFragmentManager
            fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment1)
                .commit()
        }

        cardView2.setOnClickListener {
            // Przejdź do Blackjacka
            val fragment2 = BlackJackFragment()
            val fragmentManager = requireActivity().supportFragmentManager
            fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment2)
                .commit()
        }

        cardView3.setOnClickListener {
            // Przejdź do Slotsow
            val fragment3 = FragmentSlots()
            val fragmentManager = requireActivity().supportFragmentManager
            fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment3)
                .commit()
        }



        return view
    }
}