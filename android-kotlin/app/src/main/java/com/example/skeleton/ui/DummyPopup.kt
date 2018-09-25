package com.example.skeleton.ui

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import com.example.skeleton.R
import com.example.skeleton.helper.LP
import com.example.skeleton.helper.Logger
import com.example.skeleton.helper.ResourceHelper.dp
import com.example.skeleton.helper.ResourceHelper.color
import com.example.skeleton.helper.ResourceHelper.font
import com.example.skeleton.ui.base.BaseBottomPopup
import com.example.skeleton.widget.Button
import com.example.skeleton.widget.Checkbox

class DummyPopup : BaseBottomPopup {
    var onClose: ((self: DummyPopup) -> Unit)? = null

    //region Lifecycle
    //---------------------------------------------------------------
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)
    override fun onCreateView(context: Context): View {
        val contentView = LinearLayout(context)
        contentView.orientation = LinearLayout.VERTICAL
        contentView.gravity = Gravity.CENTER_HORIZONTAL
        contentView.setBackgroundColor(color(R.color.white))

        val checkbox = Checkbox(context)
        checkbox.setText(R.string.dummy_check)
        checkbox.setTypeface(font(R.font.barlow_condensed_regular))
        checkbox.setTextSize(20f)
        checkbox.setButtonDrawable(R.drawable.ic_checkbox, dp(28), dp(28))
        contentView.addView(checkbox, LP.linear(LP.WRAP_CONTENT, LP.WRAP_CONTENT)
                .setMargins(0, dp(32), 0, dp(32))
                .build())

        val close = Button(context)
        close.setTextPadding(dp(16), dp(8), dp(16), dp(8))
        close.setBackgroundColor(color(R.color.main_next_bg))
        close.setTextColor(color(R.color.main_next_text))
        close.setTypeface(font(R.font.barlow_condensed_bold))
        close.setTextSize(24f)
        close.setText(R.string.dummy_close)
        contentView.addView(close, LP.linear(LP.WRAP_CONTENT, LP.WRAP_CONTENT)
                .setMargins(0, dp(96), 0, dp(96))
                .build())

        // Register event listener
        checkbox.onCheckChanged = onCheckToggle
        close.setOnClickListener(onCloseClick)
        return contentView
    }
    //---------------------------------------------------------------
    //endregion

    override fun onAttach(view: View) {}
    override fun onDetach(view: View) {}

    //region UI Events
    //---------------------------------------------------------------
    private val onCloseClick = OnClickListener { _ ->
        onClose?.invoke(this@DummyPopup)
    }
    private val onCheckToggle = { _: Checkbox, isChecked: Boolean ->
        Logger.d("DummyPopup", "Checkbox: " + if (isChecked) "YES" else "NO")
    }
    //---------------------------------------------------------------
    //endregion
}
