package com.hzy.restaurant.utils.ble

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.hzy.restaurant.R
import com.hzy.restaurant.bean.BluetoothParameter

/**
 * 作者： Circle
 * 创造于 2018/5/24.
 */
class BluetoothDeviceAdapter(
    private val pairedDevices: List<BluetoothParameter>,
    private val newDevices: List<BluetoothParameter>,
    private val mContext: Context,
) : BaseAdapter() {
    override fun getCount(): Int {
        return pairedDevices.size + newDevices.size + 2
    }

    override fun getItem(position: Int): Any {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        return when (getItemViewType(position)) {
            TITLE -> {
                val itemView =
                    LayoutInflater.from(mContext).inflate(R.layout.text_item, parent, false)
                val tvTitle = itemView.findViewById<TextView>(R.id.text)
                tvTitle.setTextColor(ContextCompat.getColor(mContext, R.color.white))
                tvTitle.gravity = Gravity.START
                if (position == 0) {
                    tvTitle.text = mContext.resources.getString(R.string.paired)
                    tvTitle.setTextColor(ContextCompat.getColor(mContext,R.color.theme_green))
                } else {
                    tvTitle.text = mContext.resources.getString(R.string.unpaired)
                    tvTitle.setTextColor(ContextCompat.getColor(mContext,R.color.white_40))
                }
                itemView
            }

            else -> {
                val itemView = LayoutInflater.from(mContext)
                    .inflate(R.layout.bluetooth_list_item, parent, false)
                val tvName = itemView.findViewById<TextView>(R.id.b_name)
                val tvMac = itemView.findViewById<TextView>(R.id.b_mac)
                val tvStrength = itemView.findViewById<TextView>(R.id.b_info)
                var bluetoothParameter: BluetoothParameter? = null
                if (position < pairedDevices.size + 1) {
                    bluetoothParameter = pairedDevices[position - 1]
                    tvName.setTextColor(ContextCompat.getColor(mContext,R.color.white))
                    tvMac.setTextColor(ContextCompat.getColor(mContext,R.color.white))
                    tvStrength.setTextColor(ContextCompat.getColor(mContext,R.color.white))
                }
                if (position > pairedDevices.size + 1 && newDevices.isNotEmpty()) {
                    bluetoothParameter = newDevices[position - pairedDevices.size - 2]
                    tvName.setTextColor(ContextCompat.getColor(mContext,R.color.white_40))
                    tvMac.setTextColor(ContextCompat.getColor(mContext,R.color.white_40))
                    tvStrength.setTextColor(ContextCompat.getColor(mContext,R.color.white_40))
                }
                tvName.text = bluetoothParameter?.bluetoothName
                tvMac.text = bluetoothParameter?.bluetoothMac
                tvStrength.text = bluetoothParameter?.bluetoothStrength
                itemView
            }
        }
    }

    override fun getViewTypeCount(): Int {
        return 2
    }

    override fun getItemViewType(position: Int): Int {
        return if ((position == pairedDevices.size + 1) || (position == 0)) {
            TITLE
        } else {
            CONTENT
        }
    }

    companion object {
        private const val TITLE = 0
        private const val CONTENT = 1
    }
}
