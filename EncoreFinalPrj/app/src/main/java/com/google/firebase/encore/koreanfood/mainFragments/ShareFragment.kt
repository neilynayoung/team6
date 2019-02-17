package com.google.firebase.encore.koreanfood

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.encore.koreanfood.DataModel.ContentDTO
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.android.synthetic.main.fragment_share.*
import kotlinx.android.synthetic.main.item_detail.view.*

class ShareFragment: Fragment() {

    var user: FirebaseUser? = null
    var firestore: FirebaseFirestore? = null
    var imagesSnapshot: ListenerRegistration? = null
    var mainView: View? = null
    var contentDTOs: MutableList<ContentDTO> = ArrayList()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        user = FirebaseAuth.getInstance().currentUser
        // Recycler View와 어댑터를 연결
        mainView = inflater.inflate(R.layout.fragment_share, container, false)

        contentDTOs = (activity as MainActivity).contentDTOs

        return mainView
    }

    override fun onResume() {
        super.onResume()

        shareviewfragment_recyclerview?.layoutManager = LinearLayoutManager(activity)
        shareviewfragment_recyclerview?.adapter = ShareRecyclerViewAdapter()
    }

    override fun onStop() {
        super.onStop()
        imagesSnapshot?.remove()
    }

    inner class ShareRecyclerViewAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>(){

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_detail, parent, false)

            return CustomViewHolder(view)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

            Log.d("바인드뷰", contentDTOs.size.toString())
            val viewHolder = (holder as CustomViewHolder).itemView

            // 유저 아이디
            viewHolder.shareviewitem_profile_textview.text = contentDTOs[position]?.userId
            // 음식 이미지
            Glide.with(holder.itemView.context).load(contentDTOs[position]?.photoUrl).into(viewHolder.shareviewitem_imageview_content)
            // 리뷰 텍스트
            viewHolder.shareviewitem_review_textview.text = contentDTOs[position]?.review

        }

        override fun getItemCount(): Int {

            Log.d("가나다라", contentDTOs.size.toString())
            return contentDTOs.size
        }

    }

    inner class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)


    companion object {
        fun newInstance(): ShareFragment = ShareFragment()
    }

}