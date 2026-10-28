
package com.example.healthease_part_2

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.lifecycle.lifecycleScope
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnGoogleSignIn: Button
    private lateinit var tvCreateAccount: TextView
    private lateinit var credentialManager: CredentialManager

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(UserSession.applyLocale(newBase))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etEmail = findViewById(R.id.etLoginEmail)
        etPassword = findViewById(R.id.etLoginPassword)
        btnLogin = findViewById(R.id.btnLogin)
        btnGoogleSignIn = findViewById(R.id.btnGoogleSignIn)
        tvCreateAccount = findViewById(R.id.tvCreateAccount)

        credentialManager = CredentialManager.create(this)

        btnLogin.setOnClickListener {
            loginUser()
        }

        btnGoogleSignIn.setOnClickListener {
            signInWithGoogle()
        }

        tvCreateAccount.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loginUser() {
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString()

        if (email.isEmpty()) {
            etEmail.error = getString(R.string.please_enter_email)
            etEmail.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.error = getString(R.string.please_enter_valid_email)
            etEmail.requestFocus()
            return
        }

        if (password.isEmpty()) {
            etPassword.error = getString(R.string.please_enter_password)
            etPassword.requestFocus()
            return
        }

        when (val result = AccountStore.login(this, email, password)) {
            is AccountStore.LoginResult.Success -> {
                val account = result.account

                UserSession.saveProfile(
                    this,
                    account.name,
                    account.email,
                    account.language
                )
                UserSession.logIn(this, account.email, account.name)

                Toast.makeText(
                    this,
                    getString(R.string.login_successful),
                    Toast.LENGTH_SHORT
                ).show()

                openDashboard()
            }

            AccountStore.LoginResult.InvalidCredentials -> {
                Toast.makeText(
                    this,
                    "Invalid email or password. Please try again.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun signInWithGoogle() {
        val googleOption = GetSignInWithGoogleOption.Builder(
            serverClientId = getString(R.string.default_web_client_id)
        ).build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleOption)
            .build()

        lifecycleScope.launch {
            try {
                val result = credentialManager.getCredential(
                    context = this@LoginActivity,
                    request = request
                )

                handleGoogleSignIn(result.credential)
            } catch (e: Exception) {
                Toast.makeText(
                    this@LoginActivity,
                    getString(R.string.google_sign_in_failed, e.message),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun handleGoogleSignIn(
        credential: androidx.credentials.Credential
    ) {
        if (
            credential is CustomCredential &&
            credential.type ==
            GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
            try {
                val googleCredential =
                    GoogleIdTokenCredential.createFrom(credential.data)

                val name = googleCredential.displayName ?: "User"

                // This preserves your existing Google sign-in flow.
                // For production, verify the Google ID token on a trusted
                // backend before creating a session.
                UserSession.logIn(this, googleCredential.id, name)

                Toast.makeText(
                    this,
                    getString(R.string.welcome_user, name),
                    Toast.LENGTH_SHORT
                ).show()

                openDashboard()
            } catch (e: GoogleIdTokenParsingException) {
                Toast.makeText(
                    this,
                    getString(R.string.unable_process_google, e.message),
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            Toast.makeText(
                this,
                getString(R.string.unexpected_google_credential),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun openDashboard() {
        startActivity(Intent(this, DashboardActivity::class.java))
        finish()
    }
}
