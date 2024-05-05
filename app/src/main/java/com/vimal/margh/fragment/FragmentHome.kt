package com.vimal.margh.fragment

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.vimal.margh.R
import com.vimal.margh.activity.ActivityDetail
import com.vimal.margh.adapter.AdapterCategory
import com.vimal.margh.adapter.AdapterWallpaper
import com.vimal.margh.callback.CallbackWallpaper
import com.vimal.margh.databinding.DialogCategoryBinding
import com.vimal.margh.databinding.FragmentHomeBinding
import com.vimal.margh.db.Repository
import com.vimal.margh.models.ModelCategory
import com.vimal.margh.models.ModelWallpaper
import com.vimal.margh.rest.RestAdapter.createAPI
import com.vimal.margh.util.Constant
import com.vimal.margh.util.Constant.EXTRA_KEY
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar


class FragmentHome : Fragment(), AdapterWallpaper.OnItemClickListener {

    private val modelLists: MutableList<ModelWallpaper?> = ArrayList()
    private val repository: Repository by lazy { Repository.getInstance(requireActivity())!! }
    private var adapterCategory: AdapterCategory? = null
    private var adapterWallpaper: AdapterWallpaper? = null
    private var categoryString = "random"
    private var binding: FragmentHomeBinding? = null
    private var callbackCall: Call<CallbackWallpaper?>? = null
    private var postTotal = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding!!.root

        val currentTime = Calendar.getInstance()[Calendar.HOUR_OF_DAY]
        val greetingMessage = when (currentTime) {
            in 6..11 -> {
                "Good morning"
            }

            in 12..17 -> {
                "Good afternoon"
            }

            in 18..21 -> {
                "Good evening"
            }

            else -> {
                "Good night"
            }
        }
        binding!!.tvWelcome.text = greetingMessage
        binding!!.icError.pvProgress.visibility = View.VISIBLE

        binding!!.cvCategory.setOnClickListener { showDialogCategory() }
        requestAction(1)

        binding!!.rvRecycler.layoutManager = LinearLayoutManager(requireActivity())
        adapterWallpaper = AdapterWallpaper(requireActivity(), binding!!.rvRecycler, modelLists)
        binding!!.rvRecycler.adapter = adapterWallpaper

        adapterWallpaper!!.setOnLoadMoreListener(object : AdapterWallpaper.OnLoadMoreListener {
            override fun onLoadMore(page: Int) {
                if (postTotal > adapterWallpaper!!.itemCount && page != 0) {
                    val nextPage = page + 1
                    requestAction(nextPage)
                } else {
                    adapterWallpaper!!.setLoaded()
                }
            }
        })


        adapterWallpaper!!.setOnItemClickListener(this)
        binding!!.slSwipe.setColorSchemeResources(
            R.color.color_orange,
            R.color.color_red,
            R.color.color_blue,
            R.color.color_green
        )
        binding!!.slSwipe.setOnRefreshListener {
            if (callbackCall != null && callbackCall!!.isExecuted) callbackCall!!.cancel()
            requestAction(1)
        }

        binding!!.icError.btError.setOnClickListener {
            requestAction(1)
            binding!!.icError.llErrorNetwork.visibility = View.GONE
            binding!!.rvRecycler.visibility = View.GONE
            binding!!.icError.pvProgress.visibility = View.VISIBLE
        }


        return root
    }

    private fun showDialogCategory() {
        val bindingDialog = DialogCategoryBinding.inflate(LayoutInflater.from(requireActivity()))
        val builder = MaterialAlertDialogBuilder(requireActivity(), R.style.CustomDialog)
        builder.setView(bindingDialog.root)
        builder.setCancelable(true)
        val dialog = builder.create()
        val window = dialog.window
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val numbers: MutableList<ModelCategory> = ArrayList()
        numbers.add(ModelCategory(1, R.drawable.icon_background, "random", "Random"))
        numbers.add(ModelCategory(2, R.drawable.icon_fashion, "fashion", "Fashion"))
        numbers.add(ModelCategory(3, R.drawable.icon_nature, "nature", "Nature"))
        numbers.add(ModelCategory(4, R.drawable.icon_science, "science", "Science"))
        numbers.add(ModelCategory(5, R.drawable.icon_education, "education", "Education"))
        numbers.add(ModelCategory(6, R.drawable.icon_face, "feelings", "Feelings"))
        numbers.add(ModelCategory(7, R.drawable.icon_health, "health", "Health"))
        numbers.add(ModelCategory(8, R.drawable.icon_people, "people", "People"))
        numbers.add(ModelCategory(9, R.drawable.icon_religion, "religion", "Religion"))

        adapterCategory = AdapterCategory(requireActivity(), numbers)
        bindingDialog.rvRecyclerCategory.layoutManager = GridLayoutManager(requireActivity(), 3)
        bindingDialog.rvRecyclerCategory.adapter = adapterCategory

        adapterCategory!!.setOnItemClickListener(object : AdapterCategory.OnItemClickListener {
            override fun onItemClick(category: ModelCategory?) {
                binding!!.icError.pvProgress.visibility = View.VISIBLE
                this@FragmentHome.categoryString = category!!.category
                adapterWallpaper!!.clearData()
                requestAction(1)
                dialog.dismiss()
            }
        })

        dialog.show()
    }

    private fun requestListPostApi(pageNo: Int, category: String) {
        callbackCall = createAPI(requireActivity()).getWallpapers(
            category,
            "vertical",
            Constant.LOAD_MORE,
            pageNo
        )
        callbackCall!!.enqueue(object : Callback<CallbackWallpaper?> {
            override fun onResponse(
                call: Call<CallbackWallpaper?>,
                response: Response<CallbackWallpaper?>
            ) {
                binding!!.icError.pvProgress.visibility = View.GONE
                binding!!.icError.llErrorNetwork.visibility = View.GONE
                binding!!.rvRecycler.visibility = View.VISIBLE
                binding!!.slSwipe.isRefreshing = false
                val resp = response.body()
                if (resp != null && resp.totalHits == 500) {
                    postTotal = resp.total
                    displayApiResult(resp.hits)
                } else {
                    binding!!.icError.pvProgress.visibility = View.GONE
                    binding!!.slSwipe.isRefreshing = false
                    binding!!.icError.llErrorNetwork.visibility = View.VISIBLE
                }
            }

            override fun onFailure(call: Call<CallbackWallpaper?>, t: Throwable) {
                if (!call.isCanceled) {
                    binding!!.slSwipe.isRefreshing = false
                    binding!!.icError.pvProgress.visibility = View.GONE
                    binding!!.icError.llErrorNetwork.visibility = View.VISIBLE
                }
            }
        })
    }


    private fun displayApiResult(modelLists: List<ModelWallpaper?>) {
        adapterWallpaper!!.insertData(modelLists)
        binding!!.slSwipe.isRefreshing = false
        if (modelLists.isEmpty()) {
            binding!!.icError.llErrorNetwork.visibility = View.VISIBLE
        }
    }


    private fun requestAction(page: Int) {
        if (page != 1) {
            adapterWallpaper!!.setLoading()
        }
        Handler(Looper.getMainLooper()).postDelayed(
            { requestListPostApi(page, categoryString) },
            Constant.DELAY_TIME
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        binding!!.slSwipe.isRefreshing = false
        if (callbackCall != null && callbackCall!!.isExecuted) {
            callbackCall!!.cancel()
        }
    }


    override fun onItemClick(modelWallpaper: ModelWallpaper?) {
        val intent = Intent(requireActivity(), ActivityDetail::class.java)
        intent.putExtra(EXTRA_KEY, modelWallpaper)
        startActivity(intent)
    }

    override fun onItemDelete(modelWallpaper: ModelWallpaper?) {
        repository.insertFavorite(modelWallpaper)
        Toast.makeText(requireActivity(), "Added to Favorite", Toast.LENGTH_SHORT).show()
    }
}

