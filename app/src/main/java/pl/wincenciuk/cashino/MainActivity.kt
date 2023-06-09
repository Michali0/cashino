package pl.wincenciuk.cashino

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import pl.wincenciuk.cashino.databinding.ActivityMainBinding

private lateinit var btnLogin: Button

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}