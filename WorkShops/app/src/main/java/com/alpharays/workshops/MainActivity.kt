package com.alpharays.workshops

import android.content.SharedPreferences
import android.content.res.Resources
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteException
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.Gravity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.widget.CheckedTextView
import android.widget.EditText
import android.widget.PopupWindow
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.TooltipCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.alpharays.workshops.data.entities.User
import com.alpharays.workshops.data.entities.Workshop
import com.alpharays.workshops.databinding.ActivityMainBinding
import com.alpharays.workshops.viewmodels.UsersViewModel
import com.alpharays.workshops.viewmodels.WorkShopsViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity() {
    private lateinit var tabLayout: TabLayout
    private lateinit var myViewPager: ViewPager2
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var builder: AlertDialog.Builder
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedPreferences2: SharedPreferences
    private var loginUserEmail = ""
    private var loginUserPwd = ""
    private var registerUserEmail = ""
    private var registerUserPwd = ""
    private lateinit var usersViewModel: UsersViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()

        sharedPreferences = getSharedPreferences("sharingUserDataUsingSP#01", MODE_PRIVATE)
        val loginStatusLiveData = sharedPreferences.getBooleanLiveData("login_status", false)

        // Create an Observer to update the UI when the login status changes
        val loginStatusObserver = Observer<Boolean> { isLoggedIn ->
            if (isLoggedIn) {
                // User is logged in, show the logout button
                binding.userStatusBtn.text = "Logout"
                binding.userStatusBtn.setOnClickListener {
                    // Handle logout action
                    confirmingLogOut()
                }
            } else {
                // User is not logged in, show the login button
                binding.userStatusBtn.text = "Login/Register"
                binding.userStatusBtn.setOnClickListener {
                    // Handle login action
                    showingLoginPage()
                }
            }
        }

        // Register the observer to observe changes in the login status
        loginStatusLiveData.observe(this, loginStatusObserver)

    }

    private fun init() {
        tabLayout = findViewById(R.id.myTabLayout)
        myViewPager = findViewById(R.id.MyViewPager)
        myViewPager.adapter = MyAdapter(this)
        TabLayoutMediator(tabLayout, myViewPager) { tab, index ->
            tab.text = when (index) {
                0 -> {
                    "Dashboard"
                }
                1 -> {
                    "Workshops"
                }
                else -> {
                    throw Resources.NotFoundException("Position not found")
                }
            }
        }.attach()
    }

    private fun confirmingLogOut() {
        builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.alert_message))
            .setMessage("Do you wish to Log Out?")
            .setCancelable(true)
            .setPositiveButton("Yes") { dialogInterface, it ->
                binding.userStatusBtn.text = "Login/Register"
                sharedPreferences = getSharedPreferences("sharingUserDataUsingSP#01", MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putBoolean("login_status", false)
                editor.apply()
                dialogInterface.cancel()
            }
            .setNegativeButton("No") { dialogInterface, it ->
                dialogInterface.cancel()
            }
            .show()
    }


    // ********************************    user login    ********************************
    fun showingLoginPage() {
        val popupView = LayoutInflater.from(this).inflate(R.layout.layoutlogin, null)
        val popupWindow = PopupWindow(
            popupView,
            580,
            780,
            true
        )
        popupWindow.setBackgroundDrawable(
            ColorDrawable(
                ContextCompat.getColor(
                    this,
                    android.R.color.transparent
                )
            )
        )
        popupWindow.animationStyle = R.style.PopupAnimation
        popupWindow.showAtLocation(popupView, Gravity.CENTER_VERTICAL, 0, 0)
        movingToRegisterPage(popupWindow, popupView)
        loggingInUser(popupWindow, popupView)
    }

    private fun loggingInUser(popupWindow: PopupWindow, popupView: View?) {
        textWatcherFun1(popupView)
        val loginButton = popupView?.findViewById<MaterialButton>(R.id.loginUserBtn)
        loginButton?.setOnClickListener {
            if (!isValidEmail1(loginUserEmail)) {
                Snackbar.make(binding.root, "Enter correct email", Snackbar.LENGTH_SHORT).show()
            } else {
                usersViewModel = ViewModelProvider(this)[UsersViewModel::class.java]
                usersViewModel.allUsers.observe(this, Observer { users ->
                    if (users.isEmpty()) {
                        Snackbar.make(binding.root, "Account not found", Snackbar.LENGTH_SHORT)
                            .show()
                    } else {
                        var pwd = ""
                        var id = ""
                        var validUser = false
                        for (i in users.indices) {
                            if (users[i].email == loginUserEmail) {
                                validUser = true
                                pwd = users[i].password
                                id = users[i].id.toString()
                                break
                            }
                        }
                        if (validUser) {
                            if (loginUserPwd == pwd) {
                                sharedPreferences =
                                    getSharedPreferences("sharingUserDataUsingSP#01", MODE_PRIVATE)
                                val editor = sharedPreferences.edit()
                                editor.putBoolean("login_status", true)
                                editor.apply()
                                sharedPreferences2 =
                                    getSharedPreferences("sharingUserIdUsingSP#02", MODE_PRIVATE)
                                sharedPreferences2.edit().putString("currentUserId", id).apply()
                                binding.userStatusBtn.text = "Sign Out"
                                Log.i("checkingEmail_and_Pwd", "$loginUserEmail && $loginUserPwd")
                                loginUserEmail = ""; loginUserPwd = ""
                                popupWindow.dismiss()
                                Snackbar.make(binding.root, "Welcome back", Snackbar.LENGTH_SHORT)
                                    .show()
                            } else {
                                Snackbar.make(binding.root, "Wrong password", Snackbar.LENGTH_SHORT)
                                    .show()
                            }
                        } else {
                            Snackbar.make(binding.root, "Account not found", Snackbar.LENGTH_SHORT)
                                .show()
                        }
                    }
                    Log.i("checkingAllUsers", users.toString())
                })
            }
        }
    }

    private fun textWatcherFun1(popupView: View?) {
        popupView?.findViewById<EditText>(R.id.loginUserEmail)
            ?.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    // This method is called before the text is changed.
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    // This method is called when the text is changed.
                    loginUserEmail = s.toString() // Get the updated text as a String.
                    // Do something with the updated text, such as validate it or update a ViewModel.
                }

                override fun afterTextChanged(s: Editable?) {
                    // This method is called after the text is changed.
                }
            })
        popupView?.findViewById<EditText>(R.id.loginUserPwd)
            ?.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    // This method is called before the text is changed.
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    // This method is called when the text is changed.
                    loginUserPwd = s.toString() // Get the updated text as a String.
                    // Do something with the updated text, such as validate it or update a ViewModel.
                }

                override fun afterTextChanged(s: Editable?) {
                    // This method is called after the text is changed.
                }
            })


        popupView?.findViewById<EditText>(R.id.loginUserEmail)?.setOnKeyListener { _, keyCode, _ ->
            keyCode == KeyEvent.KEYCODE_SPACE
        }
        popupView?.findViewById<EditText>(R.id.loginUserPwd)?.setOnKeyListener { _, keyCode, _ ->
            keyCode == KeyEvent.KEYCODE_SPACE
        }

    }

    private fun isValidEmail1(email: String): Boolean {
        val emailRegex = Regex(pattern = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")
        return emailRegex.matches(email)
    }

    private fun movingToRegisterPage(popupWindow: PopupWindow, popupView: View) {
        val ss = SpannableString("Not Registered? Register here")
        val clickableSpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                popupWindow.dismiss()
                showingRegisterPage()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
            }
        }
        ss.setSpan(clickableSpan, 25, 29, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        ss.setSpan(StyleSpan(Typeface.BOLD), 25, 29, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        val notRegisteredTxt = popupView.findViewById<CheckedTextView>(R.id.notRegistered)
        notRegisteredTxt.text = ss
        notRegisteredTxt.movementMethod = LinkMovementMethod.getInstance()
        notRegisteredTxt.highlightColor = Color.TRANSPARENT
    }
    // ********************************    user login    ********************************


    // ********************************    user register    ********************************
    private fun showingRegisterPage() {
        val popupView = LayoutInflater.from(this).inflate(R.layout.layoutregister, null)
        val popupWindow = PopupWindow(
            popupView,
            580,
            780,
            true
        )
        popupWindow.setBackgroundDrawable(
            ColorDrawable(
                ContextCompat.getColor(
                    this,
                    android.R.color.transparent
                )
            )
        )
        popupWindow.animationStyle = R.style.PopupAnimation
        popupWindow.showAtLocation(popupView, Gravity.CENTER_VERTICAL, 0, 0)
        movingToSignInPage(popupWindow, popupView)
        registeringUser(popupWindow, popupView)
    }

    private fun registeringUser(popupWindow: PopupWindow, popupView: View?) {
        textWatcherFun2(popupView)
        val loginButton = popupView?.findViewById<MaterialButton>(R.id.registerUserBtn)
        loginButton?.setOnClickListener {
            registerUserEmail = reorderEmail(registerUserEmail)
            popupView.findViewById<EditText>(R.id.registerUserEmail)?.setText(registerUserEmail)
            if (!isValidEmail2(registerUserEmail) || registerUserEmail.isEmpty()) {
                Snackbar.make(binding.root, "Enter correct email", Snackbar.LENGTH_SHORT).show()
            } else if (!pwdValid(registerUserPwd) || registerUserPwd.isEmpty() || registerUserPwd.length < 8) {
                Snackbar.make(
                    binding.root,
                    "Should not contain spaces and should be of minimum 8 characters",
                    Snackbar.LENGTH_SHORT
                ).show()
            } else {
                val user = User(email = registerUserEmail, password = registerUserPwd)
                usersViewModel = ViewModelProvider(this)[UsersViewModel::class.java]
                usersViewModel.statusResponse.observe(this, Observer {
                    if (it != null) {
                        if (it == true) {
                            popupWindow.dismiss()
                            Snackbar.make(binding.root, "Registered", Snackbar.LENGTH_SHORT).show()
                            registerUserEmail = ""; registerUserPwd = ""
                        } else {
                            Snackbar.make(
                                binding.root, "Email already exists", Snackbar.LENGTH_SHORT
                            ).show()
                        }
                    }
                })
                usersViewModel.insertUser(user)
            }
        }
    }

    private fun reorderEmail(email: String): String {
        var s = ""
        for (i in email.indices) {
            if (email[i] != ' ') {
                s += email[i]
            }
        }
        return s
    }

    private fun textWatcherFun2(popupView: View?) {
        popupView?.findViewById<EditText>(R.id.registerUserEmail)
            ?.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    // This method is called before the text is changed.
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    // This method is called when the text is changed.
                    registerUserEmail = s.toString() // Get the updated text as a String.
                    // Do something with the updated text, such as validate it or update a ViewModel.
                }

                override fun afterTextChanged(s: Editable?) {
                    // This method is called after the text is changed.
                }
            })
        popupView?.findViewById<EditText>(R.id.registerUserPwd)
            ?.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    // This method is called before the text is changed.
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    // This method is called when the text is changed.
                    registerUserPwd = s.toString() // Get the updated text as a String.
                    // Do something with the updated text, such as validate it or update a ViewModel.
                }

                override fun afterTextChanged(s: Editable?) {
                    // This method is called after the text is changed.
                }
            })


        popupView?.findViewById<EditText>(R.id.registerUserEmail)
            ?.setOnKeyListener { _, keyCode, _ ->
                keyCode == KeyEvent.KEYCODE_SPACE
            }
        popupView?.findViewById<EditText>(R.id.registerUserPwd)?.setOnKeyListener { _, keyCode, _ ->
            keyCode == KeyEvent.KEYCODE_SPACE
        }

    }

    private fun isValidEmail2(email: String): Boolean {
        val emailRegex = Regex(pattern = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")
        return emailRegex.matches(email)
    }

    private fun pwdValid(pwd: String): Boolean {
        for (i in pwd.indices) {
            if (pwd[i] == ' ') {
                return false
            }
        }
        return true
    }

    private fun movingToSignInPage(popupWindow: PopupWindow, popupView: View) {
        val ss = SpannableString("Already Registered? Sign in here")
        val clickableSpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                popupWindow.dismiss()
                showingLoginPage()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
            }
        }
        ss.setSpan(clickableSpan, 27, 32, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        ss.setSpan(StyleSpan(Typeface.BOLD), 27, 32, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        val registeredTxt = popupView.findViewById<CheckedTextView>(R.id.alreadyRegistered)
        registeredTxt.text = ss
        registeredTxt.movementMethod = LinkMovementMethod.getInstance()
        registeredTxt.highlightColor = Color.TRANSPARENT
    }
    // ********************************    user register    ********************************


}