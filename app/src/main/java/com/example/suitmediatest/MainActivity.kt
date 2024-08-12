package com.example.suitmediatest

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.suitmediatest.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val nameEditText: EditText = findViewById(R.id.nameEditText)
        val polindromeEditText: EditText = findViewById(R.id.palindromeEditText)
        val checkButton: Button = findViewById(R.id.checkButton)
        val nextButton: Button = findViewById(R.id.nextButton)

        checkButton.setOnClickListener {
            showLoading(true)
            val nameText = nameEditText.text.toString()
            val inputText = polindromeEditText.text.toString()

            if (TextUtils.isEmpty(nameText) || TextUtils.isEmpty(inputText)) {
                showAlertDialog("Please fill out both fields!")
                showLoading(false)
            } else {
                if (isPalindrome(inputText)) {
                    showAlertDialog("Palindrome")
                } else {
                    showAlertDialog("Not Palindrome")
                }
                showLoading(false)
            }
        }

        nextButton.setOnClickListener {
            showLoading(true)
            val nameText = nameEditText.text.toString()
            if (TextUtils.isEmpty(nameText)) {
                showAlertDialog("Please enter your name!")
                showLoading(false)
                return@setOnClickListener
            }

            val intent = Intent(this, SecondActivity::class.java)
            intent.putExtra("name", nameText)
            startActivity(intent)
            showLoading(false)
        }
    }

    private fun isPalindrome(input: String): Boolean {
        val cleanInput = input.replace("\\s".toRegex(), "").lowercase()
        return cleanInput == cleanInput.reversed()
    }

    private fun showAlertDialog(message: String) {
        AlertDialog.Builder(this)
            .setMessage(message)
            .setPositiveButton("OK", null)
            .create()
            .show()
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
            binding.checkButton.isEnabled = false
            binding.nextButton.isEnabled = false
        } else {
            binding.progressBar.visibility = View.GONE
            binding.checkButton.isEnabled = true
            binding.nextButton.isEnabled = true
        }
    }
}