package com.dc.tc601.util.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ConvertUtils
import com.chad.library.adapter4.BaseQuickAdapter
import com.chad.library.adapter4.viewholder.QuickViewHolder
import com.hzy.restaurant.R
import com.hzy.restaurant.bean.calendar.MonthItem
import com.hzy.restaurant.bean.calendar.MonthItemStatus
import com.hzy.restaurant.databinding.DialogMonthBinding
import com.hzy.restaurant.utils.TimeUtils
import com.luck.picture.lib.decoration.GridSpacingItemDecoration
import java.util.Calendar

class MonthDialog private constructor(context: Context, themeResId: Int) :
    Dialog(context, themeResId), View.OnClickListener {
    private var monthClickListener: MonthClickListener? = null
    private lateinit var year: TextView
    private lateinit var rv: RecyclerView
    private lateinit var right: ImageView
    private lateinit var myAdapt: MyAdapt
    private val calendar = Calendar.getInstance()
    private var selectTime: Long = 0
    private var isAll = false
    private var showAll = false
    private var monthBeanList: List<MonthItemStatus>? = null

    constructor(context: Context) : this(context, R.style.DialogStyle)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DialogMonthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        year = binding.year
        rv = binding.rv
        right = binding.right
        binding.left.setOnClickListener(this)
        right.setOnClickListener(this)
        initRv()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.left -> {
                calendar.add(Calendar.YEAR, -1)
                setData(showAll)
                monthClickListener?.clickLeft(calendar.timeInMillis)
            }

            R.id.right -> {
                if (TimeUtils.getyyyyTimeForCN(calendar.timeInMillis)
                        .equals(TimeUtils.getyyyyTimeForCN(Calendar.getInstance().timeInMillis))
                ) {
                    return
                }
                calendar.add(Calendar.YEAR, 1)
                setData(showAll)
                monthClickListener?.clickRight(calendar.timeInMillis)
            }
        }
    }

    private fun initRv() {
        val layoutManager = GridLayoutManager(context, 3)
        rv.layoutManager = layoutManager
        rv.addItemDecoration(GridSpacingItemDecoration(3, ConvertUtils.dp2px(1f), false))
        myAdapt = MyAdapt(listOf())
        myAdapt.addOnItemChildClickListener(
            R.id.month
        ) { adapter, _, position ->
            val monthItem = adapter.getItem(position) ?: return@addOnItemChildClickListener
            dismiss()
            monthClickListener?.clickMonth(monthItem.time, monthItem.isAll)
        }
        rv.adapter = myAdapt
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (position == 12) {
                    3
                } else 1
            }
        }
    }

    fun setMyAdapt(time: Long, isAll: Boolean = false, showAll: Boolean = false) {
        calendar.timeInMillis = time
        selectTime = time
        this.isAll = isAll
        this.showAll = showAll
        setData(showAll)
    }

    fun setMonthBeanList(monthBeanList: List<MonthItemStatus>?) {
        this.monthBeanList = monthBeanList
        myAdapt.notifyDataSetChanged()
    }

    private fun setData(showAll: Boolean) {
        year.text = TimeUtils.getyyyyTimeForCN(calendar.timeInMillis)
        val itemList: MutableList<MonthItem> = ArrayList()
        for (i in 0..11) {
            calendar[Calendar.MONTH] = i
            itemList.add(MonthItem(calendar.time.time, false))
        }
        if (showAll) {
            itemList.add(MonthItem(itemList[0].time, true))
        }
        myAdapt.submitList(itemList)
        val equals = TimeUtils.getyyyyTimeForCN(calendar.timeInMillis)
            .equals(
                TimeUtils.getyyyyTimeForCN(Calendar.getInstance().timeInMillis),
                ignoreCase = true
            )
        right.alpha = if (equals) 0.6f else 1f
    }

    private inner class MyAdapt(data: List<MonthItem>) :
        BaseQuickAdapter<MonthItem, QuickViewHolder>(data) {
        override fun onBindViewHolder(holder: QuickViewHolder, position: Int, item: MonthItem?) {
            if (item == null) return
            val month = holder.getView<TextView>(R.id.month)
            val circle = holder.getView<ImageView>(R.id.blue_circle)
            month.text =
                if (item.isAll) context.getString(R.string.all) else TimeUtils.getMMTime(
                    item.time
                )
            if (isAll) {
                month.isSelected = item.isAll && TimeUtils.getYyyy(selectTime)
                    .equals(TimeUtils.getYyyy(item.time), ignoreCase = true)
            } else {
                month.isSelected = item.isSelect(selectTime) && !item.isAll
            }
            month.isEnabled = item.isAll || item.isEnable
//            holder.addOnClickListener(R.id.month)
            holder.setGone(
                R.id.blue_circle, monthBeanList != null
                        && if (item.isAll) monthBeanList!!.isNotEmpty() && monthBeanList!![0].year.equals(
                    TimeUtils.getYyyy(item.time),
                    ignoreCase = true
                ) else monthBeanList!!.contains(
                    MonthItemStatus(
                        TimeUtils.getYyyyMM(item.time)
                    )
                )
            )
            circle.isSelected = month.isSelected && circle.visibility == View.VISIBLE
        }

        override fun onCreateViewHolder(
            context: Context, parent: ViewGroup, viewType: Int,
        ): QuickViewHolder {
            return QuickViewHolder(R.layout.month_item, parent)
        }
    }

    fun setMonthClickListener(monthClickListener: MonthClickListener?) {
        this.monthClickListener = monthClickListener
    }

    interface MonthClickListener {
        fun clickMonth(time: Long, isAll: Boolean)
        fun clickLeft(time: Long)
        fun clickRight(time: Long)
    }
}