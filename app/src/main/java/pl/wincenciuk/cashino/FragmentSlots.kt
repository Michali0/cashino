package pl.wincenciuk.cashino

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import java.util.*

class FragmentSlots : Fragment() {

    private lateinit var msg: TextView
    private lateinit var img1: ImageView
    private lateinit var img2: ImageView
    private lateinit var img3: ImageView
    private lateinit var wheel1: Wheel
    private lateinit var wheel2: Wheel
    private lateinit var wheel3: Wheel
    private lateinit var btn: Button
    private var isStarted = false

    companion object {
        private val RANDOM = Random()

        private const val MIN_FRAME_DURATION = 150L
        private const val MAX_FRAME_DURATION = 400L

        fun randomLong(lower: Long, upper: Long): Long {
            return lower + (RANDOM.nextDouble() * (upper - lower)).toLong()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_slots, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        img1 = view.findViewById(R.id.img1)
        img2 = view.findViewById(R.id.img2)
        img3 = view.findViewById(R.id.img3)
        btn = view.findViewById(R.id.btn)
        msg = view.findViewById(R.id.msg)

        btn.setOnClickListener {
            if (isStarted) {
                wheel1.stopWheel()
                wheel2.stopWheel()
                wheel3.stopWheel()

                if (wheel1.currentIndex == wheel2.currentIndex && wheel2.currentIndex == wheel3.currentIndex) {
                    msg.text = "Gratulację, wygrałeś dużą nagrodę!"
                } else if (wheel1.currentIndex == wheel2.currentIndex || wheel2.currentIndex == wheel3.currentIndex
                    || wheel1.currentIndex == wheel3.currentIndex
                ) {
                    msg.text = "Mała nagroda"
                } else {
                    msg.text = "Przegrałeś! Spróbuj ponownie"
                }

                btn.text = "Start"
                isStarted = false

            } else {

                wheel1 = Wheel(object : Wheel.WheelListener {
                    override fun newImage(img: Int) {
                        activity?.runOnUiThread {
                            img1.setImageResource(img)
                        }
                    }
                }, randomLong(MIN_FRAME_DURATION, MAX_FRAME_DURATION), randomLong(0, 200))

                wheel1.start()

                wheel2 = Wheel(object : Wheel.WheelListener {
                    override fun newImage(img: Int) {
                        activity?.runOnUiThread {
                            img2.setImageResource(img)
                        }
                    }
                }, randomLong(MIN_FRAME_DURATION, MAX_FRAME_DURATION), randomLong(150, 400))

                wheel2.start()

                wheel3 = Wheel(object : Wheel.WheelListener {
                    override fun newImage(img: Int) {
                        activity?.runOnUiThread {
                            img3.setImageResource(img)
                        }
                    }
                }, randomLong(MIN_FRAME_DURATION, MAX_FRAME_DURATION), randomLong(150, 400))

                wheel3.start()

                btn.text = "Stop"
                msg.text = ""
                isStarted = true
            }
        }
    }
}
