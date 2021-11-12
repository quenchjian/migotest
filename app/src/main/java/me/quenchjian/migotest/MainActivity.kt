package me.quenchjian.migotest

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import me.quenchjian.migotest.transaction.PassFragment

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    val fragment = supportFragmentManager.findFragmentByTag(PassFragment.TAG) ?: PassFragment()
    supportFragmentManager.beginTransaction()
      .replace(R.id.activity_container, fragment, PassFragment.TAG)
      .commit()
  }
}