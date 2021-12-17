package io.github.andrewk2112.boilerplateable.demo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            addContentFragment()
        }
    }

    private fun addContentFragment() {
        supportFragmentManager
            .beginTransaction()
            .add(R.id.fragment_container, MainFragment())
            .commit()
    }

}
