package pl.wincenciuk.cashino

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import pl.wincenciuk.cashino.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var drawerLayout: DrawerLayout
    private val fragmentContainer get() = R.id.fragment_container

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)

        drawerLayout = binding.drawerLayout
        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_navigation_bar, R.string.close_navigation_bar)

        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        val navigationView = binding.navigationView
        navigationView.setNavigationItemSelectedListener(this)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(fragmentContainer,MenuFragment()).commit()
            navigationView.setCheckedItem(R.id.drawer_menu)
        }
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.drawer_menu -> supportFragmentManager.beginTransaction().replace(fragmentContainer, MenuFragment()).commit()
            R.id.drawer_saldo -> supportFragmentManager.beginTransaction().replace(fragmentContainer, SaldoFragment()).commit()
            R.id.drawer_ruletka -> supportFragmentManager.beginTransaction().replace(fragmentContainer, RouletteFragment()).commit()
            R.id.drawer_slots -> supportFragmentManager.beginTransaction().replace(fragmentContainer, FragmentSlots()).commit()
            R.id.drawer_black -> supportFragmentManager.beginTransaction().replace(fragmentContainer, BlackJackFragment()).commit()
            R.id.drawer_about -> supportFragmentManager.beginTransaction().replace(fragmentContainer, AboutFragment()).commit()
        }

        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

}