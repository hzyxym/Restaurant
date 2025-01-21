package com.hzy.restaurant.utils.dialog

import android.os.Bundle
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.hzy.restaurant.R
import com.hzy.restaurant.databinding.EdittextDialogBinding

/**
 * Created by hzy in 2024/2/3
 * description: 编辑dialog
 * */
class EditValueDialog : DialogFragment() {
    private lateinit var binding: EdittextDialogBinding
    private var listener: ((String) -> Unit)? = null
    private var defaultValue: String? = null
    private var hint: String? = null
    private var title: String? = null
    private var ok: String? = null
    private var maxLength: Int? = null
    companion object {
        const val DEFAULT_VALUE = "defaultValue"
        const val TITLE = "title"
        const val HINT = "hint"
        const val OK = "ok"
        @JvmStatic
        fun newInstance(defaultValue: String?, title: String? = null, hint: String? = null, ok: String? = null): EditValueDialog {
            val fragment = EditValueDialog()
            val args = Bundle()
            args.putString(TITLE, title)
            args.putString(DEFAULT_VALUE, defaultValue)
            args.putString(HINT, hint)
            args.putString(OK, ok)
            fragment.setArguments(args)
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = EdittextDialogBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.apply {
            setBackgroundDrawableResource(R.color.transparent)
            setLayout(
                (resources.displayMetrics.widthPixels * 0.9).toInt(),
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        arguments?.let {
            defaultValue = it.getString(DEFAULT_VALUE)
            title = it.getString(TITLE)
            hint = it.getString(HINT)
            ok = it.getString(OK)
        }
        title?.let { binding.title.text = it }
        hint?.let { binding.editTextNumber.hint = it }
        ok?.let { binding.ok.text = it }
        defaultValue?.let { binding.editTextNumber.setText(it) }
        maxLength?.let { binding.editTextNumber.filters = arrayOf(InputFilter.LengthFilter(it)) }
        binding.close.setOnClickListener {
            this.dismiss()
        }
        binding.cancel.setOnClickListener {
            this.dismiss()
        }
        binding.ok.setOnClickListener {
            val name = binding.editTextNumber.text.toString()
            if (name.isNotEmpty()) {
                listener?.invoke(name)
                this.dismiss()
            }
        }
    }

    fun setDialogListener(listener: (String) -> Unit): EditValueDialog {
        this.listener = listener
        return this
    }

    fun setMaxLength(maxLength: Int): EditValueDialog {
        this.maxLength = maxLength
        return this
    }
}