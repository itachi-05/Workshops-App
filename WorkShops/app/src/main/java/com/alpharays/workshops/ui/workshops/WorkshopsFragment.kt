package com.alpharays.workshops.ui.workshops

import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.alpharays.workshops.MainActivity
import com.alpharays.workshops.R
import com.alpharays.workshops.data.entities.Workshop
import com.alpharays.workshops.databinding.FragmentworkshopBinding
import com.alpharays.workshops.getBooleanLiveData
import com.alpharays.workshops.ui.dashboard.DashboardFragment
import com.alpharays.workshops.viewmodels.UserWorkshopViewModel
import com.alpharays.workshops.viewmodels.WorkShopsViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WorkshopsFragment : Fragment(R.layout.fragmentworkshop) {
    private lateinit var binding: FragmentworkshopBinding
    private lateinit var workShopsViewModel: WorkShopsViewModel
    private lateinit var userWorkshopViewModel: UserWorkshopViewModel
    private var mapOfImages: HashMap<Long, Drawable> = HashMap()
    private lateinit var builder: AlertDialog.Builder
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedPreferences2: SharedPreferences
    private var currentUserId = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentworkshopBinding.inflate(inflater, container, false)

        init()
        sharedPreferences2 = requireContext().getSharedPreferences(
            "sharingUserIdUsingSP#02",
            AppCompatActivity.MODE_PRIVATE
        )
        currentUserId = sharedPreferences2.getString(
            "currentUserId",
            "-1"
        ).toString()

        // Data added in DB
        insertWorkShopsInDB()

        binding.workshopsProgressBar.visibility = View.VISIBLE
        Handler(Looper.getMainLooper()).postDelayed({
            // Code to be executed after 1 second
            getWorkShopsFromDB()
            binding.workshopsProgressBar.visibility = View.GONE
        }, 1000)

        return binding.root
    }

    private fun init() {
        mapOfImages[1] = AppCompatResources.getDrawable(requireContext(), R.drawable.advance_js)!!
        mapOfImages[2] = AppCompatResources.getDrawable(requireContext(), R.drawable.advancedsa)!!
        mapOfImages[3] =
            AppCompatResources.getDrawable(requireContext(), R.drawable.ai_ml_bootcamp)!!
        mapOfImages[4] = AppCompatResources.getDrawable(requireContext(), R.drawable.android)!!
        mapOfImages[5] =
            AppCompatResources.getDrawable(requireContext(), R.drawable.android_interview)!!
        mapOfImages[6] =
            AppCompatResources.getDrawable(requireContext(), R.drawable.artificialintelligence)!!
        mapOfImages[7] =
            AppCompatResources.getDrawable(requireContext(), R.drawable.candidatemaster)!!
        mapOfImages[8] =
            AppCompatResources.getDrawable(requireContext(), R.drawable.cybersecurity)!!
        mapOfImages[9] = AppCompatResources.getDrawable(requireContext(), R.drawable.dbms)!!
        mapOfImages[10] = AppCompatResources.getDrawable(requireContext(), R.drawable.django)!!
        mapOfImages[11] = AppCompatResources.getDrawable(requireContext(), R.drawable.dsa)!!
        mapOfImages[12] =
            AppCompatResources.getDrawable(requireContext(), R.drawable.ethicalhacking)!!
        mapOfImages[13] = AppCompatResources.getDrawable(requireContext(), R.drawable.flutter)!!
        mapOfImages[14] = AppCompatResources.getDrawable(requireContext(), R.drawable.ios)!!
        mapOfImages[15] = AppCompatResources.getDrawable(requireContext(), R.drawable.js)!!
        mapOfImages[16] =
            AppCompatResources.getDrawable(requireContext(), R.drawable.machinelearning)!!
        mapOfImages[17] = AppCompatResources.getDrawable(requireContext(), R.drawable.react)!!
        mapOfImages[18] = AppCompatResources.getDrawable(requireContext(), R.drawable.sde)!!
    }


    private fun insertWorkShopsInDB() {

        workShopsViewModel = ViewModelProvider(
            requireActivity(),
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        )[WorkShopsViewModel::class.java]

        val workshop1 = Workshop(
            4,
            "Android App Development",
            "This course includes live classes for Android App development using Java/Kotlin and Xml. A beginner friendly course intended for beginners.",
        )
        val workshop2 = Workshop(
            13,
            "Flutter Development",
            "This course includes live classes for Android App development using Flutter. Classes are meant for beginners and intermediate level students who have little knowledge about flutter development.",
        )
        val workshop3 = Workshop(
            14,
            "IOS App Development",
            "This course includes live classes for IOS App development. It is intended for developers who want to develop applications for apple phones or tablets. Classes are meant for beginners and intermediate level students who have basic knowledge about ios.",
        )
        val workshop4 = Workshop(
            10,
            "Django Crash Course",
            "This course includes recorded classes for Django development using python. Classes are meant for beginners who want to start their journey as Django developer.",
        )
        val workshop5 =
            Workshop(
                11,
                "Data Structures and Algorithms course",
                "This course includes live classes for Data Structures and Algorithms. All data structures are covered along with weekly online tests. Classes are meant for beginners and intermediate level students who have basic or no knowledge about dsa.",
            )
        val workshop6 = Workshop(
            18,
            "SDE Interview Preparation",
            "This course includes live classes for Software Development Engineer position. Selected data structures and algorithm problems are covered along with weekly online contests. These are followed discussions. Classes are meant for students who have completed or are comfortable with DSA implementation in any programming language.",
        )
        val workshop7 = Workshop(
            5,
            "Android Interview Preparation",
            "This course includes live classes for Android developer position. Core android functionalities along with libraries are covered along with weekly online contests. These are followed discussions. Classes are meant for students who have completed or are comfortable with Android development and has developed 4-5 applications.",
        )
        val workshop8 = Workshop(
            17,
            "React Development",
            "This course includes live classes for React development. It is intended for developers who want to develop web applications. Classes are meant for beginners and intermediate level students who have basic knowledge about react.",
        )
        val workshop9 =
            Workshop(
                15,
                "Javascript Course - Basic to Intermediate",
                "This course includes live classes for Javascript development. It is intended for developers who want to develop web applications. Classes are meant for beginners and intermediate level students who have basic knowledge about Javascript.",
            )
        val workshop10 = Workshop(
            1,
            "Advanced Javascript Course",
            "This course includes live classes for Advanced Javascript development. It is intended for developers who want to develop dynamic web applications. Classes are meant for intermediate and experienced level students who have good knowledge about Javascript.",
        )
        val workshop11 = Workshop(
            2,
            "Advanced Algorithms Crash course",
            "This course includes live classes for Advanced Algorithms. It is intended for students who are learning for advance studies or competitive programming. Classes are meant for intermediate and experienced level students who have advanced knowledge about dsa.",
        )
        val workshop12 =
            Workshop(
                7,
                "Road to reach Candidate Master on Codeforces",
                "This is crash course of live classes to reach candidate master on codeforces - a competitive programming website.",
            )
        val workshop13 = Workshop(
            12,
            "Ethical Hacking Bootcamp",
            "This is a bootcamp for students who want to start their journey as an Ethical Hacker. Classes are meant for beginners level students who have basic or no knowledge about ethical hacking.",
        )
        val workshop14 = Workshop(
            8,
            "Cyber Security Bootcamp",
            "This is a bootcamp for students who want to start their journey in Cyber security. Classes are meant for beginners level students who have basic or no knowledge about Cyber security."
        )
        val workshop15 =
            Workshop(
                16,
                "Machine Learning Bootcamp by Harvard",
                "This is a bootcamp intended for students who want to start their journey as Machine Learning Engineer. Classes are meant for beginners level students who have basic or no knowledge about Cyber security."
            )
        val workshop16 =
            Workshop(
                6,
                "Artificial Intelligence Crash Course",
                "This is the crash course for students/graduated students who want to research and start their journey in AI. Prior experience(little) is needed."
            )
        val workshop17 = Workshop(
            3,
            "Machine Learning + Artificial Intelligence Bootcamp",
            "This is full fledged bootcamp for students who want to go in Machine Learning and AI field. This course covers all the topics that are needed to crack any interview process for Machine learning and AI."
        )
        val workshop18 = Workshop(
            9,
            "Database (DBMS) Course",
            "This is full course for students who want to go in SQL and Database based companies. This course covers all the topics in sql and dbms from beginner to advance that are needed to crack any interview process for database companies."
        )

        workShopsViewModel.insertWorkshop(workshop1); workShopsViewModel.insertWorkshop(
            workshop2
        ); workShopsViewModel.insertWorkshop(
            workshop3
        )
        workShopsViewModel.insertWorkshop(workshop4); workShopsViewModel.insertWorkshop(
            workshop5
        ); workShopsViewModel.insertWorkshop(
            workshop6
        )
        workShopsViewModel.insertWorkshop(workshop7); workShopsViewModel.insertWorkshop(
            workshop8
        ); workShopsViewModel.insertWorkshop(
            workshop9
        )
        workShopsViewModel.insertWorkshop(workshop10); workShopsViewModel.insertWorkshop(
            workshop11
        ); workShopsViewModel.insertWorkshop(
            workshop12
        )
        workShopsViewModel.insertWorkshop(workshop13); workShopsViewModel.insertWorkshop(
            workshop14
        ); workShopsViewModel.insertWorkshop(
            workshop15
        )
        workShopsViewModel.insertWorkshop(workshop16); workShopsViewModel.insertWorkshop(
            workshop17
        ); workShopsViewModel.insertWorkshop(
            workshop18
        )
    }


    private fun getWorkShopsFromDB() {
        binding.workshopsRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        val adapter = WorkshopAdapter(object : WorkshopAdapter.OnApplyClickListener {
            override fun onApplyClicked(id: Long) = initializingUserWorkShopViewModel(id)
        }, binding.root, requireContext(), mapOfImages)

        binding.workshopsRecyclerView.adapter = adapter
        workShopsViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        )[WorkShopsViewModel::class.java]

        workShopsViewModel.allWorkshops.observe(viewLifecycleOwner, Observer { list ->
            list?.let {
                adapter.updateList(ArrayList(it))
            }
        })
    }


    private fun initializingUserWorkShopViewModel(workshopId: Long) {
        workShopsViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        )[WorkShopsViewModel::class.java]
        checkingIfUserLoggedIn(workshopId, workShopsViewModel)
    }


    private fun checkingIfUserLoggedIn(
        workShopId: Long,
        myWorkShopsViewModel: WorkShopsViewModel
    ) {
        sharedPreferences = requireContext().getSharedPreferences(
            "sharingUserDataUsingSP#01",
            AppCompatActivity.MODE_PRIVATE
        )
        val loginStatusLiveData = sharedPreferences.getBooleanLiveData("login_status", false)

        // Create an Observer to update the UI when the login status changes
        val loginStatusObserver = Observer<Boolean> { isLoggedIn ->
            if (isLoggedIn) {
                lifecycleScope.launch(Dispatchers.IO) {
                    val workshop = myWorkShopsViewModel.getWorkshopById(workShopId)
                    if (workshop != null) {
                        Log.i("workshopDetails", workshop.toString())
                        val handler = Handler(Looper.getMainLooper())
                        handler.post {
                            alertBuilder(workShopId, currentUserId, workshop.name)
                        }
                    } else {
                        Log.i("workshopDetails", "dbError")
                    }
                }
            } else {
                (activity as MainActivity).showingLoginPage()
                Snackbar.make(binding.root, "Login to apply", Snackbar.LENGTH_SHORT).show()
            }
        }

        // Register the observer to observe changes in the login status
        loginStatusLiveData.observe(requireActivity(), loginStatusObserver)
    }


    private fun alertBuilder(workShopId: Long, currentID: String, workshopName: String) {
        builder = AlertDialog.Builder(requireContext())
        builder.setTitle(R.string.alert_message)
            .setMessage("Do you wish to apply for $workshopName?")
            .setCancelable(true)
            .setPositiveButton("Yes") { dialogInterface, it ->
                applyingForWorkshop(workShopId, currentID.toLong())
                dialogInterface.dismiss()
            }
            .setNegativeButton("No") { dialogInterface, it ->
                dialogInterface.cancel()
            }
            .show()
    }


    private fun applyingForWorkshop(workShopId: Long, currentID: Long) {
        userWorkshopViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        )[UserWorkshopViewModel::class.java]
        userWorkshopViewModel.insertUserWorkshop(currentID, workShopId)
        val dashboardFragment =
            parentFragmentManager.findFragmentByTag("DashboardFragment") as DashboardFragment?
        dashboardFragment?.getWorkShopsFromDB()

        userWorkshopViewModel.userWorkshops.observe(viewLifecycleOwner, Observer { list ->
            list?.let {
                Log.i("FOUND", list.toString())
            }
        })
        userWorkshopViewModel.getWorkshopsForUser(currentUserId.toLong())
        Snackbar.make(binding.root, "Applied Successfully", Snackbar.LENGTH_SHORT).show()
    }

}
