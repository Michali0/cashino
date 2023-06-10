package pl.wincenciuk.cashino

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.animation.RotateAnimation
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import java.util.*


class RouletteFragment : Fragment() {

    private val sectors = arrayOf(
        "RED 32", "BLACK 15", "RED 19", "BLACK 4", "RED 21", "BLACK 2", "RED 25",
        "BLACK 17", "RED 34", "BLACK 6", "RED 27", "BLACK 13", "RED 36", "BLACK 11",
        "RED 30", "BLACK 8", "RED 23", "BLACK 10", "RED 5", "BLACK 24", "RED 16",
        "BLACK 33", "RED 1", "BLACK 20", "RED 14", "BLACK 31", "RED 9", "BLACK 22",
        "RED 18", "BLACK 29", "RED 7", "BLACK 28", "RED 12", "BLACK 35", "RED 3",
        "BLACK 26", "GREEN 0"
    )

    private val RANDOM = Random()
    private var degree = 0
    private var degreeOld = 0
    private var saldo = 0

    private val HALF_SECTOR = 360f / 37f / 2f

    private lateinit var spinBtn: Button
    private lateinit var resultTv: TextView
    private lateinit var saldoT: TextView
    private lateinit var wheel: ImageView
    private lateinit var stawka: EditText

    private var selectedColor: String? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_roulette, container, false)

        val blackBtn: Button = view.findViewById(R.id.btnBlack)
        val redBtn: Button = view.findViewById(R.id.btnRed)
        val greenBtn: Button = view.findViewById(R.id.btnGreen)

        readSaldo()

        blackBtn.setOnClickListener { selectColor("BLACK") }
        redBtn.setOnClickListener { selectColor("RED") }
        greenBtn.setOnClickListener { selectColor("GREEN") }

        spinBtn = view.findViewById(R.id.spinBtn2)
        resultTv = view.findViewById(R.id.resultTv2)
        wheel = view.findViewById(R.id.wheel2)
        saldoT = view.findViewById(R.id.saldoText)
        stawka = view.findViewById(R.id.stawkaText)

        spinBtn.setOnClickListener { spin(it) }

        return view
    }

    private fun selectColor(color: String) {
        selectedColor = color
        Toast.makeText(requireContext(), "Wybrany kolor: $color", Toast.LENGTH_SHORT).show()
    }

    private fun spin(v: View) {
        val stawkaA = stawka.text.toString().toInt()
        readSaldo()
        if (stawkaA > saldo || stawkaA < 1) {
            Toast.makeText(requireContext(), "Niepoprawna stawka", Toast.LENGTH_SHORT).show()
            return
        }
        writeSaldo(-stawkaA)
        if (selectedColor.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Proszę najpierw wybrać kolor", Toast.LENGTH_SHORT).show()
            return
        }

        degreeOld = degree % 360
        degree = RANDOM.nextInt(360) + 720

        val rotateAnim = RotateAnimation(
            degreeOld.toFloat(), degree.toFloat(),
            RotateAnimation.RELATIVE_TO_SELF, 0.5f,
            RotateAnimation.RELATIVE_TO_SELF, 0.5f
        )

        rotateAnim.duration = 3600
        rotateAnim.fillAfter = true
        rotateAnim.interpolator = DecelerateInterpolator()
        rotateAnim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
                resultTv.text = ""
            }

            override fun onAnimationEnd(animation: Animation) {
                resultTv.text = getSector(360 - (degree % 360))

                if (selectedColor.equals(resultTv.text.toString().split(" ")[0], ignoreCase = true)) {
                    Toast.makeText(requireContext(), "Wygrałeś!", Toast.LENGTH_SHORT).show()
                    if (resultTv.text.toString().split(" ")[0] == "GREEN") {
                        writeSaldo(6*stawkaA)
                    } else {
                        writeSaldo(2*stawkaA)
                    }
                } else {
                    Toast.makeText(requireContext(), "Przegrałeś, spróbuj jeszcze raz!", Toast.LENGTH_SHORT).show()
                }
                readSaldo()
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })

        wheel.startAnimation(rotateAnim)
    }

    private fun getSector(degrees: Int): String? {
        var i = 0
        var text: String? = null

        do {
            val start = HALF_SECTOR * (i * 2 + 1)
            val end = HALF_SECTOR * (i * 2 + 3)

            if (degrees >= start && degrees < end) {
                text = sectors[i]
            }

            i++
        } while (text == null && i < sectors.size)

        return text
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