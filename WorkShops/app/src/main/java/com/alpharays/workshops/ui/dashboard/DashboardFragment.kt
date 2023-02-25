package com.alpharays.workshops.ui.dashboard

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.alpharays.workshops.databinding.FragmentdashboardBinding
import com.alpharays.workshops.getBooleanLiveData

class DashboardFragment : Fragment() {
    private lateinit var binding: FragmentdashboardBinding
    private lateinit var sharedPreferences: SharedPreferences


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentdashboardBinding.inflate(inflater, container, false)

        sharedPreferences = requireContext().getSharedPreferences(
            "sharingUserDataUsingSP#01",
            AppCompatActivity.MODE_PRIVATE
        )
        val loginStatusLiveData = sharedPreferences.getBooleanLiveData("login_status", false)

        // Create an Observer to update the UI when the login status changes
        val loginStatusObserver = Observer<Boolean> { isLoggedIn ->
            if (isLoggedIn) {
                // User is logged in, show the logout button
                binding.noSavedWorkshops.visibility = View.VISIBLE
                binding.notLoggedInImg.visibility = View.GONE
                binding.cardView.visibility = View.GONE
//                binding.userWorkshopsRecyclerView.visibility = View.VISIBLE
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

        return binding.root
    }
}