package com.test.youtubeplayer.base.activity

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

/**
 * @param <DB> the type of the Data Binding
 */
abstract class BaseActivity<DB : ViewDataBinding> : AppCompatActivity() {

    /**
     * @return [Int]  Id for the layout to inflate
     */
    @LayoutRes
    abstract fun getLayoutRes(): Int

    val binding by lazy {
        DataBindingUtil.setContentView(this, getLayoutRes()) as DB
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.lifecycleOwner = this
        initialize()
    }

    /**
     *  To set up listener and live data observers, you can override this method
     */
    open fun initialize() {}
}