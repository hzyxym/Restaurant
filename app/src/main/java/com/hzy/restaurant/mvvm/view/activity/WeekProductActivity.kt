package com.hzy.restaurant.mvvm.view.activity

import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.hzy.restaurant.R
import com.hzy.restaurant.base.BaseActivity
import com.hzy.restaurant.bean.Week
import com.hzy.restaurant.databinding.ActivityWeekProductBinding
import com.hzy.restaurant.mvvm.view.fragment.setting.WeekProductFragment
import com.hzy.restaurant.mvvm.vm.ProductVM
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by hzy 2025/1/28
 * @Description:一周食谱activity
 */
@AndroidEntryPoint
class WeekProductActivity : BaseActivity<ActivityWeekProductBinding>() {
    private val vm by viewModels<ProductVM>()
    private val type = listOf(
        Week.Monday,
        Week.Tuesday,
        Week.Wednesday,
        Week.Thursday,
        Week.Friday,
        Week.Saturday,
        Week.Sunday
    )

    override fun getViewBinding(): ActivityWeekProductBinding {
        return ActivityWeekProductBinding.inflate(layoutInflater)
    }

    override fun initLocal() {
        super.initLocal()
        setToolsBarVisible(false)
        val titles = resources.getStringArray(R.array.week_array)
//        val titles = mutableListOf(
//            getString(R.string.monday),
//            getString(R.string.tuesday),
//            getString(R.string.wednesday),
//            getString(R.string.thursday),
//            getString(R.string.friday),
//            getString(R.string.saturday),
//            getString(R.string.sunday),
//        )
        val adapter = ViewPagerAdapter(this, type)
        binding.viewpager.adapter = adapter
        // 绑定 TabLayout 和 ViewPager2
        TabLayoutMediator(binding.tabLayout, binding.viewpager) { tab, position ->
            tab.text = titles[position]
        }.attach()
    }

    inner class ViewPagerAdapter(
        fragmentManager: FragmentActivity,
        private val tabType: List<Week>
    ) : FragmentStateAdapter(fragmentManager) {

        override fun getItemCount(): Int = tabType.size

        override fun createFragment(position: Int): Fragment {
            return WeekProductFragment.newInstance(tabType[position])
        }
    }

}