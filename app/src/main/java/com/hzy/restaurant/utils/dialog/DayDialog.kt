package com.hzy.restaurant.utils.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ConvertUtils
import com.chad.library.adapter4.BaseQuickAdapter
import com.chad.library.adapter4.viewholder.QuickViewHolder
import com.hzy.restaurant.R
import com.hzy.restaurant.bean.calendar.DayItem
import com.hzy.restaurant.bean.calendar.DayItemStatus
import com.hzy.restaurant.databinding.DialogDayBinding
import com.hzy.restaurant.utils.TimeUtils
import com.luck.picture.lib.decoration.GridSpacingItemDecoration
import java.util.Calendar

open class DayDialog : Dialog, View.OnClickListener {

    private var dayClickListener: DayClickListener? = null
    private lateinit var monthDay: TextView
    private lateinit var rv: RecyclerView
    private lateinit var right: ImageView
    private lateinit var allRl: RelativeLayout
    private lateinit var dayAll: TextView
    private lateinit var statusAll: ImageView
    private lateinit var myAdapt: MyAdapt
    private var calendar = Calendar.getInstance()
    private var selectTime: Long = 0
    private var isAll = false
    private var showAll = false


    constructor(context: Context) : this(context, R.style.DialogStyle)

    constructor(context: Context, themeResId: Int) : super(context, themeResId)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DialogDayBinding.inflate(layoutInflater)
        setContentView(binding.root)
        monthDay = binding.monthDay
        rv = binding.rv
        right = binding.right
        allRl = binding.allRl
        dayAll = binding.dayAll
        statusAll = binding.statusAll
        statusAll.isSelected = true
        binding.left.setOnClickListener(this)
        binding.right.setOnClickListener(this)
        binding.allRl.setOnClickListener(this)
        initRv()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.left -> {
                calendar.add(Calendar.MONTH, -1)
                setData()
                dayClickListener?.clickLeft(calendar.timeInMillis)
            }

            R.id.right -> {
                if (TimeUtils.getyyyyMMTime(calendar.timeInMillis)
                        .equals(TimeUtils.getyyyyMMTime(Calendar.getInstance().timeInMillis))
                ) {
                    return
                }
                calendar.add(Calendar.MONTH, 1)
                setData()
                dayClickListener?.clickRight(calendar.timeInMillis)
            }

            R.id.all_rl -> {
                //时间是今天
                val mCalendar: Calendar = Calendar.getInstance()
                mCalendar.set(Calendar.HOUR_OF_DAY, 0)
                mCalendar.set(Calendar.MINUTE, 0)
                mCalendar.set(Calendar.SECOND, 0)
                mCalendar.set(Calendar.MILLISECOND, 0)
                dayClickListener?.clickDay(mCalendar.timeInMillis, true)
                dismiss()
            }
        }
    }


    private fun initRv() {
        val layoutManager = GridLayoutManager(context, 7)
        rv.layoutManager = layoutManager
        rv.addItemDecoration(GridSpacingItemDecoration(7, ConvertUtils.dp2px(1f), false))
        myAdapt = MyAdapt(listOf())
        myAdapt.addOnItemChildClickListener(R.id.day) { adapter, _, position ->
            val dayItem: DayItem = adapter.getItem(position) ?: return@addOnItemChildClickListener
            if (dayItem.time == 0L) return@addOnItemChildClickListener
            dismiss()
            dayClickListener?.clickDay(dayItem.time, false)
        }
        rv.adapter = myAdapt
    }

    fun setMyAdapt(time: Long, isAll: Boolean = false, showAll: Boolean = false) {
        calendar.timeInMillis = time
        this.selectTime = time
        this.isAll = isAll
        this.showAll = showAll
        setData()
    }

    private var listHashMap: HashMap<String, List<DayItemStatus>>? = null

    @SuppressLint("NotifyDataSetChanged")
    fun setDayItemMap(listHashMap: HashMap<String, List<DayItemStatus>>?) {
        this.listHashMap = listHashMap
        myAdapt.notifyDataSetChanged()
        statusAll.visibility = if (listHashMap?.size!! > 0) View.VISIBLE else View.GONE
    }

    private fun setData() {
        allRl.visibility = if (showAll) View.VISIBLE else View.GONE
        dayAll.isSelected = isAll
        dayAll.paint.isFakeBoldText = dayAll.isSelected
        monthDay.text = TimeUtils.getyyyyMMTime(calendar.timeInMillis)
        val itemList: MutableList<DayItem> = ArrayList<DayItem>()
        calendar.set(Calendar.DAY_OF_MONTH, 1) //将今天设为1号
        var week: Int = calendar.get(Calendar.DAY_OF_WEEK) - 1
        if (week < 0) {
            week = 0
        }
        for (i in 0 until week) {
            itemList.add(DayItem(0))
        }
        for (i in 1..calendar.getActualMaximum(Calendar.DAY_OF_MONTH)) {
            calendar.set(Calendar.DAY_OF_MONTH, i)
            itemList.add(DayItem(calendar.time.time))
        }
        myAdapt.submitList(itemList)
        val equals: Boolean = TimeUtils.getyyyyMMTime(calendar.timeInMillis)
            .equals(TimeUtils.getyyyyMMTime(Calendar.getInstance().timeInMillis))
        right.alpha = if (equals) 0.6f else 1f
    }

    inner class MyAdapt(data: List<DayItem>) : BaseQuickAdapter<DayItem, QuickViewHolder>(data) {

        override fun onBindViewHolder(holder: QuickViewHolder, position: Int, item: DayItem?) {
            if (item == null) return
            val month = holder.getView<TextView>(R.id.day)
            val status = holder.getView<ImageView>(R.id.status)
            status.visibility =
                if (item.time == 0L || dayStatus(item.time) == -1) View.GONE else View.VISIBLE
            status.isSelected = dayStatus(item.time) == 0
            month.text = if (item.time == 0L) "" else TimeUtils.getdTime(item.time)
            month.setBackgroundResource(if (item.time == 0L) R.drawable.shape_gray2 else R.drawable.accent_white_selected)
            month.isEnabled = item.isEnable
            month.isSelected = !isAll && item.isSelect(selectTime)
            month.paint.isFakeBoldText = month.isSelected
        }

        override fun onCreateViewHolder(
            context: Context,
            parent: ViewGroup,
            viewType: Int
        ): QuickViewHolder {
            return QuickViewHolder(R.layout.day_item, parent)
        }
    }

    fun setMonthClickListener(dayClickListener: DayClickListener) {
        this.dayClickListener = dayClickListener
    }

    interface DayClickListener {
        fun clickDay(time: Long, isAll: Boolean)
        fun clickLeft(time: Long)
        fun clickRight(time: Long)
    }

    /**
     * 0:OK  1:Error
     */
    fun dayStatus(time: Long): Int {
        if (listHashMap == null || listHashMap!!.size == 0) return -1
        val itemStatusList: List<DayItemStatus>? =
            listHashMap!![TimeUtils.getyyyyMMddTimeForCN(time)]
        if (itemStatusList.isNullOrEmpty()) return -1
        for (itemStatus in itemStatusList) {
            if (itemStatus.batteryTempStatus == 1 || itemStatus.batteryVolStatus == 1) {
                return 1
            }
        }
        return 0
    }
}