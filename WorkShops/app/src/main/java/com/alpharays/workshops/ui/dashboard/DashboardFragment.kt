package com.alpharays.workshops.ui.dashboard

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
import com.alpharays.workshops.R
import com.alpharays.workshops.data.entities.Workshop
import com.alpharays.workshops.databinding.FragmentdashboardBinding
import com.alpharays.workshops.getBooleanLiveData
import com.alpharays.workshops.ui.workshops.WorkshopAdapter
import com.alpharays.workshops.viewmodels.UserWorkshopViewModel
import com.alpharays.workshops.viewmodels.WorkShopsViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch


class DashboardFragment : Fragment() {
    private lateinit var binding: FragmentdashboardBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedPreferences2: SharedPreferences
    private lateinit var sharedPreferences3: SharedPreferences
    private lateinit var userWorkshopViewModel: UserWorkshopViewModel
    private lateinit var workshopViewModel: WorkShopsViewModel
    private var currentUserId = ""
    private lateinit var builder: AlertDialog.Builder
    private var mapOfImages: HashMap<Long, Drawable> = HashMap()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentdashboardBinding.inflate(inflater, container, false)

        init()

        val loginStatusLiveData = sharedPreferences.getBooleanLiveData("login_status", false)

        // Create an Observer to update the UI when the login status changes
        val loginStatusObserver = Observer<Boolean> { isLoggedIn ->
            if (isLoggedIn) {
                // User is logged in, show the logout button
                binding.notLoggedInImg.visibility = View.GONE
                binding.cardView.visibility = View.GONE
                getWorkShopsFromDB()

            } else {
                // User is not logged in, show the login button
                binding.notLoggedInImg.visibility = View.VISIBLE
                binding.cardView.visibility = View.VISIBLE
                binding.noSavedWorkshops.visibility = View.GONE
                binding.userWorkshopsRecyclerView.visibility = View.GONE
            }
        }

        // Register the observer to observe changes in the login status
        loginStatusLiveData.observe(requireActivity(), loginStatusObserver)

        reloadingUserData()

        return binding.root
    }

    private fun reloadingUserData() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            Log.i("swiped", "true")
            Handler(Looper.getMainLooper()).postDelayed({
                if (binding.swipeRefreshLayout.isRefreshing) {
                    binding.swipeRefreshLayout.isRefreshing = false
                }
                getWorkShopsFromDB()
            }, 1500)
        }
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

        sharedPreferences = requireContext().getSharedPreferences(
            "sharingUserDataUsingSP#01",
            AppCompatActivity.MODE_PRIVATE
        )
        sharedPreferences2 = requireContext().getSharedPreferences(
            "sharingUserIdUsingSP#02",
            AppCompatActivity.MODE_PRIVATE
        )
        currentUserId = sharedPreferences2.getString("currentUserId", "-1").toString()
        sharedPreferences3 = requireContext().getSharedPreferences(
            "sharingDataUsingSP#03",
            AppCompatActivity.MODE_PRIVATE
        )
    }


    private fun getWorkShopsFromDB() {
        binding.userWorkshopsRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        val adapter = WorkshopAdapter(
            applyButtonStatus = false,
            deleteButtonStatus = true,
            onDeleteClickListener = object : WorkshopAdapter.OnDeleteClickListener {
                override fun onDeleteClicked(id: Long) {
                    initializingUserWorkShopViewModel(id)
                }
            },
            view = binding.root,
            context = requireContext(),
            listOfImages = mapOfImages
        )

        binding.userWorkshopsRecyclerView.adapter = adapter


        workshopViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        )[WorkShopsViewModel::class.java]

        userWorkshopViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        )[UserWorkshopViewModel::class.java]

        userWorkshopViewModel.userWorkshops.observe(viewLifecycleOwner, Observer { list ->
            list?.let {
                binding.dashboardProgressBar.visibility = View.VISIBLE
                lifecycleScope.launch {
                    val deferredWorkshops = it.map { userWorkshop ->
                        async(Dispatchers.IO) {
                            val workshopDetails =
                                workshopViewModel.getWorkshopById(userWorkshop.workshopId)
                            workshopDetails
                        }
                    }
                    val workshopList = deferredWorkshops.awaitAll().filterNotNull()
                    if (workshopList.isNotEmpty()) {
                        binding.noSavedWorkshops.visibility = View.GONE
                        binding.userWorkshopsRecyclerView.visibility = View.VISIBLE
                        addingWorkshopListToSharedPref(ArrayList(workshopList))
                        adapter.updateList(ArrayList(workshopList))
                    } else {
                        binding.noSavedWorkshops.visibility = View.VISIBLE
                        binding.userWorkshopsRecyclerView.visibility = View.GONE
                    }
                    binding.dashboardProgressBar.visibility = View.GONE
                }
            }
        })


//        userWorkshopViewModel.userWorkshops.observe(viewLifecycleOwner, Observer { list ->
//            list?.let {
//                val workshopList: ArrayList<Workshop> = ArrayList()
//                for (i in it.indices) {
//                    val workShopID = it[i].workshopId
//                    lifecycleScope.launch(Dispatchers.IO) {
//                        val workshopDetails = workshopViewModel.getWorkshopById(workShopID)
//                        if (workshopDetails != null) {
//                            workshopList.add(workshopDetails)
//                            Log.i("workshopListList", workshopList.toString())
//                        } else {
//                            Log.i("workshopDetails", "dbError:workshopNotFound")
//                        }
//                    }
//                }
//
//                binding.dashboardProgressBar.visibility = View.VISIBLE
//                Handler(Looper.getMainLooper()).postDelayed({
//                    // Code to be executed after 2 seconds
//                    if (workshopList.isNotEmpty()) {
//                        binding.noSavedWorkshops.visibility = View.GONE
//                        binding.userWorkshopsRecyclerView.visibility = View.VISIBLE
//                        val handler = Handler(Looper.getMainLooper())
//                        handler.post {
//                            Log.i("workshopDetails", workshopList.toString())
//                            adapter.updateList(workshopList)
//                        }
//                    } else {
//                        Log.i("ERROR_ERROR_ERROR", workshopList.toString())
//                        binding.noSavedWorkshops.visibility = View.VISIBLE
//                        binding.userWorkshopsRecyclerView.visibility = View.GONE
//                    }
//                    binding.dashboardProgressBar.visibility = View.GONE
//                }, 2000)
//            }
//        })
        userWorkshopViewModel.getWorkshopsForUser(currentUserId.toLong())
    }

    private fun addingWorkshopListToSharedPref(arrayList: ArrayList<Workshop>) {
//        val gson = Gson()
//        val arrayListJsonString = gson.toJson(arrayList)
        sharedPreferences3 =
            requireContext().getSharedPreferences(
                "sharingDataUsingSP#03",
                AppCompatActivity.MODE_PRIVATE
            )
        val editor = sharedPreferences3.edit()
        val set: MutableSet<String> = HashSet()
        val gson = Gson()
        for (workshop in arrayList) {
            val json = gson.toJson(workshop)
            set.add(json)
        }
        editor.putStringSet("workshopsList", set)
        editor.apply()
//        Log.i("checkingArrayList",arrayList.toString())
//        Log.i("checkingArrayListJson",arrayListJsonString.toString())
//        editor.putString("workshopsList", arrayListJsonString)
//        editor.apply()
    }


    private fun initializingUserWorkShopViewModel(workshopId: Long) {
        userWorkshopViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        )[UserWorkshopViewModel::class.java]
        alertBuilder(currentUserId.toLong(), workshopId)
    }

    private fun alertBuilder(currentId: Long, workShopId: Long) {
        var status = false
        builder = AlertDialog.Builder(requireContext())
        builder.setTitle(R.string.alert_message)
            .setMessage("Do you wish to unregister?")
            .setCancelable(true)
            .setPositiveButton("Yes") { dialogInterface, it ->
                status = true
                userWorkshopViewModel.deleteByUserIdAndWorkshopId(currentId, workShopId)
                dialogInterface.dismiss()
                Handler(Looper.getMainLooper()).postDelayed({
                    getWorkShopsFromDB()
                }, 500)
                Snackbar.make(binding.root, "Deleted", Snackbar.LENGTH_LONG).show()
            }
            .setNegativeButton("No") { dialogInterface, it ->
                dialogInterface.cancel()
            }
            .show()
        Log.i("statusCheck", status.toString())
    }

}