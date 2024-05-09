package com.evw.aster

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip


class Collectionsplash : Fragment() {

lateinit var recyclerView: RecyclerView
lateinit var button: Button
lateinit var linearLayout: LinearLayout
lateinit var chip: Chip
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_collectionsplash, container, false)
        recyclerView = view.findViewById(R.id.precollection_recyler)
        val manager = LinearLayoutManager(context)
        recyclerView.layoutManager = manager
        button = view.findViewById(R.id.create_collect)
        linearLayout = view.findViewById(R.id.temper)
        chip = view.findViewById(R.id.create)
        button.setOnClickListener {
         linearLayout.visibility = View.VISIBLE
        }
        chip.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.containerhere,Astershop())?.addToBackStack(null)?.commit()
        }





        return view
    }


}