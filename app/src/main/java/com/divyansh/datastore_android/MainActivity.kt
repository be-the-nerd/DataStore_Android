package com.divyansh.datastore_android

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import com.divyansh.datastore_android.databinding.ActivityMainBinding
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSave.setOnClickListener {
            it.hideKeyboard()
            lifecycleScope.launch {
                save(
                    binding.etSaveKey.text.toString(),
                    binding.etSaveValue.text.toString()
                )
            }
        }

        binding.btnRead.setOnClickListener {
            it.hideKeyboard()
            lifecycleScope.launch {
                val value = read(binding.etReadkey.text.toString())
                binding.tvReadValue.text = value ?: "No value found"
            }
        }
    }

    private fun View.hideKeyboard(){
        val inputMethodManager = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
    }

    private suspend fun save(key: String, value: String){
        val dataStoreKey = stringPreferencesKey(key)
        dataStore.edit { settings ->
            settings[dataStoreKey] = value
        }
    }

    private suspend fun read(key: String): String? {
        val dataStoreKey = stringPreferencesKey(key)
        val preferences = dataStore.data.first()
        return preferences[dataStoreKey]   //not Type-safe so, we don't know the return type here but as we saved it manually so we know
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}