package com.example.budgettracker.ui.borrowed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.budgettracker.R

class BorrowedFragment : Fragment() {

    companion object {
        fun newInstance() = BorrowedFragment()
    }

    private lateinit var viewModel: BorrowedViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_borrowed, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(BorrowedViewModel::class.java)
        // TODO: Use the ViewModel
    }

}