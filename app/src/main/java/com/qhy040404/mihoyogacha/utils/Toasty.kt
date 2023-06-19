package com.qhy040404.mihoyogacha.utils

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.ContextThemeWrapper
import android.view.Gravity
import android.widget.Toast
import androidx.annotation.AnyThread
import androidx.annotation.MainThread
import androidx.annotation.StringRes
import com.qhy040404.mihoyogacha.view.ToastView
import java.lang.ref.WeakReference

object Toasty {
    private val handler = Handler(Looper.getMainLooper())
    private var toastRef: WeakReference<Toast>? = null

    fun cancel() = toastRef?.get()?.cancel()

    @AnyThread
    fun showShort(context: Context, message: String) {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            //noinspection WrongThread
            show(context, message, Toast.LENGTH_SHORT)
        } else {
            handler.post { show(context, message, Toast.LENGTH_SHORT) }
        }
    }

    @AnyThread
    fun showShort(context: Context, @StringRes res: Int) =
        showShort(context, context.getString(res))

    @AnyThread
    fun showLong(context: Context, message: String) {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            //noinspection WrongThread
            show(context, message, Toast.LENGTH_LONG)
        } else {
            handler.post { show(context, message, Toast.LENGTH_LONG) }
        }
    }

    @AnyThread
    fun showLong(context: Context, @StringRes res: Int) =
        showLong(context, context.getString(res))

    @Suppress("DEPRECATION")
    @MainThread
    private fun show(context: Context, message: String, duration: Int) {
        cancel()

        val toast = if (OsUtils.atLeastR() && context !is ContextThemeWrapper) {
            Toast(context).also {
                it.duration = duration
                it.setText(message)
            }
        } else {
            val view = ToastView(context).also {
                it.message.text = message
            }
            Toast(context).also {
                it.setGravity(Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM, 0, 200)
                it.duration = duration
                it.view = view
            }
        }
        toastRef = WeakReference(toast)
        toast.show()
    }
}

/**
 * Show short-time toast with String
 *
 * @param message original string
 */
@AnyThread
fun Context.showToast(message: String) = Toasty.showShort(this, message)

/**
 * Show short-time toast with String
 *
 * @param res string's resource id
 */
@AnyThread
fun Context.showToast(@StringRes res: Int) = Toasty.showShort(this, res)

/**
 * Show long-time toast with String
 *
 * @param message original string
 */
@AnyThread
fun Context.showLongToast(message: String) = Toasty.showLong(this, message)

/**
 * Show long-time toast with String
 *
 * @param res string's resource id
 */
@AnyThread
fun Context.showLongToast(@StringRes res: Int) = Toasty.showLong(this, res)