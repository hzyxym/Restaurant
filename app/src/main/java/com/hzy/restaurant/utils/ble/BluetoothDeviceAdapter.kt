package com.hzy.restaurant.utils.ble

import android.content.Context
import android.drm.DrmStore.DrmObjectType.CONTENT
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

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        return when (getItemViewType(position)) {
            TITLE -> {
                val itemView = LayoutInflater.from(mContext).inflate(R.layout.text_item, parent, false)
                val tv_title = convertView?.findViewById<TextView>(R.id.text)
                tv_title?.setTextColor(ContextCompat.getColor(mContext, R.color.white))
                tv_title?.gravity = Gravity.START
                if (position == 0) {
                    tv_title?.text = mContext.resources.getString(R.string.paired)
                } else {
                    tv_title?.text = mContext.resources.getString(R.string.unpaired)
                }
                itemView
            }

            CONTENT -> {
                var bluetoothParameter: BluetoothParameter? = null
                if (position < pairedDevices.size + 1) {
                    bluetoothParameter = pairedDevices[position - 1]
                }
                if (position > pairedDevices.size + 1 && !newDevices.isEmpty()) {
                    bluetoothParameter = newDevices[position - pairedDevices.size - 2]
                }

                val itemView = LayoutInflater.from(mContext).inflate(R.layout.bluetooth_list_item, parent, false)
                val tvName = convertView?.findViewById<TextView>(R.id.b_name)
                val tvMac = convertView?.findViewById<TextView>(R.id.b_mac)
                val tvStrength = convertView?.findViewById<TextView>(R.id.b_info)
                tvName?.text = "bluetoothParameter?.bluetoothName"
                tvMac?.text = bluetoothParameter?.bluetoothMac
                tvStrength?.text = bluetoothParameter?.bluetoothStrength
                itemView
            }

            else -> {
                println("else")
                convertView
            }
        }
    }

//    override fun getView(position: Int, convertView: View, parent: ViewGroup): View {

//    }

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
