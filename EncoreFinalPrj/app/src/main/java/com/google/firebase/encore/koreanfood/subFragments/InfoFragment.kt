package com.google.firebase.encore.koreanfood

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class InfoFragment: Fragment(), View.OnClickListener {

    override fun onClick(p0: View?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view: View = inflater.inflate(R.layout.fragment_info, container, false)
        return view
    }

    companion object {
        fun newInstance(): InfoFragment = InfoFragment()
    }


}