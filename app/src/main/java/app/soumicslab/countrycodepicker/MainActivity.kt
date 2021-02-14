package app.soumicslab.countrycodepicker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    supportFragmentManager.beginTransaction()
        .add(R.id.baseFragment, CountryCodePickerFragment(), CountryCodePickerFragment.TAG)
        .addToBackStack(CountryCodePickerFragment.TAG)
        .commit();
  }
}