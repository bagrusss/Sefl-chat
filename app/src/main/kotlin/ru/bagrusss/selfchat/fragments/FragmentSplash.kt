package ru.bagrusss.selfchat.fragments

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.bagrusss.selfchat.R

/**
 * Created by bagrusss.
 */
class FragmentSplash : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_splash_map, container, false)
    }
}