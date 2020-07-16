package com.example.yeniappwkotlin.ui.fragment.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.yeniappwkotlin.R
import com.example.yeniappwkotlin.data.db.database.AppDatabase
import com.example.yeniappwkotlin.data.db.entities.Post
import com.example.yeniappwkotlin.data.db.entities.PostLikes
import com.example.yeniappwkotlin.data.network.MyApi
import com.example.yeniappwkotlin.data.network.NetworkConnectionInterceptor
import com.example.yeniappwkotlin.data.network.repositories.PostRepository
import com.example.yeniappwkotlin.databinding.FragmentHomeRowItemBinding
import com.example.yeniappwkotlin.ui.activity.comment.CommentActivity
import com.example.yeniappwkotlin.ui.activity.comment.CommentListener
import com.example.yeniappwkotlin.util.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_home_row_item.*
import kotlinx.android.synthetic.main.home_fragment.*


class HomeFragment : Fragment(), RecyclerViewClickListener, CommentListener {
    private var pageNumber = 1
    private val itemCount = 10

    //veriables for pagiantion
    private var isLoading = true
    private var pastVisibleItems : Int? = null
    private var visibleItemCount : Int? = null
    private var totalItemCount : Int? = null
    private var previouesTotal = 0
    private var viewThreshold = 10

    private var layoutManager : LinearLayoutManager? = null
    var flag = false

    var userId : Int? = null
    var userName : String? = null
    var repository : PostRepository? = null

    var navController: NavController? = null
    lateinit var adapter : HomeFragmentAdapter
    companion object {
        fun newInstance() = HomeFragment()
    }

    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.home_fragment, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        openCloseSoftKeyboard(requireContext(),requireView(), false)

        if (bottomNavigation != null){
            bottomNavigation.checkItem(R.id.nav_tab_home)
        }
        userId = PrefUtils.with(requireContext()).getInt("user_id", 0)
        userName = PrefUtils.with(requireContext()).getString("user_name", "")
        val networkConnectionInterceptor = NetworkConnectionInterceptor(requireContext())
        val api = MyApi(networkConnectionInterceptor)
        val db = AppDatabase(requireContext())
        repository = PostRepository(api, db, requireContext())
        val factory = HomeViewModelFactory(repository!!, userId!!, pageNumber, itemCount)

        viewModel = ViewModelProviders.of(this, factory).get(HomeViewModel::class.java)
        layoutManager = LinearLayoutManager(requireContext())

        //Load User AppBar Image
        val userImage = PrefUtils.with(requireContext()).getString("user_image", "")
        val isSocial = PrefUtils.with(requireContext()).getInt("is_social_account", 0)
        loadImage(ivHomePhoto,userImage, isSocial)
        //

        adapter = HomeFragmentAdapter(listOf(), this,requireContext())
        progress_bar.show()
        
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
        directsProfile(requireActivity(), navController!!, ivHomePhoto)

        getPostItems()
        recycler_home.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                visibleItemCount = layoutManager!!.childCount
                totalItemCount = layoutManager!!.itemCount
                pastVisibleItems = layoutManager!!.findFirstVisibleItemPosition()

                if (dy > 0){
                    if(isLoading && totalItemCount!! > previouesTotal ){
                        isLoading = false
                        previouesTotal = totalItemCount as Int
                    }
                    if (!isLoading && (totalItemCount!! - visibleItemCount!!) <= pastVisibleItems!!+viewThreshold){
                        pageNumber ++
                        performPagination()
                        isLoading = true
                    }
                }
            }
        })

        onRefresh.setOnRefreshListener {
            refreshPosts()
        }
    }

    private fun refreshPosts() {
        PrefUtils.with(requireContext()).remove("post_key_saved_at")
        getPostItems()
    }

    private fun getPostItems(){
        Coroutines.main {
            try {
                val posts = viewModel.getPost.await()
                posts.observe(viewLifecycleOwner, Observer { post ->
                    progress_bar.hide()
                    recycler_home.also {
                        if (!flag && post.isNotEmpty()){
                            adapter = HomeFragmentAdapter(post,this, requireContext())
                            it.adapter = adapter
                            flag = true
                        }
                        //adapter.addPost(post)
                        adapter.notifyDataSetChanged()
                        it.layoutManager = layoutManager
                        it.setHasFixedSize(true)
                        onRefresh.isRefreshing = false
                    }
                })
            } catch (e : Exception){
                e.printStackTrace()
            } catch (e : NoInternetException){
                Toast.makeText(requireContext(), getString(R.string.check_internet), Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            } catch (e : ApiException){
                e.printStackTrace()
            }
        }
    }

    override fun onRecyclerViewItemClick(
        view: View,
        post: Post,
        homeRowItemBinding: FragmentHomeRowItemBinding
    ) {
        when (view.id) {
            R.id.postBtnComment -> {
                val intent = Intent(requireContext(), CommentActivity::class.java)
                intent.putExtra("post_id", post.post_id)
                intent.putExtra("post_name", post.user_name)
                intent.putExtra("post_user_id", post.user_id)
                intent.putExtra("path", post.paths)
                startActivity(intent)
            }
            R.id.post_comment_count -> {
                val intent = Intent(requireContext(), CommentActivity::class.java)
                intent.putExtra("post_id", post.post_id)
                startActivity(intent)
            }
            R.id.home_likes -> {
                val userId = PrefUtils.with(requireContext()).getInt("user_id", 0)
                val likeCount : Int
                if (post.user_post_likes?.begeni_durum != null && post.user_post_likes!!.begeni_durum == 1 ){
                    likeCount = post.like_count!! -1
                    viewModel.btnPostLike(post.post_id!!, userId, post.like_count!!, 0)
                    post.user_post_likes!!.begeni_durum = 0
                    post.like_count = likeCount
                    homeRowItemBinding.homeLikes.setImageResource(R.drawable.icon_heart_gray)
                    homeRowItemBinding.postLikeCount.text = likeCount.toString()

                }else if(post.user_post_likes?.begeni_durum != null && post.user_post_likes!!.begeni_durum == 0){
                    likeCount = post.like_count!! +1
                    viewModel.btnPostLike(post.post_id!!, userId, post.like_count!!, 1)
                    viewModel.pushNotification(userName!!, post.user_name!!, post.share_post!!, 3)
                    post.user_post_likes!!.begeni_durum = 1
                    post.like_count = likeCount
                    homeRowItemBinding.homeLikes.setImageResource(R.drawable.icon_heart)
                    homeRowItemBinding.postLikeCount.text = likeCount.toString()
                }else {
                    likeCount = post.like_count!! +1
                    viewModel.saveUserPostLikes(userId, post.post_id!!, 1, likeCount, post.user_id!!)
                    viewModel.pushNotification(userName!!, post.user_name!!, post.share_post!!, 3)
                    post.user_post_likes = PostLikes(1)
                    post.like_count = likeCount
                    homeRowItemBinding.homeLikes.setImageResource(R.drawable.icon_heart)
                    homeRowItemBinding.postLikeCount.text = likeCount.toString()
                }
            }
            R.id.post_profile_resim -> {
                val bundle = Bundle()
                bundle.putInt("different_user_id",post.user_id!!)
                bundle.putString("different_user_name",post.user_name)
                bundle.putString("different_first_name",post.first_name)
                bundle.putString("different_last_name",post.last_name)
                bundle.putString("different_user_photo",post.paths)
                bundle.putInt("different_user_isSocial",post.is_social_account!!)
                PrefUtils.with(requireContext()).save("different_user",post.user_id)
                navController!!.navigate(R.id.profileFragment, bundle)
            }
        }
    }

    private fun performPagination() {
        progress_bar.show()
        Coroutines.main {
            try {
                progress_bar.hide()
                val data = repository!!.getPostData(userId!!, pageNumber, itemCount)
                if (data.isNotEmpty()){
                    repository!!.savePost(data)
                    recycler_home.apply {
                        (adapter as? HomeFragmentAdapter)?.addPost(data)
                    }
                    //recyclerView.post { adapter.notifyItemRangeChanged(10*pageNumber -1, 10*pageNumber) }
                }
            }catch (e : NoInternetException){
                Log.d("HATAA", e.message!!)
                Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
            }catch (e : Exception){
                Log.d("HATA",e.message!!)
            }
        }
    }

    override fun onStarted() {

    }

    override fun onSuccess(message: String) {
        home_layout.snackbar(message)
    }

    override fun onFailure(message: String) {
        home_layout.snackbar(message)
    }
}
