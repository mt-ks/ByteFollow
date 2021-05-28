package com.fastfollow.bytefollow.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.VideoView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fastfollow.bytefollow.R
import com.fastfollow.bytefollow.model.UserDetail
import com.fastfollow.bytefollow.model.VideoDetail
import de.hdodenhof.circleimageview.CircleImageView

class SearchAdapter(var context:Context, var list : List<VideoDetail>,var userDetail: UserDetail)
    : RecyclerView.Adapter<BaseViewHolder<*>>()
{
    companion object {
        const val VIEW_TYPE_PROFILE = 1
        const val VIEW_TYPE_MEDIAS  = 2
    }

    override fun getItemCount(): Int {
        return list.size + 1;
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        if (viewType == VIEW_TYPE_PROFILE) {
            return ProfileViewHolder(
                LayoutInflater.from(context).inflate(R.layout.rc_seach_profile_item, parent, false)
            )
        }
        return VideoViewHolder(
            LayoutInflater.from(context).inflate(R.layout.rc_search_video_item, parent, false)
        )
    }

    private inner class ProfileViewHolder(itemView: View) : BaseViewHolder<UserDetail>(itemView) {
        val username  : TextView = itemView.findViewById(R.id.searchUsernameField)
        val avatar    : CircleImageView = itemView.findViewById(R.id.userAvatar)
        val followers : TextView = itemView.findViewById(R.id.followersCount)
        val following : TextView = itemView.findViewById(R.id.followingCount)
        val likes     : TextView = itemView.findViewById(R.id.likesCount)
        @SuppressLint("SetTextI18n")
        override fun bind(item: UserDetail) {
            username.text = item.user.uniqueId
            Glide.with(itemView).load(item.user.avatarMedium).into(avatar)
            followers.text = item.stats.followerCount.toString()
            following.text = item.stats.followingCount.toString()
            likes.text = item.stats.heartCount.toString()
        }
    }

    private inner class VideoViewHolder(itemView: View) : BaseViewHolder<VideoDetail>(itemView) {
        val image : ImageView = itemView.findViewById(R.id.client_media)
        val likeCount : TextView = itemView.findViewById(R.id.likeCount)
        override fun bind(item: VideoDetail) {
            Glide.with(itemView).load(item.video.cover).into(image)
            likeCount.text = item.stats.diggCount.toString()
        }
    }




    override fun getItemViewType(position: Int): Int {
        return if(position == 0) VIEW_TYPE_PROFILE else VIEW_TYPE_MEDIAS
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        when(holder)
        {
            is ProfileViewHolder -> holder.bind(userDetail)
            is VideoViewHolder -> holder.bind(list[position - 1])
        }
    }


}