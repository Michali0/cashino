package pl.wincenciuk.cashino

class Wheel(private val wheelListener: WheelListener, private val frameDuration: Long, private val startIn: Long) : Thread() {

    interface WheelListener {
        fun newImage(img: Int)
    }

    companion object {
        private val imgs = intArrayOf(
            R.drawable.slot1, R.drawable.slot2, R.drawable.slot3, R.drawable.slot4,
            R.drawable.slot5, R.drawable.slot6
        )
    }

    var currentIndex = 0
    private var isStarted = true

    fun nextImg() {
        currentIndex++

        if (currentIndex == imgs.size) {
            currentIndex = 0
        }
    }

    override fun run() {
        try {
            sleep(startIn)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        while (isStarted) {
            try {
                sleep(frameDuration)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }

            nextImg()

            wheelListener.newImage(imgs[currentIndex])
        }
    }

    fun stopWheel() {
        isStarted = false
    }
}
