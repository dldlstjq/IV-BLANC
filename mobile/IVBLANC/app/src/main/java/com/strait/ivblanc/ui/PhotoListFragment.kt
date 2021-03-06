package com.strait.ivblanc.ui

import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.ImageView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.strait.ivblanc.R
import com.strait.ivblanc.adapter.HorizontalRVAdapter
import com.strait.ivblanc.adapter.PhotoListRVAdapter
import com.strait.ivblanc.config.BaseFragment
import com.strait.ivblanc.data.model.dto.Clothes
import com.strait.ivblanc.data.model.dto.Style
import com.strait.ivblanc.data.model.viewmodel.ClothesViewModel
import com.strait.ivblanc.data.model.viewmodel.FriendViewModel
import com.strait.ivblanc.data.model.viewmodel.MainViewModel
import com.strait.ivblanc.data.model.viewmodel.StyleViewModel
import com.strait.ivblanc.databinding.FragmentPhotoListBinding
import com.strait.ivblanc.src.clothesDetail.ClothesDetailActivity
import com.strait.ivblanc.src.main.MainActivity
import com.strait.ivblanc.src.photoSelect.PhotoSelectActivity
import com.strait.ivblanc.src.styleMaking.StyleMakingActivity
import com.strait.ivblanc.util.CategoryCode
import com.strait.ivblanc.util.Status


private const val TAG = "PhotoListFragment_debuk"
class PhotoListFragment : BaseFragment<FragmentPhotoListBinding>(
    FragmentPhotoListBinding::bind,
    R.layout.fragment_photo_list
) {
    lateinit var horizontalRVAdapter: HorizontalRVAdapter<Clothes>
    lateinit var photoListRVAdapter: PhotoListRVAdapter<Clothes>
    private val viewModel: MainViewModel by activityViewModels()
    private val clothesViewModel: ClothesViewModel by activityViewModels()
    lateinit var largeCategories: List<String>
    var smallCategories = listOf<Pair<Int, Int>>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(tag)
        // TODO: 2022/01/26 ?????? ????????? ?????? ?????? ??? ??????
        viewModel.clothesResponseStatus.observe(requireActivity()) {

        }
    }

    private fun init(tag: String?) {
        tag?: return
        setRecyclerView()
        setDropDown()
        setRecyclerViewClickListener(tag)
        setObserverLiveData()
        setFloatingButton(tag)
    }

    private fun setRecyclerViewClickListener(tag: String) {
        when(tag) {
            // ??? ???
            "clothes" -> {
                horizontalRVAdapter.itemClickListener = object : HorizontalRVAdapter.ItemClickListener {
                    override fun onClick(position: Int) {
                        val item = horizontalRVAdapter.data[position]
                        val intent = Intent(
                            requireActivity(),
                            ClothesDetailActivity::class.java
                        ).putExtra("clothes", item)
                        (requireActivity() as MainActivity).addClothesContract.launch(intent)
                    }
                }
                photoListRVAdapter.itemClickListener = object : PhotoListRVAdapter.ItemClickListener {
                    override fun onClick(position: Int) {
                        val item = photoListRVAdapter.data[position]
                        val intent = Intent(
                            requireActivity(),
                            ClothesDetailActivity::class.java
                        ).putExtra("clothes", item)
                        (requireActivity() as MainActivity).addClothesContract.launch(intent)
                    }
                }
                photoListRVAdapter.itemLongClickListener =
                    object : PhotoListRVAdapter.ItemLongClickListener {
                        override fun onLongClick(position: Int) {
                            val item = photoListRVAdapter.data[position]
                            showDeleteDialog(item)
                        }
                    }

            }
            // ?????? ???
            "f0" -> {
                horizontalRVAdapter.itemClickListener = object : HorizontalRVAdapter.ItemClickListener {
                    override fun onClick(position: Int) {
                        val item = horizontalRVAdapter.data[position]
                        val intent = Intent(
                            requireActivity(),
                            ClothesDetailActivity::class.java
                        ).putExtra("clothes", item).putExtra("friend", "friend")
                        startActivity(intent)
                    }
                }
                photoListRVAdapter.itemClickListener = object : PhotoListRVAdapter.ItemClickListener {
                    override fun onClick(position: Int) {
                        val item = photoListRVAdapter.data[position]
                        val intent = Intent(
                            requireActivity(),
                            ClothesDetailActivity::class.java
                        ).putExtra("clothes", item).putExtra("friend", "friend")
                        startActivity(intent)
                    }
                }
            }
        }
    }

    private fun setRecyclerView() {
        horizontalRVAdapter = HorizontalRVAdapter()

        binding.recyclerViewPhotoListFHorizontal.apply {
            adapter = horizontalRVAdapter
            layoutManager = LinearLayoutManager(requireActivity(), RecyclerView.HORIZONTAL, false)
            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    super.getItemOffsets(outRect, view, parent, state)
                    outRect.right = 20
                }
            })
        }

        photoListRVAdapter = PhotoListRVAdapter()

        binding.recyclerViewPhotoListF.apply {
            adapter = photoListRVAdapter
            layoutManager = GridLayoutManager(requireContext(), 3)
        }
    }

    private fun setObserverLiveData() {
        clothesViewModel.clothesList.observe(this) {
            photoListRVAdapter.setDatas(it)
            if(it.isEmpty()){
                binding.noClothes.visibility=View.VISIBLE
            }else{
                binding.noClothes.visibility=View.GONE
            }
        }
        clothesViewModel.recentlyAddedClothesList.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                binding.linearLayoutPhotoListFRecent.visibility = View.VISIBLE
            } else {
                binding.linearLayoutPhotoListFRecent.visibility = View.GONE
            }
            horizontalRVAdapter.setDatas(it)
        }
    }

    private fun setFloatingButton(tag: String) {
        when(tag) {
            "clothes" -> {
                binding.fabMain.setOnClickListener {
                    if(requireActivity() is MainActivity) {
                        val intent = Intent(requireActivity(), PhotoSelectActivity::class.java).apply {
                            putExtra("intend", PhotoSelectActivity.CLOTHES)
                        }
                        (requireActivity() as MainActivity).addClothesContract.launch(intent)
                    }

                }
            }
            "f0" -> {
                binding.fabMain.visibility = View.GONE
            }
        }
    }

    private fun setDropDown() {
        largeCategories = getLargeCategoryString()
        // ????????? ??? ????????? ??????, ????????? ????????? ??????
        binding.autoCompleteTextViewPhotoListFCategory.setText(resources.getText(R.string.total))
        binding.autoCompleteTextViewPhotoListFSmallCategory.setText(resources.getText(R.string.total))

        val largeCategoryAdapter =
            ArrayAdapter(requireActivity(), R.layout.list_category_item, largeCategories)
        binding.autoCompleteTextViewPhotoListFCategory.setAdapter(largeCategoryAdapter)

        // ???????????? ????????? ?????? ????????? ??????
        clothesViewModel.largeCategory.observe(this) {
            smallCategories = clothesViewModel.getSmallCategoriesByLargeCategory(it)
            val smallCategoryAdapter = ArrayAdapter(
                requireActivity(),
                R.layout.list_category_item,
                getSmallCategoryStringByLargeCategory(it)
            )
            binding.autoCompleteTextViewPhotoListFSmallCategory.setAdapter(smallCategoryAdapter)
        }

        // ????????? edittext observe -> viewModel??? ????????? ???????????? ??????, ????????? ??????????????? clothesUpdate
        // ???????????? ???????????? ???????????? ????????? ??????.
        binding.autoCompleteTextViewPhotoListFCategory.addTextChangedListener {
            val largeCategory = clothesViewModel.largeCategorySet.find { pair ->
                it.toString() == resources.getString(pair.second) && pair.first in 0..9
            }

            largeCategory?.let {
                binding.autoCompleteTextViewPhotoListFSmallCategory.setText(resources.getText(R.string.total))
                clothesViewModel.setLargeCategory(it.first)
                clothesViewModel.updateClothesByCategory(it.first)
            }
        }

        // ????????? edittext observe -> viewModel ???????????? ?????? ??? ?????????
        binding.autoCompleteTextViewPhotoListFSmallCategory.addTextChangedListener {
            if (it.toString().isBlank() || it.toString().isEmpty()) {
                clothesViewModel.setSmallCategory(CategoryCode.UNSELECTED)
                return@addTextChangedListener
            }

            val smallCategory = smallCategories.find { pair ->
                val string = resources.getString(pair.second)
                it.toString() == string
            }

            smallCategory?.let { pair ->
                if (pair.first != CategoryCode.TOTAL_SMALL) {
                    clothesViewModel.updateClothesByCategory(pair.first)
                } else {
                    // TOTAL_SMALL????????? ???????????? ??? ??????
                    clothesViewModel.updateClothesByCategory(clothesViewModel.largeCategory.value!!)
                }
            }
        }
    }

    private fun getLargeCategoryString(): List<String> {
        val result = mutableListOf<String>()
        clothesViewModel.largeCategorySet.forEach {
            result.add(resources.getString(it.second))
        }
        return result.toList()
    }

    private fun getSmallCategoryStringByLargeCategory(largeCategory: Int): List<String> {
        val result = mutableListOf<String>()
        result.add(resources.getString(R.string.total))
        clothesViewModel.getSmallCategoriesByLargeCategory(largeCategory).forEach {
            result.add(resources.getString(it.second))
        }
        return result.toList()
    }

    fun showDeleteDialog(item: Clothes) {
        val content = "??? ?????? ?????????????????????????"
        DeleteDialog(requireActivity())
            .setContent(content)
            .setOnPositiveClickListener {
                clothesViewModel.deleteClothesById((item).clothesId)
            }.build().show()
    }
}